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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import ruraomsk.list.ru.cthulhu.MainMaster;
import ruraomsk.list.ru.cthulhu.OneReg;
import ruraomsk.list.ru.cthulhu.Registers;
import ruraomsk.list.ru.cthulhu.Util;
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
    List<MasterFS> fsmaster = null;
    List<MasterDC> dcmaster = null;
    Long timestep = 500L;
    Long stepSQL = 5000L;
    String lastfunction = "";
    StrongSql sqldata;
    StrongSql sqlseek;
    Long needReadAllValues = System.currentTimeMillis();
    Long needRingBuffer = System.currentTimeMillis();
    Long needUpToData = System.currentTimeMillis();
    Long setDateTime = System.currentTimeMillis();
    Long lastWriteSQL = System.currentTimeMillis();
    int needWriteEeprom = 0;
    boolean readall = true;
    HashMap<String, Integer> mapErrorName = new HashMap<>();

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
        vd = new VariableDefinition("errors", VlrHelper.VFT_ERROR_STATUS, true, true, "Ошибки", ContextUtils.GROUP_ACCESS);
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
        mapErrorName.clear();
        for (DataRecord reg : getDeviceContext().getVariable("registers", getDeviceContext().getCallerController())) {
            if (reg.getBoolean("visible")) {
                String name = reg.getString("name");
                mapErrorName.put(name, reg.getInt("canel"));
                TableFormat tf = new TableFormat(1, 1, FieldFormat.create(name, VlrHelper.setCharType(reg.getInt("type")), reg.getString("description")));
                tf.setUnresizable(true);
                result.add(new VariableDefinition(name, tf, true, false, reg.getString("description"), "remote"));
            }
            if (reg.getBoolean("eprom")) {
                String name = reg.getString("name");
                TableFormat tf = new TableFormat(1, 1, FieldFormat.create(name, VlrHelper.setCharType(reg.getInt("type")), reg.getString("description")));
                tf.setUnresizable(true);
                result.add(new VariableDefinition(name, tf, true, true, reg.getString("description"), "remote"));

            }
        }
        return result;
    }

    @Override
    public DataTable readVariableValue(VariableDefinition vd, CallerController caller) throws ContextException, DeviceException, DisconnectionException {
        DataTable result = new DataTable(vd.getFormat());
        if (mapErrorName.containsKey(vd.getName())) {
            Integer canal = mapErrorName.get(vd.getName());
            for (DataRecord rec : getDeviceContext().getVariable("errors", getDeviceContext().getCallerController())) {
                if (rec.getInt("canel") != canal) {
                    continue;
                }
                result.addRecord(rec.getBoolean("isError"));
                return result;
            }
            result.addRecord(0);
            return result;
        }

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
        if (dcmaster == null) {
            return;
        }
        if (fsmaster == null) {
            return;
        }
        for (MasterDC dc : dcmaster) {
            dc.readAllNoGoodNotSendingValue();
        }
        if ((System.currentTimeMillis() - lastWriteSQL) > stepSQL) {
            needWriteEeprom++;
            if (needWriteEeprom > 10) {
                needWriteEeprom = 0;
            }
            ArrayList<SetValue> arrayValues = new ArrayList<>();
            for (MasterDC dc : dcmaster) {
                long timeall = System.currentTimeMillis();
                for (OneReg oreg : masterregisters.getOneRegs(dc.getController())) {
                    if (oreg.getGood() == Util.CT_DATA_NOGOOD) {
                        continue;
                    }
                    if (oreg.getReg().isConstant()) {
                        continue;
                    }
                    if (oreg.getReg().isEprom() & needWriteEeprom != 0) {
                        continue;
                    };
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
            lastWriteSQL = System.currentTimeMillis();
        }

        if ((System.currentTimeMillis() - needReadAllValues) > 30000L) {
            needReadAllValues = System.currentTimeMillis();
            for (MasterDC dc : dcmaster) {
                dc.readAllSendingValue();
                dc.readAllNotSendingValue();
            }
        }
        super.finishSynchronization(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void startSynchronization() throws DeviceException {
        if (fsmaster == null) {
            return;
        }
        if (dcmaster == null) {
            return;
        }
        for (MasterDC dc : dcmaster) {
            dc.readAllNoGoodSendingValue();
        }
        if ((System.currentTimeMillis() - needUpToData) > stepSQL) {
            needUpToData = System.currentTimeMillis();
            for (MasterDC dc : dcmaster) {
                if (readall) {
                    dc.readAllSendingValue();
//                    dc.readAllNotSendingValue();
                }
                dc.upRingBuffer();
            }
            for (MasterFS fs : fsmaster) {
                fs.requestDateTime();
            }
        }
        if ((System.currentTimeMillis() - setDateTime) > 120000L) {
            for (MasterFS fs : fsmaster) {
                TimeZone zone = new SimpleTimeZone(0, "UTC");
                fs.setDateTime(new GregorianCalendar(zone));
                fs.startKeepAlive();
            }
            setDateTime = System.currentTimeMillis();
        }
        changeStatus();
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
            readall = getDeviceContext().getVariable("connectionProperties", getDeviceContext().getCallerController()).rec().getBoolean("readall");
            boolean initSQL = getDeviceContext().getVariable("connectionProperties", getDeviceContext().getCallerController()).rec().getBoolean("initSQL");
            long longSQL = getDeviceContext().getVariable("connectionProperties", getDeviceContext().getCallerController()).rec().getLong("longSQL");
            mainmaster = new MainMaster(masterregisters, timestep);
            fsmaster = new ArrayList<>();
            dcmaster = new ArrayList<>();
            for (DataRecord dev : devices) {
                if (dev.getString("IPAddress").equals("debug")) {
                    mainmaster.addController(dev.getInt("canel"));
                } else {
                    if (dev.getBoolean("slip")) {
                        mainmaster.addController(dev.getInt("canel"), dev.getString("IPAddress"), dev.getInt("port"), true);
                    } else {
                        mainmaster.addController(dev.getInt("canel"), dev.getString("IPAddress"), dev.getInt("port"));
                    }
                }
            }
            for (DataRecord can : canels) {
                fsmaster.add(new MasterFS(mainmaster, can.getInt("canel"), timestep));
                dcmaster.add(new MasterDC(mainmaster, can.getInt("canel"), timestep));
            }
            for (MasterDC dc : dcmaster) {
                dc.readAllNotSendingValue();
                dc.readAllSendingValue();
                dc.upRingBuffer();
            }
            for (MasterFS fs : fsmaster) {
                fs.readResources();
                TimeZone zone = new SimpleTimeZone(0, "UTC");
                fs.setDateTime(new GregorianCalendar(zone));
                fs.startKeepAlive();
            }
            setDateTime = System.currentTimeMillis();
            if (initSQL) {
                Log.CORE.error("Создаем базу .....");
                ArrayList<DescrValue> arraydesc = new ArrayList<>();
                DataTable registers = getDeviceContext().getVariable("registers", getDeviceContext().getCallerController());
                Integer count = 0;
                for (DataRecord reg : registers) {
                    if (reg.getBoolean("visible")) {
                        continue;
                    }
                    String name = reg.getString("name");
                    int key = (reg.getInt("canel") << 16) | (reg.getInt("id"));
                    if (key == 0) {
                        continue;
                    }
                    arraydesc.add(new DescrValue(name, key, reg.getInt("type")));
                    count++;
                }
                new StrongSql(param, arraydesc, 1, longSQL, new Date().toString());
                Log.CORE.error("Создали базу .....");
                DataTable cp = getDeviceContext().getVariable("connectionProperties", getDeviceContext().getCallerController());
                cp.rec().setValue("initSQL", false);
                getDeviceContext().setVariable("connectionProperties", getDeviceContext().getCallerController(), cp);

            }
            sqldata = new StrongSql(param, stepSQL);
            sqlseek = new StrongSql(param);
        } catch (ContextException ex) {
            throw new DeviceException("Нет canels или devices");
        }
        super.connect();
    }

    @Override
    public void disconnect() throws DeviceException {
        if (fsmaster == null) {
            super.disconnect();
            return;
        }
        for (MasterFS fs : fsmaster) {
            fs.stopAll();
        }
        for (MasterDC dc : dcmaster) {
            dc.stopAll();
        }
        mainmaster.stopAll();
        sqldata.disconnect();
        sqlseek.disconnect();
        vlrManager.close();
        vlrManager = null;
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
        DataTable errorsTable = new DataTable(VlrHelper.VFT_ERROR_STATUS);
        masterregisters = new Registers();
        for (DataRecord crec : canels) {
            DataTable BPO, SPO, Const, Error;
            BPO = VLRDataTableManager.fromXML(vlrManager.getXML(crec.getString("spo"), 1));
            SPO = VLRDataTableManager.fromXML(vlrManager.getXML(crec.getString("ppo"), 2));
            Const = VLRDataTableManager.fromXML(vlrManager.getXML(crec.getString("ppo"), 3));
            VlrHelper.addRegisters(masterregisters, crec.getInt("canel"), crec.getString("prefix"), BPO, SPO, Const);
            Error = VLRDataTableManager.fromXML(vlrManager.getXML(crec.getString("ppo"), 4));
            VlrHelper.addErrors(errorsTable, crec.getInt("canel"), crec.getString("prefix"), Error);
            writeflag = true;
        }
        if (writeflag) {
            deviceContext.setVariable("registers", getDeviceContext().getCallerController(), VlrHelper.makeRegisters(masterregisters, canels));
            deviceContext.setVariable("errors", getDeviceContext().getCallerController(), errorsTable);
        }
    }

    private void changeStatus() {
        try {
            DataTable error = getDeviceContext().getVariable("errors", getDeviceContext().getCallerController());
            for (DataRecord rec : error) {
                rec.setValue("isOldError", rec.getBoolean("isError"));
                boolean isError = false;
                boolean isKvit = false;
                DataTable detail = rec.getDataTable("detailStatus");
                for (DataRecord rs : detail) {
                    OneReg oreg = masterregisters.getOneReg(rs.getString("name"));
                    if (oreg == null) {
                        continue;
                    }
                    boolean value = false;
                    switch (oreg.getReg().getType()) {
                        case 0:
                            value = (boolean) oreg.getValue();
                            break;
                        case 1:
                            value = ((int) oreg.getValue()) != 0;
                            break;
                        case 2:
                            value = ((float) oreg.getValue()) != 0.0f;
                            break;
                        case 3:
                            value = ((long) oreg.getValue()) != 0L;
                            break;
                        case 4:
                            value = ((byte) oreg.getValue()) != 0;
                            break;
                    }
                    if (rs.getBoolean("reverse")) {
                        value = !value;
                    }
                    boolean old = rs.getBoolean("new");
                    boolean kvit = rs.getBoolean("kvit");
                    Date time = rs.getDate("time");
                    if (value && !old) {
                        kvit = true;
                        time = new Date();
                        isKvit = true;
                    }
                    isError = isError | value;
                    rs.setValue("new", value);
                    rs.setValue("old", old);
                    rs.setValue("kvit", kvit);
                    rs.setValue("time", time);
                }
                rec.setValue("isError", isError);
                rec.setValue("isKvit", isKvit);
                if (isKvit) {
                    rec.setValue("lastTime", new Date());
                }
            }
            getDeviceContext().setVariable("errors", getDeviceContext().getCallerController(), error);
        } catch (ContextException ex) {
            return;
        }
    }

}
