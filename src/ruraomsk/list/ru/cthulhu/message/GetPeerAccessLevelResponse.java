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
public class GetPeerAccessLevelResponse extends BaseMessage
{
    int PeerAL;

    public GetPeerAccessLevelResponse()
    {
        itsResponse();
        setId(Util.GetPeerAccessLevel);
        setVersion(Util.CT_V23);
    }

    public GetPeerAccessLevelResponse(int PeerAL)
    {
        itsResponse();
        setId(Util.GetPeerAccessLevel);
        setVersion(Util.CT_V23);
        this.PeerAL = PeerAL;
    }
    public int getPeerAL(){
        return PeerAL;
    }
    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        Util.ShortToBuff(buffer, pos+4, PeerAL);
        return 6; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString()
    {
        return super.toString()+" PeerAL="+Integer.toString(PeerAL); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        PeerAL=Util.ToShort(buffer, pos+4);
    }
    
}
