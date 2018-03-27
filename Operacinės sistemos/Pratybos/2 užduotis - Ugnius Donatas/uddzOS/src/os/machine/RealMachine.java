/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package os.machine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import os.exceptions.unknownCommandException;

/**
 *
 * @author Ugnius
 */
public class RealMachine {

    private boolean run = true;

    private char[][] r; // duomenu registras
    private int pi; /* Programinio pertraukimo registras. Skirtas žinoti, kada įvyko programinis
     pertraukimas. Gali įgyti vieną iš tryjų reikšmių: 0 – Pertraukimas nereikalingas, 1 –
     pertraukimas reikalingas, sukeltas neteisingo programinio kodo, 2 – pertraukimas
     reikalingas, sukeltas bandymo pasiekti atmintį, kurią pasiekti draudžiama.*/

    private int si; /* Supervizoriaus pertraukimo registras. Gali įgyti keturias reiškmes: 0 – pertraukimas
     nereikalingas, 1 – buvo iškviesta GD komanda, 2 – buvo iškviesta SD komanda, 3 – išeiti
     (Halt)*/

    private int ioAdress;

    private int ic; //Komandų skaitiklis, naudojamas vygdomos instrukcijos adreso saugojimui.
    private int ti; /*Timer'io registras. Registras, įgyjantis 0 ir 1 reikšmes. 0 – pertraukimas nereikalingas,
     1 – pertraukimas reikalingas*/

    private int ioi; /*Registras kuris saugo pertraukimo kodą, kuris gali vykti viename iš įvesties arba
     išvesties kanalų. Reikšmės: 0 – pertraukimas nereikalingas, 1- 1 kanalas (įvestis (input)), 2
     – 2 kanalas (išvedimas (output)), 3 - 3 kanalas (išorinė atmintis (external memory)). */

    private int sr; //steko adreso regsitras
    private char mode; /*registras nurodantis veikimo rėžimą. Gali įgyti reikšmę U – vartotojo rėžimas, bei
     reikšmę S – supervizoriaus rėžimas.*/

    private boolean[] chr; //nurodo i kanalo užimtumą. 0 – kanalas nenaudojamas, 1 – kanalas naudojamas.
    private boolean c; //loginis trigeris, igyje true ir false reiskmes
    public int[] ptr;
    public char[][][] memory;
    //0-26 blokai - vartotojimo atmintis
    //27 blokas - supervizorine atmintis
    //28-29 blokai - stekui isskirta atmintis

    //kanalu irenginys;
//    private int sb; //takelio, is kurio kopijuojame numeris
//    private int db; //takelio, i kuri kopijuojame numeris
//    private int st; /* Objekto, iš kurio kopijuosime numeris. Gali įgyti tokias reikšmes: 1 - Vartotojo
//     atmintis, 2 – Supervizorinė atmintis, 3 – Išorinė atmintis, 4 – Įvedimo srautas*/
//
//    private int dt; /*Objekto, į kurį kopijuosime numeris. Gali įgyti tokias pat reikšmes kaip ST.*/
    private ChannelDevice cdevice;
    private PagerMechanism pager;
    private int currentVMIndex;

    public RealMachine() {
        r = new char[4][4];
        pi = 0;
        si = 0;
        ic = 0;
        ti = 999;
        ioi = 0;
        sr = 280; //desimtys nurodo bloka (t.y. by default, stekas prasideda dvidesimt devintame bloke
        mode = 'U';
        chr = new boolean[4];
        chr[0] = false;
        chr[1] = false;
        c = false;
        ptr = new int[2];
        ptr[0] = -1;
        ptr[1] = -1;
        memory = new char[30][10][4];//atmintis.[blokas][zodis][baitas]
        for (int block = 0; block < memory.length; block++) {
            for (int word = 0; word < memory[block].length; word++) {
                for (int thechar = 0; thechar < memory[block][word].length; thechar++) {
                    memory[block][word][thechar] = 'N';
                }
            }
        }
//        sb = 0;
//        db = 0;
//        st = 0;
//        dt = 0;
        cdevice = new ChannelDevice(this);
        cdevice.start();
        pager = new PagerMechanism(this);
    }

