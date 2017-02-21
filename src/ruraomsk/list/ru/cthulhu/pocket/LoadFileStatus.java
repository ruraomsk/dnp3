/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.pocket;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class LoadFileStatus extends FileStatus{
    private byte[] description;
    private byte[] constant;
    public LoadFileStatus(Integer number) {
        super(number);
    }
    public void setDescription(byte[] ds){
        description=ds;
    }
    public void setConstats(byte[] cnt){
        constant=cnt;
    }
    public byte[] getDescription(){
        return description;
    }
    public byte[] getConstats(){
        return constant;
    }
    
}
