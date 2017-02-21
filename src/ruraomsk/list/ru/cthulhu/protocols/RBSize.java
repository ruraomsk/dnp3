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
public class RBSize extends Inter
{
    int iRBSize;

    public RBSize()
    {
        id = 26;
    }

    public RBSize(int iRBSize)
    {
        id = 26;
        this.iRBSize = iRBSize;
    }

    public int getRBSize()
    {
        return iRBSize;
    }

    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        iRBSize = Util.ToShort(buffer, pos);
        return 2;
    }

    @Override
    public String toString()
    {
        return "RBSize=" + Integer.toString(iRBSize); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        Util.ShortToBuff(buffer, pos, iRBSize);
        return 2;
    }

}
