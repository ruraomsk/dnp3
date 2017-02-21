/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.pocket;
import ruraomsk.list.ru.cthulhu.Util;
/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class FileStatus {

    private Integer number;
    private Integer status;
    private byte[] file;
    private Integer size;
    private Integer rescode;

    public FileStatus(Integer number) {
        this.number = number;
        status = 0;
        size=-1;
        rescode=0;
    }

    @Override
    public String toString() {
        return "Файл=" + number.toString() + " Статус=" + getStatusText() + " Размер=" + getSize().toString()
                +" "+Util.getLongError(rescode);
    }

    /**
     * @return the status
     */
    public String getStatusText() {
        switch (getStatus()) {
            case 0:
                return "Не загружен";
            case 1:
                return "Отправлен запрос на получение размера";
            case 2:
                return "Файл загружается";
            case 3:
                return "Файл загружен";
            case 4:
                return "Информация обработана";
            case -1:
                return "Файла нет в устройстве";
        }
        return "Ошибка статуса";
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return the file
     */
    public byte[] getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(byte[] file,int pos,int len) {
        System.arraycopy(file, 0, this.file,pos , len);
    }

    /**
     * @return the size
     */
    public Integer getSize() {
        if(file==null) return 0;
        return file.length;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Integer size) {
        file=new byte[size];
    }

    /**
     * @return the number
     */
    public Integer getNumber() {
        return number;
    }


    /**
     * @return the rescode
     */
    public Integer getRescode() {
        return rescode;
    }

    /**
     * @param rescode the rescode to set
     */
    public void setRescode(Integer rescode) {
        this.rescode = rescode;
    }

}
