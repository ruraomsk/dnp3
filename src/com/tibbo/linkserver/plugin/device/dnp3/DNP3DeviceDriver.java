package com.tibbo.linkserver.plugin.device.dnp3;

/*
 * Собственно драйвер устройств ВЛР для Aggregate
 */
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.FunctionDefinition;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.device.AbstractDeviceDriver;
import com.tibbo.aggregate.common.device.DeviceContext;
import com.tibbo.aggregate.common.device.DeviceEntities;
import com.tibbo.aggregate.common.device.DeviceException;
import com.tibbo.aggregate.common.device.DisconnectionException;
import com.tibbo.aggregate.common.security.ServerPermissionChecker;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import ruraomsk.list.ru.cthulhu.MainMaster;
import ruraomsk.list.ru.cthulhu.OneReg;
import ruraomsk.list.ru.cthulhu.Registers;
import ruraomsk.list.ru.cthulhu.pocket.MasterDC;
import ruraomsk.list.ru.cthulhu.pocket.MasterFS;
import ruraomsk.list.ru.strongsql.DescrValue;
import ruraomsk.list.ru.strongsql.ParamSQL;
import ruraomsk.list.ru.strongsql.SetValue;
import ruraomsk.list.ru.strongsql.StrongSql;
import ruraomsk.list.ru.vlrmanager.VLRDataTableManager;
import ruraomsk.list.ru.vlrmanager.VLRXMLManager;

/**
 *
 * @author Yury Rusinov <ruraomsl@list.ru at Automatics E>
 */
public class DNP3DeviceDriver extends AbstractDeviceDriver {

    Registers masterregisters = null;
    ParamSQL param = null;
    StrongSql sSql = null;
    VLRXMLManager vlrManager = null;
    MainMaster mainmaster = null;
    List<MasterFS> fsmaster;
    List<MasterDC> dcmaster;
    Long timestep;
    Long stepSQL;
    StrongSql sqldata;
    int needReadAllValues = 0;
    int needUpToData = 0;

    public DNP3DeviceDriver() {
        super("vlrbus", VlrHelper.VFT_SQL_PROP);
    }

    @Override
    public void setupDeviceContext(DeviceContext deviceContext) throws ContextException {
        super.setupDeviceContext(deviceContext); //To change body of generated methods, choose Tools | Templates.
        deviceContext.setDefaultSynchronizationPeriod(10000L);
        VariableDefinition vd = new VariableDefinition("connectionProperties", VlrHelper.VFT_SQL_PROP, true, true, "Основные параметры", ContextUtils.GROUP_ACCESS);
        vd.setIconId("var_connection");
        vd.setHelpId("ls_drivers_vlr");
        vd.setWritePermissions(ServerPermissionChecker.getManagerPermissions());
        deviceContext.addVariableDefinition(vd);

        vd = new VariableDefinition("canels", VlrHelper.VFT_CNL_PROP, true, true, "Каналы", ContextUtils.GROUP_ACCESS);
        vd.setWritePermissions(ServerPermissionChecker.getAdminPermissions());
        deviceContext.addVariableDefinition(vd);

        vd = new VariableDefinition("devices", VlrHelper.VFT_VLR_PROP, true, true, "Устройства", ContextUtils.GROUP_ACCESS);
        vd.setWritePermissions(ServerPermissionChecker.getAdminPermissions());
        deviceContext.addVariableDefinition(vd);

        vd = new VariableDefinition("registers", VlrHelper.VFT_REGISTERS, true, true, "Переменные", ContextUtils.GROUP_ACCESS);
        vd.setWritePermissions(ServerPermissionChecker.getAdminPermissions());
        deviceContext.addVariableDefinition(vd);

        makeRegisters(deviceContext);
        deviceContext.setDeviceType("vlrbus");
    }

