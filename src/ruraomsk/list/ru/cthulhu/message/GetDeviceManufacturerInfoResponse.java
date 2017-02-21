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
public class GetDeviceManufacturerInfoResponse extends BaseMessage
{

    int ManfctrID = 1;
    String ManfctrName = "Auto";
    String ManfctrCaption = "Omsk sity";

    public GetDeviceManufacturerInfoResponse()
    {
        itsResponse();
        setId(Util.GetDeviceManufacturerInfo);
        setVersion(Util.CT_V23);
    }

    public GetDeviceManufacturerInfoResponse(int ManfctrID, String ManfctrName, String ManfctrCaption)
    {
        this.ManfctrID = ManfctrID;
        this.ManfctrName = ManfctrName;
        this.ManfctrCaption = ManfctrCaption;
    }

    public int getID()
    {
        return ManfctrID;
    }

    public String getName()
    {
        return ManfctrName;
    }

    public String getCaption()
    {
        return ManfctrCaption;
    }

    @Override
    public String toString()
    {
        return super.toString() + " ManfctrID=" + Integer.toString(ManfctrID)
                + " " + ManfctrName + " / " + ManfctrCaption; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        for (int i = 6; i < 22; i++) {
            buffer[pos+i] = 0;
        }
        for (int i = 24; i < ManfctrCaption.length() + 24; i++) {
            buffer[pos+i] = 0;
        }
        Util.ShortToBuff(buffer, pos+4, ManfctrID);
        System.arraycopy(ManfctrName.getBytes(), 0, buffer, pos+6, ManfctrName.length());
        Util.ShortToBuff(buffer, pos+22, ManfctrCaption.length());
        System.arraycopy(ManfctrCaption.getBytes(), 0, buffer, pos+24, ManfctrCaption.length());
        return 24 + ManfctrCaption.length();
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        ManfctrID = Util.ToShort(buffer, pos+4);
        int i;
        for (i = 6; i < 22; i++) {
            if (buffer[pos+i] == 0) {
                break;
            }
        }
        char[] b = new char[i - 6];
        for (i = 0; i < b.length; i++) {
            b[i] = (char) buffer[pos+i + 6];
        }
        ManfctrName = String.valueOf(b);
        b = new char[Util.ToShort(buffer, pos+22)];
        for (i = 0; i < b.length; i++) {
            b[i] = (char) buffer[i +pos+ 24];
        }
        ManfctrCaption = String.valueOf(b);
    }

}
