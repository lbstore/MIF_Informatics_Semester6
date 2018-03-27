/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package os.machine;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import os.iodevices.inputDevice;
import os.iodevices.outputDevice;

/**
 *
 * @author Ugnius
 */
public class ChannelDevice extends Thread {
    private boolean killMe = false;
    public int sb, db, st, dt;
    public boolean work = false;
    RealMachine rm;
    inputDevice inputd;
    outputDevice outputd;

    public ChannelDevice(RealMachine aThis) {
        sb = 1;
        db = 9;
        st = 1;
        dt = 1;
        rm = aThis;
        inputd = new inputDevice();
        outputd = new outputDevice();
    }

    public void run() {
        char[][] data = new char[10][4];
        while (!killMe) {
            try {
                Random r = new Random();
                Thread.sleep(r.nextInt(3) * 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ChannelDevice.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (work) {
                System.out.println("channel device begins work");
                switch (st) {
                    case 1:
                        data = rm.memory[sb];
                        //System.out.println("Duomenis imame is atminties bloko nr" + sb);
                        break;
                    case 2:
                        data = rm.memory[0];
                        break;
                    case 3:
                        System.out.println("Bandoma kopijuoti is isorines atminties, kuri nera implementuota");
                        break;
                    case 4:
                        data = inputd.getData();
                        break;
                }
                switch (dt) {
                    case 1:
                        rm.memory[db] = data;
                        break;
                    case 2:
                        rm.memory[0] = data;
                        break;
                    case 3:
                        System.out.println("Bandoma rasyti i isorine atminti, kuri nera implementuota");
                        break;
                    case 4:
                        outputd.sendData(data);
                        break;
                }
                rm.setChr(0, false);
                rm.setChr(1, false);
                rm.setChr(2, false);
                rm.setChr(3, false);
                this.work = false;
                System.out.println("channel device finished working");
       //rm.printAllMem();
                //this.
            }
        }
    }

    public void startWork() {
        this.work = true;
    }
    
    public void kill(){
        killMe = true;
    }
}
