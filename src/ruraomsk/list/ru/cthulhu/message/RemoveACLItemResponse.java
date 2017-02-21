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
public class RemoveACLItemResponse extends BaseMessage
{
    byte RmACLStage;
    int SourceData;
    public RemoveACLItemResponse()
    {
        itsResponse();
        setId(Util.RemoveACLItem);
        setVersion(Util.CT_V23);
    }
    public void setStageOne(){
        RmACLStage=1;
    }
    public void setStageTwo(int SourceData){
        RmACLStage=2;
        this.SourceData=SourceData;
    }
    @Override
    public String toString()
    {
        return super.toString()+" Stage="+Integer.toString(RmACLStage)+(RmACLStage==2?" SourceData="+Integer.toHexString(SourceData):""); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+4]=RmACLStage;
        if(RmACLStage==1) return 5;
        Util.IntegerToBuff(buffer, pos+5, SourceData);
        return 9;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        RmACLStage=buffer[pos+4];
        if(RmACLStage==1) return;
        SourceData=Util.ToInteger(buffer, pos+5);
    }
}
