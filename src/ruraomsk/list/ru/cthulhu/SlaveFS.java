/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import com.tibbo.aggregate.common.datatable.DataTable;
import java.util.HashMap;

/**
 * Файловая система устройства ВЛР
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class SlaveFS
{
    DataTable dtfiles;
    HashMap<String,String> volumes;
    String mainPath;
    String nowPath;

    public SlaveFS(String mainPath,DataTable dtfiles)
    {
        volumes=new HashMap<>();
        this.mainPath=mainPath;
        this.dtfiles = dtfiles;
        
    }
    
    
}
