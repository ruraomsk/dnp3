/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import java.net.InetAddress;
import java.util.ArrayList;
import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Util;
import ruraomsk.list.ru.cthulhu.protocols.Abonent;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class GetRegisteredPeersInfoResponse extends BaseMessage
{
    ArrayList <Abonent> abonents;
    public GetRegisteredPeersInfoResponse()
    {
        itsResponse();
        setId(Util.GetRegisteredPeersInfo);
        setVersion(Util.CT_V23);
        abonents=new ArrayList<>();
    }
    public void addAbonent(Abonent ab){
        abonents.add(ab);
    } 
    public ArrayList <Abonent> getAbonents(){
        return abonents;
    }

    @Override
    public String toString()
    {
        String res= super.toString()+" PeerCnt="+Integer.toString(abonents.size())+" ";
        for(Abonent ab:abonents){
            res+=ab.toString()+" ";
        }
        return res;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        int start=pos;
        Util.ShortToBuff(buffer, pos+4, abonents.size());
        pos=pos+6;
        for(Abonent ab:abonents){
            pos+=ab.tobuffer(buffer, pos);
        }
        return pos-start;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        abonents=new ArrayList<>();
        int count=Util.ToShort(buffer, pos+4);
        pos=pos+6;
        for (int i = 0; i < count; i++) {
            Abonent ab=new Abonent();
            pos+=ab.frombuffer(buffer, pos);
            abonents.add(ab);
        }
    }
    
}
