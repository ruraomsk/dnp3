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
public class RBInterval extends Inter
{
    int iRBInterval;
    
    public RBInterval()
    {
        id = 25;
    }
    
    public RBInterval(int iRBInterval)
    {
        id = 25;
        this.iRBInterval = iRBInterval;
    }

    public int getRBInterval()
    {
        return iRBInterval;
    }

    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        iRBInterval = Util.ToShort(buffer, pos);
        return 2;
    }
    
    @Override
    public String toString()
    {
        return "RBInterval=" + Integer.toString(iRBInterval); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        Util.ShortToBuff(buffer, pos, iRBInterval);
        return 2;
    }
    
}
