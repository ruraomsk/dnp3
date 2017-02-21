/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.pocket;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.datatable.DataTable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import ruraomsk.list.ru.cthulhu.*;
import ruraomsk.list.ru.cthulhu.message.*;
import ruraomsk.list.ru.cthulhu.message.GetResourcesResponse.Resource;
import ruraomsk.list.ru.cthulhu.protocols.Edata;
import ruraomsk.list.ru.cthulhu.protocols.InterPoint;

/**
 * Представитель файловой системы ВЛР
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class MasterFS extends BaseRout {

    ConcurrentHashMap<Integer, LoadFileStatus> loadfiles;
    ConcurrentHashMap<Integer, SaveFileStatus> savefiles;
    Long lastkeepalive=0L;
    Long timeVLR=0L;
    MainMaster mainmaster;
    Integer controller;
    Long bufferreadstep;
    Integer stage;
    Integer idDevice;
    boolean keepAliveState=false;
    
    GetResourcesResponse resourceResponse=null;

    public MasterFS(MainMaster mainmaster, int controller, long bufferreadstep) {
        this.mainmaster = mainmaster;
        this.controller = controller;
        this.bufferreadstep = bufferreadstep;
        idDevice=controller;
        loadfiles = new ConcurrentHashMap<>();
        savefiles = new ConcurrentHashMap<>();
        mainmaster.addRouter(controller, Util.GetFileSize, this);
        mainmaster.addRouter(controller, Util.ReadFile, this);
        mainmaster.addRouter(controller, Util.WriteFile, this);
        mainmaster.addRouter(controller, Util.CommitFile, this);
        mainmaster.addRouter(controller, Util.GetDataExchangeSettings, this);
        mainmaster.addRouter(controller, Util.GetResources, this);
        mainmaster.addRouter(controller, Util.KeepAlive, this);
        mainmaster.addRouter(controller, Util.SetDateTime, this);
        mainmaster.addRouter(controller, Util.GetDateTime, this);
        
//        mainmaster.sendMessage(controller, new KeepAliveRequest());
        
        start();
    }

    public void readResources() {
        mainmaster.sendMessage(controller, new GetResourcesRequest());
    }

    public  ArrayList<Resource>getResources(){
        return resourceResponse.getResources();
    }
    public void setIdDevice(Integer newID){
        idDevice=newID;
    }
    public Integer getIdDevice(){
        return idDevice;
    }
    public void startKeepAlive(){
        keepAliveState=true;
    }
    public void stopKeepAlive(){
        keepAliveState=false;
    }
    
    public void setDateTime(GregorianCalendar datetime) {
        mainmaster.sendMessage(controller, new SetDateTimeRequest(datetime));
    }
    public void stopAll(){
        this.interrupt();
            Log.CORE.info("MasterFX canel "+controller.toString()+" остановлен...");
    }
    public void startDataExch() {
        GetDataExchangeSettingsRequest req = new GetDataExchangeSettingsRequest();
        req.addEdata(new Edata(0, 0, 1));
        req.addEdata(new Edata(0, 0, 2));
        req.addEdata(new Edata(0, 0, 3));
        req.addEdata(new Edata(0, 0, 11));
        req.addEdata(new Edata(0, 0, 12));
        req.addEdata(new Edata(0, 0, 13));
        req.addEdata(new Edata(0, 0, 14));
        req.addEdata(new Edata(0, 0, 21));
        req.addEdata(new Edata(0, 0, 22));
        req.addEdata(new Edata(0, 0, 23));
        req.addEdata(new Edata(0, 0, 24));
        req.addEdata(new Edata(0, 0, 25));
        req.addEdata(new Edata(0, 0, 26));
        mainmaster.sendMessage(controller, req);
        stage = 1;
    }

    public void saveFile(Integer number, byte[] file) {
        savefiles.put(number, new SaveFileStatus(number, file));
    }

    public void loadFile(Integer number) {
        loadfiles.put(number, new LoadFileStatus(number));
    }
    public LoadFileStatus getLoadFileStatus(Integer number){
        return loadfiles.get(number);
    }

    public Integer getStatus(Integer number) {
        LoadFileStatus lfs = loadfiles.get(number);
        if (lfs == null) {
            return -2;
        }
        return lfs.getStatus();
    }

    public byte[] getFile(Integer number) {
        LoadFileStatus lfs = loadfiles.get(number);
        if (lfs == null) {
            return null;
        }
        return lfs.getFile();
    }


    public MainMaster getMainMaster() {
        return mainmaster;
    }

    public Integer getController() {
        return controller;
    }
    public Long getLastKeepAlive(){
        return lastkeepalive;
    }
    public Long getDateTimeVLR(){
        return timeVLR;
    }
    public void requestDateTime(){
        mainmaster.sendMessage(controller, new GetDateTimeRequest());
    }
    

    @Override
    public void addMessage(BaseMessage message) {
//        if (message.getId() != Util.KeepAlive) {
//            System.out.println(message.toString());
//        }
        switch (message.getId()) {
            case Util.GetFileSize:
                getfilesize((GetFileSizeResponse) message);
                break;
            case Util.ReadFile:
                readfile((ReadFileResponse) message);
                break;
            case Util.GetDataExchangeSettings:
                getExchangeSetting((GetDataExchangeSettingsResponse) message);
                break;
            case Util.GetResources:
                getResource((GetResourcesResponse) message);
                break;
            case Util.KeepAlive:
                lastkeepalive=System.currentTimeMillis();
                break;
            case Util.GetDateTime:
                getDateTimeVLR((GetDateTimeResponse) message);
                break;
        }
    }

    @Override
    public String toString() {
        String result = "MasterFS controller=" + controller.toString() + " step=" + bufferreadstep.toString() + "\n";
        result += "Files to loading\n";
        for (LoadFileStatus fs : loadfiles.values()) {
            result += fs.toString() + "\n";
        }
        result += "Files for save\n";
        for (SaveFileStatus fs : savefiles.values()) {
            result += fs.toString() + "\n";
        }
        if(resourceResponse!=null){
            result+=resourceResponse.getResources().toString();
        }
        return result;
    }

    @Override
    public void run() {
        int countKeepAlive=0;
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(bufferreadstep);
            }
            catch (InterruptedException ex) {
                return;
            }

                if(keepAliveState){
                    if(countKeepAlive++>10) {
                        mainmaster.sendMessage(controller, new KeepAliveRequest());
                        countKeepAlive=0;
                    }
                }
//            mainmaster.sendMessage(controller, new GetDateTimeRequest());

            for (LoadFileStatus fs : loadfiles.values()) {
                switch (fs.getStatus()) {
                    case 0:
                        GetFileSizeRequest req;
                        if (fs.getNumber() < 100 && fs.getNumber() > 0) {
                            req = new GetFileSizeRequest(fs.getNumber());
                        }
                        else {
                            req = new GetFileSizeRequest(fs.getNumber(),Util.makeNameFile(fs.getNumber()));
//                            req = new GetFileSizeRequest("data" + Integer.toString(fs.getNumber()));
                        }
                        mainmaster.sendMessage(controller, req);
                        fs.setStatus(1);
                        break;
                    case 3:
                        if (fs.getNumber() == 10 || fs.getNumber() == 110) {
                            try {
                                //Это zip файл нужно вытащить имена переменных
                                File tempFile = File.createTempFile("rura", ".zip");
                                FileOutputStream fos = new FileOutputStream(tempFile);
                                fos.write(fs.getFile());
                                fos.close();
//                                System.out.println("Файл номер=" + fs.getNumber().toString() + "=>" + tempFile);
                                ZipInputStream zipfile = new ZipInputStream(new FileInputStream(tempFile));
                                ZipEntry zipentry;
                                while ((zipentry = zipfile.getNextEntry()) != null) {
                                    byte[] buffer = new byte[(int) (zipentry.getSize() & 0xffff)];
                                    int pos = 0;
                                    while (pos < buffer.length) {
                                        int len = buffer.length - pos;
                                        len = Math.min(1000, len);
                                        len = zipfile.read(buffer, pos, len);
                                        pos += len;
                                    }
                                    if (zipentry.getName().equalsIgnoreCase("params.inf")) {
                                        fs.setDescription(buffer);
                                    }
                                    if (zipentry.getName().equalsIgnoreCase("const.txt")) {
                                        fs.setConstats(buffer);
//                                        System.out.println(fs.getConstats().toString());
                                    }
                                }
                                zipfile.close();
                                tempFile.delete();
                                fs.setStatus(4);
                            }
                            catch (IOException ex) {
                            }
                        }
                        break;
                }
            }
            for (SaveFileStatus fs : savefiles.values()) {
                switch (fs.getStatus()) {
                    case 0:
//                        mainmaster.sendMessage(controller, new GetFileSizeRequest(fs.getNumber()));
//                        fs.setStatus(1);
                        break;
                    case 3:
                        break;
                }
            }

        }
    }

    private void getfilesize(GetFileSizeResponse ressize) {
        LoadFileStatus lfs = loadfiles.get(ressize.getFlNum());
        if (lfs == null) {
            Log.CORE.info("cf=" + controller.toString() + " Ошибочный номер файла " + Integer.toHexString(ressize.getFlNum()));
            return;
        }
        if (ressize.getReqEC() != Util.RequestOk) {
            lfs.setStatus(-1);
            lfs.setRescode(ressize.getReqEC());
            return;
        }
        lfs.setSize(ressize.getFlSize());
        int getsize = Math.min(Util.MAX_GET_FILE_SIZE, lfs.getSize());
        ReadFileRequest getfile = new ReadFileRequest(lfs.getNumber(), 0, getsize);
        mainmaster.sendMessage(controller, getfile);
        lfs.setStatus(2);
    }

    private void readfile(ReadFileResponse resread) {
        LoadFileStatus lfs = loadfiles.get(resread.getFlNum());
        if (lfs == null) {
            Log.CORE.info("cr=" + controller.toString() + " Ошибочный номер файла " + Integer.toHexString(resread.getFlNum()));
            return;
        }
        if (resread.getReqEC() != Util.RequestOk) {
            lfs.setStatus(-1);
            lfs.setRescode(resread.getReqEC());
            return;
        }
        lfs.setFile(resread.getFlData(), resread.getDataOff(), resread.getDataSize());
        if ((resread.getDataOff() + resread.getDataSize()) < lfs.getSize()) {
            int pos = resread.getDataOff() + resread.getDataSize();
            int getsize = Math.min(Util.MAX_GET_FILE_SIZE, lfs.getSize() - pos);
            mainmaster.sendMessage(controller, new ReadFileRequest(lfs.getNumber(), pos, getsize));
        }
        else {
            lfs.setStatus(3);
        }

    }

    private void getExchangeSetting(GetDataExchangeSettingsResponse response) {

        for (InterPoint ipt : response.getProtocols()) {
            System.out.println(ipt.toString());
        }
        if (stage == 1) {
            GetDataExchangeSettingsRequest req = new GetDataExchangeSettingsRequest();
            req.addEdata(new Edata(1, 0, 11));
            req.addEdata(new Edata(1, 0, 12));
            req.addEdata(new Edata(1, 0, 13));
            mainmaster.sendMessage(controller, req);
            stage = 2;
            return;
        }
        if (stage == 2) {
            GetDataExchangeSettingsRequest req = new GetDataExchangeSettingsRequest();
            makeEdate(req, 1, 1);
            makeEdate(req, 2, 2);
            makeEdate(req, 2, 3);
            mainmaster.sendMessage(controller, req);
            stage = 3;
            return;

        }
    }
    private void makeEdate(GetDataExchangeSettingsRequest req,int i, int j){
            for (int k = 21; k <= 26; k++) {
                        req.addEdata(new Edata(i, j, k));
            }
    }
    private void getResource(GetResourcesResponse getResourcesResponse) {
        resourceResponse=getResourcesResponse;
//        System.out.println(getResourcesResponse.toString());
    }

    private void getDateTimeVLR(GetDateTimeResponse message) {
        timeVLR=message.getTime();
    }
    public void toDataTable(DataTable table) {

        table.addRecord(controller,"lastKA",new Date(lastkeepalive).toString());
        table.addRecord(controller,"VLRtime",new Date(timeVLR).toString());
        table.addRecord(controller,"KAstart",keepAliveState?"Ok":"Not");
    }

}
