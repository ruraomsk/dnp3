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
public class GetFileTimestampsResponse extends BaseMessage
{
    byte FlNum=1;
    long FlCrTS;
    long FlMdTS;
    long FlAcTS;
    
    
    public GetFileTimestampsResponse()
    {
        itsResponse();
        setId(Util.GetFileTimestamps);
        setVersion(Util.CT_V23);
    }

    public GetFileTimestampsResponse(byte FlNum, long FlCrTS, long FlMdTS, long FlAcTS)
    {
        itsResponse();
        setId(Util.GetFileTimestamps);
        setVersion(Util.CT_V23);
        this.FlNum = FlNum;
        this.FlCrTS = FlCrTS;
        this.FlMdTS = FlMdTS;
        this.FlAcTS = FlAcTS;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FlNum=buffer[pos+4];
        FlCrTS=Util.ToTime(buffer, pos+5);
        FlMdTS=Util.ToTime(buffer, pos+13);
        FlAcTS=Util.ToTime(buffer, pos+21);
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+4]=FlNum;
        Util.TimeToBuff(buffer, pos+5, FlCrTS);
        Util.TimeToBuff(buffer, pos+13, FlMdTS);
        Util.TimeToBuff(buffer, pos+21, FlAcTS);
        return 29; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString()
    {
        String res= super.toString()+" FlNum="+Integer.toString(FlNum)+
                " FlCrTS="+Long.toString(FlCrTS)+
                " FlMdTS="+Long.toString(FlMdTS)+
                " FlAcTS="+Long.toString(FlAcTS); //To change body of generated methods, choose Tools | Templates.
        return res;
    }
    
    /**
     * @return the FlNum
     */
    public byte getFlNum()
    {
        return FlNum;
    }

    /**
     * @param FlNum the FlNum to set
     */
    public void setFlNum(byte FlNum)
    {
        this.FlNum = FlNum;
    }


    /**
     * @return the FlCrTS
     */
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
