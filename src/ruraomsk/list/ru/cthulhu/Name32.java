/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

/**
 * Класс для хранения и работы с именами файлов и директорий в ктулху 
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class Name32
{
    char[] cname=new char[32];
    public Name32(String name){
        int c=0;
         for(char ch:name.toCharArray()){
             if(c<cname.length){
             cname[c++]=ch;
             }
         }
    }

    public Name32()
    {
        int c=0;
         for(char ch:"N/N".toCharArray()){
             if(c<cname.length){
             cname[c++]=ch;
             }
         }
    }
    public int tobuffer(byte[] buffer,int pos){
        for (int i = 0; i < cname.length; i++) {
            buffer[pos++]=(byte)cname[i];
        }
        return length();
    }
    public int frombuffer(byte[] buffer,int pos){
        for (int i = 0; i < cname.length; i++) {
            cname[i]=(char)buffer[pos++];
        }
        return length();
    }
    @Override
    public String toString(){
        return String.valueOf(cname);
    }
    public String getName(){
        return String.valueOf(cname);
    }
    public byte[] bytes(){
        byte[] b=new byte[32];
        for (int i = 0; i < cname.length; i++) {
            b[i]=(byte)cname[i];
        }
        return b;
    }
    public void tobytes(byte[] b){
        for(int i=0;i<b.length;i++){
            if(i<cname.length){
                cname[i]=(char)b[i];
            }
        }
    }
    public int length(){
        return cname.length;
    }
}
