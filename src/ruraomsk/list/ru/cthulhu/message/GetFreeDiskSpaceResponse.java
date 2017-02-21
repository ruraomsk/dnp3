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
public class GetFreeDiskSpaceResponse extends BaseMessage
{

    int FDSpace;

    public GetFreeDiskSpaceResponse()
    {
        itsResponse();
        setId(Util.GetFreeDiskSpace);
        setVersion(Util.CT_V21);
    }

    public GetFreeDiskSpaceResponse(int FDSpace)
    {
        itsResponse();
        setId(Util.GetFreeDiskSpace);
        setVersion(Util.CT_V21);
        this.FDSpace = FDSpace;
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        Util.IntegerToBuff(buffer, pos+4, FDSpace);
        return 8; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FDSpace = Util.ToInteger(buffer, pos+4);
    }

    @Override
    public String toString()
    {
        return super.toString() + " FDSpace=" + Integer.toString(FDSpace); //To change body of generated methods, choose Tools | Templates.
    }

    public int getFDSpace()
    {
        return FDSpace;
    }
}
