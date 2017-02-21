/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public abstract class BaseTransport extends Thread
{
    
    protected InputStream m_Input;
    protected OutputStream m_Output;
    protected boolean connect = false;
    protected boolean master = true;
    protected ConcurrentLinkedQueue<BaseMessage> qResp = null;
    protected int controller;
    protected Registers regs;
    protected int icount;

    public BaseTransport()
    {
    }
    public abstract boolean isSerial();
    public abstract Socket getSocket();
    public abstract String getCommPort();
    /**
     * Производит подключение к транспортному уровню
     *
     * @return True если успешно
     */
    public abstract boolean connect();

    public abstract String getNameTransport();

    @Override
    public abstract String toString();

    /**
     * Отключает транспортный уровень
     */
    public abstract void close();

    /**
     * Запрос состояния подключения
     *
     * @return True если установлено подключение
     */
    public abstract boolean isConnected();

    /**
     * Чтение принятого элемента из очереди принятых сообщений
     *
     * @return Очередное сообщение null если очередь пуста
     */
    public abstract BaseMessage readMessage();

    public abstract int getController();

    /**
     * Передача сообщения
     *
     * @param msg Сообщение для передачи
     * @return номер посылки если запрос иначе ноль
     */
    public abstract int writeMessage(BaseMessage msg);

    @Override
    public abstract void run();

    public abstract boolean isSlip();
    
}
