/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uddzos;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import os.exceptions.unknownCommandException;
import os.machine.ChannelDevice;
import os.machine.RealMachine;

/**
 *
 * @author Ugnius
 */
public class UddzOS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws unknownCommandException, FileNotFoundException {
//        // TODO code application logic here
////        channelDevice a = new channelDevice(new realMachine());
////        a.sb = 1;
////        a.start();
////        channelDevice b = new channelDevice(new realMachine());
////        b.sb = 2;
////        b.start();
//          realMachine rm = new realMachine();
//        
//        //TEST channel device
//        
////        System.out.println("begin test channel device");
////        try {
////            rm.regInt("dt", "4");
////            rm.regInt("st", "1");
////            rm.SCHR();
////        } catch (unknownCommandException ex) {
////            Logger.getLogger(UddzOS.class.getName()).log(Level.SEVERE, null, ex);
////            System.out.println("Unknown Command Exception");
////        }
////        
//        //TEST ADXX
//        System.out.println("begin test ADXX");
//        rm.regInt("r ", "45");
//        rm.memory[0][0][0] = '0';
//        rm.memory[0][0][1] = '0';
//        rm.memory[0][0][2] = '1';
//        rm.memory[0][0][3] = '1';
//        rm.ADXX(0, 0);
//        
//        
//        // TEST SBXX
//        System.out.println("begin test SBXX");
//        rm.regInt("r ", "45");
//        rm.memory[0][0][0] = '0';
//        rm.memory[0][0][1] = '0';
//        rm.memory[0][0][2] = '5';
//        rm.memory[0][0][3] = '6';
//        rm.SBXX(0, 0);
//          
//          
//        //TEST SRXX
//        System.out.println("begin test SRXX");
//        rm.SRXX(1, 0);
//        System.out.println(String.valueOf(rm.memory[1][0]));
//       // rm.printAllMem();
//        
//        //TEST LRXX
//        System.out.println("begin test LRXX");
//        rm.LRXX(4, 0);
//        rm.printRegisters();
//        
//        //TEST CRXX
//        System.out.println("begin test CRXX");
//        rm.CRXX(4, 0);
//        rm.printRegisters();
//        //rm.printAllMem();
//        
//        //TEST BTXX
//        System.out.println("begin test BTXX");
//        System.out.println("unable to test BTXX, until command execution is done");
//        
//        
//        //PRINT MEM
//        rm.printAllMem();
        
        RealMachine rm = new RealMachine();
        rm.runtime();
        //rm.printAllMem();
}
    
}