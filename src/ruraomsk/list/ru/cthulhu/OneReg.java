/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import java.util.Date;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class OneReg {

    private long date = System.currentTimeMillis();
    private Register reg = null;
    private Object value = null;
    private byte good = Util.CT_DATA_NOGOOD;

    public OneReg(long date, Register reg, Object value, byte good) {
        this.date = date;
        this.reg = reg;
        this.value = value;
        this.good = good;
    }

    public OneReg(long date, Register reg, Object value) {
        this.date = date;
        this.reg = reg;
        this.value = value;
        this.good = Util.CT_DATA_GOOD;
    }

    public OneReg(long date, Register reg) {
        this.date = date;
        this.reg = reg;
        this.value = reg.makeEmptyValue();
        this.good = Util.CT_DATA_NOGOOD;
    }

    @Override
    public String toString() {
        return ("{" + Util.dateStr(date) + " uId=" + Integer.toString(reg.getuId())
                + " value=" + value.toString() + " good=0x" + Integer.toHexString(good) + " "
                + reg.getTypeName() + " " + reg.getStatusName() + "}");
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return new Date(date);
    }

    public long getTime() {
        return date;
    }

    public void setTime(long time) {
        date = time;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date.getTime();
    }

    /**
     * @return the reg
     */
    public Register getReg() {
        return reg;
    }

    /**
     * @param reg the reg to set
     */
    public void setReg(Register reg) {
        this.reg = reg;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        if (reg.getType() == 3) {
            if (value.getClass().getName().equals("java.lang.Long")) {
                return ((long) value & 0xffffffff);
            }
            return (long) ((int) value & 0xffffffff);
        }
        if (reg.getType() == 4) {
            if (value.getClass().getName().equals("java.lang.Byte")) {
                return (byte) ((byte) value & 0xff);
            }
            return (byte) ((int) value & 0xff);
        }
        return value;
    }

    public String getValueToString() {
        if (reg.getType() == 3) {
            Long longValue;
            if (value.getClass().getName().equals("java.lang.Long")) {
                longValue= ((long) value & 0xffffffff);
            } else{
                longValue= (long) ((int) value & 0xffffffff);
            }
            return longValue.toString();
        }
        if (reg.getType() == 4) {
            Byte byteValue;
            if (value.getClass().getName().equals("java.lang.Byte")) {
                byteValue= (byte) ((byte) value & 0xff);
            }else{
                byteValue= (byte) ((int) value & 0xff);
            }
            return byteValue.toString();
        }
        if (reg.getType() == 0) {
            return (boolean)value?"истина":"ложь";
        }
        return value.toString();
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return the good
     */
    public byte getGood() {
        return good;
    }

    /**
     * @param good the good to set
     */
    public void setGood(byte good) {
        this.good = good;
    }

    public int getuId() {
        return reg.getuId();
    }

    public int toBuffer(byte[] buffer, int pos) {
        Util.ShortToBuff(buffer, pos, reg.getuId());
        pos += 2;
        switch (getReg().getType()) {
            case Util.CT_TYPE_BOOL:
                if (getReg().getLen() == 1) {
                    buffer[pos] = (byte) (((boolean) getValue()) ? 0x1 : 0x0);
                    break;
                }
                if (getReg().getLen() == 2) {
                    Util.ShortToBuff(buffer, pos, ((boolean) getValue()) ? 0x1 : 0x0);
                    break;
                }
                if (getReg().getLen() == 4) {
                    Util.floatToBuff(buffer, pos, (boolean) getValue() ? 1.0f : 0.0f);
                    break;
                }

            case Util.CT_TYPE_INTEGER:
                if (getReg().getLen() == 2) {
                    Util.ShortToBuff(buffer, pos, (int) getValue());
                    break;
                }
                if (getReg().getLen() == 4) {
                    Util.IntegerToBuff(buffer, pos, (int) getValue());
                    break;
                }
            case Util.CT_TYPE_FLOAT:
                Util.floatToBuff(buffer, pos, (float) getValue());
                break;
            case Util.CT_TYPE_LONG:
                Util.IntegerToBuff(buffer, pos, (int) (((long) getValue()) & 0xffffffff));
                break;
            case Util.CT_TYPE_BYTE:
                buffer[pos] = (byte) getValue();
                break;

        }
        return getReg().getLen() + 2;
    }

    public int getBuffer(byte[] buffer, int pos) {
        switch (getReg().getType()) {
            case Util.CT_TYPE_BOOL:
                if (getReg().getLen() == 1) {
                    setValue((buffer[pos] != 0));
                    break;
                }
                if (getReg().getLen() == 2) {
                    setValue((Util.ToShort(buffer, pos) != 0));
                    break;
                }
                if (getReg().getLen() == 4) {
                    setValue(Util.ToFloat(buffer, pos) != 0.0);
                    break;
                }

            case Util.CT_TYPE_INTEGER:
                if (getReg().getLen() == 2) {
                    setValue(Util.ToShort(buffer, pos));
                    break;
                }
                if (getReg().getLen() == 4) {
                    setValue((int) Util.ToFloat(buffer, pos));
                    break;
                }

            case Util.CT_TYPE_FLOAT:
                setValue(Util.ToFloat(buffer, pos));
                break;
            case Util.CT_TYPE_LONG:
                long longValue=(long)(Util.ToInteger(buffer, pos) & 0xffffffff);
                if(longValue<0) longValue=0-longValue;
                setValue(longValue);
                break;
            case Util.CT_TYPE_BYTE:
                setValue((byte) buffer[pos]);
                break;

        }
        setGood(Util.CT_DATA_GOOD);
        return getReg().getLen();
    }
}
