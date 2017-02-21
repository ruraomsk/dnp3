/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Property;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class GetPropertyResponse extends BaseMessage
{

    HashMap<Integer, ArrayList<Property>> pars = new HashMap<>();

    public GetPropertyResponse()
    {
        setId(Util.GetProperty);
        itsResponse();
    }

    public void addProperty(Property prop)
    {
        int key = prop.getRegister().getuId();
        if (!pars.containsKey(key)) {
            pars.put(key, new ArrayList<Property>());
        }
        ArrayList<Property> ar = pars.get(key);
        ar.add(prop);
    }

    public Collection<Integer> getUIds()
    {
        return pars.keySet();
    }

    public Property[] getProps(int uId)
    {
        ArrayList<Property> ar = pars.get(uId);
        Property[] aa = new Property[ar.size()];
        ar.toArray(aa);
        return aa;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        int startpos = pos;
        pos += 4;
        int pos1=pos;
        Util.ShortToBuff(buffer, pos, pars.size());
        pos += 2;
        for (Integer uId : pars.keySet()) {
            Util.ShortToBuff(buffer, pos, uId);
            pos += 2;
            ArrayList<Property> ar = pars.get(uId);
            int pos2=pos;
            Util.ShortToBuff(buffer, pos, ar.size());
            pos += 2;
            for (Property ai : ar) {
                pos+=ai.toBuffer(buffer,pos);
            }
            Util.ShortToBuff(buffer, pos2, pos-pos2-2);
        }
        Util.ShortToBuff(buffer, pos1, pos-pos1-2);
        return pos - startpos;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
//        Util.bufferToString(buffer, pos, len);
        if(true)return;
        pos += 4;
        int lenmain = Util.ToShort(buffer, pos);
        pos += 2;
        while(lenmain>0){
            int uId = Util.ToShort(buffer, pos);
            pos+=2;
            lenmain-=2;
            ArrayList<Property> ar = new ArrayList<>();
            int lensec = Util.ToShort(buffer, pos);
            pos+=2;
            lenmain-=2;
            while (lensec>0) {
                int prpId=Util.ToShort(buffer, pos);
                Property ctp=getRegs().getProperty(getController(), uId, prpId);
                pos+=2;
                lenmain-=2;
                lensec-=2;
                int sm=ctp.fromBuffer(buffer, pos);
                ar.add(ctp);
                pos+=sm;
                lenmain-=sm;
                lensec-=sm;
            }
            pars.put(uId, ar);
        }
    }

    @Override
    public String toString()
    {
        String result = super.toString();
        for (Integer uId : pars.keySet()) {
            result += " uId=" + uId.toString() + " [";
            ArrayList<Property> ar = pars.get(uId);
            for (Property par : ar) {
                result += par.toString() + " ";
            }
            result += "] ";
        }
        return result; //To change body of generated methods, choose Tools | Templates.
    }
}
