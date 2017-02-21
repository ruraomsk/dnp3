/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.message;

import com.tibbo.aggregate.common.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import ruraomsk.list.ru.cthulhu.*;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class GetRingBufferResponse extends BaseMessage {

    private int RBIter;
    private int RBFree;
    private byte RBFlags;
    private long timelost;
    private ArrayList<OneReg> losts;
    private HashMap<Long, ArrayList<OneReg>> oregs;

    public GetRingBufferResponse() {
        itsResponse();
        setId(Util.GetRingBuffer);
        losts = new ArrayList<>();
        RBIter = 0;
        RBFlags = 0;
        RBFree = 0;
        timelost = 0L;
        oregs = new HashMap<>();
    }

    public void addLostOneReg(OneReg onereg) {
        losts.add(onereg);
    }

    public ArrayList<OneReg> getLosts() {
        return losts;
    }

    public void addOneReg(OneReg onereg) {
        if (!oregs.containsKey(onereg.getTime())) {
            ArrayList<OneReg> ar = new ArrayList<>();
            oregs.put(onereg.getTime(), ar);
        }
        ArrayList<OneReg> ar = oregs.get(onereg.getTime());
        ar.add(onereg);
        oregs.put(onereg.getTime(), ar);
    }

    public Collection<Long> getTimes() {
        return oregs.keySet();
    }

    public ArrayList<OneReg> getOneRegs(long time) {
        return oregs.get(time);
    }

    @Override
    public int toBuffer(byte[] buffer, int pos) {
        makeHeader(buffer, pos);
        int startpos = pos;
        pos += 4;
        Util.ShortToBuff(buffer, pos, RBIter);
        pos += 2;
        Util.ShortToBuff(buffer, pos, RBFree);
        pos += 2;
        buffer[pos++] = RBFlags;
        int pos1 = pos;
        // тут потом запишем длину сегмента данных 1
        pos += 2;
        if (RBFlags == 0) {
            Util.TimeToBuff(buffer, pos, timelost);
            pos += 8;
            int pos2 = pos;
            Util.ShortToBuff(buffer, pos, losts.size() * 2);
            pos += 2;
            for (OneReg or : getLosts()) {
                Util.ShortToBuff(buffer, pos, or.getReg().getuId());
                pos += 2;
            }
            Util.ShortToBuff(buffer, pos2, pos - pos2 - 2);
        }
        if (RBFlags == 1) {
            for (Long time : getTimes()) {
                Util.TimeToBuff(buffer, pos, time);
                pos += 8;
                int pos2 = pos;
                // тут потом запишем длину сегмента данных 2
                pos += 2;
                for (OneReg or : getOneRegs(time)) {
                    pos += or.toBuffer(buffer, pos);
                    buffer[pos++] = or.getGood();
                }
                Util.ShortToBuff(buffer, pos2, pos - pos2 - 2);
            }
            Util.ShortToBuff(buffer, pos1, pos - pos1 - 2);
        }
        return pos - startpos;
    }

    @Override
    public void fromBuffer(byte[] buffer, int pos, int len) {
//        if (len == 28) {
//            return;
//        }
//        if (len == 11) {
//            return;
//        }
        long time = 0L;
//        Util.bufferToString(buffer, pos, len);
        pos = pos + 4;
        RBIter = Util.ToShort(buffer, pos);
        pos += 2;
        RBFree = Util.ToShort(buffer, pos);
        pos += 2;
        RBFlags = buffer[pos++];
        int lastpos = Util.ToShort(buffer, pos)+pos+2;
        pos += 2;
        if ((RBFlags & 1) != 0) {
            time = Util.ToTime(buffer, pos);
            pos += 8;
            timelost = Util.convertDate(time);
            int lenseg2 = Util.ToShort(buffer, pos);
            pos += 2;
            losts.clear();
            while (lenseg2 > 0) {
                int id = Util.ToShort(buffer, pos);
                pos += 2;
                lenseg2 -= 1;
                OneReg r = getRegs().getOneReg(getController(), id);
                if (r == null) {
                    Log.CORE.info("Нет регистра " + Integer.toString(getController()) + " " + Integer.toString(id));
                    continue;
                }
                OneReg oreg = new OneReg(timelost, r.getReg());
                losts.add(oreg);
            }

        }
        if ((RBFlags & 2) != 0) {
            while (pos < lastpos) {
                time = Util.convertDate(Util.ToTime(buffer, pos));
                pos += 8;
                int lpos = Util.ToShort(buffer, pos)+pos+2;
                pos += 2;
                while (pos<lpos) {
                    int id = Util.ToShort(buffer, pos);
                    pos += 2;
                    OneReg r = getRegs().getOneReg(getController(), id);
                    if (r == null) {
                        Util.bufferToString(buffer, pos-10, lastpos-pos-10);
                        Log.CORE.info("Нет регистра данных " + Integer.toString(getController()) + " " + Integer.toString(id));
                        pos=lpos;
                        continue;
                    }
                    OneReg oreg = new OneReg(time, r.getReg());
                    int s = oreg.getBuffer(buffer, pos);
                    pos += s;
                    oreg.setGood(buffer[pos++]);
                    addOneReg(oreg);
//                System.out.println("added " + oreg.toString());
                }
            }

        }
    }

    @Override
    public String toString() {
        String result = super.toString();
        result += " RBIter=" + Integer.toString(RBIter) + " RBFree=" + Integer.toString(RBFree)
                + " RBFlags=" + Integer.toString(RBFlags) + "\n";
        if (timelost != 0L) {
            result += " TimeLost=" + new Date(timelost).toString() + "[";
            for (OneReg or : getLosts()) {
                result += Integer.toString(or.getReg().getuId()) + " ";
            }
            result += "]\n";
        }
        for (Long time : getTimes()) {
            result += " TimeValue=" + new Date(time).toString() + "[";
            for (OneReg or : getOneRegs(time)) {
                result += or.toString();
            }
            result += "]";

        }
        return result;
    }

    /**
     * @return the RBIter
     */
    public int getRBIter() {
        return RBIter;
    }

    /**
     * @param RBIter the RBIter to set
     */
    public void setRBIter(int RBIter) {
        this.RBIter = RBIter;
    }

    /**
     * @return the RBFree
     */
    public int getRBFree() {
        return RBFree;
    }

    /**
     * @param RBFree the RBFree to set
     */
    public void setRBFree(int RBFree) {
        this.RBFree = RBFree;
    }

    /**
     * @return the RBFlags
     */
    public byte getRBFlags() {
        return RBFlags;
    }

    /**
     * @param RBFlags the RBFlags to set
     */
    public void setRBFlags(byte RBFlags) {
        this.RBFlags = RBFlags;
    }

    /**
     * @return the timelost
     */
    public long getTimelost() {
        return timelost;
    }

    /**
     * @param timelost the timelost to set
     */
    public void setTimelost(long timelost) {
        this.timelost = timelost;
    }

}
