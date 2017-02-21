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
public class SlipMode extends Inter
{
    boolean bSlipMode;

    public SlipMode()
    {
        id=13;
    }

    public SlipMode(boolean bSlipMode)
    {
        id=13;
        this.bSlipMode = bSlipMode;
    }
    public boolean isSlipMode(){
        return bSlipMode;
    }
    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        bSlipMode=buffer[pos]==1;
        return 1;
    }

    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        buffer[pos]=(byte) (bSlipMode?1:0);
        return 1;
    }

    @Override
    public String toString()
    {
        return "SlipMode "+(bSlipMode?"yes":"no"); //To change body of generated methods, choose Tools | Templates.
    }
}
