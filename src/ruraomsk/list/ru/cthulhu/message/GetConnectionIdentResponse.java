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
public class GetConnectionIdentResponse extends BaseMessage
{
    int PeerID=1;
    public GetConnectionIdentResponse()
    {
        itsResponse();
        setId(Util.GetConnectionIdent);
        setVersion(Util.CT_V23);
    }
    public GetConnectionIdentResponse(int PeerID)
    {
        itsResponse();
        setId(Util.GetConnectionIdent);
        setVersion(Util.CT_V23);
        this.PeerID=PeerID;
    }
    public int getPeerID(){
        return PeerID;
    }

    @Override
    public String toString()
    {
        return super.toString()+" PeerID="+Integer.toString(PeerID); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        Util.ShortToBuff(buffer, pos+4, PeerID);
        return 6;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        PeerID=Util.ToShort(buffer, pos+4);
    }
    
}
