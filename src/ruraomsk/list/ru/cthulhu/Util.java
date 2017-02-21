/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import com.tibbo.aggregate.common.Log;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import ruraomsk.list.ru.cthulhu.protocols.*;

/**
 * Константы и базовые функции
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class Util {

    /**
     * Вычисление контрольной суммы
     *
     * @param buffer
     * @param pos
     * @param len
     * @return контрольную сумму
     */
    public static final int Crc(byte[] buffer, int pos, int len) {
//        if ((len & 1) > 0) {
//            buffer[pos + len] = 0;
//            len++;
//        }
        int crc = ((len & 1) > 0) ? buffer[pos + len - 1] & 0xff : 0;
        len = len >> 1;
        for (int i = 0; i < len; i++) {

            crc += (ToShort(buffer, pos) & 0xffff);
            crc = crc & 0xffff;
            pos += 2;
        }
        return crc & 0xffff;
    }

    /**
     * Из буфера в короткое целое
     *
     * @param bytes
     * @param idx
     * @return
     */
    public static final int ToShort(byte bytes[], int idx) {
        return (int) ((((bytes[idx + 1] & 0xff) << 8) | (bytes[idx] & 0xff)) & 0xffff);
    }

    /**
     * Из буфера в обычное целое
     *
     * @param bytes
     * @param idx
     * @return
     */
    public static final int ToInteger(byte bytes[], int idx) {
        return (int) (((bytes[idx + 3]&0xff) << 24) | ((bytes[idx + 2]&0xff) << 16) | ((bytes[idx + 1]&0xff) << 8) | (bytes[idx] & 0xff));
    }
    /**
     * Из буфера целое как лонг целое
     *
     * @param bytes
     * @param idx
     * @return
     */
    public static final long LongAsInteger(byte bytes[], int idx) {
        return (long) (((bytes[idx + 3]&0xff) << 24) | ((bytes[idx + 2]&0xff) << 16) | ((bytes[idx + 1]&0xff) << 8) | (bytes[idx] & 0xff));
    }

    /**
     * Целое 4 байта
     *
     * @param v
     * @return
     */
    public static final byte[] intToRegisters(int v) {
        byte registers[] = new byte[4];
        registers[3] = (byte) (0xff & v >> 24);
        registers[2] = (byte) (0xff & v >> 16);
        registers[1] = (byte) (0xff & v >> 8);
        registers[0] = (byte) (0xff & v);
        return registers;
    }

    /**
     * Целое в буфер записывает два байта
     *
     * @param bytes
     * @param idx
     * @param var
     */
    public static final void ShortToBuff(byte bytes[], int idx, int var) {
        bytes[idx + 1] = (byte) ((var >> 8) & 0xff);
        bytes[idx] = (byte) (0xff & var);
    }

    /**
     * Целое в буфер записывает четыре байта
     *
     * @param bytes
     * @param idx
     * @param var
     */
    public static final void IntegerToBuff(byte bytes[], int idx, int var) {
        bytes[idx + 3] = (byte) ((var >> 24) & 0xff);
        bytes[idx + 2] = (byte) ((var >> 16) & 0xff);
        bytes[idx + 1] = (byte) ((var >> 8) & 0xff);
        bytes[idx] = (byte) (0xff & var);
    }

    /**
     * Длинное целое в буфкр массива байтов восемь байт
     *
     * @param bytes
     * @param idx
     * @param var
     */
    public static final void LongToBuff(byte bytes[], int idx, long var) {
        bytes[idx + 7] = (byte) (0xff & (var >> 56));
        bytes[idx + 6] = (byte) (0xff & (var >> 48));
        bytes[idx + 5] = (byte) (0xff & (var >> 40));
        bytes[idx + 4] = (byte) (0xff & (var >> 32));
        bytes[idx + 3] = (byte) (0xff & (var >> 24));
        bytes[idx + 2] = (byte) (0xff & (var >> 16));
        bytes[idx + 1] = (byte) (0xff & (var >> 8));
        bytes[idx] = (byte) (0xff & var);
    }

    /**
     * Из буфера 4 байта в плавающее
     *
     * @param bytes
     * @param idx
     * @return
     */
    public static final float ToFloat(byte bytes[], int idx) {
        return Float.intBitsToFloat((bytes[idx + 3] & 0xff) << 24 | (bytes[idx + 2] & 0xff) << 16 | (bytes[idx + 1] & 0xff) << 8 | bytes[idx] & 0xff);
    }

    /**
     * Плавающее в буфер 4 байта
     *
     * @param bytes
     * @param idx
     * @param f
     */
    public static final void floatToBuff(byte bytes[], int idx, float f) {
        byte registers[] = intToRegisters(Float.floatToIntBits(f));
        System.arraycopy(registers, 0, bytes, idx, registers.length);
    }

    public static Inter newProtocol(int DEStngID) {
        switch (DEStngID) {
            case 1:
                return new DEVersion();
            case 2:
                return new NTPServiceRunning();

            case 3:
                return new DEHWInterfaces();

            case 11:
                return new DEHWInterfaceCPoints();

            case 12:
                return new FullDuplexMode();

            case 13:
                return new SlipMode();

            case 21:
                return new SocketInfo();

            case 22:
                return new CommInfo();

            case 23:
                return new MessageMaxSize();

            case 24:
                return new GVPSettings();

            case 25:
                return new RBInterval();

            case 26:
                return new RBSize();

            case 31:
                return new DISRBInterval();

            case 32:
                return new DISRBSize();

            case 33:
                return new LCDMaxMessageSize();

            case 34:
                return new DERBInterval();

            case 35:
                return new DERBSize();

        }
        return null;
    }

    /**
     * Фабрика объектов запрос/ответ принятых транспортом
     *
     * @param master     если истина то объект ответ иначе получен запрос
     * @param buffer     буфер с сообщением
     * @param pos        позиция в буфере
     * @param length     длина сообщения
     * @param regs
     * @param controller
     * @return созданный экземпляр запроса или ответа
     */
    public static BaseMessage newObject(boolean master, byte[] buffer, int pos, int length, Registers regs, int controller) {
        int id = buffer[pos + 2];
        String nameClass = "ruraomsk.list.ru.cthulhu.message." + Util.getShortName(id) + Util.getTail(!master);
        try {
            Class foo = Class.forName(nameClass);
            Object ms = new Object();
            ms = foo.newInstance();
            BaseMessage mess = (BaseMessage) ms;
            mess.setupWorld(controller, regs);
            mess.makeMessage(buffer, pos, length);
            return mess;
        }
        catch (ClassNotFoundException ex) {
            Log.CORE.info("Класс " + nameClass + " не найден");
            return null;
        }
        catch (InstantiationException | IllegalAccessException ex) {
            Log.CORE.info("Экземпляр класса " + nameClass + " не создан " + ex.getMessage());
            return null;
        }
    }

    /**
     * Из буфера извлекает длинное целое
     *
     * @param bytes
     * @param idx
     * @return
     */
    public static final long ToLong(byte bytes[], int idx) {
        return (long) (bytes[idx + 7] & 0xff) << 56 | (long) (bytes[idx + 6] & 0xff) << 48
                | (long) (bytes[idx + 5] & 0xff) << 40 | (long) (bytes[idx + 4] & 0xff) << 32
                | (long) (bytes[idx + 3] & 0xff) << 24 | (long) (bytes[idx + 2] & 0xff) << 16
                | (long) (bytes[idx + 1] & 0xff) << 8 | (long) (bytes[idx] & 0xff);
    }
    
    public static final String dateStr(long date){
        SimpleDateFormat df=new SimpleDateFormat("HH:mm:ss.S dd/MM/yy");
        return df.format(new Date(date));
    }
    /**
     * Из буфера извлекает NTP метку
     *
     * @param bytes
     * @param idx
     * @return NTP метка в формате Long
     */
