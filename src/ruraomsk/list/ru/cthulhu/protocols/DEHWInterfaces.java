/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.protocols;

import java.util.ArrayList;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class DEHWInterfaces extends Inter 
{
    ArrayList<DEHWInterfacesData> arrData;
    int DEHWInterfacesIDUsed;
    public DEHWInterfaces()
    {
        id=3;
        arrData=new ArrayList<>();
    }
    public int getDEHWInterfacesCount(){
        return arrData.size();
    }
    public int getDEHWInterfacesIDUsed(){
        return DEHWInterfacesIDUsed;
    }
    public void setDEHWInterfacesIDUsed(int id){
        DEHWInterfacesIDUsed=id;
    }
    public void addDEHWInterfacesData(DEHWInterfacesData data){
        arrData.add(data);
    }
    @Override
    public int frombuffer(byte[] buffer, int pos)
    {
        int start=pos;
        int count=Util.ToShort(buffer, pos);
        pos+=2;
        DEHWInterfacesIDUsed=Util.ToShort(buffer, pos);
        pos+=2;
        arrData=new ArrayList<>();
        for (int i = 0; i < count; i++) {
            DEHWInterfacesData eData=new DEHWInterfacesData();
            pos+=eData.frombuffer(buffer, pos);
            arrData.add(eData);
        }
        return pos-start;
    }

    @Override
    public String toString()
    {
        String res= " DEHWInerfaces Count="+Integer.toString(arrData.size())+" IDUsed="+Integer.toString(DEHWInterfacesIDUsed);
        for (DEHWInterfacesData dData : arrData) {
            res+=dData.toString()+" ";
        }
        return res;
    }

    @Override
    public int tobuffer(byte[] buffer, int pos)
    {
        int start=pos;
        Util.ShortToBuff(buffer, pos, arrData.size());
        pos+=2;
        Util.ShortToBuff(buffer, pos, DEHWInterfacesIDUsed);
        pos+=2;
        for (DEHWInterfacesData dData : arrData) {
            pos+=dData.tobuffer(buffer, pos);
        }
        return pos-start;
    }

    public class DEHWInterfacesData extends Inter
    {
        int DEHWInterfaceID;
        int DEHWInterfaceTypeID;
        int DEHWInterfaceControllerID;        

        public DEHWInterfacesData()
        {
        }

        public DEHWInterfacesData(int DEHWInterfaceID, int DEHWInterfaceTypeID, int DEHWInterfaceControllerID)
        {
            this.DEHWInterfaceID = DEHWInterfaceID;
            this.DEHWInterfaceTypeID = DEHWInterfaceTypeID;
            this.DEHWInterfaceControllerID = DEHWInterfaceControllerID;
        }

        @Override
        public int frombuffer(byte[] buffer, int pos)
        {
            this.DEHWInterfaceID = Util.ToShort(buffer, pos);
            pos+=2;
            this.DEHWInterfaceTypeID = Util.ToShort(buffer, pos);
            pos+=2;
            this.DEHWInterfaceControllerID = Util.ToShort(buffer, pos);
            return 6;
        }

        @Override
        public String toString()
        {
            return "{"+Integer.toString(DEHWInterfaceID)+" "+(DEHWInterfaceTypeID==1?"COM":"Ethernet")+" "
                    +(DEHWInterfaceControllerID==0?"Unknow":DEHWInterfaceControllerID==1?"UART":"W31XXA")+"}";
        }

        @Override
        public int tobuffer(byte[] buffer, int pos)
        {
            Util.ShortToBuff(buffer, pos, DEHWInterfaceID);
            pos+=2;
            Util.ShortToBuff(buffer, pos, DEHWInterfaceTypeID);
            pos+=2;
            Util.ShortToBuff(buffer, pos, DEHWInterfaceControllerID);
            return 6;
        }
    }
    
}
