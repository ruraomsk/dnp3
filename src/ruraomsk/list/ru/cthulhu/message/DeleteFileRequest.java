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
public class DeleteFileRequest extends BaseMessage
{
        byte FlNum=1;
    Name32 FlName=new Name32();
    public DeleteFileRequest()
    {
        itsRequest();
        setId(Util.DeleteFile);
        setVersion(Util.CT_V21);
    }

    public DeleteFileRequest(byte FlNum)
    {
        itsRequest();
        setId(Util.DeleteFile);
        setVersion(Util.CT_V21);
        this.FlNum = FlNum;
    }
    public DeleteFileRequest(String FlName)
    {
        itsRequest();
        setId(Util.DeleteFile);
        setVersion(Util.CT_V21);
        FlNum=0;
        this.FlName=new Name32(FlName);
        int c=0;
    }

    @Override
    public String toString()
    {
        String res=super.toString()+" ";
        res+=((getFlNum()==0)?getFlName():Integer.toString(getFlNum()));
        
        return res; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+3]=getFlNum();
        if(getFlNum()==0){
            FlName.tobuffer(buffer,  pos+4);
        }
        return 36;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FlNum=buffer[pos+3];
        if(getFlNum()==0){
            FlName.frombuffer(buffer,  pos+4);
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
