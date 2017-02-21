/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.protocols;

import java.util.ArrayList;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class DEHWInterfaceCPoints extends Inter
{

    int DEHWInterfaceCPointIDUsed;
    ArrayList<Integer> arrData;

    public DEHWInterfaceCPoints()
    {
        id = 11;
        arrData = new ArrayList<>();
    }

    public void setDEHWInterfaceCPointIDUsed(int Id)
    {
        DEHWInterfaceCPointIDUsed = Id;
    }

    public int getDEHWInterfaceCPointIDUsed()
    {
        return DEHWInterfaceCPointIDUsed;
    }

    public ArrayList<Integer> getDatas()
    {
        return arrData;
    }

    public void addData(int CPointID)
    {
        arrData.add(CPointID);
    }

    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        int start = pos;
        int count = Util.ToShort(buffer, pos);
        pos += 2;
        DEHWInterfaceCPointIDUsed = Util.ToShort(buffer, pos);
        pos += 2;
        arrData = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Integer ed = Util.ToShort(buffer, pos);
            pos += 2;
            arrData.add(ed);
        }
        return pos - start;
    }

    @Override
    public String toString()
    {
        String res = " DEHWInerfaceCPoints Count=" + Integer.toString(arrData.size()) + " IDUsed=" + Integer.toString(DEHWInterfaceCPointIDUsed);
        for (Integer dData : arrData) {
            res += dData.toString() + " ";
        }
        return res;
    }

    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        int start = pos;
        Util.ShortToBuff(buffer, pos, arrData.size());
        pos += 2;
        Util.ShortToBuff(buffer, pos, DEHWInterfaceCPointIDUsed);
        pos += 2;
        for (Integer dData : arrData) {
            Util.ShortToBuff(buffer, pos, dData);
            pos += 2;
        }
        return pos - start;
    }
}
