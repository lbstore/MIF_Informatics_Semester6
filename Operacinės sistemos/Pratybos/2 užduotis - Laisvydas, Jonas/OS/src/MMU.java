
import java.util.ArrayList;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jonas
 */
public class MMU{
    public final static int BLOCKSIZE = 256;
    private final int userBlocks;
    private final int swapBlocks;
    private final int supervisorBlocks;
    private CPU cpu;
    private Memory memory;
    private MemoryAllocationTable userTable;
    private MemoryAllocationTable swapTable;
    private RM realMachine;
    private int fromSwap;
    private int toSwap;
    private int fromSwapPTA;
    private int toSwapPTA;
    private int fromSwapPage;
    private int toSwapPage;
    
    
    
    
    public MMU(CPU cpu, Memory realMemory, RM realMachine, int swapSize /*words*/){
        if(cpu == null || realMemory == null || realMachine == null || (realMachine.getSupervisorSize() > realMemory.getSize())){
            System.out.println("shit");
            throw new IllegalArgumentException();
        }
        if(((realMemory.getSize() - realMachine.getSupervisorSize()) % BLOCKSIZE) != 0){
            throw new IllegalArgumentException();
        }
        if((swapSize % BLOCKSIZE) != 0){
            throw new IllegalArgumentException();
        }
        if((realMachine.getSupervisorSize() % BLOCKSIZE) != 0){
            throw new IllegalArgumentException();
        }
        this.cpu = cpu;
        this.memory = realMemory;
        this.realMachine = realMachine;
        supervisorBlocks = realMachine.getSupervisorSize() / BLOCKSIZE;
        userBlocks = (memory.getSize() - realMachine.getSupervisorSize()) / BLOCKSIZE;
        userTable = new MemoryAllocationTable(memory, realMachine.getUserAllocTableAddress(), userBlocks);
        swapBlocks = swapSize/BLOCKSIZE;
        swapTable = new MemoryAllocationTable(memory, realMachine.getSwapAllocTableAddress(), swapBlocks);
        
        
    }

    public Word read(int address) throws OutOfMemoryException, BlockSwapException, MemoryException {
        if(cpu.getMODE() == CPU.SUPERVISOR){
            return memory.read(address);
        } else if (cpu.getMODE() == CPU.USER){
            int realAddress = virtualToRealAddress(address);
            if(realAddress < realMachine.getSupervisorSize()){
                throw new MemoryException();
            } else {
                return memory.read(realAddress);
            }
        } else {
            throw new Error();
        }
    }

    public void write(Word word, int address) throws OutOfMemoryException, BlockSwapException, MemoryException {
        if(cpu.getMODE() == CPU.SUPERVISOR){
            memory.write(word, address);
        } else if (cpu.getMODE() == CPU.USER){
            int realAddress = virtualToRealAddress(address);
            if(realAddress < realMachine.getSupervisorSize()){
                throw new MemoryException();
            } else {
                memory.write(word, realAddress);
            }
        } else {
            throw new Error();
        }
    }
    
    public int getBlockNrToSwap(){
        return toSwap;
    }
    
    public int getBlockNrFromSwap(){
        return fromSwap;
    }
    
    public void updateTablesAfterSwap() throws MemoryException{
        if(toSwapPage != -1){
            PageRecord swappedOut = getPageRecord(toSwapPage, toSwapPTA);
            swappedOut.setSwapped(true);
            swappedOut.setFrame(fromSwap);
            updatePageRecord(swappedOut, toSwapPage, toSwapPTA);
        } else {
            swapTable.setAllocated(fromSwap, false);
        }
        if(fromSwapPage != -1){
            PageRecord swappedIn = getPageRecord(fromSwapPage, fromSwapPTA);
            swappedIn.setSwapped(false);
            swappedIn.setFrame(toSwap - supervisorBlocks);
            updatePageRecord(swappedIn, fromSwapPage, fromSwapPTA);
        } else {
            userTable.setAllocated(toSwap -supervisorBlocks, false);
        }
    }
    
    public void freeMemory(int pageTableAddress) throws MemoryException{
        PageRecord record;
        for(int i = 0; i < userBlocks + swapBlocks; i++){
            record = getPageRecord(i, pageTableAddress);
            if(record.isMapped()){
                if(record.isSwapped()){
                    swapTable.setAllocated(record.getFrame(), false);
                } else {
                    userTable.setAllocated(record.getFrame(), false);
                }
            }
            clearPageRecord(i, pageTableAddress);
        }
    }
    
    private PageRecord getPageRecord(int pageNumber, int pageTableAddress) throws MemoryException{
        int wordNumberInPageTable = pageNumber / (Word.SIZE / 2);
        Word fromPageTable = memory.read(pageTableAddress + wordNumberInPageTable);
        int recordIndexInWord = (pageNumber % (Word.SIZE / 2));
        byte flags = fromPageTable.getByte((Word.SIZE - 1) - recordIndexInWord * 2);
        int frameNumber = fromPageTable.getByte((Word.SIZE - 2) - recordIndexInWord * 2);
        return new PageRecord(flags, frameNumber);
    }
    
