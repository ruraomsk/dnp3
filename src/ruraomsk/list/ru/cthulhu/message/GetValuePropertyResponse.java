/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import com.tibbo.aggregate.common.Log;
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
public class GetValuePropertyResponse extends BaseMessage
{

    ArrayList<OneReg> pars = new ArrayList<>();
    long time;
    public GetValuePropertyResponse()
    {
        setId(Util.GetValueProperty);
        itsResponse();
        setVersion(Util.CT_V21);
        time=System.currentTimeMillis();
    }
    public GetValuePropertyResponse(long time)
    {
        this.time=time;
        setId(Util.GetValueProperty);
        itsResponse();
        setVersion(Util.CT_V21);
    }

    public void addValue(OneReg value)
    {
        pars.add(value);
    }


    public OneReg[] getValues()
    {
        OneReg[] aa = new OneReg[pars.size()];
        pars.toArray(aa);
        return aa;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        
        int startpos = pos;
        pos +=makeHeader(buffer, pos);
        Util.TimeToBuff(buffer, pos, time);
        pos+=8;
        int pos1=pos;
        Util.ShortToBuff(buffer, pos, pars.size());
        pos += 2;
        for (OneReg onereg : pars) {
            pos+=onereg.toBuffer(buffer, pos);
            buffer[pos++]=onereg.getGood();
        }
        Util.ShortToBuff(buffer, pos1, pos-pos1-2);
        return pos - startpos;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
//        Util.bufferToString(buffer, pos, len);
        int startpos=pos;
        pos = pos+4;
        time=Util.ToTime(buffer, pos);
        if(time==0) time=System.currentTimeMillis();
        time=Util.convertDate(time);
        pos+=8;
        int lenmain = Util.ToShort(buffer, pos)+pos+2;
        pos += 2;
        pars.clear();
        OneReg onereg=null;
        while(pos<lenmain){
            int uId = Util.ToShort(buffer, pos);
            pos+=2;
            OneReg rg=getRegs().getOneReg(getController(), uId);
            if(rg==null){
                Util.bufferToString(buffer, startpos, len);
                Log.CORE.info("Нет такого uId "+Integer.toString(uId)+"/"+getController()+" "+Integer.toString(pos-2));
                Log.CORE.info(this.toString());
                return;
            }
            onereg = new OneReg(time,rg.getReg() );
            pos+=onereg.getBuffer(buffer, pos);
            onereg.setGood(buffer[pos++]);
            pars.add(onereg);
        }
    }

    @Override
    public String toString()
    {
        String result = super.toString()+" "+new Date(time).toString()+"[";
        for (OneReg onereg : pars) {
            result += " onereg=" + onereg.toString();
        }
        result+="]";
        return result; //To change body of generated methods, choose Tools | Templates.
    }
}
