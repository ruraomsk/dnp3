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
public class GetTechIdentResponse extends BaseMessage
{

    String TechIndentStr;
    String TechCaption;

    public GetTechIdentResponse()
    {
        itsResponse();
        setId(Util.GetTechIdent);
        setVersion(Util.CT_V23);
    }

    public GetTechIdentResponse(String TechIndentStr, String TechCaption)
    {
        itsResponse();
        setId(Util.GetTechIdent);
        setVersion(Util.CT_V23);
        this.TechIndentStr = TechIndentStr;
        this.TechCaption = TechCaption;
    }

    public String getTechIndent()
    {
        return TechIndentStr;
    }

    public String getTechCaption()
    {
        return TechCaption;
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + TechIndentStr + " / " + TechCaption; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        for (int i = 4; i < 36; i++) {
            buffer[pos+i] = 0;
        }
        System.arraycopy(TechIndentStr.getBytes(), 0, buffer, pos+4, TechIndentStr.length());
        Util.ShortToBuff(buffer, pos+36, TechCaption.length());
        for (int i = 38; i < 38+TechCaption.length(); i++) {
            buffer[pos+i] = 0;
        }
        System.arraycopy(TechCaption.getBytes(), 0, buffer, pos+38, TechCaption.length());
        return 38+TechCaption.length();
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        int i;
        for (i = 4; i < 36; i++) {
            if (buffer[pos+i] == 0) {
                break;
            }
        }
        char[] b = new char[i - 4];
        for (i = 0; i < b.length; i++) {
            b[i] = (char) buffer[pos+i + 4];
        }
        TechIndentStr = String.valueOf(b);
        b = new char[Util.ToShort(buffer, pos+36)];
        for (i = 0; i < b.length; i++) {
            b[i] = (char) buffer[pos+i + 38];
        }
        TechCaption = String.valueOf(b);
    }
    

}
