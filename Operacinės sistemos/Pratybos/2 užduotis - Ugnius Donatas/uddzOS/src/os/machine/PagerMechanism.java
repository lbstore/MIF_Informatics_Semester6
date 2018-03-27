/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package os.machine;

import java.util.Random;

/**
 *
 * @author Ugnius
 */
public class PagerMechanism {

    RealMachine rm;

    public PagerMechanism(RealMachine rm) {
        this.rm = rm;
    }

    public int getRealAdress(int VMIndex, int VMAdress) {
        int realAdress;
        realAdress = Integer.valueOf(String.valueOf(rm.memory[rm.ptr[VMIndex]][VMAdress / 10]).trim()) * 10 + (VMAdress % 10);
        return realAdress;
    }

    public void newVMTable() {
        int i = 0;
        while (rm.ptr[i] >= 0) {
            i++;
        }
        //System.out.println(i);
        if (i > 1) {
            System.out.println("no more memory");
        } else {
            int thisTableIndex = i;
            rm.ptr[i] = getARandomBlock();
            for (int n = 0; n < 10; n++) {
                rm.memory[rm.ptr[i]][n] = toChars(this.getARandomBlock());
            }
        }
    }

    private int getARandomBlock() {
        Random r = new Random();
        //randoming a block to keep this table in
        int testInt = r.nextInt(28);
        while (isTaken(testInt)) {
            testInt = r.nextInt(27);
        }
        return testInt;
    }

    private boolean isTaken(int block) {
        return !isFree(block);
    }

    private boolean isFree(int block) {
        for (int i = 0; i < rm.ptr.length; i++) {
            if (rm.ptr[i] == block) {
                return false;
            }
            if (rm.ptr[i] >= 0) {
                for (int n = 0; n < 10; n++) {
                    try {
                    if (block == Integer.valueOf(String.valueOf(rm.memory[rm.ptr[i]][n]).trim())) {
                        return false;
                    }
                    } catch (NumberFormatException e){
                    }
                }
            }
        }
        return true;
    }

    private char[] toChars(int aRandomBlock) {
        char[] chars = new char[4];
        String val = String.valueOf(aRandomBlock);
        int valLength = val.length();
        if (valLength < 4) {
            for (int i = valLength; i < 4; i++) {
                val+= ' ';
            }
        }
        chars = val.toCharArray();
        return chars;
    }
}
