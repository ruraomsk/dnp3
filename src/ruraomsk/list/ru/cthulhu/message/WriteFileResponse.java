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
public class WriteFileResponse extends BaseMessage
{
    private byte FlNum=1;
    private int DataOff=0;
    private int DataSize=0;

    public WriteFileResponse()
    {
        itsResponse();
        setId(Util.WriteFile);
        setVersion(Util.CT_V21);
    }
    public WriteFileResponse(byte FlNum, int DataOff,int DataSize)
    {
        itsResponse();
        setId(Util.WriteFile);
        setVersion(Util.CT_V21);
        this.FlNum = FlNum;
        this.DataOff = DataOff;
        this.DataSize = DataSize;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+4]=getFlNum();
        Util.IntegerToBuff(buffer, pos+5, DataOff);
        Util.IntegerToBuff(buffer, pos+9, DataSize);
        return 13;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FlNum=buffer[pos+4];
        DataOff=Util.ToInteger(buffer, pos+5);
        DataSize=Util.ToInteger(buffer, pos+9);
    }

    @Override
    public String toString()
    {
        String res=super.toString()+" FlNum="+Integer.toString(getFlNum())+" DataOff="+Integer.toString(getDataOff())+
                " DataSize="+Integer.toString(getDataSize());
        return  res;//To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the FlNum
     */
    public byte getFlNum()
    {
        return FlNum;
    }

    /**
     * @return the DataOff
     */
    public int getDataOff()
    {
        return DataOff;
    }

    /**
     * 
     * @return the DataSize
     */
    public int getDataSize()
    {
        return DataSize;
    }
    
}
