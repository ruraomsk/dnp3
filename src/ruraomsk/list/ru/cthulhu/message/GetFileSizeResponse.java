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
public class GetFileSizeResponse extends BaseMessage
{
    int FlNum;
    int FlSize;
    public GetFileSizeResponse()
    {
        itsResponse();
        setId(Util.GetFileSize);
        setVersion(Util.CT_V22);
    }

    public GetFileSizeResponse(byte FlNum, int FlSize)
    {
        itsResponse();
        setId(Util.GetFileSize);
        setVersion(Util.CT_V22);
        this.FlNum = FlNum;
        this.FlSize = FlSize;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+4]=(byte) (FlNum&0xff);
        Util.IntegerToBuff(buffer, pos+5, FlSize);
        return 7;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FlNum= buffer[pos+4]&0xff;
        FlSize=Util.ToInteger(buffer, pos+5);
    }

    @Override
    public String toString()
    {
        return super.toString()+" FlNum="+Integer.toString(FlNum)+" FlSize="+Integer.toString(FlSize); //To change body of generated methods, choose Tools | Templates.
    }
    
    public int getFlNum(){
        return FlNum;
    }
    public int getFlSize(){
        return FlSize;
    }
    
}
