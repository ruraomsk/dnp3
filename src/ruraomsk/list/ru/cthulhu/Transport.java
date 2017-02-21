/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import com.tibbo.aggregate.common.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class Transport extends BaseTransport {

    private InputStream m_Input;
    private OutputStream m_Output;

    private boolean connect = false;
    private boolean master = true;
    private ConcurrentLinkedQueue<BaseMessage> qResp = null;
    private Socket socket = null;
    private int controller;
    private Registers regs;
    private int icount;
    byte[] buffer = new byte[16000];
    boolean slip = false;
    boolean flag = true;
    String IP;
    int port;
    int spos = 0;

    public Transport(String IP, int port, int controller, Registers regs, boolean master) {
        this.IP = IP;
        this.port = port;
        this.regs = regs;
        this.controller = controller;
        this.master = master;
        qResp = new ConcurrentLinkedQueue();
        icount = 1;
    }

    public Transport(String IP, int port, int controller, Registers regs, boolean master, boolean slip) {
        this.IP = IP;
        this.port = port;
        this.regs = regs;
        this.controller = controller;
        this.master = master;
        qResp = new ConcurrentLinkedQueue();
        icount = 1;
        this.slip = slip;
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

            socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getByName(IP), port), Util.TCP_TIMEOUT);

            m_Input = socket.getInputStream();
            while (m_Input.available() > 0) {
                m_Input.read();
            }
            m_Output = socket.getOutputStream();
            qResp.clear();
            if (flag) {
                start();
                flag = false;
            }
            connect = true;
        } catch (IOException ex) {
            Log.CORE.info("Transport connect " + ex.getMessage());
            close();
        }
        return connect;
    }

    public String getNameTransport() {
        if (socket == null) {
            return IP + ":" + Integer.toString(getPort()) + " ";
        }
        return socket.getInetAddress().getHostAddress() + ":" + Integer.toString(getPort()) + " ";
    }

    public int getPort() {
        if (socket == null) {
            return port;
        }
        return socket.getPort();
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
//        this.interrupt();
//            this.join(1000l);
        qResp.clear();
        try {
            if (m_Input != null) {
                m_Input.close();
            }
            if (m_Output != null) {
                m_Output.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
        }
        Log.CORE.info("Транспорт приостановлен...");
        socket = null;
    }

    /**
     * Запрос состояния подключения
     *
     * @return True если установлено подключение
     */
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

    /**
     *
     * @return
     */
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
        int len;
        try {
            if (!isConnected()) {
                return 0;
            }
            byte[] outbuf = new byte[16000];
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
                if (!slip) {
                    System.arraycopy(outbuf, 0, Util.outbuf, 0, len + 10);
                    m_Output.write(Util.outbuf, 0, len + 10);

                } else {
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

                }

            }
            m_Output.flush();
//            System.out.println("Отправлено: "+msg.toString());
            if (msg.isRequest()) {
                return msg.getReqIter();
            }
        } catch (IOException ex) {
            Log.CORE.info(getNameTransport() + ex.getMessage());
            close();
        }
        return 0;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500L);
                if (!isConnected()) {
                    continue;
                }
                int pos = spos;
                while (m_Input.available() > 0) {
                    buffer[pos++] = (byte) (m_Input.read() & 0xff);
                }
//                Util.bufferToString(buffer, 0, pos);
                if (!slip) {
                    read(pos);
                    spos = 0;
                } else {
                    if (pos == 0) {
                        continue;
                    }
                    if (buffer[pos - 1] == (byte) 0xC0) {
                        read(pos);
                        spos = 0;
                    } else {
                        spos = pos;
                    }

                }

            } catch (IOException | InterruptedException ex) {
                Log.CORE.info("Transport error " + ex.getMessage());
                close();
            }
        }
    }

    private void read(Integer lenght) {
        if (lenght == 0) {
            return;
        }
        int pos = 0;
        if (slip) {
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
        }
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
    public boolean isSerial() {
        return false; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Socket getSocket() {
        return socket; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCommPort() {
        return null; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSlip() {
        return slip;
    }
}
