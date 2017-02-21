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
public class GVPSettings extends Inter 
{
    int Interval;
    int Left;
    int Right;

    public GVPSettings()
    {
        id=24;
    }

    public GVPSettings(int Interval, int Left, int Right)
    {
        id=24;
        this.Interval = Interval;
        this.Left = Left;
        this.Right = Right;
    }
    
    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        Interval=Util.ToShort(buffer, pos);
        pos+=2;
        Left=Util.ToShort(buffer, pos);
        pos+=2;
        Right=Util.ToShort(buffer, pos);
        return 6;
    }

    @Override
    public String toString()
    {
        return "GVPSetting Inteval="+Integer.toString(Interval)+
               " Left="+Integer.toString(Left)+ 
               " Right="+Integer.toString(Right); 
    }
    

    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        Util.ShortToBuff(buffer, pos, Interval);
        pos+=2;
        Util.ShortToBuff(buffer, pos, Left);
        pos+=2;
        Util.ShortToBuff(buffer, pos, Right);
        return 6;
    }
}
