/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tibbo.linkserver.plugin.device.dnp3;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.FunctionDefinition;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import static com.tibbo.aggregate.common.datatable.FieldFormat.create;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.device.DeviceException;
import com.tibbo.aggregate.common.device.DisconnectionException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import ruraomsk.list.ru.cthulhu.OneReg;
import ruraomsk.list.ru.cthulhu.pocket.MasterDC;
import ruraomsk.list.ru.cthulhu.pocket.MasterFS;
import ruraomsk.list.ru.strongsql.SetValue;

/**
 *
 * @author Yury Rusinov <ruraomsl@list.ru at Automatics E>
 */
public class DriverHelper {

    /**
     * Определение функций устройства - status - сообщает о состоянии всех
     * каналов - devices - сообщает о состоянии всех устройств - variables -
     * сообщает информацию о переменных по указанному каналу
     *
     * @param aThis
     * @return
     * @throws ContextException
     * @throws DeviceException
     * @throws DisconnectionException
     */
    static List<FunctionDefinition> makerFunctionDefinitions() throws ContextException, DeviceException, DisconnectionException {
        TableFormat devices;
        TableFormat variables;
        TableFormat canel;
        TableFormat sync;
        TableFormat history;
        TableFormat saveVLRs;

        List<FunctionDefinition> result = new LinkedList<>();
        FieldFormat iff = FieldFormat.create("devices", FieldFormat.BOOLEAN_FIELD, "Состояние всех устройств");
        TableFormat inputFormat = new TableFormat(1, 1, iff);
        devices = new TableFormat(true);
        devices.addField(FieldFormat.create("<controller><I><D=Номер канала>"));
        devices.addField(FieldFormat.create("<IPaddr><S><D=IP address контроллера>"));
        devices.addField(FieldFormat.create("<port><I><A=502><D=Номер порта>"));
        devices.addField(FieldFormat.create("<CommPort><S><D=Serial port контроллера>"));
        devices.addField(FieldFormat.create("<slip><B><A=false><D=Работа по Slip>"));
        devices.addField(FieldFormat.create("<master><B><A=false><D=Признак мастера>"));
        devices.addField(FieldFormat.create("<connect><B><A=false><D=Есть соединение>"));

        FunctionDefinition fd = new FunctionDefinition("devices", inputFormat, devices, "Состояние всех устройств", ContextUtils.GROUP_DEFAULT);
        result.add(fd);

        iff = FieldFormat.create("variables", FieldFormat.INTEGER_FIELD, "Состояние переменных на канале");
        inputFormat = new TableFormat(1, 1, iff);
        variables = new TableFormat(true);
        variables.addField(FieldFormat.create("<name><S><D=Имя переменной>"));
        variables.addField(FieldFormat.create("<value><S><D=Значение>"));
        variables.addField(FieldFormat.create("<good><I><D=Качество>"));
        variables.addField(FieldFormat.create("<time><D><D=Время обновления>"));
        variables.addField(FieldFormat.create("<lastgood><D><D=Последнее время хорошего>"));
        variables.addField(create("<send><B><A=false><D=Send>"));
        variables.addField(create("<arch><B><A=false><D=Arch>"));
        variables.addField(create("<eprom><B><A=false><D=Eprom>"));
        variables.addField(create("<constant><B><A=false><D=ПЗУ>"));

        variables.addField(FieldFormat.create("<description><S><D=Описание>"));
        fd = new FunctionDefinition("variables", inputFormat, variables, "Состояние переменных на канале", ContextUtils.GROUP_DEFAULT);
        result.add(fd);

        iff = FieldFormat.create("canels", FieldFormat.BOOLEAN_FIELD, "Состояние каналов");
        inputFormat = new TableFormat(1, 1, iff);
        canel = new TableFormat(true);
        canel.addField(FieldFormat.create("<canel><I><D=Канал>"));
        canel.addField(FieldFormat.create("<name><S><D=Параметр>"));
        canel.addField(FieldFormat.create("<value><S><D=Значение>"));
        fd = new FunctionDefinition("canels", inputFormat, canel, "Состояние каналов", ContextUtils.GROUP_DEFAULT);
        result.add(fd);

        iff = FieldFormat.create("syncSetup", FieldFormat.BOOLEAN_FIELD, "Установить синхронизацию");
        inputFormat = new TableFormat(1, 1, iff);
        sync = new TableFormat(1, 1);
        sync.addField(FieldFormat.create("<sync><S><D=Результат>"));
        fd = new FunctionDefinition("syncSetup", inputFormat, sync, "Установить синхронизацию", ContextUtils.GROUP_DEFAULT);
        result.add(fd);

        inputFormat = new TableFormat(1, 1);
        inputFormat.addField(FieldFormat.create("<name><S><D=Имя переменной>"));
        inputFormat.addField(FieldFormat.create("<from><D><D=Начало периода>"));
        inputFormat.addField(FieldFormat.create("<to><D><D=Конец периода>"));
        history = new TableFormat(true);
        history.addField(FieldFormat.create("<series><S><D=Имя серии>"));
        history.addField(FieldFormat.create("<x><D><D=Время>"));
        history.addField(FieldFormat.create("<y><F><D=Значение>"));
        fd = new FunctionDefinition("history", inputFormat, history, "История переменной", ContextUtils.GROUP_DEFAULT);
        result.add(fd);


        iff = FieldFormat.create("name", FieldFormat.STRING_FIELD, "Имя переменной");
        inputFormat = new TableFormat(1, 1, iff);
        canel = new TableFormat(true);
        canel.addField(FieldFormat.create("<value><T><D=Значение>"));
        fd = new FunctionDefinition("getVariable", inputFormat, canel, "Значение перемеенной", ContextUtils.GROUP_DEFAULT);
        result.add(fd);

        return result;
    }

