
public interface Memory{
    public Word read(int address) throws MemoryException;
    public void write(Word word, int address) throws MemoryException;
    public int getSize();
}
