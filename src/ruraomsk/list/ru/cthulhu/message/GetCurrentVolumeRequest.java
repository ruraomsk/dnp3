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
public class GetCurrentVolumeRequest extends BaseMessage
{

    public GetCurrentVolumeRequest()
    {
        itsRequest();
        setVersion(Util.CT_V23);
        setId(Util.GetCurrentVolume);
    }
}
