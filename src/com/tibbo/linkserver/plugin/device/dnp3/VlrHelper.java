/*
 * Класс где собраны все утилиты помощи для драйвера
 */
package com.tibbo.linkserver.plugin.device.dnp3;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import static com.tibbo.aggregate.common.datatable.FieldFormat.create;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.validator.LimitsValidator;
import com.tibbo.aggregate.common.datatable.validator.ValidatorHelper;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import ruraomsk.list.ru.cthulhu.OneReg;
import ruraomsk.list.ru.cthulhu.Register;
import ruraomsk.list.ru.cthulhu.Registers;
import ruraomsk.list.ru.cthulhu.Util;
import ruraomsk.list.ru.strongsql.ParamSQL;

/**
 *
 * @author Yury Rusinov <ruraomsl@list.ru at Automatics E>
 */
class VlrHelper {

    public static TableFormat VFT_SQL_PROP;
    public static TableFormat VFT_VLR_PROP;
    public static TableFormat VFT_CNL_PROP;
    public static TableFormat VFT_REGISTERS;
    public static TableFormat VFT_ERROR_STATUS;
    public static TableFormat VFT_DETAIL_STATUS;

    static {
        VFT_SQL_PROP = new TableFormat(1, 1);
        VFT_SQL_PROP.addField(FieldFormat.create("<JDBCDriver><S><A=org.postgresql.Driver><D=Драйвер БД>"));
        VFT_SQL_PROP.addField(FieldFormat.create("<url><S><A=jdbc:postgresql://192.168.1.76:5432/vlrbase><D=URL БД>"));
        VFT_SQL_PROP.addField(FieldFormat.create("<user><S><A=postgres><D=Пользователь БД>"));
        VFT_SQL_PROP.addField(FieldFormat.create("<password><S><A=162747><D=Пароль БД>"));
        VFT_SQL_PROP.addField(FieldFormat.create("<setups><S><A=vlr><D=Имя таблицы с настройками ВЛР>"));
        VFT_SQL_PROP.addField(FieldFormat.create("<datas><S><A=data><D=Префикс имен таблиц для хранения истории переменных>"));
        VFT_SQL_PROP.addField(FieldFormat.create("<timestep><L><A=500><D=Основной квант времени>"));
        VFT_SQL_PROP.addField(FieldFormat.create("<stepSQL><L><A=5000><D=Интервал сохранения переменных в БД >"));
        VFT_SQL_PROP.addField(FieldFormat.create("<initSQL><B><A=true><D=Создавать БД при первом запуске>"));
        VFT_SQL_PROP.addField(FieldFormat.create("<longSQL><L><A=5000000><D=Размер кольцевой таблицы БД>"));
        VFT_SQL_PROP.addField(FieldFormat.create("<readall><B><A=true><D=Принудительное чтение всех переменных>"));

        VFT_CNL_PROP = new TableFormat();
        VFT_CNL_PROP.addField(FieldFormat.create("<canel><I><D=Номер канала>"));
        VFT_CNL_PROP.addField(FieldFormat.create("<spo><S><D=Номер версии СПО>"));
        VFT_CNL_PROP.addField(FieldFormat.create("<ppo><S><D=Номер версии ППО>"));
        VFT_CNL_PROP.addField(FieldFormat.create("<prefix><S><D=Префикс имен переменных>"));
        VFT_CNL_PROP.addField(FieldFormat.create("<description><S><D=Описание>"));

        VFT_VLR_PROP = new TableFormat();
        VFT_VLR_PROP.addField(FieldFormat.create("<canel><I><D=Номер канала>"));
        VFT_VLR_PROP.addField(FieldFormat.create("<IPAddress><S><D=IP адрес устройства >"));
        VFT_VLR_PROP.addField(FieldFormat.create("<port><I><D=Номер порта>"));
        VFT_VLR_PROP.addField(FieldFormat.create("<slip><B><A=false><D=SLIP протокол>"));
        VFT_VLR_PROP.addField(FieldFormat.create("<description><S><D=Описание>"));

        VFT_REGISTERS = new TableFormat();
        FieldFormat ff = FieldFormat.create("<name><S><D=Имя переменной>");
//        ff.getValidators().add(ValidatorHelper.NAME_LENGTH_VALIDATOR);
//        ff.getValidators().add(ValidatorHelper.NAME_SYNTAX_VALIDATOR);
        VFT_REGISTERS.addField(ff);
        ff = FieldFormat.create("<canel><I><D=Номер канала>");
        VFT_REGISTERS.addField(ff);
        ff = FieldFormat.create("<id><I><D=Номер переменной>");
        VFT_REGISTERS.addField(ff);
        ff = FieldFormat.create("<description><S><D=Описание>");
//        ff.getValidators().add(new LimitsValidator(1, 600));
//        ff.getValidators().add(ValidatorHelper.DESCRIPTION_SYNTAX_VALIDATOR);
        VFT_REGISTERS.addField(ff);
        ff = FieldFormat.create("<type><I><D=Тип переменной>");
        ff.setSelectionValues(typeSelectionValues());
        VFT_REGISTERS.addField(ff);
        VFT_REGISTERS.addField(create("<send><B><A=false><D=Флаг Send>"));
        VFT_REGISTERS.addField(create("<arch><B><A=false><D=Флаг Arch>"));
        VFT_REGISTERS.addField(create("<eprom><B><A=false><D=Флаг Eprom>"));
        VFT_REGISTERS.addField(create("<constant><B><A=false><D=Флаг Constant>"));
        VFT_REGISTERS.addField(create("<readonly><B><A=false><D=Только чтение>"));
        VFT_REGISTERS.addField(create("<visible><B><A=false><D=Видимость>"));

        VFT_ERROR_STATUS = new TableFormat();
        VFT_ERROR_STATUS.addField(create("<canel><I><D=Номер канала>"));
        VFT_ERROR_STATUS.addField(create("<isError><B><A=false><D=Наличие ошибок на канале>"));
        VFT_ERROR_STATUS.addField(create("<isKvit><B><A=false><D=Квитирована ошибка?>"));
        VFT_ERROR_STATUS.addField(create("<isOldError><B><A=false><D=Прошлое состояние>"));
        VFT_ERROR_STATUS.addField(create("<lastTime><D><D=Время последней проверки>"));
        VFT_ERROR_STATUS.addField(create("<detailStatus><T><D=Детальное состояние сигналов>"));

        VFT_DETAIL_STATUS = new TableFormat();
        VFT_DETAIL_STATUS.addField(create("<name><S><D=Имя сигнала>"));
        VFT_DETAIL_STATUS.addField(create("<description><S><D=Описание>"));
        VFT_DETAIL_STATUS.addField(create("<reverse><B><A=false><D=Инверсия>"));
        VFT_DETAIL_STATUS.addField(create("<old><B><A=false><D=Предыдущий>"));
        VFT_DETAIL_STATUS.addField(create("<new><B><A=false><D=Текущее>"));
        VFT_DETAIL_STATUS.addField(create("<kvit><B><A=false><D=Не инфентировано>"));
        VFT_DETAIL_STATUS.addField(create("<time><D><D=Время>"));
    }

