
import java.util.Arrays;



public class RealMemory implements Memory{
    private int size; //words
    private Word[] memory;
    
    public RealMemory(int size){
        if(size >= 0){
           this.size = size;
           memory = new Word[size];
           for(int i = 0; i < size; i++){
               memory[i] = new Word();
           }
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public RealMemory(RealMemory mem){
        if(mem == null)
            throw new IllegalArgumentException();
        size = mem.size;
        
    }
    
    
    
    @Override
    public Word read(int address){
        return memory[address].clone();
    }
    @Override
    public void write(Word word, int address){
        memory[address] = word.clone();
    }

    @Override
    public int getSize() {
        return size;
    }
    
    public Word[] viewData(){
        return Arrays.copyOf(memory, size);
    }
}