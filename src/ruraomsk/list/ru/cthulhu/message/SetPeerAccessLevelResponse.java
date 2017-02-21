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
public class SetPeerAccessLevelResponse extends BaseMessage
{
    byte SetALStage;
    int PeerAL;
    int SourceData;

    public SetPeerAccessLevelResponse()
    {
        itsResponse();
        setId(Util.SetPeerAccessLevel);
        setVersion(Util.CT_V23);
    }


    public void setStageOne(int PeerAL)
    {
        SetALStage=1;
        this.PeerAL = PeerAL;
    }

    public void setStageTwo(int SourceData)
    {
        SetALStage=2;
        this.SourceData = SourceData;
    }

    public int getStage()
    {
        return SetALStage;
    }

    public int getPeerAL()
    {
        return PeerAL;
    }

    public int getSourceData()
    {
        return SourceData;
    }

    @Override
    public String toString()
    {
        String res = super.toString() + " Stage=" + Integer.toString(SetALStage);
        if (SetALStage == 1) {
            res += " PeerAL=" + Integer.toHexString(PeerAL);
        }
        else {
            res += " SourceData=" + Integer.toString(SourceData);
        }
        return res;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+4]=SetALStage;
        if(SetALStage==1){
            Util.ShortToBuff(buffer, pos+5, PeerAL);
            return 7;
        } else{
            Util.IntegerToBuff(buffer, pos+5, SourceData);
            return 9;
        }
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        SetALStage=buffer[pos+3];
        if(SetALStage==1){
            PeerAL=Util.ToShort(buffer, pos+5);
        } else{
            SourceData=Util.ToInteger(buffer, pos+5);
        }        
    }
   
}
