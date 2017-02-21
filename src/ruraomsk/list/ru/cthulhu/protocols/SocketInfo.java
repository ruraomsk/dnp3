/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.protocols;

import com.tibbo.aggregate.common.Log;
import java.net.InetAddress;
import java.net.UnknownHostException;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class SocketInfo extends Inter
{

    InetAddress LocalIP;
    int LocalPort;
    InetAddress RemoteIP;
    int RemotePort;
    InetAddress NetMask;
    boolean Protocol;
    boolean SocketType;
    byte SocketState;

    public SocketInfo()
    {
        id = 21;
    }

    public SocketInfo(String LocalIP, int LocalPort, String RemoteIP, int RemotePort, String NetMask)
    {
        id=21;
        try {
            this.LocalIP = InetAddress.getByName(LocalIP);
            this.LocalPort = LocalPort;
            this.RemoteIP = InetAddress.getByName(RemoteIP);
            this.RemotePort = RemotePort;
            this.NetMask = InetAddress.getByName(NetMask);
        }
        catch (UnknownHostException ex) {
            Log.CORE.info("Ошибка SocketInfo " + ex.getMessage());
        }
    }

    public void setStatus(boolean Protocol, boolean SocketType, byte SocketState)
    {
        this.Protocol = Protocol;
        this.SocketType = SocketType;
        this.SocketState = SocketState;
    }

    @Override
    public String toString()
    {
        String res="SocketInfo ";
        res+=Util.makeIP(LocalIP.getAddress())+":"+Integer.toString(LocalPort)+" ";
        res+=Util.makeIP(RemoteIP.getAddress())+":"+Integer.toString(RemotePort)+" ";
        res+=Util.makeIP(NetMask.getAddress())+(Protocol?" TCP":" UPD")+" ";
        res+=(SocketType?"Server":"Slave")+" "+Integer.toHexString(SocketState);
        return res;
    }

    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        int start=pos;
        LocalIP=Util.IAfrombuffer(buffer, pos);
        pos+=4;
        LocalPort=Util.ToShort(buffer, pos);
        pos+=2;
        RemoteIP=Util.IAfrombuffer(buffer, pos);
        pos+=4;
        RemotePort=Util.ToShort(buffer, pos);
        pos+=2;
        NetMask=Util.IAfrombuffer(buffer, pos);
        pos+=4;
        Protocol=(buffer[pos++]==0);
        SocketType=(buffer[pos++]==0);
        SocketState=buffer[pos++];
        return pos-start;
    }

    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        int start=pos;
        Util.IAtobuffer(buffer, pos, LocalIP);
        pos+=4;
        Util.ShortToBuff(buffer, pos, LocalPort);
        pos+=2;
        Util.IAtobuffer(buffer, pos, RemoteIP);
        pos+=4;
        Util.ShortToBuff(buffer, pos, RemotePort);
        pos+=2;
        Util.IAtobuffer(buffer, pos, NetMask);
        pos+=4;
        buffer[pos++]=(byte)(Protocol?0:1);
        buffer[pos++]=(byte)(SocketType?0:1);
        buffer[pos++]=SocketState;
        return pos-start;
    }


    /**
     * @return the LocalIP
     */
    public InetAddress getLocalIP()
    {
        return LocalIP;
    }

    /**
     * @return the LocalPort
     */
    public int getLocalPort()
    {
        return LocalPort;
    }

    /**
     * @return the RemoteIP
     */
    public InetAddress getRemoteIP()
    {
        return RemoteIP;
    }

    /**
     * @return the RemotePort
     */
    public int getRemotePort()
    {
        return RemotePort;
    }

    /**
     * @return the NetMask
     */
    public InetAddress getNetMask()
    {
        return NetMask;
    }

    /**
     * @return the Protocol
     */
    public boolean isProtocol()
    {
        return Protocol;
    }

    /**
     * @return the SocketType
     */
    public boolean isSocketType()
    {
        return SocketType;
    }

    /**
     * @return the SocketState
     */
    public byte getSocketState()
    {
        return SocketState;
    }

}
