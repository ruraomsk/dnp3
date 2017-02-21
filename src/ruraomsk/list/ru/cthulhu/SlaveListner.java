/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu;

import com.tibbo.aggregate.common.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class SlaveListner extends Thread
{

    private int port;
    private SlaveDevice sld;
    private int controller;
    private Registers regs;

    public SlaveListner(int port, SlaveDevice sld, int controller, Registers regs)
    {
        this.port = port;
        this.sld = sld;
        this.controller = controller;
        this.regs = regs;
        start();
    }

    @Override
    public void run()
    {
        ServerSocket slave;
        try {
            slave = new ServerSocket(this.port);
            while (!Thread.interrupted()) {
                Socket insc = slave.accept();
//                Transport tr = new Transport(insc, controller, regs, false);
//                tr.connect();
//                sld.addTransport(tr);
            }
        }
        catch (IOException ex) {
            Log.CORE.info("Ошибка SlaveListner " + ex.getMessage());
        }

    }

}
