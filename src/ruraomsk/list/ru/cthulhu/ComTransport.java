/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import com.tibbo.aggregate.common.Log;
import gnu.io.*;
import java.io.*;
import java.net.Socket;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class ComTransport extends BaseTransport implements SerialPortEventListener {

    String sPort = null;
    private CommPortIdentifier mPortIdent;
    SerialPort commport;
    byte[] buffer = new byte[16000];
    Integer spos = 0;

    public ComTransport(String sPort, int controller, Registers regs, boolean master) {
        this.sPort = sPort;
        this.regs = regs;
        this.sPort = sPort;
        this.controller = controller;
        this.master = master;
        qResp = new ConcurrentLinkedQueue();
        icount = 1;
    }

    /**
     * Производит подключение к транспортному уровню
     *
     * @return True если успешно
     */
    @Override
    public boolean connect() {
        try {
            if (connect) {
                return connect;
            }
            connect = false;
            try {
                Log.CORE.info(System.getProperty("java.library.path"));
                Enumeration pList = CommPortIdentifier.getPortIdentifiers();
                while (pList.hasMoreElements()) {
                    CommPortIdentifier cpi = (CommPortIdentifier) pList.nextElement();
                    Log.CORE.info("Port " + cpi.getName());
                }

                mPortIdent = CommPortIdentifier.getPortIdentifier(sPort);
                commport = (SerialPort) mPortIdent.open("Serial Master " + sPort, 100);
                commport.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_ODD);
//            System.err.println(Integer.toString(commport.getInputBufferSize()));
            } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException ex) {
                Log.CORE.info("Ошибка COM " + sPort + " " + ex.getMessage());
                return false;
            }

            m_Input = commport.getInputStream();
            m_Output = commport.getOutputStream();
            commport.addEventListener(this);
            spos = 0;
            while (m_Input.available() > 0) {
                m_Input.read();
            }
            commport.notifyOnDataAvailable(true);
            qResp.clear();
            connect = true;
        } catch (TooManyListenersException | IOException ex) {
            Log.CORE.info("ComTransport connect " + ex.getMessage());
            close();
        }
        return connect;
    }

    @Override
    public String getNameTransport() {
        return commport.getName();
    }

    @Override
    public String toString() {
        return "transport " + getNameTransport() + (connect ? " connect" : " disconnect") + (master ? " master" : " slave") + " очередь=" + qResp.size();
    }

    /**
     * Отключает транспортный уровень
     */
    @Override
    public void close() {
        connect = false;
        qResp.clear();
        try {
            if (m_Input != null) {
                m_Input.close();
            }
            if (m_Output != null) {
                m_Output.close();
            }
            if (commport != null) {
                commport.removeEventListener();
                commport.close();
            }
        } catch (IOException ex) {
        }
    }

    /**
     * Запрос состояния подключения
     *
     * @return True если установлено подключение
     */
    @Override
    public boolean isConnected() {
        return connect;
    }

    /**
     * Чтение принятого элемента из очереди принятых сообщений
     *
     * @return Очередное сообщение null если очередь пуста
     */
    @Override
    public BaseMessage readMessage() {
        return qResp.poll();
    }

    @Override
    public int getController() {
        return controller;
    }

    /**
     * Передача сообщения
     *
     * @param msg Сообщение для передачи
     * @return номер посылки если запрос иначе ноль
     */
    @Override
    public int writeMessage(BaseMessage msg) {
        try {
            if (!isConnected()) {
                return 0;
            }
            byte[] outbuf = new byte[16000];
            int len;
            if (msg.isRequest()) {
                msg.setReqIter(++icount > 32000 ? 1 : icount);
            }
            outbuf[2] = msg.getVersion();
            Util.ShortToBuff(outbuf, 5, 1);
            outbuf[7] = msg.getType();
            len = msg.toBuffer(outbuf, 8);
            Util.ShortToBuff(outbuf, 3, len);
            Util.ShortToBuff(outbuf, 0, len + 6);
            int crc = Util.Crc(outbuf, 2, len + 6);
            Util.ShortToBuff(outbuf, len + 8, crc);
            synchronized (Util.outbuf) {
                int pos = 2;
                Util.outbuf[0] = (byte) 0xc0;
                Util.outbuf[1] = (byte) 0xc0;
                for (int i = 0; i < len + 10; i++) {
                    switch (outbuf[i]) {
                        case (byte) 0xC0:
                            Util.outbuf[pos++] = (byte) 0xDB;
                            Util.outbuf[pos++] = (byte) 0xDC;
                            break;
                        case (byte) 0xDB:
                            Util.outbuf[pos++] = (byte) 0xDB;
                            Util.outbuf[pos++] = (byte) 0xDD;
                            break;
                        default:
                            Util.outbuf[pos++] = outbuf[i];
                    }
                }
                Util.outbuf[pos++] = (byte) 0xc0;
                m_Output.write(Util.outbuf, 0, pos);
//                Util.bufferToString(Util.outbuf, 0, pos);
//                for (int j = 0; j < len + 10; j++) {
//                    byte b = Util.outbuf[j];
//                    System.out.print(Integer.toHexString(b & 0xff) + " ");
//                }
//                System.out.println("]");

            }
            m_Output.flush();
            if (msg.isRequest()) {
                return msg.getReqIter();
            }
        } catch (IOException ex) {
            Log.CORE.info(getNameTransport() + "WriteMessage " + ex.getMessage());
            close();
        }
        return 0;
    }

    @Override
    public void run() {
        while (!Thread.interrupted() && isConnected()) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }

    private void read(Integer lenght) {
        if (lenght == 0) {
            return;
        }
        int pos = 0;
        Integer i = 0;
        while (i < lenght) {
            if (buffer[i] == (byte) 0xC0) {
                i++;
                continue;
            };
            if (buffer[i] == (byte) 0xDB && buffer[i + 1] == (byte) 0xDD) {
                buffer[pos++] = (byte) 0xDB;
                i += 2;

                continue;
            }
            if (buffer[i] == (byte) 0xDB && buffer[i + 1] == (byte) 0xDC) {
                buffer[pos++] = (byte) 0xC0;
                i += 2;
                continue;
            }
            buffer[pos++] = buffer[i++];
        }
        lenght = pos;
        pos = 0;
        while (pos < lenght) {
            Integer len = Util.ToShort(buffer, pos);
            if ((len + pos) > lenght) {
                return;
            }
            int crc = Util.ToShort(buffer, pos + len + 2);
            int wcrc = Util.Crc(buffer, pos + 2, len);
            if (crc != wcrc) {
                Util.bufferToString(buffer, pos, len + 2);
                Log.CORE.info(getNameTransport() + " Bad CRC=" + Integer.toString(crc) + " wait= " + Integer.toString(wcrc));
                Log.CORE.info(getNameTransport() + " len=" + Integer.toString(len) + " pos= " + Integer.toString(pos));
                pos += len + 4;
                return;
            } else {
            }
            int ipos = 3;
            while (ipos < len) {
                int inLen = Util.ToShort(buffer, pos + ipos);
                int inController = Util.ToShort(buffer, pos + ipos + 2);
                byte[] bm = new byte[inLen];
                System.arraycopy(buffer, pos + ipos + 5, bm, 0, inLen);
                BaseMessage mess = Util.newObject(master, bm, 0, bm.length, regs, controller);
                if (mess != null) {
                    qResp.add(mess);
                }
                ipos += inLen + 5;
            }
            pos += len + 4;
        }

    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (!isConnected()) {
            return;
        }
        if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                int pos = spos;
                while (m_Input.available() > 0) {
                    buffer[pos++] = (byte) (m_Input.read() & 0xff);
                }
//                Util.bufferToString(buffer, 0, pos);
                if (buffer[pos - 1] == (byte) 0xC0) {
                    read(pos);
                    spos = 0;
                } else {
                    spos = pos;
                }
            } catch (IOException ex) {
                return;
            }
        }
    }

    @Override
    public boolean isSerial() {
        return true; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Socket getSocket() {
        return null; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCommPort() {
        return sPort; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSlip() {
        return true;
    }
}