    static DataTable executeFunction(DNP3DeviceDriver aThis, FunctionDefinition fd, CallerController caller, DataTable parameters) {
        if (fd.getName().equalsIgnoreCase("getVariable")) {
            if (parameters.rec().getString("name").length() < 1) {
                return null;
            }
            return doGetVariable(aThis, fd, caller, parameters);
        }
        if (fd.getName().equalsIgnoreCase("devices")) {
            if (!parameters.rec().getBoolean("devices")) {
                return null;
            }
            return aThis.mainmaster.toDataTable(fd.getOutputFormat());
        }
        if (fd.getName().equalsIgnoreCase("syncSetup")) {
            if (!parameters.rec().getBoolean("syncSetup")) {
                return null;
            }
            return doSyncSetup(aThis, fd, caller);
        }
        if (fd.getName().equalsIgnoreCase("canels")) {
            if (!parameters.rec().getBoolean("canels")) {
                return null;
            }
            return doCanels(aThis, fd, caller);
        }
        if (fd.getName().equalsIgnoreCase("variables")) {
            if (parameters.rec().getInt("variables") < 1) {
                return null;
            }
            return doVariables(aThis, fd, caller, parameters);
        }
        if (fd.getName().equalsIgnoreCase("history")) {
            return doHistory(aThis, fd, caller, parameters);
        }
        return null;
    }

    private static DataTable doSyncSetup(DNP3DeviceDriver aThis, FunctionDefinition fd, CallerController caller) {
        try {
            DataTable result = new DataTable(fd.getOutputFormat());
            DataTable registers = aThis.getDeviceContext().getVariable("registers", caller);
            DataTable syncOptions = aThis.getDeviceContext().getVariable("settingSyncOptions", caller);
            DataRecord sync;
            Integer count = 0;
            for (DataRecord regs : registers) {
                if ((sync = seekSync(syncOptions, regs.getString("name"))) == null) {
                    continue;
                }
                if (regs.getBoolean("constant")) {
                    count++;
                    sync.setValue("mode", 1);
                }
                if (regs.getBoolean("eprom")|(regs.getBoolean("send"))) {
                    count++;
                    sync.setValue("mode", 0);
                    sync.setValue("syncPeriod", 600000L);
                }
            }
            aThis.getDeviceContext().setVariable("settingSyncOptions", caller, syncOptions);
            result.addRecord().addString("Изменено " + count.toString());
            return result;
        } catch (ContextException ex) {
            return null;
        }
    }

