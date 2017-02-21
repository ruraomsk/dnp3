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
public class SetPeerAccessLevelRequest extends BaseMessage
{

    byte SetALStage;
    int PeerID;
    int PeerAL;
    int SourceData;
    int ConvertedData;

    public SetPeerAccessLevelRequest()
    {
        itsRequest();
        setId(Util.SetPeerAccessLevel);
        setVersion(Util.CT_V23);
    }

    public void setStageOne(int PeerID, int PeerAL)
    {
        SetALStage=1;
        this.PeerID = PeerID;
        this.PeerAL = PeerAL;
    }

    public void setStageTwo(int SourceData, int ConvertData)
    {
        SetALStage=2;
        this.SourceData = SourceData;
        ConvertedData = ConvertData;
    }

    public int getStage()
    {
        return SetALStage;
    }

    public int getPeerID()
    {
        return PeerID;
    }

    public int getPeerAL()
    {
        return PeerAL;
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
        String res = super.toString() + " Stage=" + Integer.toString(SetALStage);
        if (SetALStage == 1) {
            res += " PeerID=" + Integer.toString(PeerID)
                    + " PeerAL=" + Integer.toHexString(PeerAL);
        }
        else {
            res += " SourceData=" + Integer.toString(SourceData) + " ConvertedData=" + Integer.toString(ConvertedData);
        }
        return res;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+3]=SetALStage;
        if(SetALStage==1){
            Util.ShortToBuff(buffer, pos+4, PeerID);
            Util.ShortToBuff(buffer, pos+6, PeerAL);
            return 8;
        } else{
            Util.IntegerToBuff(buffer, pos+4, SourceData);
            Util.IntegerToBuff(buffer, pos+8, SourceData);
            return 12;
        }
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        SetALStage=buffer[pos+3];
        if(SetALStage==1){
            PeerID=Util.ToShort(buffer, pos+4);
            PeerAL=Util.ToShort(buffer, pos+6);
        } else{
            SourceData=Util.ToInteger(buffer, pos+4);
            ConvertedData=Util.ToInteger(buffer, pos+4);
        }        
    }

}
