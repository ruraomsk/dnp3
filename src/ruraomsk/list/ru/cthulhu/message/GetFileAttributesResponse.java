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
public class GetFileAttributesResponse extends BaseMessage
{
    byte FlNum=1;
    int FlAttr;

    public GetFileAttributesResponse()
    {
        itsResponse();
        setId(Util.GetFileAttributes);
        setVersion(Util.CT_V23);
    }

    public GetFileAttributesResponse(byte FlNum,int FlAttr)
    {
        itsResponse();
        setId(Util.GetFileAttributes);
        setVersion(Util.CT_V23);

        this.FlNum=FlNum;
        this.FlAttr = FlAttr;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+4]=getFlNum();
        Util.ShortToBuff(buffer, pos+5, getFlAttr());
        return 7;
    }

    @Override
    public String toString()
    {
        return super.toString()+" FlNum="+Integer.toString(getFlNum())+" FlAttr="+Integer.toHexString(getFlAttr()); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FlNum=buffer[pos+4];
        FlAttr=Util.ToShort(buffer, pos+5);
    }

    /**
     * @return the FlNum
     */
    public byte getFlNum()
    {
        return FlNum;
    }

    /**
     * @return the FlAttr
     */
    public int getFlAttr()
    {
        return FlAttr;
    }

    
}
