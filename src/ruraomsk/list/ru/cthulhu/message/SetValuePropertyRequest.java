/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import java.util.ArrayList;
import java.util.Date;
import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.OneReg;
import ruraomsk.list.ru.cthulhu.Register;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class SetValuePropertyRequest extends BaseMessage
{
    ArrayList<OneReg> values;

    public SetValuePropertyRequest()
    {
        itsRequest();
        setId(Util.SetValueProperty);
        values=new ArrayList<>();
    }
    public void addValue(OneReg oreg){
        values.add(oreg);
    }
    public ArrayList<OneReg> getValues(){
        return values;
    }
    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        int posstart=pos;
        pos+=makeHeader(buffer, pos);
        int pos1=pos;
        pos+=2;
        for(OneReg oreg:values){
            pos+=oreg.toBuffer(buffer, pos);
        }
        Util.ShortToBuff(buffer, pos1, pos-pos1-2);
        return pos-posstart;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        pos=pos+3;
        int lendata=Util.ToShort(buffer, pos);
        pos+=2;
        while(lendata>0){
            int uId=Util.ToShort(buffer, pos);
            pos+=2;
            lendata-=2;
            OneReg or=getRegs().getOneReg(getController(), uId);
            Register reg=or.getReg();
            OneReg oreg=new OneReg(System.currentTimeMillis(),reg);
            int l=oreg.getBuffer(buffer, pos);
            oreg.setGood(Util.CT_DATA_GOOD);
            values.add(oreg);
            pos+=l;
            lendata-=l;
        }
    }
    
    @Override
    public String toString()
    {
        String result=super.toString()+"[";
        for(OneReg oreg:values){
            result+=oreg.toString()+" ";
        }
        return result+"\n";
    }
}
