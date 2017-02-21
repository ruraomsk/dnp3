/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import com.tibbo.aggregate.common.Log;
import java.net.InetAddress;
import java.net.UnknownHostException;
import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class SetConnectionIdentRequest extends BaseMessage
{
    int PeerID;
    int DevCPID;
    InetAddress NSIP;
    InetAddress NSNetMask;
    int NSPort;

    public SetConnectionIdentRequest()
    {
        itsRequest();
        setId(Util.SetConnectionIdent);
        setVersion(Util.CT_V23);
    }
    public SetConnectionIdentRequest(int PeerID,int DevCPID, String NSIP, String NSNetMask, int NSPort)
    {
        itsRequest();
        setId(Util.SetConnectionIdent);
        setVersion(Util.CT_V23);
        try {
            this.PeerID=PeerID;
            this.DevCPID = DevCPID;
            this.NSIP = InetAddress.getByName(NSIP);
            this.NSNetMask = InetAddress.getByName(NSNetMask);
            this.NSPort = NSPort;
        }
        catch (UnknownHostException ex) {
            Log.CORE.info("GetConnectionIdentRequest"+ex.getMessage());
        }
    }
    public int getPeerID(){
        return PeerID;
    }
    public int getDevCPID(){
        return DevCPID;
    }
    public int getNSPort(){
        return NSPort;
    }
    public InetAddress getNSIP(){
        return NSIP;
    }
    public InetAddress getNSNetMask(){
        return NSNetMask;
    }
    @Override
    public String toString()
    {
        return super.toString()+" PeerID"+Integer.toString(PeerID)+" DevCPID="+Integer.toString(DevCPID)+" "
                +Util.makeIP(NSIP.getAddress())+" "
                +Util.makeIP(NSNetMask.getAddress())+" "
                +Integer.toString(NSPort); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        Util.ShortToBuff(buffer, pos+3, PeerID);
        Util.ShortToBuff(buffer, pos+6, DevCPID);
        Util.IAtobuffer(buffer, pos+8, NSIP);
        Util.IAtobuffer(buffer, pos+12, NSNetMask);
        Util.ShortToBuff(buffer, pos+16, NSPort);
        return 18;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        PeerID=Util.ToShort(buffer, pos+3);
        DevCPID=Util.ToShort(buffer, pos+6);
        NSIP=Util.IAfrombuffer(buffer, pos+8);
        NSNetMask=Util.IAfrombuffer(buffer, pos+12);
        NSPort=Util.ToShort(buffer, pos+16);
    }
}
