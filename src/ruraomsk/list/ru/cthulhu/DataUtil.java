/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import java.io.File;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class DataUtil
{
    public static final DataTable initDataTables(){
        TableFormat vftdt=new TableFormat(true);
        vftdt.addField(FieldFormat.create("<FlNum><I><A=999><D=Номер файла в системе>"));
        vftdt.addField(FieldFormat.create("<FlName><S><A=NewFile><D=Имя файла>"));
        vftdt.addField(FieldFormat.create("<FlDir><S><A=><D=Директория расположения>"));
        DataTable res=new DataTable(vftdt);
        res.addRecord(101,"BPO1",File.separator+"bpo1");
        res.addRecord(102,"BPO2",File.separator+"bpo1");
        return res;
    }
    public static final DataTable initVolumes(){
        TableFormat vftdt=new TableFormat(true);
        vftdt.addField(FieldFormat.create("<Volume><S><A=C><D=Краткое имя тома>"));
        vftdt.addField(FieldFormat.create("<VlmName><S><A=Drive C><D=Полное имя тома>"));
        vftdt.addField(FieldFormat.create("<VlmDir><S><A=C><D=Директория расположения>"));
        DataTable res=new DataTable(vftdt);
        res.addRecord("C","Drive C",File.separator+"dc");
        res.addRecord("A","Drive A",File.separator+"da");
        res.addRecord("D","Drive D",File.separator+"dd");
        return res;
    }
    
    
}