    private void updatePageRecord(PageRecord record, int pageNumber, int pageTableAddress) throws MemoryException{
        int wordNumberInPageTable = pageNumber / (Word.SIZE / 2);
        Word fromPageTable = memory.read(pageTableAddress + wordNumberInPageTable);
        int recordIndexInWord = (pageNumber % (Word.SIZE / 2));
        fromPageTable.setByte((Word.SIZE - 1) - recordIndexInWord * 2, record.getFlags());
        fromPageTable.setByte((Word.SIZE - 2) - recordIndexInWord * 2, (byte) record.getFrame());
        memory.write(fromPageTable, pageTableAddress + wordNumberInPageTable);
    }
    
    private void clearPageRecord(int pageNumber, int pageTableAddress) throws MemoryException{
        int wordNumberInPageTable = pageNumber / (Word.SIZE / 2);
        Word fromPageTable = memory.read(pageTableAddress + wordNumberInPageTable);
        int recordIndexInWord = (pageNumber % (Word.SIZE / 2));
        fromPageTable.setByte((Word.SIZE - 1) - recordIndexInWord * 2, (byte) 0);
        fromPageTable.setByte((Word.SIZE - 2) - recordIndexInWord * 2, (byte) 0);
        memory.write(fromPageTable, pageTableAddress + wordNumberInPageTable);
    }
    
    private int virtualToRealAddress(int address) throws OutOfMemoryException, BlockSwapException, MemoryException{
        int pageNumber = address/BLOCKSIZE;
        PageRecord record = getPageRecord(pageNumber, cpu.getPTR());
        if(!record.isMapped()){
            record = map(pageNumber);
        } else if(record.isSwapped()){
            fromSwapPTA = cpu.getPTR();
            fromSwapPage = pageNumber;
            fromSwap = record.getFrame();
            toSwap = blockNumberToReplace();
            throw new BlockSwapException();
        }
        return (record.getFrame() + supervisorBlocks) * BLOCKSIZE + (address % BLOCKSIZE);
    }

    private int blockNumberToReplace() throws MemoryException {
        int frame = userTable.getFreeBlockIndex();
        if(frame != -1){
            toSwapPTA = -1;
            toSwapPage = -1;
            return frame;
        }
        PageRecord record;
        Triplet chosenPage = null;
        ArrayList<Triplet> class1Pages = new ArrayList();
        ArrayList<Triplet> class2Pages = new ArrayList();
        ArrayList<Triplet> class3Pages = new ArrayList();
        for(int i = 0; i < userBlocks + swapBlocks; i++){
            if(chosenPage != null){
                break;
            }
            VirtualMachine vm = realMachine.getVM(i);
            if(vm.getLoaded() == 0){
                continue;
            }
            int pta = vm.getPTR();
            for(int j = 0; j < userBlocks + swapBlocks; j++){
                record = getPageRecord(j, pta);
                if(record.isMapped() && !record.isSwapped()){
                    if(!record.isReferenced() && !record.isModified()){ //class0
                        chosenPage = new Triplet(pta, j, record);
                        break;
                    } else if (record.isReferenced() && record.isModified()){
                        class3Pages.add(new Triplet(pta, j, record));
                    } else if (record.isReferenced()){
                        class2Pages.add(new Triplet(pta, j, record));
                    } else {
                        class1Pages.add(new Triplet(pta, j, record));
                    }
                }
            }
        }
        if(chosenPage == null){
            if(class1Pages.size() > 0){
                chosenPage = class1Pages.get(0);
            } else if (class2Pages.size() > 0){
                chosenPage = class2Pages.get(0);
            } else {
                chosenPage = class3Pages.get(0);
            }
        }
        toSwapPTA = (int) chosenPage.x;
        toSwapPage = (int) chosenPage.y;
        return ((PageRecord) chosenPage.z).getFrame() + supervisorBlocks;
    }

    private PageRecord map(int pageNumber) throws OutOfMemoryException, BlockSwapException, MemoryException {
        int frame = userTable.getFreeBlockIndex();
        if(frame == -1){
            fromSwap = swapTable.getFreeBlockIndex();
            if(fromSwap == -1){
                throw new OutOfMemoryException();
            }
            swapTable.setAllocated(fromSwap, true);
            fromSwapPTA = -1;
            fromSwapPage = -1;
            toSwap = blockNumberToReplace();
            throw new BlockSwapException();
        }
        userTable.setAllocated(frame, true);
        PageRecord record = new PageRecord();
        record.setFrame(frame);
        record.setMapped(true);
        updatePageRecord(record, pageNumber, cpu.getPTR());
        
        return record;
    }
    
}
