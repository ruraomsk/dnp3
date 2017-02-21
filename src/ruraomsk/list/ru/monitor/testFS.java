/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.monitor;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import ruraomsk.list.ru.cthulhu.*;
import ruraomsk.list.ru.cthulhu.pocket.*;
import ruraomsk.list.ru.strongsql.DescrValue;
import ruraomsk.list.ru.strongsql.ParamSQL;
import ruraomsk.list.ru.strongsql.SetValue;
import ruraomsk.list.ru.strongsql.StrongSql;
import ruraomsk.list.ru.vlrmanager.VLRDataTableManager;
import ruraomsk.list.ru.vlrmanager.VLRXMLManager;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class testFS {

    static final String IPVLR1 = "10.12.8.30";
    static final int port1 = 257;
    static final String IPVLR2 = "10.12.8.31";
    static final int port2 = 257;
    static final String IPVLR3 = "192.168.1.69";
    static final int port3 = 5201;

    static Registers masterregisters;
    static final Long timestep = 500L;
    static ParamSQL param;
    static VLRXMLManager vlrbase;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        // TODO code application logic here
//        System.out.println("OS name=" + System.getProperty("os.name"));
        masterregisters = new Registers();
        param = new ParamSQL();
        param.myDB = "vlr";
        param.JDBCDriver = "org.postgresql.Driver";
        param.url = "jdbc:postgresql://192.168.1.76:5432/vlrbase";
        param.user = "postgres";
        param.password = "162747";

        MainMaster mainmaster = new MainMaster(masterregisters, timestep);
//        mainmaster.addController(1, IPVLR1, port1);
        mainmaster.addController(2, IPVLR2, port2);
//        mainmaster.addController(3, IPVLR3, port3,true);
        MasterFS[] masterfs = {
            //            new MasterFS(mainmaster, 1, timestep),
            new MasterFS(mainmaster, 2, timestep)
//            ,new MasterFS(mainmaster, 3, timestep)
        };
        GregorianCalendar startDateTime = new GregorianCalendar();
        Thread.sleep(timestep * 20);
        System.out.println(mainmaster.toString());
        if (args.length > 0) {
            vlrbase = new VLRXMLManager(param, true);
        } else {
            vlrbase = new VLRXMLManager(param);
        }

        for (MasterFS mfs : masterfs) {
//                mfs.startDataExch();
//                mfs.setDateTime(startDateTime);
            mfs.readResources();
            Thread.sleep(timestep * 5);

            if (args.length > 0) {
                for (Integer file : Util.NUM_FILES) {
                    mfs.loadFile(file);
                    Thread.sleep(timestep * 5);
                    while (mfs.getStatus(file) == 2) {
                        Thread.sleep(timestep * 5);
                    }
                }
                if (mfs.getStatus(10) == 4) {
                    LoadFileStatus lfs = mfs.getLoadFileStatus(10);
                    DataTable table = VLRDataTableManager.loadVariables(lfs.getDescription());
                    vlrbase.putXML(mfs.getIdDevice().toString(), 1, VLRDataTableManager.toXML(table));
                }
                if (mfs.getStatus(110) == 4) {
                    LoadFileStatus lfs = mfs.getLoadFileStatus(110);
                    DataTable table = VLRDataTableManager.loadVariables(lfs.getDescription());
                    vlrbase.putXML(mfs.getIdDevice().toString(), 2, VLRDataTableManager.toXML(table));
                    table = VLRDataTableManager.loadConstants(lfs.getConstats());
                    vlrbase.putXML(mfs.getIdDevice().toString(), 3, VLRDataTableManager.toXML(table));

                }
            }
            System.out.println(mfs.toString());
        }
        for (MasterFS mfs : masterfs) {
            int controller = mfs.getController();
            int lastid = 0;
            String prefix = "U" + Integer.toString(controller) + "_";
            Register reg;
            DataTable table = VLRDataTableManager.fromXML(vlrbase.getXML(mfs.getIdDevice().toString(), 1));
            for (DataRecord vd : table) {
                lastid = Math.max(lastid, vd.getInt("id"));
                reg = new Register(vd.getInt("id"), vd.getInt("type"));
                reg.setArchived(vd.getBoolean("arch"));
                reg.setEprom(vd.getBoolean("eprom"));
                reg.setSending(vd.getBoolean("send"));
                masterregisters.addNewRegister(controller, reg, prefix + vd.getString("name"));
            }
            int nextid = lastid;
            table = VLRDataTableManager.fromXML(vlrbase.getXML(mfs.getIdDevice().toString(), 2));
            for (DataRecord vd : table) {
                reg = new Register(vd.getInt("id") + lastid, vd.getInt("type"));
                nextid = Math.max(nextid, vd.getInt("id") + lastid);
                reg.setArchived(vd.getBoolean("arch"));
                reg.setEprom(vd.getBoolean("eprom"));
                reg.setSending(vd.getBoolean("send"));
                masterregisters.addNewRegister(controller, reg, prefix + vd.getString("name"));
            }
            table = VLRDataTableManager.fromXML(vlrbase.getXML(mfs.getIdDevice().toString(), 3));
            for (DataRecord cd : table) {
                reg = new Register(nextid++, cd.getInt("type"));
                reg.setConstant(true);
                masterregisters.addNewRegister(controller, reg, prefix + cd.getString("name"));
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
                masterregisters.changeRegister(controller, oreg);
            }
        }

        MasterDC[] mdcs = {
            //            new MasterDC(mainmaster, 1, timestep * 10L),
            new MasterDC(mainmaster, 2, timestep * 10L)
//            ,new MasterDC(mainmaster, 3, timestep * 10L)
        };
        for (MasterDC mdc : mdcs) {

//            for (OneReg oreg : masterregisters.getOneRegs(mdc.getController())) {
//                if (oreg.getReg().isSending()) {
//                    mdc.setWriteble(oreg.getuId());
//                }
//            }
            System.out.println(mdc.toString());
        }
        ArrayList<DescrValue> arraydesc = new ArrayList<>();
        for (MasterDC mdc : mdcs) {
            int count = 0;
            for (OneReg oreg : masterregisters.getOneRegs(mdc.getController())) {
                count++;
                String name = masterregisters.getNameReg(mdc.getController(), oreg.getuId());
                System.out.println(mdc.getController().toString() + "\t" + count + "\t" + oreg.toString() + "\t" + name);
                Integer newID = mdc.getController() << 16 | oreg.getuId();
                Integer type = oreg.getReg().getType();
                arraydesc.add(new DescrValue(name, newID, type));
            }
        }
        param.myDB = "data";
        if(args.length>0){
            new StrongSql(param, arraydesc, 1, 5000000L, "VLR Table");
            System.out.println("База " + param.toString() + " создана...");
        }
        StrongSql stSQL = new StrongSql(param);

        System.out.println("База " + param.toString() + " открыта...");

        while (true) {
            Thread.sleep(timestep * 5L);
            System.out.println("**********************************************************************************");
            ArrayList<SetValue> arrayValues = new ArrayList<>();
            for (MasterDC mdc : mdcs) {
                Integer count = 0;

                for (OneReg oreg : masterregisters.getOneRegs(mdc.getController())) {
                        Integer newID = mdc.getController() << 16 | oreg.getuId();
                        Object value = oreg.getValue();
                        arrayValues.add(new SetValue(newID, oreg.getTime(), value,oreg.getGood()));
                        count++;
                        for (OneReg horeg : masterregisters.getHistoryOneReg(mdc.getController(), oreg.getuId())) {
                            value = horeg.getValue();
                            arrayValues.add(new SetValue(newID, horeg.getTime(), value,oreg.getGood()));
                            count++;

                        }

//                        String name = masterregisters.getNameReg(mdc.getController(), oreg.getuId());
//                        System.out.println(mdc.getController().toString() + "\t" + oreg.toString() + "\t" + name);

                }
                System.out.println(mdc.getController().toString() + "\t" + "Значений=" + count.toString());
            }
            stSQL.addValues(new Timestamp(System.currentTimeMillis()), arrayValues);
            for (MasterFS mfs : masterfs) {
                Integer controller = mfs.getController();
                System.out.print(controller.toString() + "\t");
                mfs.requestDateTime();
                mfs.startKeepAlive();
                System.out.print(Util.dateStr(mfs.getLastKeepAlive()) + "<=>" + Util.dateStr(mfs.getDateTimeVLR()));
                System.out.println("===" + (mfs.getLastKeepAlive() - mfs.getDateTimeVLR()));
            }

        }
//        System.out.println("waiting...");
//        System.in.read( );
//        
//        System.exit(0);
    }

}
