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
public class RemoveACLItemRequest extends BaseMessage
{
    byte RmACLStage;
    int ACLItemID;
    int SourceDara;
    int ConvertedData;
    
    public RemoveACLItemRequest()
    {
        itsRequest();
        setId(Util.RemoveACLItem);
        setVersion(Util.CT_V23);
    }

    public RemoveACLItemRequest( int ACLItemID)
    {
        itsRequest();
        setId(Util.RemoveACLItem);
        setVersion(Util.CT_V23);
        this.RmACLStage = 1;
        this.ACLItemID = ACLItemID;
    }
    public RemoveACLItemRequest( int SourceDara, int ConvertedData)
    {
        itsRequest();
        setId(Util.RemoveACLItem);
        setVersion(Util.CT_V23);
        this.RmACLStage = 2;
        this.SourceDara = SourceDara;
        this.ConvertedData = ConvertedData;
    }
    public int getStage(){
        return RmACLStage;
    } 
    public int getItemID(){
        return ACLItemID;
    }
    public int getSource(){
        return SourceDara;
    }
    public int getConverted(){
        return ConvertedData;
    }
    @Override
    public String toString()
    {
        String res=super.toString()+" Stage="+Integer.toString(RmACLStage); //To change body of generated methods, choose Tools | Templates.
        if(RmACLStage==1){
            res+=" ACLItemID="+Integer.toString(ACLItemID);
        }else{
            res+=" SourceData="+Integer.toHexString(SourceDara);
            res+=" ConvertedData="+Integer.toHexString(ConvertedData);
        }
        return res;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+3]=RmACLStage;
        if(RmACLStage==1){
            Util.ShortToBuff(buffer,pos+4,ACLItemID);
            return 6;
        }else{
            Util.IntegerToBuff(buffer, pos+4, SourceDara);
            Util.IntegerToBuff(buffer, pos+8, ConvertedData);
            return 12;
        }
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        RmACLStage=buffer[pos+3];
        if(RmACLStage==1){
            ACLItemID=Util.ToShort(buffer,pos+4);
        }else{
            SourceDara=Util.ToShort(buffer,pos+4);
            ConvertedData=Util.ToShort(buffer,pos+8);
        }
    }

}
