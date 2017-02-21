/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class RegisterPeerResponse extends BaseMessage
{

    byte RegStage;
    int SourceData;

    public RegisterPeerResponse()
    {
        itsResponse();
        setId(Util.RegisterPeer);
        setVersion(Util.CT_V23);
        RegStage = 1;
    }

    public RegisterPeerResponse(int SourceData)
    {
        itsResponse();
        setId(Util.RegisterPeer);
        setVersion(Util.CT_V23);
        this.RegStage = 2;
        this.SourceData = SourceData;
    }

    public byte getRegStage()
    {
        return RegStage;
    }

    public int getSourceData()
    {
        return SourceData;
    }

    @Override
    public String toString()
    {
        return super.toString() + " RegStage=" + Integer.toString(RegStage) + (RegStage == 1 ? "" : Integer.toHexString(SourceData)); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        buffer[pos+4] = RegStage;
        if (RegStage == 1) {
            return 5;
        }
        else {
            Util.IntegerToBuff(buffer, pos+5, SourceData);
            return 9;
        }
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        RegStage = buffer[pos+4];
        if (RegStage == 2) {
            SourceData = Util.ToInteger(buffer, pos+5);
        }
    }

}
