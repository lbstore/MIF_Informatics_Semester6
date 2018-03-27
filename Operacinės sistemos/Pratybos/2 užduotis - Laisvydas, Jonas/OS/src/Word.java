
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;


public class Word {
    public static final int SIZE = 4; //bytes
    private final byte[] data;
    
    public Word(){
        data = new byte[SIZE];
    }
    
    public Word(Word src){
        data = src.data.clone();
    }
    
    public byte getByte(int index){
        return data[index];
    }
    public void setByte(int index, byte info){
        data[index] = info;
    }
    @Override
    public Word clone(){
        return new Word(this);
    }
    
    public boolean equals(Word another){
        return Arrays.equals(data, another.data);
    }

    byte[] getBytes() {
        return Arrays.copyOf(data, SIZE);
    }
    
    public static int wordToInt(Word word) {
        ByteBuffer bb = ByteBuffer.allocateDirect(SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.clear();
        for(int i = 0; i < SIZE; i++){
            bb.put(word.getByte(i));
        }
        bb.position(0);
        return bb.getInt();
    }
    
    public static Word intToWord(int value) {
        ByteBuffer bb = ByteBuffer.allocateDirect(SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.clear();
        bb.putInt(value);
        Word word = new Word();
        for(int i = 0; i < SIZE; i++){
            word.setByte(i, bb.get(i));
        }
        return word;
    }
}
