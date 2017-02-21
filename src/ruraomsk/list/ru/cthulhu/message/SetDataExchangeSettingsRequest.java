/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import com.tibbo.aggregate.common.Log;
import java.util.ArrayList;
import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Util;
import ruraomsk.list.ru.cthulhu.protocols.Inter;
import ruraomsk.list.ru.cthulhu.protocols.InterPoint;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class SetDataExchangeSettingsRequest extends BaseMessage
{
    ArrayList<InterPoint> arrProtocols;

    public SetDataExchangeSettingsRequest()
    {
        itsRequest();
        setId(Util.SetDataExchangeSettings);
        setVersion(Util.CT_V22);
        arrProtocols = new ArrayList<>();
    }

    public void addProtocol(InterPoint iPData)
    {
        arrProtocols.add(iPData);
    }

    public ArrayList<InterPoint> getProtocols()
    {
        return arrProtocols;
    }

    @Override
    public String toString()
    {
        String res = super.toString() + "\n";
        for (InterPoint arrProtocol : arrProtocols) {
            res += arrProtocol.toString() + "\n";
        }
        return res; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        int start=pos;
        makeHeader(buffer, pos);
        Util.ShortToBuff(buffer, pos+3, arrProtocols.size());
        pos = pos+5;
        for (InterPoint intPoint : arrProtocols) {
            Util.ShortToBuff(buffer, pos, intPoint.DEHWInterfaceID);
            pos += 2;
            Util.ShortToBuff(buffer, pos, intPoint.CPointID);
            pos += 2;
            Util.ShortToBuff(buffer, pos, intPoint.interData.getId());
            pos += 2;
            pos += intPoint.interData.tobuffer(buffer, pos);
        }
        return pos-start; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
        int count = Util.ToShort(buffer, pos+3);
        pos = pos+5;
        for (int i = 0; i < count; i++) {
            int DEHWInterfaceID = Util.ToShort(buffer, pos);
            pos += 2;
            int CPointID = Util.ToShort(buffer, pos);
            pos += 2;
            int DEStngID = Util.ToShort(buffer, pos);
            pos += 2;
            Inter temp = Util.newProtocol(DEStngID);
            if (temp == null) {
                Log.CORE.info("SetDataExchangeSettingsRequest DEStngID нет такого" + Integer.toString(DEStngID));
                return;
            }
            pos += temp.frombuffer(buffer, pos);
            InterPoint intPoint = new InterPoint(DEHWInterfaceID, CPointID, temp);
            arrProtocols.add(intPoint);
        }
    }
    
}
