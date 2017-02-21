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
public class GetDeviceIdentResponse extends BaseMessage
{
    int DeviceID=1;
    String DeviceIdentStr="123456789";
    public GetDeviceIdentResponse()
    {
        itsResponse();
        setId(Util.GetDeviceIdent);
        setVersion(Util.CT_V23);
    }

    public GetDeviceIdentResponse(int DeviceID, String DeviceIdentStr)
    {
        itsResponse();
        setId(Util.GetDeviceIdent);
        setVersion(Util.CT_V23);
        this.DeviceID = DeviceID;
        this.DeviceIdentStr = DeviceIdentStr;
    }

    @Override
    public String toString()
    {
        return super.toString()+" DeviceID="+Integer.toString(DeviceID)+" '"+DeviceIdentStr+"'"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        Util.ShortToBuff(buffer, pos+4, DeviceID);
        for (int i = 0; i < 16; i++) {
            buffer[i+pos+6]=0;
        }
        byte[] res=DeviceIdentStr.getBytes();
        System.arraycopy(res, 0, buffer, pos+6, res.length);
        return 22;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        DeviceID=Util.ToShort(buffer, pos+4);
        int i;
        for ( i= 0; i < 16; i++) {
            if(buffer[6+pos+i]==0) break;
        }
        
        char[] b=new char[i];
        for (i = 0; i < b.length; i++) {
            b[i]=(char) buffer[pos+6+i];
        }
        DeviceIdentStr=String.valueOf(b);
    }
    
}
