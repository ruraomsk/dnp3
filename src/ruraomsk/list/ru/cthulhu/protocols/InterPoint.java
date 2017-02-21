/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.protocols;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class InterPoint
{

    public int DEHWInterfaceID;
    public int CPointID;
    public Inter interData;

    public InterPoint()
    {
    }

    public InterPoint(int DEHWInterfaceID, int CPointID, Inter interData)
    {
        this.DEHWInterfaceID = DEHWInterfaceID;
        this.CPointID = CPointID;
        this.interData = interData;
    }

    @Override
    public String toString()
    {
        return "InterPoint DEHWInterfaceID=" + Integer.toString(DEHWInterfaceID)
                + " CPointID=" + Integer.toString(CPointID)+" "
                + interData.toString(); //To change body of generated methods, choose Tools | Templates.
    }

}
