/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import ruraomsk.list.ru.cthulhu.*;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class SetPropertyRequest extends BaseMessage
{
    private HashMap<Integer,ArrayList<Property>> mapprop;
    
    public SetPropertyRequest()
    {
        itsRequest();
        setId(Util.GetProperty);
        mapprop=new HashMap<>();
    }
    public void addPropertyValue(Property prop){
        int uId=prop.getRegister().getuId();
        ArrayList<Property> arrprop=mapprop.get(uId);
        if(arrprop==null){
            arrprop=new ArrayList<>();
        }
        arrprop.add(prop);
        mapprop.put(uId, arrprop);
    }
    public ArrayList<Property> getProperties(int uId){
        ArrayList<Property> arrprop=mapprop.get(uId);
        if(arrprop==null){
            arrprop=new ArrayList<>();
        }
        return arrprop;
    }
    public Collection<Integer> getUIds(){
        return mapprop.keySet();
    }
    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        int startpos=pos;
        pos+=makeHeader(buffer, pos);
        int pos1=pos;
        pos+=2;
        for(Integer uId:mapprop.keySet()){
            ArrayList<Property> arrprop=mapprop.get(uId);
            if(arrprop==null) continue;
            for(Property prop:arrprop){
                Util.ShortToBuff(buffer,pos, uId);
                pos++;
                pos+=prop.toBuffer(buffer, pos);
            }
        }
        Util.ShortToBuff(buffer, pos1, pos-pos1-2);
        return pos-startpos;
    }
    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        pos=pos+3;
        int lenseg=Util.ToShort(buffer, pos);
        pos+=2;
        while (lenseg>0) {            
            int uId=Util.ToShort(buffer, pos);
            pos+=2;
            lenseg-=2;
            OneReg oreg=getRegs().getOneReg(getController(), uId);
            int pId=Util.ToShort(buffer, pos);
            pos+=2; lenseg-=2;
            Property prop=new Property(oreg.getReg(), pId);
            int L=prop.fromBuffer(buffer, pos);
            pos+=L;
            lenseg-=L;
            ArrayList<Property> arrprop=mapprop.get(uId);
            if(arrprop==null) continue;
            arrprop.add(prop);
            mapprop.put(uId, arrprop);
        }
    }

    @Override
    public String toString()
    {
        String result= super.toString()+"[";
        for(Integer uId:mapprop.keySet()){
            ArrayList<Property> arrprop=mapprop.get(uId);
            if(arrprop==null) continue;
            for(Property prop:arrprop){
                result+=prop.toString()+" ";
            }
        }       
        return result+"]";
    }
    
    
}
