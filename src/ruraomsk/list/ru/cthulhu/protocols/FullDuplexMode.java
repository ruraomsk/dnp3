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
public class FullDuplexMode extends Inter 
{
    boolean bFullDuplexMode;

    public FullDuplexMode()
    {
        id=12;
    }

    public FullDuplexMode(boolean bFullDuplexMode)
    {
        id=12;
        this.bFullDuplexMode = bFullDuplexMode;
    }
    public boolean isFullDuplexMode(){
        return bFullDuplexMode;
    }
    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        bFullDuplexMode=buffer[pos]==1;
        return 1;
    }

    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        buffer[pos]=(byte) (bFullDuplexMode?1:0);
        return 1;
    }

    @Override
    public String toString()
    {
        
        return "FullDuplexMode "+(bFullDuplexMode?"yes":"no"); //To change body of generated methods, choose Tools | Templates.
    }
    
}
