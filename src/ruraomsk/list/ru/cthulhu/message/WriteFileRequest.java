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
public class WriteFileRequest extends BaseMessage
{
    private byte FlNum=1;
    private int DataOff=0;
    private int DataSize=0;
    private byte[] FlData=new byte[10];
    public WriteFileRequest()
    {
        itsRequest();
        setId(Util.WriteFile);
        setVersion(Util.CT_V21);
    }
    public WriteFileRequest(byte FlNum, int DataOff, byte[] FlData)
    {
        itsRequest();
        setId(Util.WriteFile);
        setVersion(Util.CT_V21);
        this.FlData=new byte[FlData.length];
        this.FlNum = FlNum;
        this.DataOff = DataOff;
        this.DataSize = FlData.length;
        System.arraycopy(FlData, 0, this.FlData, 0, FlData.length);
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+3]=getFlNum();
        Util.IntegerToBuff(buffer, pos+4, DataOff);
        Util.IntegerToBuff(buffer, pos+8, DataSize);
        System.arraycopy(FlData, 0, buffer, pos+12, DataSize);
        return getDataSize()+12;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FlNum=buffer[pos+3];
        DataOff=Util.ToInteger(buffer, pos+4);
        DataSize=Util.ToInteger(buffer, pos+8);
        FlData=new byte[DataSize];
        System.arraycopy(buffer, pos+12, FlData, 0, DataSize);
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

    /**
     * @return the FlData
     */
    public byte[] getFlData()
    {
        return FlData;
    }

}