//    private static long SMESH=524287l;//-664467855000l;
    public static final long ToTime(byte bytes[], int idx) {
        long sec = LongAsInteger(bytes, idx + 4);
        long dsec = LongAsInteger(bytes, idx);//&0x7fffffff;
        if(dsec<0) dsec=0-dsec;
        return (sec*1000L)+(dsec/418938L);//418938);//+((long)dsec); 
    }

    public static final long convertDate(long time) {
        return time;
    }

    /**
     * Записывает в буфер NTP метку
     *
     * @param bytes
     * @param idx
     * @param var
     */
    public static final void TimeToBuff(byte bytes[], int idx, long var) {
        bytes[idx + 3] = (byte) (0xff & (var >> 56));
        bytes[idx + 2] = (byte) (0xff & (var >> 48));
        bytes[idx + 1] = (byte) (0xff & (var >> 40));
        bytes[idx + 0] = (byte) (0xff & (var >> 32));
        bytes[idx + 7] = (byte) (0xff & (var >> 24));
        bytes[idx + 6] = (byte) (0xff & (var >> 16));
        bytes[idx + 5] = (byte) (0xff & (var >> 8));
        bytes[idx + 4] = (byte) (0xff & var);
    }

    public static final String getTail(boolean isRequest) {
        return isRequest ? "Request" : "Response";
    }

    public static final String makeEventName(boolean isRequest, int id) {
        return "Env" + (isRequest ? "Req" : "Res") + Integer.toHexString(id).toUpperCase();
    }

    public static final boolean isCorrectIdMessage(int id) {
        for (String[] nameRequest1 : nameRequest) {
            if (Integer.parseInt(nameRequest1[1], 16) == id) {
                return true;
            }
        }
        return false;
    }

    public static final String getShortName(int id) {
        for (String[] nameRequest1 : nameRequest) {
            if (Integer.parseInt(nameRequest1[1], 16) == id) {
                return nameRequest1[0];
            }
        }
        return "notFoundName";
    }

    public static final String getShortError(int code) {
        for (String[] nameErrorCode1 : nameErrorCode) {
            if (Integer.parseInt(nameErrorCode1[1]) == code) {
                return nameErrorCode1[0];
            }
        }
        return "notFoundError";
    }

    public static final String getLongName(int id) {
        for (String[] nameRequest1 : nameRequest) {
            if (Integer.parseInt(nameRequest1[1], 16) == id) {
                return nameRequest1[2];
            }
        }
        return "notFoundName";
    }

    public static final String getLongError(int code) {
        for (String[] nameErrorCode1 : nameErrorCode) {
            if (Integer.parseInt(nameErrorCode1[1]) == code) {
                return nameErrorCode1[2];
            }
        }
        return "notFoundError";
    }

    public static final InetAddress IAfrombuffer(byte[] buffer, int pos) {
        try {
            byte[] tadr = new byte[4];
            System.arraycopy(buffer, pos, tadr, 0, 4);
            return InetAddress.getByName(makeIP(tadr));
        }
        catch (UnknownHostException ex) {
            return null;
        }
    }

    public static final String makeIP(byte[] tadr) {
        return Integer.toString((int) (tadr[0] & 0xff))
                + "." + Integer.toString((int) (tadr[1] & 0xff))
                + "." + Integer.toString((int) (tadr[2] & 0xff))
                + "." + Integer.toString((int) (tadr[3] & 0xff));
    }

    public static final int IAtobuffer(byte[] buffer, int pos, InetAddress IAddr) {
        byte[] tadr = IAddr.getAddress();
        System.arraycopy(tadr, 0, buffer, pos, 4);
        return 4;
    }

    public static final void bufferToString(byte[] buffer, int pos, int len) {
        String shex = "";
        String sint = "";
        for (int i = pos; i < pos + len; i++) {
            String sout = Integer.toHexString(buffer[i] & 0xff);
            if (sout.length() == 1) {
                sout = "0" + sout;
            }
            shex += sout.toUpperCase() + " ";
            sout = Integer.toString(i & 0xff);
            if (sout.length() == 1) {
                sout = "0" + sout;
            }
            if (sout.length() > 2) {
                sout = sout.substring(1);
            }
            sint += sout + " ";
        }
        Log.CORE.info("[" + shex + "]");

    }
