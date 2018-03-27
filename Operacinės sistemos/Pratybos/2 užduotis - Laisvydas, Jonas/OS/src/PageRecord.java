
public class PageRecord {
    private int frameNumber;
    private boolean mapped;
    private boolean swapped;
    private boolean referenced;
    private boolean modified;
    
    private final static byte mappedMask = (byte) 0x80;
    private final static byte swappedMask = (byte) 1;
    private final static byte referencedMask = (byte) 2;
    private final static byte modifiedMask = (byte) 4;
    
    
    public PageRecord(){
        frameNumber = 0;
        mapped = false;
        swapped = false;
        referenced = false;
        modified = false;
    }
    
    public PageRecord(byte flags, int frameNumber){
        this.frameNumber = frameNumber;
        mapped = (flags & mappedMask) != 0;
        swapped = (flags & swappedMask) != 0;
        referenced = (flags & referencedMask) != 0;
        modified = (flags & modifiedMask) != 0;
    }
    
    public byte getFlags(){
        byte flags = 0;
        if(mapped){
            flags = (byte) (flags | mappedMask);
        }
        if(referenced){
            flags = (byte) (flags | referencedMask);
        }
        if(modified){
            flags = (byte) (flags | modifiedMask);
        }
        if(swapped){
            flags = (byte) (flags | swappedMask);
        }
        return flags;
    }
    
    public int getFrame(){
        return frameNumber;
    }
    public void setFrame(int frameNumber){
        this.frameNumber = frameNumber;
    }
    public boolean isMapped(){
        return mapped;
    }
    public void setMapped(boolean value){
        mapped = value;
    }
    public boolean isSwapped(){
        return swapped;
    }
    public void setSwapped(boolean value){
        swapped = value;
    }
    public boolean isReferenced(){
        return referenced;
    }
    public void setReferenced(boolean value){
        referenced = value;
    }
    public boolean isModified(){
        return modified;
    }
    public void setModified(boolean value){
        modified = value;
    }
    
    public void clear(){
        frameNumber = 0;
        mapped = false;
        swapped = false;
        referenced = false;
        modified = false;
    }
}