    private static DataRecord seekSync(DataTable syncOptions, String name) {
        for (DataRecord rec : syncOptions) {
            if (rec.getString("name").equals(name)) {
                return rec;
            }
        }
        return null;
    }

    private static DataTable doCanels(DNP3DeviceDriver aThis, FunctionDefinition fd, CallerController caller) {
        DataTable result = new DataTable(fd.getOutputFormat());
        for (MasterDC dc : aThis.dcmaster) {
            dc.toDataTable(result);
        }
        for (MasterFS fs : aThis.fsmaster) {
            fs.toDataTable(result);
        }
        result.addRecord().addInt(0).addString("lastfunction").addString(aThis.lastfunction);
        return result;
    }

    private static DataTable doHistory(DNP3DeviceDriver aThis, FunctionDefinition fd, CallerController caller, DataTable parameters) {
        String svar = parameters.rec().getString("name");
        Timestamp dfrom = new Timestamp(parameters.rec().getDate("from").getTime());
        Timestamp dto = new Timestamp(parameters.rec().getDate("to").getTime());
        aThis.lastfunction = svar + " " + dfrom.toString() + ":" + dto.toString();
        OneReg oreg = aThis.masterregisters.getOneReg(svar);
        int canel = aThis.masterregisters.getContoller(svar);
        int key = (canel << 16) | (oreg.getuId());
        ArrayList<SetValue> asv = aThis.sqlseek.seekData(dfrom, dto, key);
        DataTable result = new DataTable(fd.getOutputFormat());
        for (SetValue sv : asv) {
            if (sv.getTime() == 0L) {
                continue;
            }
            DataRecord rec = result.addRecord();
            rec.setValue("series", svar);
            rec.setValue("x", new Timestamp(sv.getTime()));
            rec.setValue("y", sv.getFloatValue());
        }
        return result;
    }

    private static DataTable doVariables(DNP3DeviceDriver aThis, FunctionDefinition fd, CallerController caller, DataTable parameters) {
        int controller = parameters.rec().getInt("variables");
        for (MasterDC dc : aThis.dcmaster) {
            if (dc.getController() == controller) {
                DataTable result = new DataTable(fd.getOutputFormat());
                for (OneReg oreg : aThis.masterregisters.getOneRegs(controller)) {
                    DataRecord res = result.addRecord();
                    String temp = aThis.masterregisters.getDescription(controller, oreg.getuId());
                    String name = temp.substring(0, temp.indexOf(":"));
                    String descr = temp.substring(temp.indexOf(":") + 1);
                    res.setValue("name", name);
                    res.setValue("description", descr);
                    res.setValue("good", oreg.getGood());
                    res.setValue("value", oreg.getValueToString());
                    res.setValue("time", oreg.getDate());
                    res.setValue("send", oreg.getReg().isSending());
                    res.setValue("arch", oreg.getReg().isArchived());
                    res.setValue("eprom", oreg.getReg().isEprom());
                    res.setValue("constant", oreg.getReg().isConstant());

                    Long lastgood = aThis.masterregisters.getLastGoodTime(controller, oreg.getuId());
                    if (lastgood == null) {
                        lastgood = 0L;
                    }
                    res.setValue("lastgood", new Date(lastgood));
                }
                return result;
            }
        }
        return null;
    }

    private static DataTable doGetVariable(DNP3DeviceDriver aThis, FunctionDefinition fd, CallerController caller, DataTable parameters) {
        String name = parameters.rec().getString("name");
        OneReg oreg = aThis.masterregisters.getOneReg(name);
        if (oreg == null) {
            return null;
        }
        String description = aThis.masterregisters.getDescription(name);
        TableFormat tf = new TableFormat(1, 1, FieldFormat.create(name, VlrHelper.setCharType(oreg.getReg().getType()), description));
        DataTable result = new DataTable(tf);
        result.addRecord(oreg.getValue());
        DataTable rez=new DataTable(fd.getOutputFormat());
        rez.addRecord().setValue("value", result);
        return rez;
    }

}
