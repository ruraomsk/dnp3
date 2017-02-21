/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import java.util.ArrayList;
import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Util;
import ruraomsk.list.ru.cthulhu.protocols.*;


/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class GetACLResponse extends BaseMessage
{
    ArrayList<ACLItem> items;
    public GetACLResponse()
    {
        itsResponse();
        setId(Util.GetACL);
        setVersion(Util.CT_V23);
        items=new ArrayList<>();
    }

    @Override
    public String toString()
    {
        String res= super.toString()+" ACLItemCnt="+Integer.toString(items.size())+" "; //To change body of generated methods, choose Tools | Templates.
        for(ACLItem acl:items){
            res+=acl.toString()+" ";
        }
        return res;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        Util.ShortToBuff(buffer, pos+4, items.size());
        pos=6;
        for(ACLItem acl:items){
            pos+=acl.tobuffer(buffer, pos);
        }
        return pos;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        int count=Util.ToShort(buffer, pos+4);
        pos=pos+6;
        for (int i = 0; i < count; i++) {
            ACLItem acl=new ACLItem();
            pos+=acl.frombuffer(buffer, pos);
            items.add(acl);
        }
    }

}
