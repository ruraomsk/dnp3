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
public class ACLItem
{

    int ACLItemID;
    int ACLItemAL;
    byte TerminalType;
    InetAddress ACLItemIP;
    InetAddress ACLItemNetMask;
    int ACLItemPort;
    int COMNumber;

    public ACLItem()
    {
    }

    public ACLItem(int ACLItemID, int ACLItemAL, String ACLItemIP, String ACLItemNetMask, int ACLItemPort)
    {
        try {
            this.ACLItemID = ACLItemID;
            this.ACLItemAL = ACLItemAL;
            this.TerminalType = 0x11;
            this.ACLItemIP = InetAddress.getByName(ACLItemIP);
            this.ACLItemNetMask = InetAddress.getByName(ACLItemNetMask);
            this.ACLItemPort = ACLItemPort;
        }
        catch (UnknownHostException ex) {
            TerminalType = 0x21;
        }
    }

    public ACLItem(int ACLItemID, int ACLItemAL, int COMNumber)
    {
        this.ACLItemID = ACLItemID;
        this.ACLItemAL = ACLItemAL;
        this.TerminalType = 0x21;
        this.COMNumber = COMNumber;
    }

    @Override
    public String toString()
    {
        String res = "ACLItem ID=" + Integer.toString(ACLItemID) + " AL=" + Integer.toString(ACLItemAL) + " ";
        if (TerminalType == 0x11) {
            res += Util.makeIP(ACLItemIP.getAddress()) + " " + Util.makeIP(ACLItemNetMask.getAddress()) + " "
                    + Integer.toString(ACLItemPort);
        }
        else {
            res += Integer.toString(COMNumber);
        }
        return res; //To change body of generated methods, choose Tools | Templates.
    }

    public int tobuffer(byte[] buffer, int pos)
    {
        int start = pos;
        Util.ShortToBuff(buffer, pos, ACLItemID);
        pos += 2;
        Util.ShortToBuff(buffer, pos, ACLItemAL);
        pos += 2;
        buffer[pos++] = TerminalType;
        if (TerminalType == 0x11) {
            Util.IAtobuffer(buffer, pos, ACLItemIP);
            pos += 4;
            Util.IAtobuffer(buffer, pos, ACLItemNetMask);
            pos += 4;
            Util.ShortToBuff(buffer, pos, ACLItemPort);
            pos += 2;

        }
        else {
            Util.ShortToBuff(buffer, pos, COMNumber);
            pos += 2;
        }
        return pos - start;
    }

    public int frombuffer(byte[] buffer, int pos)
    {
        int start = pos;
        ACLItemID = Util.ToShort(buffer, pos);
        pos += 2;
        ACLItemAL = Util.ToShort(buffer, pos);
        pos += 2;
        TerminalType = buffer[pos++];
        if (TerminalType == 0x11) {
            ACLItemIP = Util.IAfrombuffer(buffer, pos);
            pos += 4;
            ACLItemNetMask = Util.IAfrombuffer(buffer, pos);
            pos += 4;
            ACLItemPort = Util.ToShort(buffer, pos);
            pos += 2;
        }
        else {
            COMNumber = Util.ToShort(buffer, pos);
            pos += 2;
        }
        return pos - start;
    }
}