    public void readCommand() {
    }

    public void regInt(String reg, String data) {
        if (mode == 'S') {
            int intData;
            char[] charData;
            try {
                switch (reg) {
                    case "r ":
//                charData = new char[32];
//                if (data.length() < 31) {
//                    data.getChars(0, data.length(), charData, 0);
//                } else {
//                    data.getChars(0, 31, charData, 0);
//                }
                        // this.r = charData;
                        // this.r[1][0] = charData[0];
                        this.r = stringToDoubleCharArray(data, 4, 4);

//                        System.out.println("r is : #" + String.valueOf(this.r) + "#");
//                        System.out.println(String.valueOf(this.r));
//                        //System.out.println(Integer.valueOf("'45"));
//                        String temp = String.valueOf(this.r).trim();
//                        //System.out.println("Temp =" + temp + ".");
//                        System.out.println("After parsing r into an integer: #" + Integer.parseInt(String.valueOf(this.r).trim()) +"#");
                        break;
                    case "pi":
                        intData = Integer.parseInt(data);
                        if ((intData < 3) && (intData > -1)) {
                            this.pi = intData;
                        } else {
                            throw new unknownCommandException("invalid value for pi");
                        }
                        break;
                    case "si":
                        intData = Integer.parseInt(data);
                        if ((intData < 4) && (intData > -1)) {
                            this.si = intData;
                        } else {
                            throw new unknownCommandException("invalid value for si");
                        }
                        break;
                    case "ic":
                        intData = Integer.parseInt(data);
                        if (intData >= 0) {
                            this.ic = intData;
                        } else {
                            throw new unknownCommandException("invalid value for ic");
                        }
                        break;
                    case "ti":
                        intData = Integer.parseInt(data);
                        if ((intData == 1) || (intData == 0)) {
                            this.ti = intData;
                        } else {
                            throw new unknownCommandException("invalid value for ti");
                        }
                        break;
                    case "ioi":
                        intData = Integer.parseInt(data);
                        if ((intData <= 3) && (intData >= 0)) {
                            this.ioi = intData;
                        } else {
                            throw new unknownCommandException("invalid value for ioi");
                        }
                        break;
                    case "sr":
                        intData = Integer.parseInt(data);
                        if (intData >= 0) {
                            this.sr = intData;
                        } else {
                            throw new unknownCommandException("invalid value for sr");
                        }
                        break;
                    case "md"/*short for mode*/:
                        charData = new char[1];
                        data.getChars(0, 1, charData, 0);
                        if ((charData[0] == 'U') || (charData[0] == 'S')) {
                            this.mode = charData[0];
                        } else {
                            throw new unknownCommandException("invalid value for mode");
                        }
                        break;
                    case "ch":
                        int kanalas;
                        boolean uzimtumas;
                        charData = new char[1];
                        data.getChars(0, 1, charData, 0);
                        kanalas = Integer.parseInt(charData.toString());
                        data.getChars(1, 2, charData, 0);
                        try {
                            if (charData[0] == '1') {
                                chr[kanalas] = true;
                            } else {
                                if (charData[0] == '0') {
                                    chr[kanalas] = false;
                                } else {
                                    throw new unknownCommandException("invalid value for chr[i]");
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            throw new unknownCommandException("invalid chanel for chr[i]");
                        }
                        break;
                    case "c ":
                        charData = new char[1];
                        data.getChars(0, 1, charData, 0);
                        if ((charData[0] == '1') || (charData[0] == 't') || (charData[0] == 'T')) {
                            this.c = true;
                        } else {
                            if ((charData[0] == '0') || (charData[0] == 'f') || (charData[0] == 'F')) {
                                this.c = false;
                            } else {
                                throw new unknownCommandException("invalid value for c");
                            }
                        }
                        break;
                    case "pt"/*short for ptr*/:
                        intData = Integer.parseInt(data);
                        this.ptr[intData / 10] = intData % 10;
                        break;
                    case "sb":
                        intData = Integer.parseInt(data);
                        if ((intData < 30) && (intData >= 0)) {
                            this.cdevice.sb = intData;
                        } else {
                            throw new unknownCommandException("invalid value for sb");
                        }
                        break;
                    case "db":
                        intData = Integer.parseInt(data);
                        if ((intData < 28) && (intData >= 0)) {
                            this.cdevice.db = intData;
                        } else {
                            throw new unknownCommandException("invalid value for db");
                        }
                        break;
                    case "st":
                        intData = Integer.parseInt(data);
                        if ((intData <= 4) && (intData >= 1)) {
                            this.cdevice.st = intData;
                        } else {
                            throw new unknownCommandException("invalid value for st");
                        }
                        break;
                    case "dt":
                        intData = Integer.parseInt(data);
                        if ((intData <= 4) && (intData >= 1)) {
                            this.cdevice.dt = intData;
                        } else {
                            throw new unknownCommandException("invalid value for dt");
                        }
                        break;
                }
            } catch (unknownCommandException ex) {
                this.pi = 1;
                System.out.println("SPEWING ERRORS" + reg + data);
            }
        } else {
            System.out.println("Calling Reg Int in User mode. Forbidden!");
        }
    }

//    public void SCHR() {
//        //nurodama kanalu irenginiui pradeti darba asinchroniskai
//        //jau privalo buti nustatyt SD, DB, ST, DT registrai
//        if (mode == 'S') {
//            //perkelti i pertraukimus 
//            cdevice.startWork();
//        } else {
//            System.out.println("Calling SCHR in User mode. Forbidden!");
//        }
//    }
    public void ADXX(int x1, int x2) {
        int valueFromR, valueFromMem;
        //System.out.println("String.valueOf(r)#" + String.valueOf(r) + "#");
        valueFromR = valueFromR();
        //System.out.println("valueFromR" + valueFromR);
        int realAdress = pager.getRealAdress(currentVMIndex, (x1 * 10 + x2));
        valueFromMem = Integer.valueOf(String.valueOf(memory[realAdress / 10][realAdress % 10]).trim());
        //System.out.println("valueFromMem" + valueFromMem);
        this.r = stringToDoubleCharArray(String.valueOf(valueFromR + valueFromMem));
        // (String.valueOf(Integer.valueOf(String.valueOf(r)) + Integer.valueOf(String.valueOf(memory[x1][x2])))).getChars(0, 36, r, 0);
        System.out.println("The value of r is " + doubleCharArrayToString(r).trim());
    }

    public void SBXX(int x1, int x2) {
        int valueFromR, valueFromMem;
        valueFromR = valueFromR();
        int realAdress = pager.getRealAdress(currentVMIndex, (x1 * 10 + x2));
        valueFromMem = Integer.valueOf(String.valueOf(memory[realAdress / 10][realAdress % 10]).trim());
        this.r = stringToDoubleCharArray(String.valueOf(valueFromR - valueFromMem));
    }

    public void LRXX(int x1, int x2) {
        int realAdress = pager.getRealAdress(currentVMIndex, (x1 * 10 + x2));
        for (int i = 0; i < 4; i++) {

            this.r[i] = memory[realAdress / 10][realAdress % 10 + i];
        }
    }

    public void SRXX(int x1, int x2) {
        int realAdress = pager.getRealAdress(currentVMIndex, (x1 * 10 + x2));
        for (int i = 0; i < 4; i++) {
            memory[realAdress / 10][realAdress % 10 + i] = this.r[i];
        }
    }

    public void CRXX(int x1, int x2) {
        int realAdress = pager.getRealAdress(currentVMIndex, (x1 * 10 + x2));
        boolean same = true;
        for (int i = 0; i < 4; i++) {
            for (int n = 0; n < 4; n++) {
                if (!(String.valueOf(r[i][n]).equals(String.valueOf(memory[realAdress / 10][realAdress % 10 + i][n])))) {
                    same = false;
                }
            }

        }
        this.c = same;
    }

    public void BTXX(int x1, int x2) {
        if (c) {
            ic = x1 * 10 + x2;
        }
    }

    public void GDXX(int x1) {
//        while (chr[0] || chr[1]){
//            try {
//             Thread.sleep(45);
//             } catch (InterruptedException ex) {
//                Logger.getLogger(ChannelDevice.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        this.ioAdress = x1;
        //this.cdevice.db = x1;
        this.si = 1;
    }

    public void PDXX(int x1) {
//        while (chr[0] || chr[1]){
//            try {
//             Thread.sleep(45);
//             } catch (InterruptedException ex) {
//                Logger.getLogger(ChannelDevice.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        this.ioAdress = x1;
        //this.cdevice.sb = x1;
        this.si = 2;
    }

    public void Halt() {
        this.si = 3;
    }

    public void pushXX(int x1, int x2) {
        int realAdress = pager.getRealAdress(currentVMIndex, (x1 * 10 + x2));
        sr++;
        this.memory[sr / 10][sr % 10] = this.memory[realAdress / 10][realAdress % 10];

    }

    public void pushReg(String reg) {
        char[] value;
        switch (reg) {
            case "r ":
                for (int i = 0; i < 4; i++) {
                    sr++;
                    this.memory[sr / 10][sr % 10] = this.r[i];
//                    if (this.r[i].length<4){
//                        for (int n = 3; n > (4-this.r[i].length); n--){
//                           this.memory[sr / 10][sr % 10][n] = ' '; 
//                        }
//                    }
                }
                break;
            case "pi":
                if (mode == 'S') {
                    sr++;
                    value = String.valueOf(this.pi).toCharArray();
                    this.memory[sr / 10][sr % 10] = this.appendCharArray(value);
                } else {
                    System.out.println("trying to push pi in User mode.");
                }
                break;
            case "si":
                if (mode == 'S') {
                    sr++;
                    value = String.valueOf(this.si).toCharArray();
                    this.memory[sr / 10][sr % 10] = this.appendCharArray(value);
                } else {
                    System.out.println("trying to push si in User mode.");
                }
                break;
            case "ic":
                sr++;
                value = String.valueOf(this.ic).toCharArray();
                this.memory[sr / 10][sr % 10] = this.appendCharArray(value);
                break;
            case "ti":
                if (mode == 'S') {
                    sr++;
                    value = String.valueOf(this.ti).toCharArray();
                    this.memory[sr / 10][sr % 10] = this.appendCharArray(value);
                } else {
                    System.out.println("trying to push pi ti User mode.");
                }
                break;
            case "io":
                if (mode == 'S') {
                    sr++;
                    value = String.valueOf(this.ioi).toCharArray();
                    this.memory[sr / 10][sr % 10] = this.appendCharArray(value);
                } else {
                    System.out.println("trying to push ioi in User mode.");
                }
                break;

            case "sr":
                if (mode == 'S') {
                    sr++;
                    value = String.valueOf(this.sr).toCharArray();
                    this.memory[sr / 10][sr % 10] = this.appendCharArray(value);
                } else {
                    System.out.println("trying to push sr in User mode.");
                }
                break;
            case "md":
                if (mode == 'S') {
                    sr++;
                    this.memory[sr / 10][sr % 10][0] = this.mode;
                } else {
                    System.out.println("trying to push mode in User mode.");
                }
                break;
            case "ch":
                if (mode == 'S') {
                    sr++;

                    for (int i = 0; i < 4; i++) {
                        if (chr[i]) {
                            this.memory[sr / 10][sr % 10][i] = 'T';
                        } else {
                            this.memory[sr / 10][sr % 10][i] = 'F';
                        }
                    }
                } else {
                    System.out.println("trying to push chr in User mode.");
                }
                break;
            case "c":
                sr++;
                if (c) {
                    this.memory[sr / 10][sr % 10][0] = 'T';
                } else {
                    this.memory[sr / 10][sr % 10][0] = 'F';
                }
                break;
            case "pt":
                if (mode == 'S') {
                    String s = "";
                    sr++;
                    for (int i = 0; i < 4; i++) {
                        s += String.valueOf(ptr[i]);
                    }
                    this.memory[sr / 10][sr % 10] = s.toCharArray();
                } else {
                    System.out.println("trying to push ptr in User mode.");
                }
                break;
            default:
                System.out.println("Unkown register name when trying to push a register");

        }
    }

    private char[] appendCharArray(char[] array) {
        //extends the aray to length 4 (by providing blanks)
        int amountToAppend = 4 - array.length;
        char[] trueArray = new char[4];
        for (int i = 0; i < 4; i++) {
            if (i < array.length) {
                trueArray[i] = array[i];
            } else {
                trueArray[i] = ' ';
            }
        }
        return trueArray;
    }

    public void popXX(int x1, int x2) {
        int realAdress = pager.getRealAdress(currentVMIndex, (x1 * 10 + x2));
        this.memory[realAdress / 10][realAdress % 10] = this.memory[sr / 10][sr % 10];
        sr--;
    }

    public void popReg(String reg) {
        switch (reg) {
            case "r ":
                for (int i = 3; i > 0; i--) {
                    this.r[i] = this.memory[sr / 10][sr % 10];
                    sr--;
                }
                break;
            case "pi":
                if (mode == 'S') {
                    pi = Integer.valueOf(String.valueOf(this.memory[sr / 10][sr % 10]).trim());
                    sr--;
                } else {
                    System.out.println("Trying to pop into pi register");
                }
                break;
            case "si":
                if (mode == 'S') {
                    si = Integer.valueOf(String.valueOf(this.memory[sr / 10][sr % 10]).trim());
                    sr--;
                } else {
                    System.out.println("Trying to pop into si register");
                }
                break;
            case "ic":
                ic = Integer.valueOf(String.valueOf(this.memory[sr / 10][sr % 10]).trim());
                sr--;
                break;
            case "ti":
                if (mode == 'S') {
                    ti = Integer.valueOf(String.valueOf(this.memory[sr / 10][sr % 10]).trim());
                    sr--;
                } else {
                    System.out.println("Trying to pop into ti register");
                }
                break;
            case "io":
                if (mode == 'S') {
                    ioi = Integer.valueOf(String.valueOf(this.memory[sr / 10][sr % 10]).trim());
                    sr--;
                } else {
                    System.out.println("Trying to pop into ioi register");
                }
                break;
            case "sr":
                if (mode == 'S') {
                    sr = Integer.valueOf(String.valueOf(this.memory[sr / 10][sr % 10]).trim());
                    sr--;
                } else {
                    System.out.println("Trying to pop into sr register");
                }
                break;
            case "md":
                if (mode == 'S') {
                    this.mode = this.memory[sr / 10][sr % 10][0];
                    sr--;
                } else {
                    System.out.println("Trying to pop into mode register");
                }
                break;
            case "ch":
                if (mode == 'S') {
                    for (int i = 4; i > 0; i--) {
                        if (String.valueOf(this.memory[sr / 10][sr % 10][i]).equals("T")) {
                            chr[i] = true;
                        } else {
                            chr[i] = false;
                        }
                    }
                    sr--;
                } else {
                    System.out.println("Trying to pop into chr register");
                }
                break;
            case "c":
                if (String.valueOf(this.memory[sr / 10][sr % 10][0]).equals("T")) {
                    c = true;
                } else {
                    c = false;
                }
                sr--;
                break;
            case "pt":
                if (mode == 'S') {
                    String s = String.valueOf(this.memory[sr / 10][sr % 10]);
                    for (int i = 0; i < 4; i++) {
                        ptr[i] = s.charAt(i);
                    }
                    sr--;
                } else {
                    System.out.println("Trying to pop into ptr register");
                }
                break;
            default:
                System.out.println("Unkown register name when trying to pop a register");

        }
    }

    public void CallXX(int x1, int x2) {
        pushReg("ic");
        ic = x1 * 10 + x2;
    }

    public void Ret() {
        popReg("ic");
    }

    public void Jmp(int x1, int x2) {
        ic = x1 * 10 + x2;
    }

    public void Jc(int x1, int x2) {
        if (c) {
            ic = x1 * 10 + x2;
        }
    }

    private char[][] stringToDoubleCharArray(String charData) {
        return stringToDoubleCharArray(charData, 4, 4);
    }

    private char[][] stringToDoubleCharArray(String charData, int firstA, int secondA) {
        int counter = 0;
        int charDataLength = charData.length();
        char[][] data = new char[firstA][secondA];
        for (int i = charDataLength; i < 16; i++) {
            charData += " ";
        }
        for (int i = 0; i < firstA; i++) {
            charData.getChars(counter, counter + secondA, data[i], 0);
            counter += 4;
        }
//        for (int i = 0; i < firstA; i++) {
//            for (int n = 0; n < secondA; n++) {
//                charData.getChars(counter, counter+3, data[i], 0);
//                counter+=4;
//            }
//        }
        return data;
    }

    private String doubleCharArrayToString(char[][] charData) {
        String data = "";
        for (int i = 0; i < charData.length; i++) {
            data += String.valueOf(charData[i]);
        }
        return data;
    }

    private int valueFromR() {
        return Integer.valueOf(doubleCharArrayToString(r).trim());
    }

    public void printAllMem() {
        System.out.println("printing all memory");
        for (int i = 0; i < 30; i++) {
            System.out.println("memory block #" + i);
            for (int n = 0; n < 10; n++) {
                System.out.print("word #" + n + " ");
                for (int m = 0; m < 4; m++) {
                    System.out.print(String.valueOf(memory[i][n][m]));
                }
                System.out.println();
            }

        }
    }

    public void printRegisters() {
        System.out.println("printing registers");
        System.out.println("r = " + doubleCharArrayToString(r));
        System.out.println("c = " + Boolean.toString(c));
        System.out.println("ic = " + this.ic);
    }

    public void runtime() throws FileNotFoundException {
        String[] cmd;
        pager.newVMTable();
        currentVMIndex = 0;
        ProgramosPakrovimas(currentVMIndex);
        System.out.println("Loader loaded.");
        this.printAllMem();
        while (run) {

            cmd = KomandosDekodavimas();
            KomandosVykdymas(cmd);
            if (chr[0] || chr[1]) {
            } else {
                Timer();
            }

            //Timer();
            PertraukimuTikrinimas();
            KanaluIrenginys();
            //System.out.println(this.ti);
            if (chr[0] || chr[1]) {
            } else {
                this.printRegisters();
            }
            //this.printAllMem();
            //Scanner sc = new Scanner(System.in);
            //sc.next();
        }
        cdevice.kill();
    }

    private void ProgramosPakrovimas(int VMIndex) throws FileNotFoundException {
        // Pakraunama programa į atmintį, nustatoma puslapių lentelė.
        // Puslapių lentelės puslpius geriau formuoti atsitiktine tvarka (ne iš eilės), tokiu atveju tikrai bus ištestuotas puslapiavimo lentelės veikimas.
        String fileName = "loading.txt";
        String data;
        int currBlock = pager.getRealAdress(VMIndex, 0);
        int currWord = 0;
        Scanner reader = new Scanner(new FileInputStream(fileName));
        if (!reader.nextLine().equals("START")) {
            System.exit(1);
        }
        while (reader.hasNext()) {
            data = reader.nextLine();
            System.out.println("nextLine:" + data);
            if (String.valueOf(data.charAt(0)).equals("#")) {
                currBlock = pager.getRealAdress(VMIndex, Integer.valueOf(data.substring(1, data.lastIndexOf("#"))) * 10) / 10;
                //System.out.println(currBlock);
                currWord = 0;
            } else {
                if (!data.equals("END")) {
                    this.memory[currBlock][currWord] = data.toCharArray();
                    currWord++;
                }
            }
        }

    }

    private String[] KomandosDekodavimas() {
        // Pagal komandų skaitliuko registrą nuskaitoma komanda, nustatoma kokia tai yra komanda.
        int realAdress = pager.getRealAdress(currentVMIndex, ic);
        String komanda = String.valueOf(this.memory[realAdress / 10][ realAdress % 10]);
        //String komanda = pager.getRealAdress(0, ic);
        String komandosPr = komanda.substring(0, 2);
        String[] cmd = new String[2];
        switch (komandosPr) {
            case "AD":
                cmd[0] = "AD";
                cmd[1] = komanda.substring(2, 4);
                break;
            case "SB":
                cmd[0] = "SB";
                cmd[1] = komanda.substring(2, 4);
                break;
            case "LR":
                cmd[0] = "LR";
                cmd[1] = komanda.substring(2, 4);
                break;
            case "SR":
                cmd[0] = "SR";
                cmd[1] = komanda.substring(2, 4);
                break;
            case "CR":
                cmd[0] = "CR";
                cmd[1] = komanda.substring(2, 4);
                break;
            case "BT":
                cmd[0] = "BT";
                cmd[1] = komanda.substring(2, 4);
                break;
            case "GD":
                cmd[0] = "GD";
                cmd[1] = komanda.substring(2, 4);
                break;
            case "PD":
                cmd[0] = "PD";
                cmd[1] = komanda.substring(2, 4);
                break;
            case "Ha":
                cmd[0] = "Halt";
                break;
            case "PO":
                cmd[0] = "POP";
                cmd[1] = komanda.substring(2, 4);
                break;
            case "PU":
                cmd[0] = "PUSH";
                cmd[1] = komanda.substring(2, 4);
                break;
            case "CA":
                cmd[0] = "CALL";
                cmd[1] = komanda.substring(2, 4);
                break;
            case "RE":
                cmd[0] = "RET";
                break;
            case "JM":
                cmd[0] = "JMP";
                cmd[1] = komanda.substring(2, 4);
                break;
            case "JC":
                cmd[0] = "JC";
                cmd[1] = komanda.substring(2, 4);
                break;
            default:
                this.pi = 1;
                break;
        }
        ic++;
        return cmd;
    }

    private void KomandosVykdymas(String[] cmd) {
        // Įvykdoma komanda. Gali būti keičiamos tik registrų arba atminties reišmės.
        int value;
        switch (cmd[0]) {
            case "AD":
                value = Integer.valueOf(cmd[1]);
                this.ADXX(value / 10, value % 10);
                break;
            case "SB":
                value = Integer.valueOf(cmd[1]);
                this.SBXX(value / 10, value % 10);
                break;
            case "LR":
                value = Integer.valueOf(cmd[1]);
                this.LRXX(value / 10, value % 10);
                break;
            case "SR":
                value = Integer.valueOf(cmd[1]);
                this.SRXX(value / 10, value % 10);
                break;
            case "CR":
                value = Integer.valueOf(cmd[1]);
                this.CRXX(value / 10, value % 10);
                break;
            case "BT":
                value = Integer.valueOf(cmd[1]);
                this.BTXX(value / 10, value % 10);
                break;
            case "GD":
                if (chr[0] || chr[1]) {
                    this.ic--;
                } else {
                    value = Integer.valueOf(cmd[1]);
                    this.GDXX(value / 10);
                }
                break;
            case "PD":
                if ((chr[0]) || (chr[1])) {
                    this.ic--;
                } else {
                    value = Integer.valueOf(cmd[1]);
                    this.PDXX(value / 10);
                }
                break;
            case "Halt":
                this.Halt();
                break;
            case "POP":
                if (RealMachine.isInteger(cmd[1])) {
                    //this is a POPXX command
                    value = Integer.valueOf(cmd[1]);
                    this.popXX(value / 10, value % 10);
                } else {
                    // this is a POP REG command
                    this.popReg(cmd[1]);
                }
                break;
            case "PUSH":
                if (RealMachine.isInteger(cmd[1])) {
                    //this is a PUSHXX command
                    value = Integer.valueOf(cmd[1]);
                    this.pushXX(value / 10, value % 10);
                } else {
                    // this is a PUSH REG command
                    this.pushReg(cmd[1]);
                }
                break;
            case "CALL":
                value = Integer.valueOf(cmd[1]);
                this.CallXX(value / 10, value % 10);
                break;
            case "RET":
                this.Ret();
                break;
            case "JMP":
                value = Integer.valueOf(cmd[1]);
                this.Jmp(value / 10, value % 10);
                break;
            case "JC":
                value = Integer.valueOf(cmd[1]);
                this.Jc(value / 10, value % 10);
                break;
        }
        if ((cmd[0] == "PD") || (cmd[0] == "GD")) {
            if (chr[0] || chr[1]) {
            } else {
                System.out.println("Command:" + cmd[0]);
            }
        } else {
            System.out.println("Command:" + cmd[0]);
        }
    }

    private void Timer() {
        // Sumažinamas timer registras.
        this.ti--;
    }

    private void KanaluIrenginys() {
        // Pagal nustatytus kanalų įrenginio registrus (arba parametrus) suveikia kanalų įrenginys. Baigus duomenų perdavimą nustatoma IOI registro reikšmė.
//        if ((chr[0] == true) || (chr[1] == true) || (chr[2] == true) || chr[3] == true) {
//            mode = 'S';
//            this.SCHR();
//            mode = 'U';
//        }
    }

    private void PertraukimuTikrinimas() {
        // Tikrinama ar visų ankstesnių žingsnių metu nebuvo pertraukimų (procesoriaus, timer, kanalų įrenginio).
        // Jeigu pertraukimas buvo persijungia į supervizoriaus režimą ir įvykdo atitinkamą pertraukimo procedūrą.
        // Esant duomenų įvedimui / išvedimui nustatomi kanalų įrenginio registrai(parametrai) įvedimas-išvedimas vykdomas kanalų įrenginio procedūros metu.
        // Antroje užduotyje laukiant įvedimo-išvedimo nėra kitų procesų kuriems būtų perduodamas procesoriaus laikas,
        // tad galima kol negautas įvedimas tiesiog sustabdyti procesoriaus komandų vykdymą (1,2 ciklo žingsniai).
        if ((this.pi + this.si > 0) || this.ti <= 0) {
            mode = 'S';
        }
        if (this.pi != 0) {
            switch (this.pi) {
                case 1:
                    System.out.println("Bad command");
                    run = false;
                    break;
                case 2:
                    System.out.println("Trying to adress unaccesible memory");
                    run = false;
                    break;
                default:
                    this.regInt("pi", "0");
                //this.pi = 0;
            }
        }
        if (this.si != 0) {
            switch (this.si) {
                case 1:
                    chr[0] = true;
                    //this.cdevice.dt = 1;
                    //this.cdevice.st = 4;
                    this.regInt("db", String.valueOf(pager.getRealAdress(currentVMIndex, this.ioAdress * 10) / 10));
                    this.regInt("dt", "1");
                    this.regInt("st", "4");
                    cdevice.startWork();
                    break;
                case 2:
                    chr[1] = true;
                    //this.cdevice.st = 1;
                    //this.cdevice.dt = 4;
                    this.regInt("sb", String.valueOf(pager.getRealAdress(currentVMIndex, this.ioAdress * 10) / 10));
                    this.regInt("st", "1");
                    this.regInt("dt", "4");
                    cdevice.startWork();
                    break;
                case 3:
                    run = false;
                    break;
                default:
                    //this.si = 0;
                    this.regInt("si", "0");
            }
        }
        if (this.ti <= 0) {
            System.out.println("Time out exception");
            run = false;
        }
        mode = 'U';
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public void setChr(int i, boolean value) {
        this.chr[i] = value;
    }

}
