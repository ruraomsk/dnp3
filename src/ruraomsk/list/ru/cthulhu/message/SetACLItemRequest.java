/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class SetACLItemRequest extends BaseMessage
{

    byte SetACLStage;
    int AL;
    byte TerminalType;
    InetAddress NSIP;
    InetAddress NSNetMask;
    int NSPort;
    int COMNumber;
    int SourceData;
    int ConvertData;

    public SetACLItemRequest()
    {
        itsRequest();
        setId(Util.SetACLItem);
        setVersion(Util.CT_V23);
    }

    public void setStageOneIP(int AL, String NSIP, String NSNetMask, int NSPort)
    {
        try {
            SetACLStage = 1;
            TerminalType = 0x11;
            this.NSIP = InetAddress.getByName(NSIP);
            this.NSNetMask = InetAddress.getByName(NSNetMask);
            this.NSPort = NSPort;
        }
        catch (UnknownHostException ex) {
            TerminalType = 0x21;
        }
    }

    public void setStageOne(int Al, int COMNumber)
    {
        SetACLStage = 1;
        TerminalType = 0x21;
        this.COMNumber = COMNumber;
    }

    public void setStageTwo(int SourceData, int ConvertedData)
    {
        SetACLStage = 2;
        this.SourceData = SourceData;
        ConvertData = ConvertedData;
    }

    @Override
    public String toString()
    {
        String res = super.toString() + " AL=" + Integer.toHexString(AL) + " TerminalType=" + Integer.toHexString(TerminalType); //To change body of generated methods, choose Tools | Templates.
        if (SetACLStage == 1) {
            if (TerminalType == 0x11) {
                res += " " + Util.makeIP(NSIP.getAddress());
                res += " " + Util.makeIP(NSNetMask.getAddress());
                res += " " + Integer.toString(NSPort);
            }
            else {
                res += " COMNumber=" + Integer.toString(COMNumber);
            }
        }
        else {
            res += " Source=" + Integer.toString(SourceData);
            res += " Converted=" + Integer.toString(ConvertData);
        }
        return res;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        buffer[pos+3]=SetACLStage;
        if(SetACLStage==1){
            Util.ShortToBuff(buffer, pos+4, AL);
            buffer[pos+6]=TerminalType;
            if(TerminalType==0x11){
                Util.IAtobuffer(buffer, pos+7, NSIP);
                Util.IAtobuffer(buffer, pos+11, NSNetMask);
                Util.ShortToBuff(buffer, pos+15, NSPort);
                return 17;
            }
            Util.ShortToBuff(buffer, pos+7, COMNumber);
            return 9;
        }
        Util.IntegerToBuff(buffer, pos+4, SourceData);
        Util.IntegerToBuff(buffer, pos+8, ConvertData);
        return 12;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        SetACLStage=buffer[pos+3];
        if(SetACLStage==1){
            AL=Util.ToShort(buffer, pos+4);
            TerminalType=buffer[pos+6];
            if(TerminalType==0x11){
                NSIP=Util.IAfrombuffer(buffer, pos+7);
                NSNetMask=Util.IAfrombuffer(buffer, pos+11);
                NSPort=Util.ToShort(buffer, pos+15);
                return;
            }
            COMNumber=Util.ToShort(buffer, pos+7);
            return;
        }
        SourceData=Util.ToInteger(buffer, pos+4);
        ConvertData=Util.ToInteger(buffer, pos+8);
    }

}
