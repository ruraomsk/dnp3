/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import java.net.InetAddress;
import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class RegisterPeerRequest extends BaseMessage
{

    byte RegStage;
    int DevCPID;
    InetAddress NSIP;
    InetAddress NSNetMask;
    int NSPort;
    int SourceData;
    int ConvertedData;

    public RegisterPeerRequest()
    {
        itsRequest();
        setId(Util.RegisterPeer);
        setVersion(Util.CT_V23);
    }

    public RegisterPeerRequest(int DevCPID, InetAddress NSIP, InetAddress NSNetMask, int NSPort)
    {
        itsRequest();
        setId(Util.RegisterPeer);
        setVersion(Util.CT_V23);
        this.RegStage = 1;
        this.DevCPID = DevCPID;
        this.NSIP = NSIP;
        this.NSNetMask = NSNetMask;
        this.NSPort = NSPort;
    }

    public RegisterPeerRequest(int SourceData, int ConvertedData)
    {
        itsRequest();
        setId(Util.RegisterPeer);
        setVersion(Util.CT_V23);
        this.RegStage = 2;
        this.SourceData = SourceData;
        this.ConvertedData = ConvertedData;

    }

    public byte getRegStage()
    {
        return RegStage;
    }

    public int getDevCPID()
    {
        return DevCPID;
    }

    public InetAddress getNSIP()
    {
        return NSIP;
    }

    ;
    public InetAddress getNSNetMask()
    {
        return NSNetMask;
    }

    public int getNSPort()
    {
        return NSPort;
    }

    public int getSourceData()
    {
        return SourceData;
    }

    public int getConvertedData()
    {
        return ConvertedData;
    }

    @Override
    public String toString()
    {
        String res = super.toString() + " RegStage=" + Integer.toString(RegStage) + " "; //To change body of generated methods, choose Tools | Templates.
        res += (RegStage == 1) ? (Util.makeIP(NSIP.getAddress()) + " " + Util.makeIP(NSNetMask.getAddress()) + " " + Integer.toString(NSPort))
                : (Integer.toHexString(SourceData) + " " + Integer.toHexString(ConvertedData));
        return res;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+3] = RegStage;
        if (RegStage == 1) {
            Util.ShortToBuff(buffer, pos+4, DevCPID);
            Util.IAtobuffer(buffer, pos+6, NSIP);
            Util.IAtobuffer(buffer, pos+10, NSNetMask);
            Util.ShortToBuff(buffer, pos+14, NSPort);
            return 16;
        }
        else {
            Util.IntegerToBuff(buffer, pos+4, SourceData);
            Util.IntegerToBuff(buffer, pos+8, ConvertedData);
            return 12;
        }
    }
}
