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
public class DISRBSize extends Inter 
{

    int iDISRBSize;

    public DISRBSize()
    {
        id = 32;
    }

    public DISRBSize(int iDISRBSize)
    {
        id = 32;
        this.iDISRBSize = iDISRBSize;
    }

    public int getDISRBSize()
    {
        return iDISRBSize;
    }

    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        iDISRBSize = Util.ToShort(buffer, pos);
        return 2;
    }

    @Override
    public String toString()
    {
        return "DISRBSize=" + Integer.toString(iDISRBSize); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        Util.ShortToBuff(buffer, pos, iDISRBSize);
        return 2;
    }
}
