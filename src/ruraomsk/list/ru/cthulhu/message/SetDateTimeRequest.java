/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import java.util.Calendar;
import java.util.GregorianCalendar;
import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class SetDateTimeRequest extends BaseMessage
{
    GregorianCalendar datetime;
//int Year;
//int Month;
//int Day;
//int Hour;
//int Minute;
//int Second;
//int Millisecond;

    public SetDateTimeRequest()
    {
        itsRequest();
        setId(Util.SetDateTime);
        setVersion(Util.CT_V21);
        datetime= new GregorianCalendar();
    }

    public SetDateTimeRequest(GregorianCalendar datetime)
    {
        itsRequest();
        setId(Util.SetDateTime);
        setVersion(Util.CT_V21);
        this.datetime = datetime;
    }
    public GregorianCalendar getdatetime(){
        return datetime;
    }

    @Override
    public String toString()
    {
        return super.toString()+" "+Integer.toString(datetime.get(Calendar.YEAR))
                +"."+Integer.toString(datetime.get(Calendar.MONTH)+1)
                +"."+Integer.toString(datetime.get(Calendar.DAY_OF_MONTH))
                +" "+Integer.toString(datetime.get(Calendar.HOUR_OF_DAY))
                +":"+Integer.toString(datetime.get(Calendar.MINUTE))
                +":"+Integer.toString(datetime.get(Calendar.SECOND));
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        Util.ShortToBuff(buffer, pos+3, datetime.get(Calendar.YEAR));
        buffer[pos+5]=(byte) (datetime.get(Calendar.MONTH)+1);
        buffer[pos+6]=(byte) datetime.get(Calendar.DAY_OF_MONTH);
        buffer[pos+7]=(byte) datetime.get(Calendar.HOUR_OF_DAY);
        buffer[pos+8]=(byte) datetime.get(Calendar.MINUTE);
        buffer[pos+9]=(byte) datetime.get(Calendar.SECOND);
        Util.ShortToBuff(buffer, pos+10, datetime.get(Calendar.MILLISECOND));
        return 12;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        datetime.set(   (int)Util.ToShort(buffer, pos+3),(int)buffer[pos+5]-1, (int)buffer[pos+6],
                        (int)buffer[pos+7],(int)buffer[pos+8],(int)buffer[pos+9]);
    }
}
