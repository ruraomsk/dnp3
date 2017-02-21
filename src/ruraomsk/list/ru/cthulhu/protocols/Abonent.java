/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.protocols;

import java.net.InetAddress;
import java.net.UnknownHostException;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class Abonent
{

    int PeerAL;
    int DevCPID;
    boolean bSocket;
    InetAddress IP;
    InetAddress NetMask;
    int Port;

    public Abonent()
    {
    }

    public Abonent(int PeerAL, int DevCPID, String IP, String NetMask, int port)
    {
        try {
            this.PeerAL = PeerAL;
            this.DevCPID = DevCPID;
            this.bSocket = true;
            this.IP = InetAddress.getByName(IP);
            this.NetMask = InetAddress.getByName(NetMask);
            this.Port = port;
        }
        catch (UnknownHostException ex) {
            bSocket=false;
        }
    }

    public Abonent(int PeerAL, int DevCPID)
    {
        this.PeerAL = PeerAL;
        this.DevCPID = DevCPID;
        this.bSocket = false;
    }

    @Override
    public String toString()
    {
        String res = "Abonent PeerAL=" + Integer.toHexString(PeerAL) + " DevCPID=" + Integer.toString(DevCPID);
        if (bSocket) {
            res += " " + Util.makeIP(IP.getAddress()) + " " + Util.makeIP(NetMask.getAddress()) + " "
                    + Integer.toString(Port);
        }
        return res;
    }

    public int tobuffer(byte[] buffer, int pos)
    {
        int start = pos;
        Util.ShortToBuff(buffer, pos, PeerAL);
        pos += 2;
        Util.ShortToBuff(buffer, pos, DevCPID);
        pos += 2;
        if (bSocket) {
            Util.IAtobuffer(buffer, pos, IP);
            pos += 4;
            Util.IAtobuffer(buffer, pos, NetMask);
            pos += 4;
            Util.ShortToBuff(buffer, pos, Port);
            pos += 2;
        }
        return pos-start;
    }

    public int frombuffer(byte[] buffer, int pos)
    {
        int start = pos;
        PeerAL = Util.ToShort(buffer, pos);
        pos += 2;
        DevCPID = Util.ToShort(buffer, pos);
        pos += 2;
        if (bSocket) {
            IP = Util.IAfrombuffer(buffer, pos);
            pos += 4;
            NetMask = Util.IAfrombuffer(buffer, pos);
            pos += 4;
            Port = Util.ToShort(buffer, pos);
            pos += 2;
        }
        return pos-start;
    }
}