    private static Map typeSelectionValues() {
        Map types = new LinkedHashMap();
        types.put(0, "Boolean");
        types.put(1, "Integer");
        types.put(2, "Float");
        types.put(3, "Long");
        types.put(4, "One byte");
        return types;
    }

    public static void addRegisters(Registers masterregisters, Integer canel, String prefix, DataTable BPO, DataTable SPO, DataTable Const) {
        int lastid = Integer.MIN_VALUE;
        Register reg;
        for (DataRecord bpo : BPO) {
            lastid = Math.max(lastid, bpo.getInt("id"));
            reg = new Register(bpo.getInt("id"), bpo.getInt("type"));
            reg.setArchived(bpo.getBoolean("arch"));
            reg.setEprom(bpo.getBoolean("eprom"));
            reg.setSending(bpo.getBoolean("send"));
            reg.setConstant(false);
            reg.setReadOnly(!reg.isEprom());
            masterregisters.addNewRegister(canel, reg, prefix + bpo.getString("name"));
            masterregisters.addDescription(canel, reg.getuId(), bpo.getString("name") + ":" + bpo.getString("description"));
        }
        int nextid = lastid;
        for (DataRecord spo : SPO) {
            reg = new Register(spo.getInt("id") + lastid, spo.getInt("type"));
            nextid = Math.max(nextid, spo.getInt("id") + lastid);
            reg.setArchived(spo.getBoolean("arch"));
            reg.setEprom(spo.getBoolean("eprom"));
            reg.setSending(spo.getBoolean("send"));
            reg.setConstant(false);
            reg.setReadOnly(!reg.isEprom());
            masterregisters.addNewRegister(canel, reg, prefix + spo.getString("name"));
            masterregisters.addDescription(canel, reg.getuId(), spo.getString("name") + ":" + spo.getString("description"));
        }
        for (DataRecord cd : Const) {
            reg = new Register(nextid++, cd.getInt("type"));
            reg.setConstant(true);
            reg.setReadOnly(true);
            masterregisters.addNewRegister(canel, reg, prefix + cd.getString("name"));
            masterregisters.addDescription(canel, reg.getuId(), cd.getString("name") + ":" + cd.getString("description"));
            Object value = 0;
            switch (cd.getInt("type")) {
                case 0:
                    value = Boolean.parseBoolean(cd.getString("value"));
                    break;
                case 1:
                    value = Integer.parseInt(cd.getString("value"));
                    break;
                case 2:
                    value = Float.parseFloat(cd.getString("value"));
                    break;
                case 3:
                    value = Long.parseLong(cd.getString("value"));
                    break;
            }
            OneReg oreg = new OneReg(System.currentTimeMillis(), reg, value, Util.CT_DATA_GOOD);
            masterregisters.changeRegister(canel, oreg);
        }
    }

