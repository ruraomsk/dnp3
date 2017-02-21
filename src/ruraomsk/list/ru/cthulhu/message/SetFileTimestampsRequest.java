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
public class SetFileTimestampsRequest extends BaseMessage
{

    private byte FlNum = 0;
    private Name32 FlName = new Name32();
    long FlCrTS;
    long FlMdTS;
    long FlAcTS;

    public SetFileTimestampsRequest()
    {
        itsRequest();
        setId(Util.SetFileTimestamps);
        setVersion(Util.CT_V23);
    }

    public SetFileTimestampsRequest(byte FlNum)
    {
        itsRequest();
        setId(Util.SetFileTimestamps);
        setVersion(Util.CT_V23);
        this.FlNum = FlNum;
    }

    public SetFileTimestampsRequest(String FlName)
    {
        itsRequest();
        setId(Util.SetFileTimestamps);
        setVersion(Util.CT_V23);
        this.FlNum = 0;
        this.FlName=new Name32(FlName);
    }

    public void addTimestamps(long FlCrTS, long FlMdTS, long FlAcTS)
    {
        this.FlCrTS = FlCrTS;
        this.FlMdTS = FlMdTS;
        this.FlAcTS = FlAcTS;
    }

    @Override
    public String toString()
    {
        String res = super.toString() + " ";
        res += ((getFlNum() == 0) ? FlName.getName() : Integer.toString(FlNum))
                + " FlCrTS=" + Long.toString(FlCrTS)
                + " FlMdTS=" + Long.toString(FlMdTS)
                + " FlAcTS=" + Long.toString(FlAcTS); //To change body of generated methods, choose Tools | Templates.

        return res; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+3] = getFlNum();
        if (getFlNum() == 0) {
            FlName.tobuffer(buffer, pos+4);
        }
        Util.TimeToBuff(buffer, pos+36, FlCrTS);
        Util.TimeToBuff(buffer, pos+44, FlMdTS);
        Util.TimeToBuff(buffer, pos+52, FlAcTS);
        return 60;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FlNum = buffer[pos+3];
        if (getFlNum() == 0) {
            FlName.frombuffer(buffer, pos+4);
        }
        FlCrTS = Util.ToTime(buffer, pos+36);
        FlMdTS = Util.ToTime(buffer, pos+44);
        FlAcTS = Util.ToTime(buffer, pos+52);
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

    public long getFlCrTS()
    {
        return FlCrTS;
    }

    /**
     * @return the FlMdTS
     */
    public long getFlMdTS()
    {
        return FlMdTS;
    }

    /**
     * @return the FlAcTS
     */
    public long getFlAcTS()
    {
        return FlAcTS;
    }

}
