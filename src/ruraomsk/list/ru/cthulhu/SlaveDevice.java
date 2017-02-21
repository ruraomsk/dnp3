/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import com.tibbo.aggregate.common.Log;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class SlaveDevice extends Thread
{

    private boolean active;
    public ArrayList<Transport> transport;
    private long timestep;
    HashMap<Integer, BaseRout> mapRequestRout;

    public SlaveDevice(long timestep)
    {
        transport = new ArrayList<>();
        this.timestep = timestep;
        active = false;
        mapRequestRout=new HashMap<>(Util.nameRequest.length);
    }

    /**
     * Начинает работу мастера устройства
     */
    public void startDevice()
    {
        mapRequestRout = new HashMap<>(Util.nameRequest.length);
        active = true;
        start();
    }

    /**
     * Останавливает работу мастера устройства и закрывает транспорт
     */
    public synchronized void stopDevice()
    {
        active = false;
        for (Transport tr : transport) {
            tr.close();
        }
        mapRequestRout.clear();
    }
    /**
     * Добавляет транспорт в устройство 
     * @param transport 
     */
    public synchronized void addTransport(Transport tr){
        transport.add(tr);
    }
    /**
     * Регистрация обработчика ответа
     *
     * @param uId - номер запроса
     * @param rout - класс обработчика ответа
     */
    public synchronized void appendRout(int uId, BaseRout rout)
    {
        if (!Util.isCorrectIdMessage(uId)) {
            Log.CORE.info("Попытка зарегистрировать несуществующий код запроса=" + Integer.toHexString(uId));
            return;
        }
        mapRequestRout.put(uId, rout);
    }

    /**
     * Возвращает состояние устройства
     *
     * @return True если активно
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Передает сообщение транспорту
     *
     * @param mess
     */
    public synchronized void sendMessage(BaseMessage mess)
    {
        if (mess.isRequest()) {
            Log.CORE.info("сообщение не ответ " + mess.toString());
            return;
        }
        if (!mapRequestRout.containsKey(mess.getId())) {
            Log.CORE.info("Не зарегистрирован обработчик запросов на" + Integer.toHexString(mess.getId()));
            return;
        }
        for (Transport tr : transport) {
            tr.writeMessage(mess);
        }
    }

    @Override
    public String toString()
    {
        String result = "slave device timestep=" + Long.toString(timestep) + " use \n";
        for (Transport tr : transport) {
            result += tr.toString() + "\n";
        }
        result += "\n может обрабатывать ответы по запросам [";
        for (Integer resp : mapRequestRout.keySet()) {
            result += Util.getShortName(resp) + " ";
        }
        result += "]";
        return result;
    }

    @Override
    public void run()
    {
        while (!Thread.interrupted()) {
            try {
                if (!isActive()) {
                    break;
                }
                BaseMessage message;
                for (Transport tr : transport) {
                    while ((message = tr.readMessage()) != null) {
                        if (!mapRequestRout.containsKey(message.getId())) {
                            Log.CORE.info("Не зарегистрирован обработчик ответа на "
                                    + Integer.toHexString(message.getId()));
                            continue;
                        }
                        BaseRout rout = mapRequestRout.get(message.getId());
                        rout.addMessage(message);
                    }

                }
                Thread.sleep(timestep);
            }
            catch (InterruptedException ex) {
                break;
            }
        }
    }

}
