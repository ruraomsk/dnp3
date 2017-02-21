/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Name32;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class SetFileAttributesRequest extends BaseMessage
{
    byte FlNum = 1;
    private Name32 FlName = new Name32();
    int FlAttr;

    public SetFileAttributesRequest()
    {
        itsRequest();
        setId(Util.SetFileAttributes);
        setVersion(Util.CT_V23);
    }

    public SetFileAttributesRequest(byte FlNum, int FlAttr)
    {
        itsRequest();
        setId(Util.SetFileAttributes);
        setVersion(Util.CT_V23);

        this.FlNum = FlNum;
        this.FlAttr = FlAttr;
    }

    public SetFileAttributesRequest(String FlName, int FlAttr)
    {
        itsRequest();
        setId(Util.SetFileAttributes);
        setVersion(Util.CT_V23);
        FlNum = 0;
        this.FlName=new Name32(FlName);
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        buffer[pos+3] = FlNum;
        if (FlNum == 0) {
            FlName.tobuffer(buffer,  pos+4);
        }
        Util.ShortToBuff(buffer, pos+36, FlAttr);
        return 38;
    }

    @Override
    public String toString()
    {
        return super.toString() + ((FlNum == 0) ? FlName.getName() : Integer.toString(FlNum)) + " FlAttr=" + Integer.toHexString(getFlAttr()); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        FlNum = buffer[pos+3];
        if (FlNum == 0) {
            FlName.frombuffer(buffer, pos+4);
        }
        FlAttr = Util.ToShort(buffer, pos+36);
    }

    /**
     * @return the FlNum
     */
    public byte getFlNum()
    {
        return FlNum;
    }

    /**
     * @return the FlAttr
     */
    public int getFlAttr()
    {
        return FlAttr;
    }

    /**
     * @return the FlName
     */
    public String getFlName()
    {
        return FlName.getName();
    }
}
