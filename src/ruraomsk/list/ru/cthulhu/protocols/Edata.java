/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.protocols;

import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class Edata
{

    public Integer DEHWIntefaceID;
    public Integer CPointID;
    public Integer DEStngID;

    public Edata()
    {
    }

    public Edata(int DEHWIntefaceID, int CPointID, int DEStngID)
    {
        this.DEHWIntefaceID = DEHWIntefaceID;
        this.CPointID = CPointID;
        this.DEStngID = DEStngID;
    }

    @Override
    public String toString()
    {
        return "{" + DEHWIntefaceID.toString() + "/" + CPointID.toString() + "/" + DEStngID.toString() + "}"; //To change body of generated methods, choose Tools | Templates.
    }

    public int tobuffer(byte[] buffer, int pos)
    {
        Util.ShortToBuff(buffer, pos, DEHWIntefaceID);
        pos += 2;
        Util.ShortToBuff(buffer, pos, CPointID);
        pos += 2;
        Util.ShortToBuff(buffer, pos, DEStngID);
        return 6;
    }

    public int frombuffer(byte[] buffer, int pos)
    {
        DEHWIntefaceID = Util.ToShort(buffer, pos);
        pos += 2;
        CPointID = Util.ToShort(buffer, pos);
        pos += 2;
        DEStngID = Util.ToShort(buffer, pos);
        return 6;
    }
}
