/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.pocket;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import ruraomsk.list.ru.cthulhu.message.*;
import ruraomsk.list.ru.cthulhu.*;

/**
 * Центр обработки данных со стороны мастера
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class MasterDC extends BaseRout
{

    private MainMaster mainmaster;
    private Integer controller;
    private Registers regs;
    private long bufferreadstep;
//    private ArrayList<Integer> write;
    private ConcurrentLinkedQueue <OneReg> needwrite;
    private ConcurrentLinkedQueue <BaseMessage> needread;
    private int lastwrite;
    private long lasttimeread=0L;
    private long lasttimewrite=0L;
    static final int PART=100;
    
    public MasterDC(MainMaster mainmaster, int controller, long bufferreadstep)
    {
        this.mainmaster = mainmaster;
        this.controller = controller;
        regs = mainmaster.getRegisters();
        mainmaster.addRouter(controller, Util.GetValueProperty, this);
        mainmaster.addRouter(controller, Util.GetRingBuffer, this);
        mainmaster.addRouter(controller, Util.SetValueProperty, this);
        mainmaster.addRouter(controller, Util.GetProperty, this);
        mainmaster.addRouter(controller, Util.SetProperty, this);
        this.bufferreadstep = bufferreadstep;
        needwrite=new ConcurrentLinkedQueue<>();
        needread=new ConcurrentLinkedQueue<>();
        lastwrite = Util.RequestOk;
        start();
    }
    public void stopAll(){
        this.interrupt();
//            this.join(bufferreadstep*2);
            Log.CORE.info("MasterDC canel "+controller.toString()+" остановлен...");
    }

    /**
     * Прочитать все переменные
     */
    public void readAllSendingValue()
    {
        GetValuePropertyRequest message = new GetValuePropertyRequest();
        int count=0;
        for (OneReg oreg : regs.getOneRegs(controller)) {
            if(oreg.getReg().isSending()){
                if(count++>PART){
                    count=0;
                    needread.add(message);
                    message = new GetValuePropertyRequest();
                }
                message.addValue(oreg.getuId());
            }
        }
        if(message.getLenght()>0) needread.add(message);

//        mainmaster.sendMessage(controller, message);
    }
    /**
     * Прочитать все переменные не принятые еще
     */
    public void readAllNoGoodSendingValue()
    {
        GetValuePropertyRequest message = new GetValuePropertyRequest();
        int count=0;
        for (OneReg oreg : regs.getOneRegs(controller)) {
            if(oreg.getGood()!=Util.CT_DATA_NOGOOD) continue;
            if(oreg.getReg().isSending()){
                if(count++>PART){
                    count=0;
                    needread.add(message);
                    message = new GetValuePropertyRequest();
                }
                message.addValue(oreg.getuId());
            }
        }
        if(message.getLenght()>0) needread.add(message);

//        mainmaster.sendMessage(controller, message);
    }
    public void readAllNotSendingValue()
    {
        GetValuePropertyRequest message = new GetValuePropertyRequest();
        int count=0;
        for (OneReg oreg : regs.getOneRegs(controller)) {
            if(!oreg.getReg().isSending()&&!oreg.getReg().isConstant()){
                if(count++>PART){
                    count=0;
                    needread.add(message);
                    message = new GetValuePropertyRequest();
                }
                message.addValue(oreg.getuId());
            }
        }
        if(message.getLenght()>0) needread.add(message);
//        mainmaster.sendMessage(controller, message);
    }
    public void readAllNoGoodNotSendingValue()
    {
        GetValuePropertyRequest message = new GetValuePropertyRequest();
        int count=0;
        for (OneReg oreg : regs.getOneRegs(controller)) {
            if(oreg.getGood()!=Util.CT_DATA_NOGOOD) continue;
            if(!oreg.getReg().isSending()&&!oreg.getReg().isConstant()){
                if(count++>PART){
                    count=0;
                    needread.add(message);
                    message = new GetValuePropertyRequest();
                }
                message.addValue(oreg.getuId());
            }
        }
        if(message.getLenght()>0) needread.add(message);
//        mainmaster.sendMessage(controller, message);
    }
    public void upRingBuffer(){
            needread.add( new GetRingBufferRequest());
            
    }
    public void readAllProperties()
    {
        GetPropertyRequest message = new GetPropertyRequest();
        for (OneReg oreg : regs.getOneRegs(controller)) {
            for(Property prop:regs.getProperties(controller, oreg.getuId())){
                message.addProperty(prop);
            }
        }
        mainmaster.sendMessage(controller,message);
    }
    public void setProperties(int uId){
        SetPropertyRequest message=new SetPropertyRequest();
        for(Property prop:regs.getProperties(controller, uId)){
            message.addPropertyValue(prop);
        }
        mainmaster.sendMessage(controller,message);
    }
    public Integer getController(){
        return controller;
    }
    /**
     * Передать данные в устройство
     *
     * @param oreg - Значение регистра
     */
    public void putValue(OneReg oreg)
    {
        needwrite.add(oreg);
        return;
    }

    public void sendValues()
    {
        SetValuePropertyRequest req = new SetValuePropertyRequest();
        boolean sendflag=false;
        OneReg oreg;
        while((oreg=needwrite.poll())!=null){
            req.addValue(oreg);
            sendflag=true;
        }
        if(sendflag) {
            mainmaster.sendMessage(controller, req);
            lasttimewrite=System.currentTimeMillis();
        }
    }

    @Override
    public void addMessage(BaseMessage message)
    {
        lasttimeread=System.currentTimeMillis();
        if (message.getId() == Util.SetProperty){
            lastwrite = message.getReqEC();
            return;
        }
        if (message.getId() == Util.GetProperty) {
            GetPropertyResponse res = (GetPropertyResponse) message;
            for(Integer uId:res.getUIds()){
                for(Property prop:res.getProps(uId)){
                    Property pr=regs.getProperty(controller, uId, prop.getPrpId());;
                    pr.setValue(prop.getValue());
                    regs.setProperty(controller, pr);
                }
            }
            return;
        }
        if (message.getId() == Util.GetValueProperty) {
            GetValuePropertyResponse res = (GetValuePropertyResponse) message;
            for (OneReg oreg : res.getValues()) {
                regs.setRegister(controller, oreg);
            }
            return;
        }
        if (message.getId() == Util.GetRingBuffer) {
            GetRingBufferResponse res = (GetRingBufferResponse) message;
            for (OneReg oreg : res.getLosts()) {
                oreg.setGood(Util.CT_DATA_LOST);
                regs.setRegister(controller, oreg);
            }
            for (Long time : res.getTimes()) {
                ArrayList<OneReg> arone = res.getOneRegs(time);
                if (arone == null) {
                    Log.CORE.info("Нет значений в буфере на время=" + Long.toString(time));
                    continue;
                }
                for (OneReg oreg : arone) {
                    regs.setRegister(controller, oreg);
                }
            }
            if(res.getReqEC()!=Util.RequestOk) upRingBuffer();
            return;
        }
        if (message.getId() == Util.SetValueProperty) {
            lastwrite = message.getReqEC();
            return;
        }
    }

    @Override
    public String toString()
    {
        String result = "master DC=" + Integer.toString(controller) + " step=" + Long.toString(bufferreadstep);
        return result;
    }
    public void toDataTable(DataTable table) {

        table.addRecord(controller,"readtime",new Date(lasttimeread).toString());
        table.addRecord(controller,"writetime",new Date(lasttimewrite).toString());
        table.addRecord(controller,"write",Util.getShortError(lastwrite)+"/"+Util.getLongError(lastwrite));
        table.addRecord(controller,"queue",Integer.toString(needread.size()));
        
    }
    @Override
    public void run()
    {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(bufferreadstep);
            }
            catch (InterruptedException ex) {
                break;
            }
            sendValues();
            BaseMessage message;
            int count=0;
            while((message=needread.poll())!=null){
                mainmaster.sendMessage(controller, message);
                if(count++>3) break;
            }
        }
    }

}
