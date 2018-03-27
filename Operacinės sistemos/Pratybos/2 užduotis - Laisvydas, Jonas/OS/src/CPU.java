
import java.util.logging.Level;
import java.util.logging.Logger;

public class CPU {
    
    public final static byte PUSH = 10;
    public final static byte PSHC = 20;
    public final static byte POP = 30;
    public final static byte POP1 = 31;
    public final static byte POP0 = 32;
    public final static byte SET = 50;
    public final static byte SETC = 60;
    public final static byte TOP = 70;
    public final static byte ADD = 80;
    public final static byte SUB = 90;
    public final static byte MUL = 100;
    public final static byte DIV = 110;
    public final static byte JZ = 120;
    public final static byte JP = (byte) 130;
    public final static byte JN = (byte) 140;
    public final static byte JMP = (byte) 150;
    public final static byte GETD = (byte) 160;
    public final static byte PUTD = (byte) 170;
    public final static byte HALT = (byte) 180;
    
    
    
    
    
    
    
    private int MODE;
    private int PC;
    private int PTR;
    private int SP;
    private int TI;
    private int SI;
    private int PI;
    private int CH1;
    private int CH2;
    private int CH3;
    private MMU mmu;
    private static final int TIME = 20;
    public static final int SUPERVISOR = 0;
    public static final int USER = 1;
    
    public CPU() {
        MODE = SUPERVISOR;
        PC = 0;
        SP = 0;
        TI = TIME;
        SI = 0;
        PI = 0;
        CH1 = 0;
        CH2 = 0;
        CH3 = 0;
    }

    public void setMMU(MMU mmu) {
        this.mmu = mmu;
    }
    
    public void resetInterrupts() {
        resetTI();
        SI = 0;
        PI = 0;
    }
    
    public void doCycle() {
        try {
            interpretCommand(mmu.read(PC));
        } catch (MemoryException ex) {
            Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
        }
        TI--;
    }
    
    /*
    PUSH xy - 1
    PSHC x - 2
    POP xy - 3
    POP - 4
    SET xy - 5
    SETC x - 6
    TOP xy - 7
    ADD - 8
    SUB - 9
    MUL - 10
    DIV - 11
    JZ xy - 12
    JP xy - 13
    JN xy - 14
    JMP xy - 15
    GETD xy - 16
    PUTD xy - 17
    HALT - 18
    */
    private void interpretCommand(Word word) {
        PC++;
        byte command = word.getByte(3);
        word.setByte(3, (byte) 0);
        try {
        switch (command) {
            // PUSH xy
            case PUSH: {
                mmu.write(mmu.read(Word.wordToInt(word)), SP);
                SP++;
                break;
            }
            // PSHC x
            case PSHC: {
                mmu.write(word, SP);
                SP++;
                break;
            }
            // POP xy
            case POP1: { 
                mmu.write(mmu.read(SP-1), Word.wordToInt(word));
                SP--;
                break;
            }
            // POP
            case POP0: {
                SP--;
                break;
            }
            // SET xy
            case SET: {
                mmu.write(mmu.read(Word.wordToInt(word)), SP-1);
                break;
            }
            // SETC x
            case SETC: {
                mmu.write(word, SP-1);
                break;
            }
            // TOP xy
            case TOP: {
                mmu.write(mmu.read(SP-1), Word.wordToInt(word));
                break;
            }
            // ADD
            case ADD: {
                mmu.write(Word.intToWord(Word.wordToInt(mmu.read(SP-2)) + Word.wordToInt(mmu.read(SP-1))), SP-2);
                SP--;
                break;
            }
            // SUB
            case SUB: {
                mmu.write(Word.intToWord(Word.wordToInt(mmu.read(SP-2)) - Word.wordToInt(mmu.read(SP-1))), SP-2);
                SP--;
                break;
            }
            // MUL
            case MUL: {
                mmu.write(Word.intToWord(Word.wordToInt(mmu.read(SP-2)) * Word.wordToInt(mmu.read(SP-1))), SP-2);
                SP--;
                break;
            }
            // DIV
            case DIV: {
                mmu.write(Word.intToWord(Word.wordToInt(mmu.read(SP-2)) / Word.wordToInt(mmu.read(SP-1))), SP-2);
                SP--;
                break;
            }
            // JZ xy
            case JZ: {
                if(Word.wordToInt(mmu.read(SP-1)) == 0) {
                    PC = Word.wordToInt(word);
                }
                break;
            }
            // JP xy
            case JP: {
                if(Word.wordToInt(mmu.read(SP-1)) > 0) {
                    PC = Word.wordToInt(word);
                }
                break;
            }
            // JN xy
            case JN: {
                if(Word.wordToInt(mmu.read(SP-1)) < 1) {
                    PC = Word.wordToInt(word);
                }
                break;
            }
            // JMP xy
            case JMP: {
                PC = Word.wordToInt(word);
                break;
            }
            // GETD xy
            case GETD: {
                SI = 1;
                break;
            }
            // PUTD xy
            case PUTD: {
                SI = 2;
                break;
            }
            // HALT
            case HALT: {
                SI = 3;
                break;
            } 
            default: {
                PI = 2;
                break;
            }
        }
        }
        catch (BlockSwapException ex) {
            SI = 4; 
            PC--;
                    //NOTE: after swap we need to reexecute command
        } catch (MemoryException ex) {
            PI = 1;
        }
    }
    
    
    /* 
    1 - neteisingas adresas
    2 - neegzistuoja operacijos kodas
    3 - nepakanka atminties isoriniame diske
    4 - komanda GETD
    5 - PUTD
    6 - HALT
    7 - taimerio
    8 - swapinimo
    */
    public int getInterrupt() {
        switch (PI) {
            case 1: return 1;
            case 2: return 2;
            case 3: return 3;
        }
        switch(SI) {
            case 1: return 4;
            case 2: return 5;
            case 3: return 6;
            case 4: return 8;
        }
        if(TI == 0) {
            return 7;
        } 
        return 0;
    }
    
    public void resetTI() {
        this.TI = TIME;
    }

    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

    public int getPTR() {
        return PTR;
    }

    public void setPTR(int PTR) {
        this.PTR = PTR;
    }

    public int getSP() {
        return SP;
    }

    public void setSP(int SP) {
        this.SP = SP;
    }

    public int getTI() {
        return TI;
    }

    public void setTI(int TI) {
        this.TI = TI;
    }

    public int getSI() {
        return SI;
    }

    public void setSI(int SI) {
        this.SI = SI;
    }

    public int getPI() {
        return PI;
    }

    public void setPI(int PI) {
        this.PI = PI;
    }

    public int getCH1() {
        return CH1;
    }

    public void setCH1(int CH1) {
        this.CH1 = CH1;
    }

    public int getCH3() {
        return CH3;
    }

    public void setCH3(int CH3) {
        this.CH3 = CH3;
    }

    public int getMODE() {
        return MODE;
    }

    public void setMODE(int MODE) {
        this.MODE = MODE;
    }

    public int getCH2() {
        return CH2;
    }

    public void setCH2(int CH2) {
        this.CH2 = CH2;
    }
    
}
