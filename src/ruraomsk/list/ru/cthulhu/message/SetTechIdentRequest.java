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
public class SetTechIdentRequest extends BaseMessage
{
    String TechIndentStr;
    String TechCaption;

    public SetTechIdentRequest()
    {
        itsRequest();
        setId(Util.SetTechIdent);
        setVersion(Util.CT_V23);
    }
    public SetTechIdentRequest(String TechIndentStr, String TechCaption)
    {
        itsRequest();
        setId(Util.SetTechIdent);
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
        for (int i = 3; i < 19; i++) {
            buffer[pos+i] = 0;
        }
        System.arraycopy(TechIndentStr.getBytes(), 0, buffer, pos+3, TechIndentStr.length());
        Util.ShortToBuff(buffer, pos+19, TechCaption.length());
        for (int i = 21; i < 21+TechCaption.length(); i++) {
            buffer[pos+i] = 0;
        }
        System.arraycopy(TechCaption.getBytes(), 0, buffer, pos+21, TechCaption.length());
        return 21+TechCaption.length();
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        int i;
        for (i = 3; i < 19; i++) {
            if (buffer[pos+i] == 0) {
                break;
            }
        }
        char[] b = new char[i - 3];
        for (i = 0; i < b.length; i++) {
            b[i] = (char) buffer[pos+i + 3];
        }
        TechIndentStr = String.valueOf(b);
        b = new char[Util.ToShort(buffer, pos+19)];
        for (i = 0; i < b.length; i++) {
            b[i] = (char) buffer[pos+i + 21];
        }
        TechCaption = String.valueOf(b);
    }
    
}
