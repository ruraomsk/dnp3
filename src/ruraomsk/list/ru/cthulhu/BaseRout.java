/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public abstract class BaseRout extends Thread
{
    
    abstract public void addMessage (BaseMessage message);
    @Override
    abstract public String toString();
}
