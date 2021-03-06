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
public class SetFileTimestampsResponse extends BaseMessage
{
            byte FlNum;

    public SetFileTimestampsResponse()
    {
        itsResponse();
        setId(Util.SetFileTimestamps);
        setVersion(Util.CT_V23);
    }

    public SetFileTimestampsResponse(byte FlNum)
    {
        itsResponse();
        setId(Util.SetFileTimestamps);
        setVersion(Util.CT_V23);
        this.FlNum = FlNum;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+4]=getFlNum();
        return 5;
    }

    @Override
    public String toString()
    {
        return super.toString()+" FlNum="+Integer.toString(getFlNum()); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FlNum=buffer[pos+4];
    }

    /**
     * @return the FlNum
     */
    public byte getFlNum()
    {
        return FlNum;
    }

}
