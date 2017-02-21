/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import java.util.ArrayList;
import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Name32;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class GetDirectoryContentsResponse extends BaseMessage
{
    ArrayList<FileDescription> afd;
    public GetDirectoryContentsResponse()
    {
        itsResponse();
        setId(Util.GetDirectoryContents);
        setVersion(Util.CT_V23);
        afd=new ArrayList<>();
    }
    public void addDescription(FileDescription fs){
        afd.add(fs);
    }
    public ArrayList<FileDescription> getDescriptions(){
        return afd;
    }

    @Override
    public String toString()
    {
        String res= super.toString()+"\n";
        for(FileDescription fd:afd){
            res+=fd.toString()+" ";
        }
        return res;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        int start=pos;
        makeHeader(buffer, pos);
        pos=pos+4;
        for(FileDescription fd:afd){
            pos+=fd.tobuffer(buffer, pos);
        }
        return pos-start; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        pos=pos+4;
        while((pos+53)<=len){
            FileDescription fd=new FileDescription();
            pos+=fd.frombuffer(buffer, pos);
            afd.add(fd);
        }
    }
    public  class FileDescription
    {
        byte FlNum;
        Name32 FlName=new Name32();
        int FlSize;
        int FlAttr;
        long FlCrTS;
        long FlMdTS;
        long FlAcTS;
        public FileDescription(){
        }
        public FileDescription(byte FlNum,Name32 FlName ,int FlSize,int FlAttr)
        {
            this.FlNum=FlNum;
            this.FlName=FlName;
            this.FlSize=FlSize;
            this.FlAttr=FlAttr;
        }
        public void addTimes(long FlCrTS,long FlMdTS,long FlAcTS){
            this.FlCrTS=FlCrTS;
            this.FlMdTS=FlMdTS;
            this.FlAcTS=FlAcTS;
        }

        @Override
        public String toString()
        {
            return "["+Integer.toString(FlNum)+" "+FlName.getName()+" FlSize="+Integer.toString(FlSize)
                        +" FlAttr="+Integer.toHexString(FlAttr)
                        +" FlCrTs="+Long.toString(FlCrTS)
                        +" FlMdTs="+Long.toString(FlMdTS)
                        +" FlAcTs="+Long.toString(FlAcTS);
        }
        public int tobuffer(byte[] buffer, int pos){
            int start=pos;
            buffer[pos++]=FlNum;
            pos+=FlName.tobuffer(buffer, pos);
            Util.IntegerToBuff(buffer, pos, FlSize);
            pos+=4;
            Util.ShortToBuff(buffer, pos, FlAttr);
            pos+=2;
            Util.TimeToBuff(buffer, pos, FlCrTS);
            pos+=8;
            Util.TimeToBuff(buffer, pos, FlMdTS);
            pos+=8;
            Util.TimeToBuff(buffer, pos, FlAcTS);
            pos+=8;
            return pos-start;
        }
        public int frombuffer(byte[] buffer, int pos){
            int start=pos;
            FlNum=buffer[pos++];
            pos+=FlName.frombuffer(buffer, pos);
            FlSize=Util.ToInteger(buffer, pos);
            pos+=4;
            FlAttr=Util.ToShort(buffer, pos);
            pos+=2;
            FlCrTS=Util.ToTime(buffer, pos);
            pos+=8;
            FlMdTS=Util.ToTime(buffer, pos);
            pos+=8;
            FlAcTS=Util.ToTime(buffer, pos);
            pos+=8;
            return pos-start;
        }
    }
    
}
