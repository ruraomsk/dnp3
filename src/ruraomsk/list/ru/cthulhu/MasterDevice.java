/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import com.tibbo.aggregate.common.Log;
import java.util.HashMap;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class MasterDevice extends Thread
{

    private boolean active;
    private BaseTransport transport;
    private long timestep;
    HashMap<Integer, BaseRout> mapResponseRout;
    private boolean masterCanal;

    public MasterDevice(BaseTransport transport, long timestep)
    {
        this.transport = transport;
        this.timestep = timestep;
        active = false;
        mapResponseRout = new HashMap<>(Util.nameRequest.length);
        masterCanal=false;
    }

    /**
     * Начинает работу мастера устройства
     */
    public void startDevice()
    {
        if (!transport.connect()) {
            return;
        }
        if(!active) start();
        active = true;
    }
    /**
     * Останавливает работу мастера устройства и закрывает транспорт
     */
    public void stopDevice()
    {
        this.interrupt();
        //            this.join(timestep*2);
        active = false;
        if (!transport.isConnected()) {
            return;
        }
        transport.close();
           Log.CORE.info("MasterDevice остановлен...");
    }
    /**
     * Регистрация обработчика ответа 
     * @param uId - номер запроса
     * @param rout - класс обработчика ответа 
     */
    public void appendRout(int uId, BaseRout rout){
        if(!Util.isCorrectIdMessage(uId)){
            Log.CORE.info("Попытка зарегистрировать несуществующий код запроса="+Integer.toHexString(uId));
            return;
        }
        mapResponseRout.put(uId, rout);
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
    public void sendMessage(BaseMessage mess)
    {
        if (!mess.isRequest()) {
            Log.CORE.info("сообщение не запрос " + mess.toString());
            return;
        }
        if (!mapResponseRout.containsKey(mess.getId())) {
            Log.CORE.info("Не зарегистрирован обработчик ответа на" + Integer.toHexString(mess.getId()));
            return;
        }
        transport.writeMessage(mess);
    }

    @Override
    public String toString()
    {
        String result = "master device "+(masterCanal?"основной":"резервный")+" timestep=" + Long.toString(timestep) + " use " + transport.toString()
                + "\n может обрабатывать ответы по запросам [";
        for (Integer resp : mapResponseRout.keySet()) {
            result += Util.getShortName(resp) + " ";
        }
        result += "]";
        return result;
    }
    public BaseTransport getTransport(){
        return transport;
    }
    public void setTransport(BaseTransport transport){
        this.transport=transport;
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
                while((message=transport.readMessage())!=null){
                    if(!masterCanal) continue;
                    if(!mapResponseRout.containsKey(message.getId())){
                        Log.CORE.info("Не зарегистрирован обработчик ответа на " 
                                + Integer.toHexString(message.getId()));
                        continue;
                    }
                    BaseRout rout=mapResponseRout.get(message.getId());
                    rout.addMessage(message);
                }
                Thread.sleep(timestep);
            }
            catch (InterruptedException ex) {
                break;
            }
        }
    }

    public void setReservCanal()
    {
        masterCanal=false;
    }

    public void setMainCanal()
    {
        masterCanal=true;
    }
    public boolean isMasterCanal(){
        return masterCanal;
    }
    

}
