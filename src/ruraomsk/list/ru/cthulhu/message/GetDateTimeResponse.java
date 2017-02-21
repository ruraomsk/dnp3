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
public class GetDateTimeResponse extends BaseMessage
{
    private long time;

    public GetDateTimeResponse()
    {
        setId(Util.GetDateTime);
        setVersion(Util.CT_V21);
        itsResponse();
    }
    

    public GetDateTimeResponse(long time)
    {
        itsResponse();
        setId(Util.GetDateTime);
        setVersion(Util.CT_V21);
        this.time=time;
    }
    public long getTime(){
        return time;
    }
    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        time=Util.ToTime(buffer,pos+4);
    }

    @Override
    public String toString()
    {
        return super.toString()+" Время="+Util.dateToStr(time); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer,pos);
        Util.TimeToBuff(buffer, pos+4, time);
        return 12;
    }
}
