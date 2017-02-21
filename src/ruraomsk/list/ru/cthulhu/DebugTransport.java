/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import ruraomsk.list.ru.cthulhu.message.GetRingBufferResponse;
import ruraomsk.list.ru.cthulhu.message.GetValuePropertyRequest;
import ruraomsk.list.ru.cthulhu.message.GetValuePropertyResponse;

/**
 *
 * @author Yury Rusinov <ruraomsl@list.ru at Automatics E>
 */
public class DebugTransport extends BaseTransport {

    private boolean connect = true;
    private String nameTransport = "one";
    private int controller = 1;
    private Registers regs;
    private ConcurrentLinkedQueue<BaseMessage> qResp = null;

    public DebugTransport(String nameTransport, int controller, Registers regs) {
        this.regs = regs;
        this.nameTransport = nameTransport;
        this.controller = controller;
        qResp = new ConcurrentLinkedQueue<>();
    }

    @Override
    public boolean isSerial() {
        return false;
    }

    @Override
    public Socket getSocket() {
        return null;
    }

    @Override
    public String getCommPort() {
        return "notComm";
    }

    @Override
    public boolean connect() {
        connect = true;
        start();
        return connect;
    }

    @Override
    public String getNameTransport() {
        return "debugTransport/" + nameTransport;
    }

    @Override
    public String toString() {
        return getNameTransport();
    }

    @Override
    public void close() {
        connect = false;
    }

    @Override
    public boolean isConnected() {
        return connect;
    }

    @Override
    public BaseMessage readMessage() {
        return qResp.poll();

    }

    @Override
    public int getController() {
        return controller;
    }

    @Override
    public int writeMessage(BaseMessage msg) {
        if (!connect) {
            return 0;
        }
        switch (msg.getId()) {
            case Util.GetRingBuffer:
                makeRingBufer();
                break;
            case Util.GetValueProperty:
                makeResponseValue((GetValuePropertyRequest) msg);
                break;
        }
        if (msg.isRequest()) {
            return msg.getReqIter();
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

    @Override
    public boolean isSlip() {
        return false;
    }

    private void makeResponseValue(GetValuePropertyRequest getValueRequest) {
        long timeall = System.currentTimeMillis();
        GetValuePropertyResponse resp = new GetValuePropertyResponse(timeall);
        resp.setupWorld(controller, regs);
        for (int id : getValueRequest.getValues()) {
            if (Math.random() > 0.5f) {
                continue;
            }
            Register reg = regs.getOneReg(controller, id).getReg();
            OneReg oreg = new OneReg(timeall, reg);

            switch (reg.getType()) {
                case Util.CT_TYPE_BOOL:
                    oreg.setValue(Math.random() > 0.5f);
                    break;
                case Util.CT_TYPE_INTEGER:
                    oreg.setValue((int) Math.round(Math.random() * 10000));
                    break;
                case Util.CT_TYPE_LONG:
                    oreg.setValue((long) Math.round(Math.random() * 100000));
                    break;
                case Util.CT_TYPE_FLOAT:
                    oreg.setValue((float) Math.random() * 100000);
                    break;
                case Util.CT_TYPE_BYTE:
                    oreg.setValue((byte) Math.round(Math.random() * 127));
                    break;
            }
            oreg.setGood(Util.CT_DATA_GOOD);
            resp.addValue(oreg);
        }
        qResp.add(resp);
    }

    private void makeRingBufer() {
        long timeall = System.currentTimeMillis();
        GetRingBufferResponse resp = new GetRingBufferResponse();
        resp.setupWorld(controller, regs);
        for (OneReg oregs : regs.getOneRegs(controller)) {
            if (!oregs.getReg().isSending()) {
                continue;
            }
            int count=10;
            if (Math.random() > 0.9f) {
                count=1;
            }
            for (int i = 0; i < count; i++) {
                OneReg oreg = new OneReg(timeall+(i*30L), oregs.getReg());
                switch (oregs.getReg().getType()) {
                    case Util.CT_TYPE_BOOL:
                        oreg.setValue(Math.random() > 0.5f);
                        break;
                    case Util.CT_TYPE_INTEGER:
                        oreg.setValue((int) Math.round(Math.random() * 15000));
                        break;
                    case Util.CT_TYPE_LONG:
                        oreg.setValue((long) Math.round(Math.random() * 150000));
                        break;
                    case Util.CT_TYPE_FLOAT:
                        oreg.setValue((float) Math.random() * 150000);
                        break;
                    case Util.CT_TYPE_BYTE:
                        oreg.setValue((byte) Math.round(Math.random() * 127));
                        break;
                }
                oreg.setGood(Util.CT_DATA_GOOD);
                resp.addOneReg(oreg);

            }
        }
        qResp.add(resp);
    }

}
