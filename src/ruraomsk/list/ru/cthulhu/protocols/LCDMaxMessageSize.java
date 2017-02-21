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
public class LCDMaxMessageSize extends Inter
{

    int iLCDMaxMessageSize;

    public LCDMaxMessageSize()
    {
        id = 33;
    }

    public LCDMaxMessageSize(int iLCDMaxMessageSize)
    {
        id = 33;
        this.iLCDMaxMessageSize = iLCDMaxMessageSize;
    }

    public int getLCDMaxMessageSize()
    {
        return iLCDMaxMessageSize;
    }

    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        iLCDMaxMessageSize = Util.ToShort(buffer, pos);
        return 2;
    }

    @Override
    public String toString()
    {
        return "LCDMaxMessageSize=" + Integer.toString(iLCDMaxMessageSize); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        Util.ShortToBuff(buffer, pos, iLCDMaxMessageSize);
        return 2;
    }
}
