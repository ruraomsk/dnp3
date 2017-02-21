/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class MainSlave {

    private ConcurrentHashMap<Integer, SlaveDevice> mapMain; // ключ номер контроллера
    private Registers regs;
    private long timestep;

    public MainSlave(Registers regs, long timestep) {
        this.regs = regs;
        this.timestep = timestep;
        mapMain = new ConcurrentHashMap<>();
    }

    /**
     * Останавливает все устройства
     */
    public void stopAll() {
        for (Integer controller : mapMain.keySet()) {
            SlaveDevice sd = mapMain.get(controller);
            if (sd == null) {
                continue;
            }
            sd.stopDevice();
        }
    }

    /**
     * добавляет обработчик ответа от устройства
     *
     * @param controller - номер канала контроллера
     * @param Id         - идентификатор ответа
     * @param rout       - экземпляр класса обработчикф
     * @return - true усли успешно
     */
    public boolean addRouter(int controller, int Id, BaseRout rout) {
        SlaveDevice sd = mapMain.get(controller);
        if (sd == null) {
            return false;
        }
        sd.appendRout(Id, rout);
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
        SlaveDevice sd = mapMain.get(controller);
        if (sd == null) {
            return false;
        }
        sd.sendMessage(message);
        return true;
    }

    public Registers getRegisters() {
        return regs;
    }

    /**
     * Добавляет канал в устройства
     *
     * @param controller - номер канала
     * @param IP         - адресс устройства
     * @param port       - порт
     * @return - true если успешно
     */
    public void addController(int controller, SlaveDevice sd) {
        mapMain.put(controller, sd);
        sd.startDevice();
    }

    @Override
    public String toString() {
        String result = "main slave device ";
        for (Integer controller : mapMain.keySet()) {
            result += "controller=" + controller.toString() + "\n";
            SlaveDevice sd = mapMain.get(controller);
            if (sd == null) {
                result += "empty ";
            }
            result += sd.toString() + "\n";
        }
        return result;
    }

    public DataTable toDataTable() {
        TableFormat VFT = new TableFormat(true);
        VFT.addField(FieldFormat.create("<controller><I><D=Номер контроллера>"));
        VFT.addField(FieldFormat.create("<IPaddr><S><D=IP address контроллера>"));
        VFT.addField(FieldFormat.create("<port><I><A=502><D=Номер порта>"));
        VFT.addField(FieldFormat.create("<connect><B><A=false><D=Есть соединение>"));

        DataTable dt = new DataTable(VFT);
        for (Integer controller : mapMain.keySet()) {
            SlaveDevice sd = mapMain.get(controller);
            if (sd == null) {
                continue;
            }
            for (Transport tr : sd.transport) {
                DataRecord dr = dt.addRecord();
                dr.setValue("controller", controller);
                dr.setValue("IPaddr", tr.getSocket().getInetAddress().getHostAddress());
                dr.setValue("port", tr.getSocket().getPort());
                dr.setValue("connect", tr.isConnected());
            }
        }
        return dt;
    }

}
