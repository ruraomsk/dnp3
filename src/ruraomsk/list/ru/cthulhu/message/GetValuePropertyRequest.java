/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import java.util.ArrayList;
import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class GetValuePropertyRequest extends BaseMessage 
{

    byte cmd;
    int Left;
    int Right;
    ArrayList<Integer> pars = new ArrayList<>();

    public GetValuePropertyRequest()
    {
        cmd = 0;
        setId(Util.GetValueProperty);
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
    public int getLenght(){
        if(cmd==1) return pars.size();
        return Right-Left;
    }
    public void addValue(int uId)
    {
        cmd=1;
        pars.add(uId);
    }
    public Integer[] getValues()
    {
        Integer[] aa = new Integer[pars.size()];
        pars.toArray(aa);
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
        for (Integer uId : pars) {
            Util.ShortToBuff(buffer, pos, uId);
            pos += 2;
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
        pars.clear();
        pos += 2;
        for (int i = 0; i < CountUid; i++) {
            int uId = Util.ToShort(buffer, pos);
            pos += 2;
            pars.add(uId);
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
            for (Integer uId : pars) {
                result += " uId=" + uId.toString();
            }
        }
        return result; //To change body of generated methods, choose Tools | Templates.
    }
}
