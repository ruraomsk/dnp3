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
public class CommitFileRequest extends BaseMessage
{
    byte FlNum=1;
    Name32 FlName=new Name32();
    int DataOff;
    public CommitFileRequest()
    {
        itsRequest();
        setId(Util.CommitFile);
        setVersion(Util.CT_V21);
    }

    public CommitFileRequest(byte FlNum, int DataOff)
    {
        itsRequest();
        setId(Util.CommitFile);
        setVersion(Util.CT_V21);
        this.FlNum = FlNum;
        this.DataOff = DataOff;
    }
    public CommitFileRequest(String FlName, int DataOff)
    {
        itsRequest();
        setId(Util.CommitFile);
        setVersion(Util.CT_V21);
        FlNum=0;
        this.FlName=new Name32(FlName);
        this.DataOff = DataOff;
    }

    @Override
    public String toString()
    {
        String res=super.toString()+" ";
        res+=((getFlNum()==0)?String.valueOf(getFlName()):Integer.toString(getFlNum()))+
                " offset="+Integer.toString(getDataOff());
        
        return res; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+3]=getFlNum();
        if(getFlNum()==0){
            FlName.tobuffer(buffer, pos+ 4);
        }
        Util.IntegerToBuff(buffer, pos+36, getDataOff());
        return 40;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FlNum=buffer[pos+3];
        if(getFlNum()==0){
            FlName.frombuffer(buffer,  pos+4);
        }
        DataOff=Util.ToInteger(buffer, pos+36);
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
    
}
