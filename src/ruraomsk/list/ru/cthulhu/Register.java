/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import com.tibbo.aggregate.common.Log;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class Register {

    /**
     * @return the constant
     */
    public boolean isConstant() {
        return constant;
    }

    /**
     * @param constant the constant to set
     */
    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    private int uId = 0;
    private int type = 0; // 0-bool  1-int  2-float
    private int len = 0;
    private boolean archived = false;
    private boolean sending = false;
    private boolean eprom = false;
    private boolean constant=false;
    private boolean readonly=false;
    

    public Register(int uId, int type) {
        this.uId = uId;
        this.type = type;
        if (type == Util.CT_TYPE_FLOAT) {
            this.len = 4;
        }
        if (type == Util.CT_TYPE_BOOL) {
            this.len = 1;
        }
        if (type == Util.CT_TYPE_BYTE) {
            this.len = 1;
        }
        if (type == Util.CT_TYPE_LONG) {
            this.len = 4; //??????????????????????????
        }
        if (type == Util.CT_TYPE_INTEGER) {
            this.len = 2;
        }
    }

    public Register(int uId, int type, int len) {
        this.uId = uId;
        this.type = type;
        this.len = len;
    }

    public Object makeEmptyValue() {
        switch (getType()) {
            case Util.CT_TYPE_BOOL:
                return false;
            case Util.CT_TYPE_INTEGER:
                return 0;
            case Util.CT_TYPE_FLOAT:
                return 0.0f;
            case Util.CT_TYPE_LONG:
                return 0L;
            case Util.CT_TYPE_BYTE:
                return (byte)0;
        }
        Log.CORE.info("Нет такого типа " + toString());
        return 0;
    }

    /**
     * @return the uId
     */
    public int getuId() {
        return uId;
    }

    /**
     * @param uId the uId to set
     */
    public void setuId(int uId) {
        this.uId = uId;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    public long getKey(int controller) {
        return (controller << 16) + uId;
    }

    public static long makeKey(int controller, int uId) {
        return (controller << 16) + uId;
    }

    @Override
    public String toString() {
        return (" uId=" + Integer.toString(getuId()) + " type=" + Integer.toString(getType()) + " len=" + Integer.toString(getLen()));
    }

    /**
     * @return the len
     */
    public int getLen() {
        return len;
    }

    /**
     * @param len the len to set
     */
    public void setLen(int len) {
        this.len = len;
    }

    /**
     * @return the archived
     */
    public boolean isArchived() {
        return archived;
    }

    /**
     * @param archived the archived to set
     */
    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    /**
     * @return the sending
     */
    public boolean isSending() {
        return sending;
    }

    /**
     * @param sending the sending to set
     */
    public void setSending(boolean sending) {
        this.sending = sending;
    }

    /**
     * @return the eprom
     */
    public boolean isEprom() {
        return eprom;
    }

    /**
     * @param eprom the eprom to set
     */
    public void setEprom(boolean eprom) {
        this.eprom = eprom;
    }
    public void setReadOnly(boolean readonly){
        this.readonly=readonly;
    }
    public boolean isReadOnly(){
        return readonly;
    }
    
    public String getTypeName() {
        switch (this.type) {
            case Util.CT_TYPE_BOOL:
                return "bool";
            case Util.CT_TYPE_INTEGER:
                return "short";
            case Util.CT_TYPE_FLOAT:
                return "float";
            case Util.CT_TYPE_LONG:
                return "long";
            case Util.CT_TYPE_BYTE:
                return "byte";
        }
        return "error";
    }
    public String getStatusName(){
        String res="";
        res+=isSending()?"send ":"";
        res+=isEprom()?"eprom ":"";
        res+=isArchived()?"arch ":"";
        res+=isConstant()?"const ":"";
        
        return res;
    }
}
