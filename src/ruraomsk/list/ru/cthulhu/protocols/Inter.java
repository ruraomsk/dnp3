/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.protocols;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class Inter
{

    int id;

    public int getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }
/**
 * Записать описание в протокол из буфера
 * @param buffer
 * @param pos
 * @return кол-во прочитанных байт
 */
    public int frombuffer(byte[] buffer, int pos){
        return 0;
    }


/**
 * Записать описание протокола в буфер
 * @param buffer
 * @param pos
 * @return кол-во записанных байт
 */
    public int tobuffer(byte[] buffer, int pos){
        return 0;
    }
    

}
