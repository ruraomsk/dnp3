/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class Property
{

    private int PrpId;       //Идентификатор свойства параметра
    private Object value;    //значение
    private Register reg;

    public Property()
    {
        PrpId = 0;
        value = 0;
    }

    public Property(Register reg, int PrpId)
    {
        this.reg = reg;
        this.PrpId = PrpId;
        this.value = reg.makeEmptyValue();
    }
    public Register getRegister(){
        return reg;
    }
    public int getPrpId(){
        return PrpId;
    }
    public void setValue(Object value){
        this.value=value;
    }
    public Object getValue(){
        return value;
    }
    /**
     * считываем свойства параметра из буфера
     *
     * @param buffer
     * @param pos
     * @return кол=во прочитанных байт
     */
    public int fromBuffer(byte[] buffer, int pos)
    {
        switch (reg.getType()) {
            case Util.CT_TYPE_BOOL:
                if (reg.getLen() == 1) {
                    value = ((buffer[pos] != 0));
                    break;
                }
                if (reg.getLen() == 2) {
                    value = ((Util.ToShort(buffer, pos) != 0));
                    break;
                }
                if (reg.getLen() == 4) {
                    value = (Util.ToFloat(buffer, pos) != 0.0);
                    break;
                }
            case Util.CT_TYPE_INTEGER:
                if (reg.getLen() == 2) {
                    value = (Util.ToShort(buffer, pos));
                    break;
                }
                if (reg.getLen() == 4) {
                    value = ((int) Util.ToFloat(buffer, pos));
                    break;
                }
            case Util.CT_TYPE_FLOAT:
                value=(Util.ToFloat(buffer, pos));
                break;
            case Util.CT_TYPE_LONG:
                value=(Util.ToInteger(buffer, pos));
                break;
            case Util.CT_TYPE_BYTE:
                value=buffer[pos];
                break;
        }
        return reg.getLen();
    }
    /**
     * заносим свойства параметра в буфер
     *
     * @param buffer
     * @param pos
     * @return кол=во записанных байт
     */
    public int toBuffer(byte[] buffer, int pos)
    {
        Util.ShortToBuff(buffer, pos,PrpId);
        pos+=2;
        switch (reg.getType()) {
            case Util.CT_TYPE_BOOL:
                if (reg.getLen() == 1) {
                    buffer[pos] = (byte) (((boolean) value) ? 0x1 : 0x0);
                    break;
                }
                if (reg.getLen() == 2) {
                    Util.ShortToBuff(buffer, pos, ((boolean) value) ? 0x1 : 0x0);
                    break;
                }
                if (reg.getLen() == 4) {
                    Util.floatToBuff(buffer, pos, (boolean) value ? 1.0f : 0.0f);
                    break;
                }

            case Util.CT_TYPE_INTEGER:
                if (reg.getLen() == 2) {
                    Util.ShortToBuff(buffer, pos, (int) value);
                    break;
                }
                if (reg.getLen() == 4) {
                    Util.floatToBuff(buffer, pos, (int) value);
                    break;
                }
            case Util.CT_TYPE_FLOAT:
                Util.floatToBuff(buffer, pos, (float) value);
                break;
            case Util.CT_TYPE_LONG:
                Util.IntegerToBuff(buffer, pos, (int) value);
                break;
            case Util.CT_TYPE_BYTE:
                buffer[pos] = (byte) (((int)value)&0xff);
                break;
                
        }
        return reg.getLen()+2;
    }
    @Override
    public String toString(){
        return "uId="+Integer.toString(reg.getuId())+" pId="+Integer.toString(PrpId)+" value="+value.toString();
    }
}


