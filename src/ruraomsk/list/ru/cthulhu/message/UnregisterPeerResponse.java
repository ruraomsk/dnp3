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
public class UnregisterPeerResponse extends BaseMessage
{
    byte RegStage;
    int SourceData;
    public UnregisterPeerResponse()
    {
        itsResponse();
        setId(Util.UnregisterPeer);
        setVersion(Util.CT_V23);
        RegStage=1;
    }

    public UnregisterPeerResponse( int SourceData)
    {
        this.RegStage = 2;
        this.SourceData = SourceData;
    }
    
    public int getRegStage(){
        return RegStage;
    }
    @Override
    public String toString()
    {
        return super.toString()+" RegStage="+Integer.toString(RegStage)+" "+(RegStage==2?Integer.toHexString(SourceData):""); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+4]=RegStage;
        if(RegStage==1) return 5;
        Util.IntegerToBuff(buffer, pos+5, SourceData);
        return 9;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        RegStage=buffer[pos+4];
        if(RegStage==1) return;
        SourceData=Util.ToInteger(buffer, pos+5);
    }
    
}
