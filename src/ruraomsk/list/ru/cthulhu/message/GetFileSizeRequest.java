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
public class GetFileSizeRequest extends BaseMessage
{
    private int FlNum=1;
    private Name32 FlName=new Name32();

    public GetFileSizeRequest()
    {
        itsRequest();
        setId(Util.GetFileSize);
        setVersion(Util.CT_V22);
    }

    public GetFileSizeRequest(int FlNum)
    {
        itsRequest();
        setId(Util.GetFileSize);
        setVersion(Util.CT_V22);
        this.FlNum = FlNum;
        this.FlName=new Name32("");
        
    }
    public GetFileSizeRequest(int FlNum,String FlName)
    {
        itsRequest();
        setId(Util.GetFileSize);
        setVersion(Util.CT_V22);
        this.FlNum = FlNum;
        this.FlName=new Name32(FlName);
    }
    @Override
    public String toString()
    {
        String res=super.toString()+" ";
        res+=Integer.toString(FlNum)+":"+FlName.getName();
        
        return res; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        
        buffer[pos+3]=(byte) (getFlNum()&0xff);
        Util.clearbuffer(buffer, pos+4, 32);
//        if(getFlNum()>=100){
//            buffer[pos+3]=0;
            FlName.tobuffer(buffer,  pos+4);
//        }
//        Util.bufferToString(buffer, pos, pos+36);
        return 36;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FlNum=buffer[pos+3]&0xff;
//        if(getFlNum()==0){
            FlName.frombuffer(buffer, pos+4);
//        }
    }

    /**
     * @return the FlNum
     */
    public int getFlNum()
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
