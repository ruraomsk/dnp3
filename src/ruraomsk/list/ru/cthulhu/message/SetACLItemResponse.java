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
public class SetACLItemResponse extends BaseMessage
{
    byte SetACLStage;
    int ACLItemID;
    int SourceData;
    public SetACLItemResponse()
    {
        itsResponse();
        setId(Util.SetACLItem);
        setVersion(Util.CT_V23);
    }
    public void setStage(int Stage, int value){
        SetACLStage=(byte)Stage;
        if(SetACLStage==1){
            ACLItemID=value;
        } else{
            SourceData=value;
        }
    }
    public int getStage(){
        return SetACLStage;
    }
    public int getItemID(){
        return ACLItemID;
    }
    public int getData(){
        return SourceData;
    }
    @Override
    public String toString()
    {
        String res= super.toString()+" Stage="+Integer.toString(SetACLStage); //To change body of generated methods, choose Tools | Templates.
        if(SetACLStage==1){
            res+=" ACLItemID="+Integer.toString(ACLItemID);
        } else{
            res+=" SourceData="+Integer.toString(SourceData);
        }
        return res;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+4]=SetACLStage;
        if(SetACLStage==1){
            Util.ShortToBuff(buffer, pos+5, ACLItemID);
            return 7;
        }
        Util.IntegerToBuff(buffer, pos+5, SourceData);
        return 9;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        SetACLStage=buffer[pos+4];
        if(SetACLStage==1){
            ACLItemID=Util.ToShort(buffer, pos+5);
            return;
        }
        SourceData=Util.ToInteger(buffer, pos+5);
    }
    
}
