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
public class GetFileRecordResponse extends BaseMessage
{
    byte FlNum=1;
    Name32 FlName=new Name32();
    int FlSize;
    int FlAttr;
    long FlCrTS;
    long FlMdTS;
    long FlAcTS;
    
    
    public GetFileRecordResponse()
    {
        itsResponse();
        setId(Util.GetFileRecord);
        setVersion(Util.CT_V23);
        
    }

    public GetFileRecordResponse(byte FlNum, String FlName,int FlSize)
    {
        itsResponse();
        setId(Util.GetFileRecord);
        setVersion(Util.CT_V23);
        this.FlNum = FlNum;
        this.FlName=new Name32(FlName);
        this.FlSize = FlSize;
    }
    public void addAttr( int FlAttr, long FlCrTS, long FlMdTS, long FlAcTS){
        this.FlAttr = FlAttr;
        this.FlCrTS = FlCrTS;
        this.FlMdTS = FlMdTS;
        this.FlAcTS = FlAcTS;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FlNum=buffer[pos+4];
        FlName.frombuffer(buffer, pos+5);
        FlSize=Util.ToInteger(buffer, pos+37);
        FlAttr=Util.ToShort(buffer, pos+41);
        FlCrTS=Util.ToTime(buffer, pos+43);
        FlMdTS=Util.ToTime(buffer, pos+51);
        FlAcTS=Util.ToTime(buffer, pos+59);
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+4]=FlNum;
        FlName.tobuffer(buffer, pos+5);
        Util.IntegerToBuff(buffer,pos+37, FlSize);
        Util.ShortToBuff(buffer,pos+41, FlAttr);
        Util.TimeToBuff(buffer, pos+43, FlCrTS);
        Util.TimeToBuff(buffer, pos+51, FlMdTS);
        Util.TimeToBuff(buffer, pos+59, FlAcTS);
        return 67; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString()
    {
        String res= super.toString()+" FlNum="+Integer.toString(FlSize)+" FlAttr="+Integer.toHexString(FlAttr); //To change body of generated methods, choose Tools | Templates.
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
     * @return the FlName
     */
    public String getFlName()
    {
        return FlName.getName();
    }

    /**
     * @param FlName the FlName to set
     */
    public void setFlName(String FlName)
    {
        this.FlName = new Name32(FlName);
    }

    /**
     * @return the FlSize
     */
    public int getFlSize()
    {
        return FlSize;
    }

    /**
     * @return the FlAttr
     */
    public int getFlAttr()
    {
        return FlAttr;
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
