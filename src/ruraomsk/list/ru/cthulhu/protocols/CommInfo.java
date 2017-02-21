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
public class CommInfo extends Inter
{

    byte Type;
    int BaudRate;
    byte Parity;
    byte DataBits;
    byte StopBits;
    byte FlowControl;

    public CommInfo()
    {
        id = 22;
    }

    public CommInfo(byte Type, int BaudRate, byte Parity, byte DataBits, byte StopBits, byte FlowControl)
    {
        id=22;
        this.Type = Type;
        this.BaudRate = BaudRate;
        this.Parity = Parity;
        this.DataBits = DataBits;
        this.StopBits = StopBits;
        this.FlowControl = FlowControl;
    }

    @Override
    public String toString()
    {
        String res = "CommInfo ";
        switch (Type) {
            case 1:
                res += "RS-232";
                break;
            case 2:
                res += "RS-422";
                break;
            case 3:
                res += "RS-485";
                break;
            default:
                res += "Error";
        }
        res += " BaudRate=" + Integer.toString(BaudRate);
        res += " Parity=" + Integer.toString(Parity);
        res += " DataBits=" + Integer.toString(DataBits);
        res += " StopBits=" + Integer.toString(StopBits);
        res += " FlowControl=" + Integer.toString(FlowControl);
        return res;
    }

    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        Type = buffer[pos++];
        BaudRate = Util.ToInteger(buffer, pos);
        pos += 4;
        Parity = buffer[pos++];
        DataBits = buffer[pos++];
        StopBits = buffer[pos++];
        FlowControl = buffer[pos++];
        return 9;
    }

    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        buffer[pos++] = Type;
        Util.IntegerToBuff(buffer, pos, BaudRate);
        pos += 4;
        buffer[pos++] = Parity;
        buffer[pos++] = DataBits;
        buffer[pos++] = StopBits;
        buffer[pos++] = FlowControl;
        return 9;
    }

}
