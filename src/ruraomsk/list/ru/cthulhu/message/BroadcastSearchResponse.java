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
public class BroadcastSearchResponse extends BaseMessage
{

    public BroadcastSearchResponse()
    {
        itsResponse();
        setId(Util.BroadcastSearch);
        setVersion(Util.CT_V23);
    }

    @Override
    public String toString()
    {
        return super.toString()+" Не реализовано!"; //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
