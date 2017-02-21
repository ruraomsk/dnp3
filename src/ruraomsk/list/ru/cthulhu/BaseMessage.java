/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import com.tibbo.aggregate.common.datatable.DataTable;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class BaseMessage
{
    private int reqIter;
    private int id;
    private int ReqEC=0;
    private byte type=(byte) 0xff;
    private byte version=Util.CT_V21;
    private boolean request=true;
    private DataTable dt=null;
    private int controller;
    private Registers regs=null;
    public BaseMessage(){
        reqIter=0;
        id=0xFF;
    }
    /**
     */
    
    /**
     * Устанавливвает отношения с внешним миром
     * @param controller номер контроллера
     * @param regs       глобальная таблица
     */
    public void setupWorld(int controller,Registers regs){
        this.controller=controller;
        this.regs=regs;
    }
    /**
     * Конструктор по умолчанию для приема из буфера
     *
     * @param buffer - буфер из транспорта
     * @param pos - начальная позиция сообщения
     * @param len - длина буфера
     */
    public void makeMessage(byte[] buffer, int pos,int len)
    {
        type=0;
        reqIter = Util.ToShort(buffer, pos);
        id = buffer[pos + 2];
        ReqEC=buffer[pos+3];
        fromBuffer(buffer, pos, len);
    }
    /**
     * Является ли ответ одиночным (последним)
     *
     * @return true если он одиночный ответ 
     */
    public boolean isAlone(){
        return true;
    }
    /**
     * Перенос запроса в буфер для передачи
     *
     * @param buffer
     * @param pos
     * @return сколько байт записано в буфер
     */
    public int toBuffer(byte[] buffer, int pos)
    {
        return makeHeader(buffer, pos);
    }
    /**
     * Принимает запрос из буфера
     *
     * @param buffer    буфер 
     * @param pos       позиция
     * @param len       длина
     */
    public void fromBuffer(byte[] buffer, int pos, int len){
        
        return;
    }

    /**
     * Вывод запроса в строку. Нужен в основном для отладки
     * @return Строковое представление запроса
     */
    @Override
    public String toString(){
        String res=Util.getShortName(id)+Util.getTail(request)+"#"+Integer.toString(reqIter)+"#";
        res+=(!isRequest()?" ReqEC="+Util.getShortError(getReqEC()):"");
        res+=" Controller="+Integer.toString(controller)+" ";
        return res;
    }

    /**
     * Вывод имени запроса в строку. Нужен в основном для формирования имени события
     * @return Строковое представление имени запроса
     */
    public String getEventName(){
        return Util.makeEventName(isRequest(), id);
    }
    /**
     * Вывод версии протокола
     * @return версия протокола
     */
    public byte getVersion()
    {
        return version;
    }
    /**
     * Изменение версии протокола
     */
    public void setVersion(byte version){
        this.version=version;
    }
    /**
     * Вывод типа протокола
     * @return тип протокола
     */
    public byte getType(){
        return type;
    }
    /**
     * Возврат номера последовательности
     * @return the reqIter
     */
    public int getReqIter()
    {
        return reqIter;
    }

    /**
     * Установить номер последовательности
     * @param reqIter the reqIter to set
     */
    public void setReqIter(int reqIter)
    {
        this.reqIter = reqIter;
    }

    /**
     * Узнать номер запроса или ответа
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * Установить номер запроса или ответа
     * @param id the id to set
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Это запрос или ответ на запрос
     * @return the request
     */
    public boolean isRequest()
    {
        return request;
    }

    /**
     * Установить запрос или  ответ
     * @param request the request to set
     */
    public void setRequest(boolean request)
    {
        this.request = request;
    }
    public void itsResponse(){
        this.request = false;
    }
    public void itsRequest(){
        this.request = true;
    }

    /**
     * @return the ReqEC
     */
    public int getReqEC()
    {
        return ReqEC;
    }

    /**
     * @param ReqEC the ReqEC to set
     */
    public void setReqEC(int ReqEC)
    {
        this.ReqEC = ReqEC;
    }
    public int makeHeader(byte[] buffer,int pos){
        if(isRequest()){
        Util.ShortToBuff(buffer, pos,getReqIter());
        buffer[pos+2]=(byte)(getId()&0xff);
        return 3;
        }
        Util.ShortToBuff(buffer, pos, getReqIter());
        buffer[pos+2]=(byte)(getId()&0xff);
        buffer[pos+3]=(byte)(ReqEC&0xff);
        return 4;
    }
    public DataTable getDataTable(){
        if(dt==null){
            
        }
        return dt;
    }

    /**
     * @return the controller
     */
    public int getController()
    {
        return controller;
    }

    /**
     * @return the regs
     */
    public Registers getRegs()
    {
        return regs;
    }
}
