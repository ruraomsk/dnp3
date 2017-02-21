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
public class GetDirectoryContentsRequest extends BaseMessage
{
    Name32 DirName=new Name32();
    int FlInd=0;

    public GetDirectoryContentsRequest()
    {   
        itsRequest();
        setId(Util.GetDirectoryContents);
        setVersion(Util.CT_V23);
    }
    public GetDirectoryContentsRequest(String DirName,int FlInd)
    {   
        itsRequest();
        setId(Util.GetDirectoryContents);
        setVersion(Util.CT_V23);
        this.DirName=new Name32(DirName);
        this.FlInd=FlInd;
    }

    @Override
    public String toString()
    {
        return super.toString()+" DirName="+DirName.getName()+
                " FlInd="+Integer.toString(FlInd); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        DirName.tobuffer(buffer, pos+3);
        Util.ShortToBuff(buffer, pos+35, FlInd);
        return 37; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        DirName.frombuffer(buffer, pos+3);
        FlInd=Util.ToShort(buffer, pos+35); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
