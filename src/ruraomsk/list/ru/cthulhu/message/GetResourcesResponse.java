/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import java.util.ArrayList;
import ruraomsk.list.ru.cthulhu.BaseMessage;
import ruraomsk.list.ru.cthulhu.Util;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class GetResourcesResponse extends BaseMessage
{

    ArrayList<Resource> resources;

    public GetResourcesResponse()
    {
        itsResponse();
        setId(Util.GetResources);
        resources = new ArrayList<>();
    }

    public void addResource(Resource res)
    {
        resources.add(res);
    }

    public void addResource(int SSID, int SSVer, byte SSResFlNum, int SSResCRC)
    {
        resources.add(new Resource(SSID, SSVer, SSResFlNum, SSResCRC));
    }

    public ArrayList<Resource> getResources()
    {
        return resources;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len)
    {
//        itsResponse();
//        setReqEC(buffer[pos+3]);
        pos = pos+4;
        int count = buffer[pos++];
        while (count > 0) {
            int ssid = Util.ToShort(buffer, pos);
            pos += 2;
            int ssver = Util.ToInteger(buffer, pos);
            pos += 4;
            byte ssresflnum = buffer[pos++];
            int ssrescrc = Util.ToShort(buffer, pos);
            pos += 2;
            Resource rs = new Resource(ssid, ssver, ssresflnum, ssrescrc);
            resources.add(rs);
            count--;
        }
    }

    @Override
    public int toBuffer(byte[] buffer, int pos)
    {
        makeHeader(buffer, pos);
        pos=pos+4;
        buffer[pos++]=(byte) (resources.size()&0x7f);
        int len=5;

        for (Resource resource : resources) {
            Util.ShortToBuff(buffer, pos,resource.getSSID());
            pos+=2;
            Util.IntegerToBuff(buffer, pos,resource.getSSVer());
            pos+=4;
            buffer[pos++]=resource.getSSResFlNum();
            Util.ShortToBuff(buffer, pos,resource.getSSResCRC());
            pos+=2;
            
        }
        
        return super.toBuffer(buffer, pos); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString()
    {
        String res = super.toString();
        res += "{";
        for (Resource resource : resources) {
            res += " " + resource.toString();
        }
        res += "}";
        return res;
    }

    public static class Resource
    {

        private Integer SSID;
        private Integer SSVer;
        private Byte SSResFlNum;
        private Integer SSResCRC;

        public Resource(int SSID, int SSVer, byte SSResFlNum, int SSResCRC)
        {
            this.SSID = SSID;
            this.SSVer = SSVer;
            this.SSResFlNum = SSResFlNum;
            this.SSResCRC = SSResCRC;
        }

        @Override
        public String toString()
        {
            return "[" + Integer.toHexString(getSSID()) + ":" + Integer.toHexString(getSSVer()) + ":" + getSSResFlNum().toString() + ":" + Integer.toHexString(getSSResCRC()) + "]";
        }

        /**
         * @return the SSID
         */
        public Integer getSSID()
        {
            return SSID&0xffff;
        }

        /**
         * @param SSID the SSID to set
         */
        public void setSSID(Integer SSID)
        {
            this.SSID = SSID;
        }

        /**
         * @return the SSVer
         */
        public Integer getSSVer()
        {
            return SSVer&0xffff;
        }

        /**
         * @param SSVer the SSVer to set
         */
        public void setSSVer(Integer SSVer)
        {
            this.SSVer = SSVer;
        }

        /**
         * @return the SSResFlNum
         */
        public Byte getSSResFlNum()
        {
            return SSResFlNum;
        }

        /**
         * @param SSResFlNum the SSResFlNum to set
         */
        public void setSSResFlNum(Byte SSResFlNum)
        {
            this.SSResFlNum = SSResFlNum;
        }

        /**
         * @return the SSResCRC
         */
        public Integer getSSResCRC()
        {
            return SSResCRC&0xffff;
        }

        /**
         * @param SSResCRC the SSResCRC to set
         */
        public void setSSResCRC(Integer SSResCRC)
        {
            this.SSResCRC = SSResCRC;
        }
    }
}
