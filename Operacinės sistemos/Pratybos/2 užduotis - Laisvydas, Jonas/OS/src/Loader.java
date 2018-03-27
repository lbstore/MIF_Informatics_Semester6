
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


//NOTE: data string cannot contain symbols ';' and ','

public class Loader {

    HashMap<String, Byte> commands;
    
    public Loader(){
        commands = new HashMap<>();
        commands.put("PUSH", CPU.PUSH);
        commands.put("PSHC", CPU.PSHC);
        commands.put("POP", CPU.POP);
        commands.put("SET", CPU.SET);
        commands.put("SETC", CPU.SETC);
        commands.put("TOP", CPU.TOP);
        commands.put("ADD", CPU.ADD);
        commands.put("SUB", CPU.SUB);
        commands.put("MUL", CPU.MUL);
        commands.put("DIV", CPU.DIV);
        commands.put("JZ", CPU.JZ);
        commands.put("JP", CPU.JP);
        commands.put("JN", CPU.JN);
        commands.put("JMP", CPU.JMP);
        commands.put("GETD", CPU.GETD);
        commands.put("PUTD", CPU.PUTD);
        commands.put("HALT", CPU.HALT);
    }
    
    private Word commandToWord(String line) throws LoaderParseException{
        String[] parts = line.split("\\s");
        Word word = new Word();
        Byte commandObj;
        byte command = 0;
        int parameter = 0;
        if(parts.length > 0){
            if((commandObj = commands.get(parts[0].toUpperCase())) != null){
                command = commandObj.byteValue();
            }
            if(parts.length == 2){
                parameter = Integer.parseInt(parts[1]);
            }
            if(command == CPU.POP){
                if(parts.length == 1){
                    command = CPU.POP0;
                } else {
                    command = CPU.POP1;
                }
            }
            word.setByte(3, command);
            word.setByte(2, (byte)(parameter/65536));
            word.setByte(1, (byte)((parameter/256) % 256));
            word.setByte(0, (byte)(parameter % 256));
            return word;
        } else {
            throw new LoaderParseException();
        }
    }

    public VirtualMachine load(RM rm, MMU memory, File code, VirtualMachine vm) throws LoaderParseException, OutOfMemoryException, MemoryException{
        if(memory == null || vm == null || vm.getPTR() < 0){
            throw new IllegalArgumentException();
        }
        try {
            BufferedReader codeReader;
            codeReader = new BufferedReader(new FileReader(code));
            String currentLine;
            boolean fillData = false;
            boolean fillCode = false;
            int commentStart;
            int currentAddress = 0;
            int lastDataWord = -1;
            while(codeReader.ready()){
                currentLine = codeReader.readLine();
                if(currentLine.isEmpty()){
                    continue;
                }
                //System.out.println(currentLine);
                if((commentStart = currentLine.indexOf(";")) != -1){
                    //System.out.println("Comment");
                    currentLine = currentLine.substring(0, commentStart);
                }
                currentLine = currentLine.trim();
                if(currentLine.equalsIgnoreCase("DATA")){
                    //System.out.println("DATA");
                    if(!fillData && !fillCode){
                        fillData = true;
                        continue;
                    } else {
                        throw new LoaderParseException();
                    }
                }
                if(currentLine.equalsIgnoreCase("CODE")){
                    //System.out.println("CODE");
                    if(!fillCode){
                        fillData = false;
                        fillCode = true;
                        currentAddress = lastDataWord + 1;
                        continue;
                    } else {
                        throw new LoaderParseException();
                    }
                }
                if(fillData){
                    int temp;
                    try {
                        temp = setData(currentLine, memory);
                    } catch (BlockSwapException ex) {
                        rm.executeSwap();
                        try {
                            temp = setData(currentLine, memory);
                        } catch (BlockSwapException ex1) {
                            throw new Error();
                        }
                    }
                    
                    if(temp > lastDataWord){
                        lastDataWord = temp;
                    }
                    continue;
                }
                if(fillCode){
                    try {
                        memory.write(commandToWord(currentLine), currentAddress);
                    } catch (BlockSwapException ex) {
                        rm.executeSwap();
                        try {
                            memory.write(commandToWord(currentLine), currentAddress);
                        } catch (BlockSwapException ex1) {
                            throw new Error();
                        }
                    }
                    currentAddress++;
                    continue;
                }
                throw new LoaderParseException();
            }
            if(fillCode){
                vm.setPC(lastDataWord + 1);
                vm.setSP(currentAddress);
                return vm;
            } 
            
        } catch (FileNotFoundException ex) {
            throw new LoaderParseException();
        } catch (IOException ex) {
            throw new LoaderParseException(); 
        }
        throw new LoaderParseException();
    }

    
    private int setData(String line, MMU memory) throws LoaderParseException, BlockSwapException, OutOfMemoryException, MemoryException {
        int firstSpace = line.indexOf(" ");
        if(firstSpace == -1){
            throw new LoaderParseException();
        }
        int startAddress = Integer.parseInt(line.substring(0, firstSpace));
        line = line.substring(firstSpace + 1);
        int secondSpace = line.indexOf(" ");
        if(secondSpace == -1){
            throw new LoaderParseException();
        }
        boolean words;
        words = line.substring(0, secondSpace).trim().equalsIgnoreCase("w");
        line = line.substring(secondSpace + 1);
        String[] data = line.split(",");
        for(int i = 0; i < data.length; i++){
            data[i] = data[i].trim();
        }
        ArrayList rawData = new ArrayList();
        if(data[0].equalsIgnoreCase("dup")){
            int size = Integer.parseInt(data[1]);
            int val = Integer.parseInt(data[2]);
            rawData.ensureCapacity(size);
            for(int i = 0; i <size; i++){
                rawData.add(val);
            }
        } else {
            for (String data1 : data) {
                if (data1.contains("\"")) {
                    for (int j = 1; j < data1.length() - 1; j++) {
                        rawData.add((int) data1.charAt(j));
                    }
                } else {
                    rawData.add(Integer.parseInt(data1));
                }
            }
        }
        if(rawData.isEmpty()){
                return -1;
        }
        if(words){
            for(int i = 0; i < rawData.size(); i++){
                memory.write(Word.intToWord((Integer) rawData.get(i)), startAddress + i);
            }
            return startAddress + rawData.size() -1;
        } else {
            Word tempW;
            for(int i = 0; i < rawData.size(); i++){
                tempW = memory.read(startAddress + (i / Word.SIZE));
                tempW.setByte(Word.SIZE -1 - (i % Word.SIZE), (byte) (int) (Integer) rawData.get(i));
                memory.write(tempW, startAddress + (i / Word.SIZE));
            }
            int returnValue = startAddress + (rawData.size() / Word.SIZE);
            if(rawData.size() % Word.SIZE == 0){
                returnValue--;
            }
            return returnValue;
        }
        
    }
    
}
