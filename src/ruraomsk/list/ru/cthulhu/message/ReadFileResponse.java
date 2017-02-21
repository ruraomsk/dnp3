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
public class ReadFileResponse extends BaseMessage
{
    private int FlNum=1;
    private int DataOff=0;
    private int DataSize=0;
    private byte[] FlData=new byte[10];
    public ReadFileResponse()
    {
        itsResponse();
        setId(Util.ReadFile);
        setVersion(Util.CT_V21);
    }
    public ReadFileResponse(int FlNum, int DataOff, byte[] FlData)
    {
        itsResponse();
        setId(Util.ReadFile);
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
        buffer[pos+4]= (byte) (FlNum&0xff);
        Util.IntegerToBuff(buffer, pos+5, DataOff);
        Util.IntegerToBuff(buffer, pos+9, DataSize);
        System.arraycopy(FlData, 0, buffer, pos+13, DataSize);
        return getDataSize()+13;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
//        Util.bufferToString(buffer, pos, len);
        FlNum=buffer[pos+4]&0xff;
        DataOff=Util.ToInteger(buffer, pos+5);
        DataSize=Util.ToInteger(buffer, pos+9);
        FlData=new byte[DataSize];
        System.arraycopy(buffer, pos+13, FlData, 0, DataSize);
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
    public Integer getFlNum()
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
