/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Управляет всеми мастерами устройств
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class MainMaster extends Thread {

    private ConcurrentHashMap<Integer, ArrayList<MasterDevice>> mapMain; // ключ номер контроллера
    private Registers regs;
    private long timestep;
    private Integer countDebugTransport = 0;

    public MainMaster(Registers regs, long timestep) {
        this.regs = regs;
        this.timestep = timestep;
        mapMain = new ConcurrentHashMap<>();
        start();
    }

    /**
     * Останавливает все устройства
     */
    public void stopAll() {
        this.interrupt();
        //            this.join(timestep*2);
        for (Integer controller : mapMain.keySet()) {
            ArrayList<MasterDevice> armd = mapMain.get(controller);
            if (armd == null) {
                continue;
            }
            for (MasterDevice md : armd) {
                md.stopDevice();
            }

        }
        Log.CORE.info("MainMaster остановлен...");

    }

    /**
     * добавляет обработчик ответа от устройства
     *
     * @param controller - номер канала контроллера
     * @param Id - идентификатор ответа
     * @param rout - экземпляр класса обработчикф
     * @return - true усли успешно
     */
    public boolean addRouter(int controller, int Id, BaseRout rout) {
        ArrayList<MasterDevice> armd = mapMain.get(controller);
        if (armd == null) {
            return false;
        }
        for (MasterDevice md : armd) {
            md.appendRout(Id, rout);
        }
        return true;
    }

    /**
     * отправляет сообщение
     *
     * @param message
     * @return истина если успешно
     */
    public boolean sendMessage(int controller, BaseMessage message) {
        message.setupWorld(controller, regs);
        ArrayList<MasterDevice> armd = mapMain.get(controller);
        if (armd == null) {
            return false;
        }
        for (MasterDevice md : armd) {
            md.sendMessage(message);
        }
        return true;
    }

    /**
     * Добавляет канал в устройства
     *
     * @param controller - номер канала
     * @param IP - адресс устройства
     * @param port - порт
     * @return - true если успешно
     */
    public boolean addController(Integer controller, String IP, Integer port) {
        ArrayList<MasterDevice> armd;
        Transport tr = new Transport(IP, port, controller, regs, true);
        if (!tr.connect()) {
            Log.CORE.info("Не запустился транспорт " + IP + ":" + Integer.toString(port));
            return false;
        }
        MasterDevice md = new MasterDevice(tr, timestep);
        md.startDevice();
        if (!mapMain.containsKey(controller)) {
            armd = new ArrayList<>();
        } else {
            armd = mapMain.get(controller);
        }
        armd.add(md);
        mapMain.put(controller, armd);
        return true;
    }

    public boolean addController(Integer controller) {
        ArrayList<MasterDevice> armd;
        countDebugTransport++;
        String nameTr = "debug" + countDebugTransport.toString();
        DebugTransport tr = new DebugTransport(nameTr, controller, regs);
        MasterDevice md = new MasterDevice(tr, timestep);
        md.startDevice();
        if (!mapMain.containsKey(controller)) {
            armd = new ArrayList<>();
        } else {
            armd = mapMain.get(controller);
        }
        armd.add(md);
        mapMain.put(controller, armd);
        return true;
    }

    /**
     * Добавляет канал в устройства
     *
     * @param controller - номер канала
     * @param IP - адресс устройства
     * @param port - порт
     * @return - true если успешно
     */
    public boolean addController(Integer controller, String IP, Integer port, boolean slip) {
        ArrayList<MasterDevice> armd;
        Transport tr = new Transport(IP, port, controller, regs, true, slip);
        if (!tr.connect()) {
            Log.CORE.info("Не запустился транспорт " + IP + ":" + Integer.toString(port) + " slip");
            return false;
        }
        MasterDevice md = new MasterDevice(tr, timestep);
        md.startDevice();
        if (!mapMain.containsKey(controller)) {
            armd = new ArrayList<>();
        } else {
            armd = mapMain.get(controller);
        }
        armd.add(md);
        mapMain.put(controller, armd);
        return true;
    }

    /**
     * Добавляет канал в устройства
     *
     * @param controller - номер канала
     * @param commPort - адресс устройства
     * @param port - порт
     * @return - true если успешно
     */
    public boolean addController(int controller, String commPort) {
        ArrayList<MasterDevice> armd;
        ComTransport tr = new ComTransport(commPort, controller, regs, true);
        MasterDevice md = new MasterDevice(tr, timestep);
        md.startDevice();
        if (!mapMain.containsKey(controller)) {
            armd = new ArrayList<>();
        } else {
            armd = mapMain.get(controller);
        }
        armd.add(md);
        mapMain.put(controller, armd);
        return true;
    }

    public String toString() {
        String result = "main master device \n";
        for (Integer controller : mapMain.keySet()) {
            result += "on controller=" + controller.toString() + "\n";
            ArrayList<MasterDevice> armd = mapMain.get(controller);
            if (armd == null) {
                result += "empty ";
            }
            for (MasterDevice md : armd) {
                result += md.toString() + "\n";
            }
        }
        return result;
    }

    public long getTimeStep() {
        return timestep;
    }

    public DataTable toDataTable(TableFormat VFT) {

        DataTable dt = new DataTable(VFT);
        for (Integer controller : mapMain.keySet()) {
            ArrayList<MasterDevice> armd = mapMain.get(controller);
            if (armd == null) {
                continue;
            }
            for (MasterDevice md : armd) {
                DataRecord dr = dt.addRecord();
                dr.setValue("controller", controller);
                if (!md.getTransport().isSerial()) {
                    if (md.getTransport().getNameTransport().indexOf("debug") > 0) {
                        dr.setValue("IPaddr", md.getTransport().getNameTransport());
                        dr.setValue("port", 0);

                    } else {
                        if (md.getTransport().getSocket() != null) {
                            dr.setValue("IPaddr", md.getTransport().getSocket().getInetAddress().getHostAddress());
                            dr.setValue("port", md.getTransport().getSocket().getPort());
                        }
                    }
                } else {
                    dr.setValue("CommPort", md.getTransport().getCommPort());
                }
                dr.setValue("slip", md.getTransport().isSlip());
                dr.setValue("connect", md.getTransport().isConnected());
                dr.setValue("master", md.isMasterCanal());
            }
        }
        return dt;
    }

    /**
     * Возвращает главную таблицу
     *
     * @return
     */
    public Registers getRegisters() {
        return regs;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Thread.sleep(timestep * 10L);
            } catch (InterruptedException ex) {
                break;
            }
            for (Integer cnt : mapMain.keySet()) {
                ArrayList<MasterDevice> armd = mapMain.get(cnt);
                if (armd == null) {
                    continue;
                }
                boolean flag = true;
                for (MasterDevice md : armd) {
                    BaseTransport tr = md.getTransport();
                    //Назначаем мастер и резервный канал
                    if (tr.isConnected() && flag) {
                        md.setMainCanal();
                        flag = false;
                    } else {
                        md.setReservCanal();
                    }
                    if (!tr.isConnected()) {
                        if (!tr.connect()) {
                            tr.close();
                            continue;
                        }
                        Log.CORE.info("Поднимаем " + md.toString());
                        md.setTransport(tr);
                        md.startDevice();
                        //Назначаем мастер и резервный канал
                        if (tr.isConnected() && flag) {
                            md.setMainCanal();
                            flag = false;
                        } else {
                            md.setReservCanal();
                        }
                    }
                }
//                mapMain.put(cnt, armd);
            }
        }
    }

}
