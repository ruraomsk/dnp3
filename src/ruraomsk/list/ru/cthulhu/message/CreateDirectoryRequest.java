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
public class CreateDirectoryRequest extends BaseMessage
{
    Name32 DirName= new Name32();
    public CreateDirectoryRequest()
    {
        itsRequest();
        setId(Util.CreateDirectory);
        setVersion(Util.CT_V23);
    }
    public CreateDirectoryRequest(String DirName)
    {
        itsRequest();
        setId(Util.CreateDirectory);
        setVersion(Util.CT_V23);
        this.DirName=new Name32(DirName);
    }

    @Override
    public String toString()
    {
        return super.toString()+" DirName="+DirName.getName(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        DirName.tobuffer(buffer, pos+3);
        return 35; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        DirName.frombuffer(buffer, pos+3);
    }
    
}
