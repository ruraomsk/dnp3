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
public class MessageMaxSize extends Inter
{

    int iMessageMaxSize;

    public MessageMaxSize()
    {
        id = 23;
    }
    
    public MessageMaxSize(int iMessageMaxSize)
    {
        id=23;
        this.iMessageMaxSize = iMessageMaxSize;
    }

    public int getMessageMaxSize()
    {
        return iMessageMaxSize;
    }
    
    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        iMessageMaxSize = Util.ToShort(buffer, pos);
        return 4;
    }
    
    @Override
    public String toString()
    {
        return "MessageMaxSize=" + Integer.toString(iMessageMaxSize); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        Util.ShortToBuff(buffer, pos, iMessageMaxSize);
        return 2;
    }
    
}
