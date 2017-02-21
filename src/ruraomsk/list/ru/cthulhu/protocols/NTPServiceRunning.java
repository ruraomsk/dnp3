/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.protocols;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class NTPServiceRunning extends Inter
{
    byte ServiceRunning;

    public NTPServiceRunning()
    {
        id=2;
    }

    public NTPServiceRunning(boolean isRunning)
    {
        id=2;
        if(isRunning) ServiceRunning=1;
        else ServiceRunning=0;
    }
    public boolean isServiceRunning(){
        return (ServiceRunning==1);
    }
    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        ServiceRunning=buffer[pos];
        return 1;
    }

    @Override
    public String toString()
    {
        return "Сервис NTP "+(ServiceRunning==0?"не запущен":"запущен");
    }

    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        buffer[pos]=ServiceRunning;
        return 1;
    }
}
