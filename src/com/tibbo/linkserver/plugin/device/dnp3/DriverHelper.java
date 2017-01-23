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
import static com.tibbo.linkserver.plugin.device.dnp3.VlrHelper.VFT_REGISTERS;
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

        return result;
    }

    static DataTable executeFunction(DNP3DeviceDriver aThis, FunctionDefinition fd, CallerController caller, DataTable parameters) {
        if (fd.getName().equalsIgnoreCase("devices")) {
            if (!parameters.rec().getBoolean("devices")) {
                return null;
            }
            return aThis.mainmaster.toDataTable(fd.getOutputFormat());
        };
        if (fd.getName().equalsIgnoreCase("canels")) {
            if (!parameters.rec().getBoolean("canels")) {
                return null;
            }
            DataTable result=new DataTable(fd.getOutputFormat());
            for (MasterDC dc : aThis.dcmaster) {
                dc.toDataTable(result);
            }
            for (MasterFS fs : aThis.fsmaster) {
                fs.toDataTable(result);
            }
            return result;
        };
        if (fd.getName().equalsIgnoreCase("variables")) {
            if (parameters.rec().getInt("variables") < 1) {
                return null;
            }
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
                        res.setValue("value", oreg.getValue().toString());
                        res.setValue("time", oreg.getDate());
                        res.setValue("send",oreg.getReg().isSending());
                        res.setValue("arch",oreg.getReg().isArchived());
                        res.setValue("eprom",oreg.getReg().isEprom());
                        res.setValue("constant",oreg.getReg().isConstant());
                        
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
        };
        return null;
    }

}
