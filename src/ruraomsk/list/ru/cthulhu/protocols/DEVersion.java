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
public class DEVersion extends Inter 
{
    byte Version=Util.CT_V21;

    public DEVersion()
    {
        id=1;
    }
    
    public DEVersion(byte Version){
        id=1;
        this.Version=Version;
    }
    @Override
    public String toString(){
        return "Версия протокола="+Integer.toHexString(Version);
    }
    @Override
    public int tobuffer(byte[] buffer,int pos){
        buffer[pos]=Version;
        return 1;
    }
    @Override
    public int frombuffer(byte[] buffer,int pos){
        Version=buffer[pos];
        return 1;
    }
}
