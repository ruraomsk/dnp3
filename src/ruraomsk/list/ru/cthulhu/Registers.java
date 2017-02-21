/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import com.tibbo.aggregate.common.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class Registers {

    private ConcurrentHashMap<Long, OneReg> datac = null;
    private ConcurrentHashMap<Long, ArrayList<Property>> propc = null;
    private ConcurrentHashMap<Long, String> nameregc = null;
    private ConcurrentHashMap<String, Long> regcname;
    private ConcurrentHashMap<Long, ArrayList<OneReg>> history = null;
    private ConcurrentHashMap<Long, String> namepropc = null;
    private ConcurrentHashMap<Long, String> descriptions = null;
    private ConcurrentHashMap<Long, Long> goodtime = null;

    private Boolean block;

    public Registers() {
        regcname = new ConcurrentHashMap(Util.VALUE_UIDS);
        datac = new ConcurrentHashMap(Util.VALUE_UIDS);
        propc = new ConcurrentHashMap(Util.VALUE_UIDS);
        nameregc = new ConcurrentHashMap(Util.VALUE_UIDS);
        namepropc = new ConcurrentHashMap(Util.VALUE_UIDS);
        history = new ConcurrentHashMap(Util.VALUE_UIDS);
        descriptions = new ConcurrentHashMap(Util.VALUE_UIDS);
        goodtime = new ConcurrentHashMap(Util.VALUE_UIDS);
        block = true;
    }

    /**
     * Добавляем новую переменную в глобальную карту
     *
     * @param controller
     * @param reg
     * @param name
     */
    public OneReg addNewRegister(int controller, Register reg, String name) {
//      Таблица описаний регистров  
        Long key = reg.getKey(controller);
//        Создаем объект значений
        OneReg one = new OneReg(System.currentTimeMillis(), reg);
        ArrayList<Property> ar = new ArrayList<>();
        ArrayList<OneReg> arone = new ArrayList<>();
        propc.put(key, ar);
        datac.put(key, one);
        nameregc.put(key, name);
        regcname.put(name, key);
        history.put(key, arone);

        return one;
    }

    /**
     * Добавляем описание регистра
     *
     * @param controller
     * @param id
     * @param description
     */
    public void addDescription(int controller, int id, String description) {
        Long key = Register.makeKey(controller, id);
        descriptions.put(key, description);
    }

    public String getDescription(int controller, int id) {
        Long key = Register.makeKey(controller, id);
        return descriptions.get(key);
    }

    public String getDescription(String name) {
        Long key = regcname.get(name);
        if (key == null) {
            Log.CORE.info("Нет такого имени " + name);
            return null;
        }
        return descriptions.get(key);
    }

    /**
     * Уcтановить значение переменной
     *
     * @param controller
     * @param onereg
     */
    public void setRegister(int controller, OneReg onereg) {
        synchronized (block) {
            Long key = onereg.getReg().getKey(controller);
            if (onereg.getGood() == Util.CT_DATA_GOOD) {
                goodtime.put(key, onereg.getTime());
            }
            datac.put(key, onereg);
            ArrayList<OneReg> arone = history.get(key);
            arone.add(onereg);
            history.put(key, arone);
        }
    }

    /**
     * Уcтановить значение переменной не зависимо от времени
     *
     * @param controller
     * @param onereg
     */
    public void changeRegister(int controller, OneReg onereg) {
        synchronized (block) {
            Long key = onereg.getReg().getKey(controller);
            OneReg loreg = datac.get(key);
            if (onereg.getGood() == Util.CT_DATA_GOOD) {
                goodtime.put(key, onereg.getTime());
            }
            datac.put(key, onereg);
            ArrayList<OneReg> arone = history.get(key);
            arone.add(onereg);
            history.put(key, arone);
        }
    }

    /**
     * Возвращает коллекцию значений переменных по данному контроллеру
     *
     * @param controller номер контроллера
     * @return коллецию OneReg
     */
    public synchronized Collection<OneReg> getOneRegs(int controller) {
        synchronized (block) {
            HashMap<Long, OneReg> col = new HashMap<>();
            for (Long key : datac.keySet()) {
                int k = (int) (key >> 16);
                if (k == controller) {
                    col.put(key, datac.get(key));
                }
            }
            return col.values();
        }
    }

    /**
     * Возвращает значение переменных по данному контроллеру
     *
     * @param controller номер контроллера
     * @param uId
     * @return экземпляр getOneReg
     */
    public OneReg getOneReg(int controller, int uId) {
        long key = Register.makeKey(controller, uId);
        return datac.get(key);
    }

    /**
     * Возвращает последнее хорошее время
     *
     * @param controller
     * @param uId
     * @return
     */
    public Long getLastGoodTime(int controller, int uId) {
        long key = Register.makeKey(controller, uId);
        return goodtime.get(key);
    }

    /**
     * Возвращает историю измениения значения регистра
     *
     * @param controller
     * @param uId
     * @return ArrayList<CtOneReg>
     */
    public ArrayList<OneReg> getHistoryOneReg(int controller, int uId) {
        synchronized (block) {
            long key = Register.makeKey(controller, uId);
            ArrayList<OneReg> temp = new ArrayList<>(history.get(key));
            history.put(key, new ArrayList<OneReg>());
            return temp;
        }
    }

    /**
     * Возвращает историю измениения значения регистра
     *
     * @param name
     * @return ArrayList<CtOneReg>
     */
    public ArrayList<OneReg> getHistoryOneReg(String name) {
        Long key = regcname.get(name);
        if (key == null) {
            Log.CORE.info("Нет такого имени " + name);
            return new ArrayList<>();
        }
        synchronized (block) {
            ArrayList<OneReg> temp = new ArrayList<>(history.get(key));
            history.put(key, new ArrayList<OneReg>());
            return temp;
        }
    }

    /**
     * Возвращает значение переменных по имени
     *
     * @param name - имя регистра данных
     * @return экземпляр getOneReg
     */
    public OneReg getOneReg(String name) {
        Long key = regcname.get(name);
        if (key == null) {
            Log.CORE.info("Нет такого имени " + name);
            return null;
        }
        return datac.get(key);
    }

    public int getContoller(String name) {
        Long key = regcname.get(name);
        return (int) (key >> 16);
    }

    /**
     * Возвращает имя переменной по данному контроллеру и идентификатору
     *
     * @param controller номер контроллера
     * @param uId
     * @return имя переменной
     */
    public String getNameReg(int controller, int uId) {
        long key = Register.makeKey(controller, uId);
        return nameregc.get(key);
    }

    /**
     * Возвращает имя свойства по данному контроллеру,идентификатору и номеру
     * свойства
     *
     * @param controller номер контроллера
     * @param uId
     * @param propId
     * @return имя переменной
     */
    public String getNameProp(int controller, int uId, int propId) {
        long key = Register.makeKey(controller, uId);
        key = (key << 16) + propId;
        return namepropc.get(key);
    }

    /**
     * Возвращает экземпляр свойства по данному контроллеру,идентификатору и
     * номеру свойства
     *
     * @param controller номер контроллера
     * @param uId
     * @param propId
     * @return имя переменной
     */
    public Property getProperty(int controller, int uId, int propId) {
        long key = Register.makeKey(controller, uId);
        ArrayList<Property> ap = propc.get(key);
        for (Property ct : ap) {
            if (ct.getPrpId() == propId) {
                return ct;
            }
        }
        return null;
    }

    /**
     * добавляет экземпляр свойства по данному контроллеру,идентификатору и
     * номеру свойства
     *
     * @param controller номер контроллера
     * @param uId
     * @param propId
     */
    public Property addProperty(int controller, int uId, int propId, String name) {
        long key = Register.makeKey(controller, uId);
        OneReg reg = datac.get(key);
        if (reg == null) {
            Log.CORE.info("Нет регистра с " + Long.toString(key));
            return null;
        }
        ArrayList<Property> ap = propc.get(key);
        if (ap == null) {
            ap = new ArrayList<>();
        }
        Property ctp = new Property(reg.getReg(), propId);
        synchronized (block) {
            ap.add(ctp);
            propc.put(key, ap);
            key = (key << 16) | propId;
            namepropc.put(key, name);
            return ctp;
        }
    }

    /**
     * Заменяет экземпляр свойства по данному контроллеру,идентификатору и
     * номеру свойства
     *
     * @param controller номер контроллера
     * @param prp
     */
    public synchronized void setProperty(int controller, Property prp) {
        synchronized (block) {
            Register reg = prp.getRegister();
            long key = reg.getKey(controller);
            ArrayList<Property> ap = propc.get(key);
            for (Property ct : ap) {
                if (ct.getPrpId() == prp.getPrpId()) {
                    ap.remove(ct);
                    break;
                }
            }
            ap.add(prp);
            propc.put(key, ap);
        }
    }

    /**
     * Возвращает список параметров переменной по данному контроллеру и
     * идентификатору
     *
     * @param controller номер контроллера
     * @param uId
     * @return ArrayList с Property
     */
    public ArrayList<Property> getProperties(int controller, int uId) {
        long key = Register.makeKey(controller, uId);
        return propc.get(key);
    }

    @Override
    public String toString() {
        String result = "registers ";
        for (Long key : datac.keySet()) {
            result += nameregc.get(key) + " " + datac.get(key).toString() + "\n";
            result += "properties \n";
            ArrayList<Property> arpr = propc.get(key);
            if (arpr == null) {
                result += "empty\n";
                continue;
            }
            for (Property pr : arpr) {
                Long key1 = (key << 16) | pr.getPrpId();
                result += namepropc.get(key1) + " " + pr.toString() + "\n";
            }
        }
        return result;
    }
}
