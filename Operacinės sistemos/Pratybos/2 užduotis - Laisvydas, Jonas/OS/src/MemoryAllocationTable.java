
import java.util.Arrays;

/*
public class MemoryAllocationTable {
    public final int SIZE; //blocks
    private final boolean[] table;
    
    public MemoryAllocationTable(int size){
        SIZE = size;
        table = new boolean[size];
        Arrays.fill(table, false);
    }
    public boolean allocated(int index){
        return table[index];
    }
    
    public void setAllocated(int index, boolean value){
        table[index] = value;
    }

    public int getFreeBlockIndex() {
        for(int i= 0; i < SIZE; i++){
            if(!table[i])
                return i;
        }
        return -1;
    }
}
*/

public class MemoryAllocationTable {
    public final int SIZE; //blocks
    private final boolean[] table;
    private Memory memory;
    private int address;
    
    public MemoryAllocationTable(Memory memory, int address, int size /*blocks*/){
        if((size/ (Word.SIZE * 8)) + address + 1 > memory.getSize()){
            throw new IllegalArgumentException();
        }
        SIZE = size;
        table = new boolean[size];
        this.memory = memory;
        this.address = address;
    }
    public boolean isAllocated(int index){
        return table[index];
    }
    
    public void setAllocated(int index, boolean value) throws MemoryException{
        table[index] = value;
        Word wordWithNeededBit = memory.read(address + (index / (Word.SIZE * 8)));
        byte byteWithNeededBit = wordWithNeededBit.getByte((index % (Word.SIZE * 8)) / 8);
        if(value){
            byteWithNeededBit = (byte) (byteWithNeededBit | (1 << (index % 8)));
        } else {
            byteWithNeededBit = (byte) (byteWithNeededBit & ~(1 << (index % 8)));
        }
        wordWithNeededBit.setByte((index % (Word.SIZE * 8)) / 8, byteWithNeededBit);
        memory.write(wordWithNeededBit, address + (index / (Word.SIZE * 8)));
    }

    public int getFreeBlockIndex() {
        for(int i= 0; i < SIZE; i++){
            if(!table[i])
                return i;
        }
        return -1;
    }
}
