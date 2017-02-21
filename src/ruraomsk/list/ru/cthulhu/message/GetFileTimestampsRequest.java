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
public class GetFileTimestampsRequest extends BaseMessage
{
    private byte FlNum=1;
    private Name32 FlName=new Name32();
    public GetFileTimestampsRequest()
    {
        itsRequest();
        setId(Util.GetFileTimestamps);
        setVersion(Util.CT_V23);
    }

    public GetFileTimestampsRequest(byte FlNum)
    {
        itsRequest();
        setId(Util.GetFileTimestamps);
        setVersion(Util.CT_V23);
        this.FlNum = FlNum;
    }
    public GetFileTimestampsRequest(String FlName)
    {
        itsRequest();
        setId(Util.GetFileTimestamps);
        setVersion(Util.CT_V23);
        this.FlNum = 0;
        this.FlName=new Name32(FlName);
    }    
    @Override
    public String toString()
    {
        String res=super.toString()+" ";
        res+=((getFlNum()==0)?FlName.getName():Integer.toString(FlNum));
        
        return res; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+3]=getFlNum();
        if(getFlNum()==0)FlName.tobuffer(buffer, pos+4);
        return 36;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FlNum=buffer[pos+3];
        if(getFlNum()==0){
            FlName.frombuffer(buffer, pos+4);
        }
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

}
