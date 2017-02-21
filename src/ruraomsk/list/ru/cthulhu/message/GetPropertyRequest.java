/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Property;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class GetPropertyRequest extends BaseMessage 
{

    byte cmd;
    int Left;
    int Right;
    HashMap<Integer, ArrayList<Integer>> pars = new HashMap<>();

    public GetPropertyRequest()
    {
        cmd = 0;
        setId(Util.GetProperty);
        setReqIter(1);
        itsRequest();
    }

    public void setRange(int Left, int Right)
    {
        this.Left = Left;
        this.Right = Right;
    }

    public boolean isRange()
    {
        return (cmd == 0);
    }

    public int getLeft()
    {
        return Left;
    }

    public int getRight()
    {
        return Right;
    }

    public void addProperty(Property prop)
    {
        cmd=1;
        int key = prop.getRegister().getuId();
        if (!pars.containsKey(key)) {
            pars.put(key, new ArrayList<Integer>());
        }
        ArrayList<Integer> ar = pars.get(key);
        ar.add(prop.getPrpId());
        pars.put(key, ar);
    }

    public Collection<Integer> getUIds()
    {
        return pars.keySet();
    }

    public Integer[] getProps(int uId)
    {
        ArrayList<Integer> ar = pars.get(uId);
        Integer[] aa = new Integer[ar.size()];
        ar.toArray(aa);
        return aa;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        if (cmd == 0) {
            buffer[pos + 3] = cmd;
            Util.ShortToBuff(buffer, pos + 4, Left);
            Util.ShortToBuff(buffer, pos + 6, Right);
            return 8;
        }
        int startpos = pos;
        pos += 3;
        Util.ShortToBuff(buffer, pos, pars.size());
        pos += 2;
        for (Integer uId : pars.keySet()) {
            Util.ShortToBuff(buffer, pos, uId);
            pos += 2;
            ArrayList<Integer> ar = pars.get(uId);
            Util.ShortToBuff(buffer, pos, ar.size());
            pos += 2;
            for (Integer ai : ar) {
                Util.ShortToBuff(buffer, pos, ai);
                pos += 2;
            }
        }
        return pos - startpos;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        if (buffer[pos + 3] == 0) {
            cmd = 0;
            Left = Util.ToShort(buffer, pos + 4);
            Right = Util.ToShort(buffer, pos + 6);
            return;
        }
        cmd=1;
        pos += 3;
        int CountUid = Util.ToShort(buffer, pos);
        pos += 2;
        for (int i = 0; i < CountUid; i++) {
            int uId = Util.ToShort(buffer, pos);
            pos += 2;
            ArrayList<Integer> ar = new ArrayList<>();
            int CountPar = Util.ToShort(buffer, pos);
            pos += 2;
            for (int j = 0; j < CountPar; j++) {
                int par = Util.ToShort(buffer, pos);
                pos += 2;
                ar.add(par);
            }
            pars.put(uId, ar);
        }
    }

    @Override
    public String toString()
    {
        String result = super.toString();
        if (cmd == 0) {
            result += " Left=" + Integer.toString(Left) + " Right=" + Integer.toString(Right);
        }
        else {
            for (Integer uId : pars.keySet()) {
                result += " uId=" + uId.toString() + " [";
                ArrayList<Integer> ar = pars.get(uId);
                for (Integer par : ar) {
                    result += par.toString() + " ";
                }
                result += "] ";
            }
        }
        return result; //To change body of generated methods, choose Tools | Templates.
    }
}
