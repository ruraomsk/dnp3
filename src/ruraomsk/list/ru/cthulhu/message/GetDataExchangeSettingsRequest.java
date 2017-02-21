/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import java.util.ArrayList;
import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Util;
import ruraomsk.list.ru.cthulhu.protocols.Edata;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class GetDataExchangeSettingsRequest extends BaseMessage
{

    ArrayList<Edata> arreq;

    public GetDataExchangeSettingsRequest()
    {
        itsRequest();
        setId(Util.GetDataExchangeSettings);
        setVersion(Util.CT_V22);
        arreq = new ArrayList<>();
    }

    public void addEdata(Edata ed)
    {
        arreq.add(ed);
    }

    public ArrayList<Edata> getEdatas()
    {
        return arreq;
    }

    @Override
    public String toString()
    {
        String res = super.toString() + "\n"; //To change body of generated methods, choose Tools | Templates.
        for (Edata ed : arreq) {
            res += ed.toString() + " ";
        }
        return res;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        int start=pos;
                
        makeHeader(buffer, pos);
        Util.ShortToBuff(buffer, pos+3, arreq.size());
        pos = pos+5;
        for (Edata ed : arreq) {
            pos += ed.tobuffer(buffer, pos);
        }
//        Util.bufferToString(buffer, start, pos-start);
        return pos-start; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        int size = Util.ToShort(buffer, pos+3);
        arreq = new ArrayList<>();
        pos = pos+5;
        for (int i = 0; i < size; i++) {
            if (pos > len) {
                return;
            }
            Edata ed = new Edata();
            pos += ed.frombuffer(buffer, pos);
            arreq.add(ed);
        }
    }

}