/**
 * Заполнение буфера нулем
 * @param buffer
 * @param pos
 * @param len 
 */
    public static final void clearbuffer(byte[] buffer, int pos, int len) {
        for (int i = 0; i < len; i++) {
            buffer[pos++] = 0;
        }
    }
    /**
     * Создание имени файла для чтения из eeprom
     * @param number
     * @return сформированное имя файла
     */
    public static String makeNameFile(Integer number){
        return "c:/eeprom/data" + number.toString();
    }
    public final static int MAX_LEN = 16000;
    static final byte[] outbuf = new byte[MAX_LEN];
    static final byte[] buffer = new byte[MAX_LEN];
    public final static int MAX_GET_FILE_SIZE = 841;

    public static final String dateToStr(long date) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.S ");
        return df.format(new Date(date));
    }

    /**
     * Коды запросов
     */
    static final public String[][] nameRequest = {
        {"GetProperty", "11", "Получить значение свойства. Каждый параметр имеет фиксированный набор свойств. Каждое свойство имеет идентификатор и наименование."},
        {"GetValueProperty", "12", "Получить значение свойства “значение”"},
        {"GetRingBuffer", "13", "Получить содержимое кольцевого буфера значений параметров"}, //
        {"SetProperty", "21", "Установить значение свойства"}, //
        {"SetValueProperty", "22", "Установить значение свойства “значение”"}, //
        {"ReadFile", "31", "Получить (фрагмент) файла"}, //
        {"GetFileSize", "32", "Получить размер файла"}, //
        {"GetFileRecord", "33", "Получить сведения о файле. Сведения включают размер, атрибуты, а также метки времени создания, модификации, доступа к файлу"}, //
        {"GetFileAttributes", "34", "Получить атрибуты файла"}, //
        {"GetFileTimestamps", "35", "Получить метки времени (создания, модификации, доступа) файла"}, //
        {"WriteFile", "41", "Записать (фрагмент) файла. Данный запрос не приводит к модификации файла."}, //
        {"CommitFile", "42", "Сохранить изменения в файле, полученные в результате предшествующих запросов WriteFile"}, //
        {"DeleteFile", "43", "Удалить файл"}, //
        {"SetFileAttributes", "44", "Установить атрибуты файла"}, //
        {"SetFileTimestamps", "45", "Установить метки времени (создания, модификации, доступа) файла"}, //
        {"GetFreeDiskSpace", "51", "Получить размер свободного дискового про-странства (Начиная с версии протокола 0x23 считается, что в рамках данного запроса требу-ется установить размер свободного дискового пространства текущего тома)"}, //
        {"GetCurrentVolume", "52", "Получить наименование текущего тома"}, //
        {"ChangeVolume", "53", "Установить текущий том"}, //
        {"GetCurrentDirectory", "54", "Получить наименование текущей папки"}, //	
        {"SetCurrentDirectory", "55", "Установить текущую папку"}, //	
        {"CreateDirectory", "56", "Создать папку"}, //
        {"RemoveDirectory", "57", "Удалить пустую папку"}, //
        {"GetDirectoryContents", "58", "Получить содержимое папки"},//	
        {"GetResources", "61", "Получить ресурсные данные"}, //
        {"GetDataExchangeSettings", "78", "Получить значения параметров настройки протокола обмена данными"},// 
        {"SetDataExchangeSettings", "79", "Установить значения параметров настройки протокола обмена данными"},// 
        {"KeepAlive", "71", "Подтвердить функционирование"}, //
        {"Reset", "72", "Перезагрузить контроллер"}, //
        {"ShowMessage", "73", "Осуществить вывод сообщения на ЖКИ"}, //
        {"GetDateTime", "76", "Получить дату/время"}, //
        {"SetDateTime", "77", "Установить дату/время"}, //
        {"BroadcastSearch", "81", "Запрос широковещательного поиска контроллеров"}, //
        {"RegisterPeer", "82", "Зарегистрировать абонента “сетевая станция” с целью обеспечения организации сеанса связи"}, //
        {"GetPeerAccessLevel", "83", "Получить сведения о текущем уровне доступа абонента – сетевой станции"}, //	
        {"GetRegisteredPeersInfo", "84", "Получить сведения о зарегистрированных абонентах “сетевая станция”"},//	
        {"GetACL", "85", "Получить сведения о предустановленных уровнях доступа для абонентов - сетевых станций"}, //
        {"UnregisterPeer", "86", "Удалить сведения об абоненте - сетевой станции"}, //
        {"SetPeerAccessLevel", "87", "Установить уровень доступа для абонента – сетевой станции"}, //	
        {"SetACLItem", "88", "Внести запись о предустановленных уровнях доступа для абонентов - сетевых станций"}, //
        {"RemoveACLItem", "89", "Удалить запись о предустановленных уровнях доступа для абонентов - сетевых станций"},
        {"GetDeviceIdent", "91", "Получить идентификатор устройства"},
        {"GetDeviceManufacturerInfo", "92", "Получить информацию о производителе"},
        {"GetConnectionIdent", "93", "Получить идентификатор соединения контроллера с сетевым абонентом"},
        {"GetTechIdent", "94", "Получить технологический идентификатор устройства"},
        {"SetConnectionIdent", "95", "Установить идентификатор соединения с сетевым абонентом"},
        {"SetTechIdent", "96", "Установить технологический идентификатор устройства"},
        {"GetDeviceInterfaceState", "A1", "Получить сведения о состоянии аппаратного интерфейса устройства–элементов панели управления (кнопок, индикаторов), ЖКИ (меню, текст)"},
        {"GetLCDText", "A2", "Получить сведения о тексте, отображаемом на ЖКИ"},
        {"GetControlPanelInfo", "A3", "Получить сведения о панели управления аппаратного интерфейса устройства (кнопок, индикаторов)"},
        {"GetMenuInfo", "A4", "Получить сведения о меню, отображаемом на ЖКИ устройства"},
        {"GetLCDInfo", "A5", "Получить сведения о ЖКИ устройства"},
        {"SetKeyDown", "A7", "Имитировать нажатие на клавишу клавиатуры устройства"},
        {"SetKeyUp", "A7", "Имитировать опускание клавиши клавиатуры устройства"},
        {"SetKeyPressed", "A9", "Имитировать нажатие с последующим опусканием клавиши клавиатуры устройства"},
        {"GetDeviceEvents", "B1", "Получить сведения о наступивших событиях устройства (под событием устройства понимается изменение состояния, требующее внимания)"},
        {"GetDeviceEventsInfo", "B2", "Получить сведения о перечне событий устройства"},
        {"GetDeviceEventsSourcesInfo", "B3", "Получить сведения о перечне источников событий устройства"},};
    public final static String[][] nameErrorCode = {
        {"Ok", "0", "Запрос выполнен успешно"},
        {"Failed", "1", "Запрос не выполнен"},
        {"Partially", "2", "Запрос выполнен частично"},
        {"NotImplemented", "3", "Запрос не поддерживается (отсутствует предусмотренная спецификацией реализация)"},
        {"Unexpected", "6", "Неожиданный запрос (запрос не может быть выполнен в силу его неуместности)"},
        {"InvalidRequestId", "7", "Некорректное значение идентификатора запроса"},
        {"InvalidRequestParam", "8", "Некорректное значение одного или нескольких параметров запроса"},
        {"FileCreateError", "10", "Ошибка создания файла"},
        {"FileNotFound", "11", "Отсутствует файл с указанным номером/именем"},
        {"FileSizeExceeded", "12", "Сегмент данных выходит за границы файла"},
        {"SegmentSizeExceeded", "13", "Превышен максимально допустимый размер сегмента данных"},
        {"InvalidFileFormat", "15", "Формат сохраняемых данных не соответствует формату файла с указанным номером"},
        {"InsufficientDiskSpace", "16", "Недостаточно места на жестком диске контроллера"},
        {"FileReadError", "17", "Ошибка чтения файла с жесткого диска контроллера"},
        {"FileWriteError", "18", "Ошибка записи файла на жесткий диск контроллера"},
        {"FileCloseError", "19", "Ошибка закрытия (описателя) файла"},
        {"PathNotFound", "20", "Указанная папка/том не обнаружен на жестком диске контроллера"},
        {"UnexpectedFileNumberChange", "21", "Нарушена последовательность команд (смена объекта применения) в рамках сессии работы с файлом."},
        {"RequestNotSupported", "41", "Запрос не поддерживается данной версией протокола (спецификацией не предусмотрена реализация запроса для данной версии протокола)"},
        {"RequestAlready", "42", "Выполнение запроса недопустимо, т.к. контроллер уже находится в состоянии, требуемом запросом"},
        {"HandlerActive", "43", "Имеется находящийся в активном состоянии более предпочитаемый обработчик текущего запроса (факт выполнения запроса исходит из его описания)"},
        {"RequestAccessDenied", "44", "Недостаточно прав для выполнения запроса"},
        {"AuthFailed", "51", "Процедура аутентификации абонента завершилась неуспешно"},
        {"NotRegistered", "52", "Абонент не зарегистрирован"}
    };
    //Запросы по работе с параметрами
    static final public int GetProperty = 0x11;       //+Получить значение свойства. Каждый параметр имеет фиксированный набор свойств. Каждое свойство имеет идентификатор и наименование. Перечень свойств параметров представлен в таблице 6.1.1-3
    static final public int GetValueProperty = 0x12;	//+Получить значение свойства “значение”
    static final public int GetRingBuffer = 0x13;     //+Получить содержимое кольцевого буфера значений параметров
    static final public int SetProperty = 0x21;       //+Установить значение свойства
    static final public int SetValueProperty = 0x22;	//+Установить значение свойства “значение”

    //Запросы по работе с файлами
    static final public int ReadFile = 0x31;          //+Получить (фрагмент) файла
    static final public int GetFileSize = 0x32;	//+Получить размер файла
    static final public int GetFileRecord = 0x33;	//-Получить сведения о файле. Сведения включают размер, атрибуты, а также метки времени создания, модификации, доступа к файлу
    static final public int GetFileAttributes = 0x34;	//-Получить атрибуты файла
    static final public int GetFileTimestamps = 0x35;	//-Получить метки времени (создания, модификации, доступа) файла
    static final public int WriteFile = 0x41;         //+Записать (фрагмент) файла. Данный запрос не приводит к модификации файла.
    static final public int CommitFile = 0x42;	//+Сохранить изменения в файле, полученные в результате предшествующих запросов WriteFile
    static final public int DeleteFile = 0x43;	//+Удалить файл
    static final public int SetFileAttributes = 0x44;	//-Установить атрибуты файла
    static final public int SetFileTimestamps = 0x45;	//-Установить метки времени (создания, модификации, доступа) файла

    //Запросы по работе с файловой системой
    static final public int GetFreeDiskSpace = 0x51;	//Получить размер свободного дискового про-странства (Начиная с версии протокола 0x23 считается, что в рамках данного запроса требу-ется установить размер свободного дискового пространства текущего тома)
    static final public int GetCurrentVolume = 0x52;	//-Получить наименование текущего тома
    static final public int ChangeVolume = 0x53;	//-Установить текущий том
    static final public int GetCurrentDirectory = 0x54;//-	Получить наименование текущей папки
    static final public int SetCurrentDirectory = 0x55;//-	Установить текущую папку
    static final public int CreateDirectory = 0x56;	//-Создать папку
    static final public int RemoveDirectory = 0x57;	//-Удалить пустую папку
    static final public int GetDirectoryContents = 0x58;//-	Получить содержимое папки

    //Запрос на предоставление ресурсных данных
    static final public int GetResources = 0x61;	//Получить ресурсные данные

    //Запросы на определение параметров настройки протокола обмена данными
    static final public int GetDataExchangeSettings = 0x78;// Получить значения параметров настройки протокола обмена данными
    static final public int SetDataExchangeSettings = 0x79;// Установить значения параметров настройки протокола обмена данными

    //Функциональные запросы
    static final public int KeepAlive = 0x71;         //+Подтвердить функционирование
    static final public int Reset = 0x72;     	//+Перезагрузить контроллер
    static final public int ShowMessage = 0x73;	//-Осуществить вывод сообщения на ЖКИ
    static final public int GetDateTime = 0x76;	//+Получить дату/время
    static final public int SetDateTime = 0x77;	//+Установить дату/время

    //Запросы по организации соединения с абонентами
    static final public int BroadcastSearch = 0x81;	//Запрос широковещательного поиска контроллеров
    static final public int RegisterPeer = 0x82;	//Зарегистрировать абонента “сетевая станция” с целью обеспечения организации сеанса связи
    static final public int GetPeerAccessLevel = 0x83;//	Получить сведения о текущем уровне доступа абонента – сетевой станции
    static final public int GetRegisteredPeersInfo = 0x84;//	Получить сведения о зарегистрированных абонентах “сетевая станция”
    static final public int GetACL = 0x85;            //Получить сведения о предустановленных уровнях доступа для абонентов - сетевых станций
    static final public int UnregisterPeer = 0x86;	//Удалить сведения об абоненте - сетевой станции
    static final public int SetPeerAccessLevel = 0x87;//	Установить уровень доступа для абонента – сетевой станции
    static final public int SetACLItem = 0x88;	//Внести запись о предустановленных уровнях доступа для абонентов - сетевых станций
    static final public int RemoveACLItem = 0x89;	//Удалить запись о предустановленных уровнях доступа для абонентов - сетевых станций

    //Запросы по идентификации устройства
    static final public int GetDeviceIdent = 0x91;	//Получить идентификатор устройства
    static final public int GetDeviceManufacturerInfo = 0x92;//	Получить информацию о производителе
    static final public int GetConnectionIdent = 0x93;//	Получить идентификатор соединения контроллера с сетевым абонентом
    static final public int GetTechIdent = 0x94;	//Получить технологический идентификатор устройства
    static final public int SetConnectionIdent = 0x95;//	Установить идентификатор соединения с сетевым абонентом
    static final public int SetTechIdent = 0x96;	//Установить технологический идентификатор устройства

    //Запросы касательно взаимодействия пользователя с устройством
    static final public int GetDeviceInterfaceState = 0xA1;//	Получить сведения о состоянии аппаратного интерфейса устройства – элементов панели управления (кнопок, индикаторов), ЖКИ (меню, текст)
    static final public int GetLCDText = 0xA2;	//Получить сведения о тексте, отображаемом на ЖКИ
    static final public int GetControlPanelInfo = 0xA3;//	Получить сведения о панели управления аппаратного интерфейса устройства (кнопок, индикаторов)
    static final public int GetMenuInfo = 0xA4;	//Получить сведения о меню, отображаемом на ЖКИ устройства
    static final public int GetLCDInfo = 0xA5;	//Получить сведения о ЖКИ устройства
    static final public int SetKeyDown = 0xA7;	//Имитировать нажатие на клавишу клавиатуры устройства
    static final public int SetKeyUp = 0xA7;          //Имитировать опускание клавиши клавиатуры устройства
    static final public int SetKeyPressed = 0xA9;	//Имитировать нажатие с последующим опусканием клавиши клавиатуры устройства

    //Запросы по получению сведений о событиях устройства
    static final public int GetDeviceEvents = 0xB1;	//Получить сведения о наступивших событиях устройства (под событием устройства понимается изменение состояния, требующее внимания)
    static final public int GetDeviceEventsInfo = 0xB2;//	Получить сведения о перечне событий устройства
    static final public int GetDeviceEventsSourcesInfo = 0xB3;//	Получить сведения о перечне источников событий устройства
    /*
    *   Коды завершения
     */
    static final public int RequestOk = 0;            //Запрос выполнен успешно
    static final public int RequestFailed = 1;	//Запрос не выполнен
    static final public int RequestPartially = 2;	//Запрос выполнен частично
    static final public int RequestNotImplemented = 3;//	Запрос не поддерживается (отсутствует предусмотренная спецификацией реализация)
    static final public int RequestUnexpected = 6;	//Неожиданный запрос (запрос не может быть выполнен в силу его неуместности)
    static final public int InvalidRequestId = 7;	//Некорректное значение идентификатора запроса
    static final public int InvalidRequestParam = 8;	//Некорректное значение одного или нескольких параметров запроса
    static final public int FileCreateError = 10;	//Ошибка создания файла
    static final public int FileNotFound = 11;	//Отсутствует файл с указанным номером/именем
    static final public int FileSizeExceeded = 12;	//Сегмент данных выходит за границы файла
    static final public int SegmentSizeExceeded = 13;	//Превышен максимально допустимый размер сегмента данных
    static final public int InvalidFileFormat = 15;	//Формат сохраняемых данных не соответствует формату файла с указанным номером
    static final public int InsufficientDiskSpace = 16;//	Недостаточно места на жестком диске контроллера
    static final public int FileReadError = 17;	//Ошибка чтения файла с жесткого диска контроллера
    static final public int FileWriteError = 18;	//Ошибка записи файла на жесткий диск контроллера
    static final public int FileCloseError = 19;	//Ошибка закрытия (описателя) файла
    static final public int PathNotFound = 20;	//Указанная папка/том не обнаружен на жестком диске контроллера
    static final public int UnexpectedFileNumberChange = 21;//	Нарушена последовательность команд (смена объекта применения) в рамках сессии работы с файлом.
    static final public int RequestNotSupported = 41;	//Запрос не поддерживается данной версией протокола (спецификацией не предусмотрена реализация запроса для данной версии протокола)
    static final public int RequestAlready = 42;	//Выполнение запроса недопустимо, т.к. контроллер уже находится в состоянии, требуемом запросом
    static final public int RequestPreferred = 43;    //Смотри выше
    static final public int HandlerActive = 43;	//Имеется находящийся в активном состоянии более предпочитаемый обработчик текущего запроса (факт выполнения запроса исходит из его описания)
    static final public int RequestAccessDenied = 44;	//Недостаточно прав для выполнения запроса
    static final public int AuthFailed = 51;          //Процедура аутентификации абонента завершилась неуспешно
    static final public int NotRegistered = 52;	//Абонент не зарегистрирован

    public static final int CT_TYPE_BOOL = 0;        //boolean
    public static final int CT_TYPE_INTEGER = 1;      //integer
    public static final int CT_TYPE_FLOAT = 2;       //float
    public static final int CT_TYPE_LONG = 3;       //long
    public static final int CT_TYPE_BYTE = 4;       //byte 8 bits

    public static final int VALUE_UIDS = 10000;
    public static final byte CT_DATA_GOOD = 0x0;      // данные достоверные
    public static final byte CT_DATA_NOGOOD = 0x7f;   // данные совсем плохие
    public static final byte CT_DATA_LOST = 0xf;      // данные потеряны

    public static final byte CT_V21 = 0x21;
    public static final byte CT_V22 = 0x22;
    public static final byte CT_V23 = 0x23;
    public static final byte CT_V24 = 0x24;

    public static final Integer[] NUM_FILES = {10, 110};//, 200, 201, 11, 80, 81, 100, 111, 200};
    public static final int TCP_TIMEOUT=10000;
}
