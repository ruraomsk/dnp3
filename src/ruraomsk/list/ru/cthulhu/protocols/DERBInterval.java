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
public class DERBInterval extends Inter
{

    int iDERBInterval;

    public DERBInterval()
    {
        id = 34;
    }

    public DERBInterval(int iDERBInterval)
    {
        id = 34;
        this.iDERBInterval = iDERBInterval;
    }

    public int getDERBInterval()
    {
        return iDERBInterval;
    }

    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        iDERBInterval = Util.ToShort(buffer, pos);
        return 2;
    }

    @Override
    public String toString()
    {
        return "DERBInterval=" + Integer.toString(iDERBInterval); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        Util.ShortToBuff(buffer, pos, iDERBInterval);
        return 2;
    }
}