    /**
     *
     * @param name
     */
    @Override
    public void accessSettingUpdated(String name) {
        if (name.equalsIgnoreCase("canels") || name.equalsIgnoreCase("devices")) {
            try {
                makeRegisters(getDeviceContext());
            } catch (ContextException ex) {
                Log.CORE.error("Ошибка редакции " + name + " " + ex.getMessage());
            }
        }
        super.accessSettingUpdated(name); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<VariableDefinition> readVariableDefinitions(DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException {
        List<VariableDefinition> result = new ArrayList<>();
        for (DataRecord reg : getDeviceContext().getVariable("registers", getDeviceContext().getCallerController())) {
            String name = reg.getString("name");
            TableFormat tf = new TableFormat(1, 1, FieldFormat.create(name, VlrHelper.setCharType(reg.getInt("type")), reg.getString("description")));
            tf.setUnresizable(true);
            result.add(new VariableDefinition(name, tf, true, !reg.getBoolean("readonly"), reg.getString("description"), "remote"));
        }
        return result;
    }

    @Override
    public DataTable readVariableValue(VariableDefinition vd, CallerController caller) throws ContextException, DeviceException, DisconnectionException {
        DataTable result = new DataTable(vd.getFormat());
        OneReg oreg = masterregisters.getOneReg(vd.getName());
        if (oreg == null) {
            result.addRecord(0);
        } else {
            result.addRecord(oreg.getValue());
        }
        return result;
    }

    @Override
    public void writeVariableValue(VariableDefinition vd, CallerController caller, DataTable value, DataTable deviceValue) throws ContextException, DeviceException, DisconnectionException {
        OneReg oreg = masterregisters.getOneReg(vd.getName());
        if (oreg == null) {
            return;
        }
        if (oreg.getReg().isReadOnly()) {
            return;
        }
        Object val = value.get();
        if (oreg.getReg().getType() == 3) {
            val = ((int) (((Long) value.get()) & 0xffffffff));
        };
        if (oreg.getReg().getType() == 4) {
            val = ((byte) (Integer.parseInt((String) value.get()) & 0xff));
        }
        oreg.setValue(val);
        int canel = masterregisters.getContoller(vd.getName());
        for (MasterDC dc : dcmaster) {
            if (dc.getController() == canel) {
                dc.putValue(oreg);
                return;
            }
        }
    }

    @Override
    public void finishSynchronization() throws DeviceException, DisconnectionException {
        ArrayList<SetValue> arrayValues = new ArrayList<>();
        for (MasterDC dc : dcmaster) {
            for (OneReg oreg : masterregisters.getOneRegs(dc.getController())) {
                if (oreg.getGood() != 0) {
                    continue;
                }
                Integer newID = dc.getController() << 16 | oreg.getuId();
                Object value = oreg.getValue();
                arrayValues.add(new SetValue(newID, oreg.getTime(), value, oreg.getGood()));
                for (OneReg horeg : masterregisters.getHistoryOneReg(dc.getController(), oreg.getuId())) {
                    value = horeg.getValue();
                    arrayValues.add(new SetValue(newID, horeg.getTime(), value, oreg.getGood()));

                }
            }
        }
        if (!arrayValues.isEmpty()) {
            sqldata.addValues(new Timestamp(System.currentTimeMillis()), arrayValues);
        }
        if (++needReadAllValues > 9) {
            needReadAllValues = 0;
            for (MasterDC dc : dcmaster) {
                dc.readAllNotSendingValue();
                dc.readAllSendingValue();
//                dc.upRingBuffer();
            }

        }
        super.finishSynchronization(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void startSynchronization() throws DeviceException {
        if (++needUpToData > 4) {
            needUpToData = 0;
//            for (MasterDC dc : dcmaster) {
//            }
            for (MasterFS fs : fsmaster) {
                fs.requestDateTime();
            }
        }
        super.startSynchronization(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<FunctionDefinition> readFunctionDefinitions(DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException {
        return DriverHelper.makerFunctionDefinitions();

    }

    @Override
    public DataTable executeFunction(FunctionDefinition fd, CallerController caller, DataTable parameters) throws ContextException, DeviceException, DisconnectionException {
        return DriverHelper.executeFunction(this, fd, caller, parameters);
    }

    @Override
    public void connect() throws DeviceException {
        try {
            DataTable canels = getDeviceContext().getVariable("canels", getDeviceContext().getCallerController());
            DataTable devices = getDeviceContext().getVariable("devices", getDeviceContext().getCallerController());
            param = VlrHelper.setParamData(getDeviceContext().getVariable("connectionProperties", getDeviceContext().getCallerController()));
            stepSQL = getDeviceContext().getVariable("connectionProperties", getDeviceContext().getCallerController()).rec().getLong("stepSQL");
            timestep = getDeviceContext().getVariable("connectionProperties", getDeviceContext().getCallerController()).rec().getLong("timestep");
            boolean initSQL = getDeviceContext().getVariable("connectionProperties", getDeviceContext().getCallerController()).rec().getBoolean("initSQL");
            long longSQL = getDeviceContext().getVariable("connectionProperties", getDeviceContext().getCallerController()).rec().getLong("longSQL");
            mainmaster = new MainMaster(masterregisters, timestep);
            fsmaster = new ArrayList<>();
            dcmaster = new ArrayList<>();
            for (DataRecord dev : devices) {
                if (dev.getBoolean("slip")) {
                    mainmaster.addController(dev.getInt("canel"), dev.getString("IPAddress"), dev.getInt("port"), true);
                } else {
                    mainmaster.addController(dev.getInt("canel"), dev.getString("IPAddress"), dev.getInt("port"));
                }
            }
            for (DataRecord can : canels) {
                fsmaster.add(new MasterFS(mainmaster, can.getInt("canel"), timestep));
                dcmaster.add(new MasterDC(mainmaster, can.getInt("canel"), timestep));
            }
            for (MasterDC dc : dcmaster) {
                dc.readAllNotSendingValue();
                dc.readAllSendingValue();
            }
            for (MasterFS fs : fsmaster) {
                fs.readResources();
                fs.startKeepAlive();
            }
            if (initSQL) {
                ArrayList<DescrValue> arraydesc = new ArrayList<>();
                DataTable registers = getDeviceContext().getVariable("registers", getDeviceContext().getCallerController());
                Integer count = 0;
                for (DataRecord reg : registers) {
                    String name = reg.getString("name");
                    int key = (reg.getInt("canel") << 16) | (reg.getInt("id"));
                    arraydesc.add(new DescrValue(name, key, reg.getInt("type")));
                    count++;
                }
                new StrongSql(param, arraydesc, 1, longSQL, "VLR DataTable");
            }
            sqldata = new StrongSql(param, stepSQL);
        } catch (ContextException ex) {
            throw new DeviceException("Нет canels или devices");
        }
        super.connect();
    }

    @Override
    public void disconnect() throws DeviceException {
        for (MasterFS fs : fsmaster) {
            fs.stopAll();
        }
        for (MasterDC dc : dcmaster) {
            dc.stopAll();
        }
        mainmaster.stopAll();
        sqldata.disconnect();
        super.disconnect();
    }

    private void makeRegisters(DeviceContext deviceContext) throws ContextException {
        boolean writeflag = false;
        param = VlrHelper.setParamVlr(deviceContext.getVariable("connectionProperties", getDeviceContext().getCallerController()));
        if (vlrManager != null) {
            vlrManager.close();
        }
        vlrManager = new VLRXMLManager(param);
        if (!vlrManager.connected) {
            Log.CORE.error("Отсутствует соединение " + param.toString());
        }
        DataTable canels = deviceContext.getVariable("canels", getDeviceContext().getCallerController());
        masterregisters = new Registers();
        for (DataRecord crec : canels) {
            DataTable BPO, SPO, Const;
            BPO = VLRDataTableManager.fromXML(vlrManager.getXML(crec.getInt("bpo"), 1));
            SPO = VLRDataTableManager.fromXML(vlrManager.getXML(crec.getInt("spo"), 2));
            Const = VLRDataTableManager.fromXML(vlrManager.getXML(crec.getInt("spo"), 3));
            VlrHelper.addRegisters(masterregisters, crec.getInt("canel"), crec.getString("prefix"), BPO, SPO, Const);
            writeflag = true;
        }
        if (writeflag) {
            deviceContext.setVariable("registers", getDeviceContext().getCallerController(), VlrHelper.makeRegisters(masterregisters, canels));
        }
        vlrManager.close();
        vlrManager = null;
    }

}
