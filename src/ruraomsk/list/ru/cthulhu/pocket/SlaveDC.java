/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.pocket;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import ruraomsk.list.ru.cthulhu.message.*;
import ruraomsk.list.ru.cthulhu.*;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class SlaveDC extends BaseRout
{

    private MainSlave mainslave;
    private int controller;
    private Registers regs;
    private long changestep;
    private HashMap<Long, ArrayList<OneReg>> history;
    Long count = 0L;

    public SlaveDC(MainSlave mainslave, int controller, long changestep)
    {
        this.mainslave = mainslave;
        this.controller = controller;
        regs = mainslave.getRegisters();
        mainslave.addRouter(controller, Util.GetValueProperty, this);
        mainslave.addRouter(controller, Util.GetRingBuffer, this);
        mainslave.addRouter(controller, Util.SetValueProperty, this);
        mainslave.addRouter(controller, Util.GetProperty, this);
        mainslave.addRouter(controller, Util.SetProperty, this);

        history = new HashMap<>();
        this.changestep = changestep;
        start();
    }

    @Override
    public void addMessage(BaseMessage message)
    {
        if (message.getId() == Util.SetProperty) {
            makeSetPropertyResponse(message);
            return;
        }
        if (message.getId() == Util.GetProperty) {
            makeGetPropertyResponse(message);
            return;
        }
        if (message.getId() == Util.GetValueProperty) {
            makeGetValuePropertyResponse(message);
            return;
        }
        if (message.getId() == Util.GetRingBuffer) {
            GetRingBufferResponse res = getBufferRing();
            res.setReqIter(message.getReqIter());
            mainslave.sendMessage(controller, res);
            return;
        }
        if (message.getId() == Util.SetValueProperty) {
            SetValuePropertyRequest req = (SetValuePropertyRequest) message;
            for (OneReg oreg : req.getValues()) {
                regs.changeRegister(controller, oreg);
            }
            SetValuePropertyResponse res = new SetValuePropertyResponse();
            res.setReqIter(req.getReqIter());
            mainslave.sendMessage(controller, res);
            return;
        }

    }

    @Override
    public String toString()
    {
        return "slave DC=" + Integer.toString(controller) + " step=" + Long.toString(changestep);
    }

    @Override
    public void run()
    {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(changestep);
            }
            catch (InterruptedException ex) {
                break;
            }
            synchronized (history) {
                count++;
                for (OneReg oreg : regs.getOneRegs(controller)) {
                    Object value = oreg.getValue();
                    switch (oreg.getReg().getType()) {
                        case Util.CT_TYPE_BOOL:
                            oreg.setValue(!((boolean) value));
                            break;
                        case Util.CT_TYPE_INTEGER:
                            oreg.setValue(((int) value) + 1);
                            break;
                        case Util.CT_TYPE_FLOAT:
                            oreg.setValue(((float) value) + 1.0f);
                            break;
                        case Util.CT_TYPE_LONG:
                            oreg.setValue(((int) value) + 1);
                            break;
                        case Util.CT_TYPE_BYTE:
                            oreg.setValue((((int) value) + 1)&0xff);
                            break;
                            
                    }
                    oreg.setGood(Util.CT_DATA_GOOD);
                    Long time = System.currentTimeMillis();
                    oreg.setTime(time);
                    OneReg or = new OneReg(time, oreg.getReg(), oreg.getValue(), oreg.getGood());
                    ArrayList<OneReg> aroreg = history.get(count);
                    if (aroreg == null) {
                        aroreg = new ArrayList<>();
                    }
                    aroreg.add(or);
                    history.put(count, aroreg);
                }

            }

        }
    }

    private GetRingBufferResponse getBufferRing()
    {
        GetRingBufferResponse res = new GetRingBufferResponse();
        res.setRBIter(1);
        res.setRBFlags((byte) 1);
        synchronized (history) {
            for (Long cnt : history.keySet()) {
                ArrayList<OneReg> aroreg = history.get(cnt);
                for (OneReg oreg : aroreg) {
                    res.addOneReg(oreg);
                }
            }
            history.clear();
            count = 0L;
        }
//        System.out.println(res.toString());
        return res;
    }

    private void makeGetValuePropertyResponse(BaseMessage message)
    {
        GetValuePropertyRequest req = (GetValuePropertyRequest) message;
        GetValuePropertyResponse res = new GetValuePropertyResponse();
        if (req.isRange()) {
            for (int i = req.getLeft(); i <= req.getRight(); i++) {
                OneReg oreg = regs.getOneReg(controller, i);
                if (oreg == null) {
                    continue;
                }
                res.addValue(oreg);
            }
        }
        else {
            for (Integer uId : req.getValues()) {
                OneReg oreg = regs.getOneReg(controller, uId);
                if (oreg == null) {
                    continue;
                }
                res.addValue(oreg);
            }
        }
        res.setReqIter(req.getReqIter());
        mainslave.sendMessage(controller, res);

    }

    private void makeGetPropertyResponse(BaseMessage message)
    {
        GetPropertyRequest req = (GetPropertyRequest) message;
        GetPropertyResponse res = new GetPropertyResponse();
        if (req.isRange()) {
            for (int i = req.getLeft(); i <= req.getRight(); i++) {
                for (Property prop : regs.getProperties(controller, i)) {
                    res.addProperty(prop);
                }
            }
        }
        else {
            for (Integer uId : req.getUIds()) {
                for (Integer iprop : req.getProps(uId)) {
                    res.addProperty(regs.getProperty(controller, uId, iprop));
                }
            }
        }
        res.setReqIter(req.getReqIter());
        mainslave.sendMessage(controller, res);
    }

    private void makeSetPropertyResponse(BaseMessage message)
    {
        SetPropertyRequest req=(SetPropertyRequest)message;
        SetPropertyResponse res=new SetPropertyResponse();
        for(Integer uId:req.getUIds()){
            for(Property prop:req.getProperties(uId)){
                regs.setProperty(controller, prop);
            }
        }
        res.setReqIter(req.getReqIter());
        mainslave.sendMessage(controller, res);
    }

}