    static public DataTable makeRegisters(Registers masterregisters, DataTable canel_prop) {
        DataTable table = new DataTable(VFT_REGISTERS);
        for (DataRecord cp : canel_prop) {
            for (OneReg oreg : masterregisters.getOneRegs(cp.getInt("canel"))) {
                DataRecord reg = table.addRecord();
                reg.setValue("name", masterregisters.getNameReg(cp.getInt("canel"), oreg.getuId()));
                reg.setValue("canel", cp.getInt("canel"));
                reg.setValue("id", oreg.getuId());
                String str = cp.getString("prefix") + masterregisters.getDescription(cp.getInt("canel"), oreg.getuId());
                if (str == null || str.length() < 1) {
                    str = "+";
                }
                reg.setValue("description", str);
                reg.setValue("type", oreg.getReg().getType());
                reg.setValue("send", oreg.getReg().isSending());
                reg.setValue("arch", oreg.getReg().isArchived());
                reg.setValue("eprom", oreg.getReg().isEprom());
                reg.setValue("constant", oreg.getReg().isConstant());
                reg.setValue("readonly", oreg.getReg().isReadOnly());
                reg.setValue("visible", false);
            }
            DataRecord reg = table.addRecord();
            reg.setValue("name", cp.getString("prefix") + "isError");
            reg.setValue("description", cp.getString("prefix") + ": Есть ошибка на канале");
            reg.setValue("canel", cp.getInt("canel"));
            reg.setValue("type", 0);
            reg.setValue("visible", true);
            reg = table.addRecord();
        }
        return table;
    }

    public static ParamSQL setParamVlr(DataTable SQLProp) {
        ParamSQL param = new ParamSQL();
        DataRecord rec = SQLProp.rec();
        param.JDBCDriver = rec.getString("JDBCDriver");
        param.url = rec.getString("url");
        param.user = rec.getString("user");
        param.password = rec.getString("password");
        param.myDB = rec.getString("setups");
        return param;
    }

    public static ParamSQL setParamData(DataTable SQLProp) {
        ParamSQL param = new ParamSQL();
        DataRecord rec = SQLProp.rec();
        param.JDBCDriver = rec.getString("JDBCDriver");
        param.url = rec.getString("url");
        param.user = rec.getString("user");
        param.password = rec.getString("password");
        param.myDB = rec.getString("datas");
        return param;
    }

    static public Character setCharType(int type) {
        switch (type) {
            case 0:
                return 'B';
            case 1:
                return 'I';
            case 2:
                return 'F';
            case 3:
                return 'L';
            case 4:
                return 'S';
        }
        return 'S';
    }


    static void addErrors(DataTable errorsTable, Integer canal,String prefix, DataTable Error) {
        DataRecord rec=errorsTable.addRecord();
        rec.setValue("canel", canal);
        DataTable status=new DataTable(VFT_DETAIL_STATUS);
        for(DataRecord r:Error){
            DataRecord rs=status.addRecord();
            rs.setValue("name", prefix+r.getString("name"));
            rs.setValue("reverse", r.getBoolean("reverse"));
        }
        rec.setValue("detailStatus", status);
    }
}
