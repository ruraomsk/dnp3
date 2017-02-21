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
public class DISRBInterval extends Inter 
{

    int iDISRBInterval;

    public DISRBInterval()
    {
        id = 31;
    }

    public DISRBInterval(int iDISRBInterval)
    {
        id = 31;
        this.iDISRBInterval = iDISRBInterval;
    }

    public int getDISRBInterval()
    {
        return iDISRBInterval;
    }

    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        iDISRBInterval = Util.ToShort(buffer, pos);
        return 2;
    }

    @Override
    public String toString()
    {
        return "DISRBInterval=" + Integer.toString(iDISRBInterval); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        Util.ShortToBuff(buffer, pos, iDISRBInterval);
        return 2;
    }
}
