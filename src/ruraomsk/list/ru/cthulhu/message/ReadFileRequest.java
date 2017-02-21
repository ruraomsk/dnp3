/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Name32;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class ReadFileRequest extends BaseMessage
{
    byte FlNum=1;
    Name32 FlName=new Name32("");
    int DataOff;
    int DataSize;
    public ReadFileRequest()
    {
        itsRequest();
        setId(Util.ReadFile);
        setVersion(Util.CT_V21);
    }

    public ReadFileRequest(int FlNum, int DataOff, int DataSize)
    {
        itsRequest();
        setId(Util.ReadFile);
        setVersion(Util.CT_V21);
        this.FlNum = (byte)FlNum;
        this.DataOff = DataOff;
        this.DataSize = DataSize;
        if(FlNum<1||FlNum>99){
            FlName=new Name32(Util.makeNameFile(FlNum));
        }
    }
    public ReadFileRequest(int FlNum,String FlName, int DataOff, int DataSize)
    {
        itsRequest();
        setId(Util.ReadFile);
        setVersion(Util.CT_V21);
        this.FlNum=(byte) FlNum;
        this.FlName=new Name32(FlName);
        this.DataOff = DataOff;
        this.DataSize = DataSize;
    }

    @Override
    public String toString()
    {
        String res=super.toString()+" ";
        res+=((getFlNum()==0)?getFlName():Integer.toString(getFlNum()))+
                " offset="+Integer.toString(getDataOff())+" size="+Integer.toString(getDataSize());
        
        return res; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+3]=getFlNum();
        Util.clearbuffer(buffer, pos+4, 32);
        if(getFlNum()>=100){
            FlName.tobuffer(buffer, pos+4);
        }
        Util.IntegerToBuff(buffer, pos+36, getDataOff());
        Util.IntegerToBuff(buffer, pos+40, getDataSize());
        return 44;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FlNum=buffer[pos+3];
        if(getFlNum()==0){
            FlName.frombuffer(buffer,pos+4);
        }
        DataOff=Util.ToInteger(buffer, pos+36);
        DataSize=Util.ToInteger(buffer, pos+40);
    }

    /**
     * @return the FlNum
     */
    public byte getFlNum()
    {
        return FlNum;
    }

    /**
     * @return the FlName
     */
    public String getFlName()
    {
        return FlName.getName();
    }

    /**
     * @return the DataOff
     */
    public int getDataOff()
    {
        return DataOff;
    }

    /**
     * @return the DataSize
     */
    public int getDataSize()
    {
        return DataSize;
    }
}
