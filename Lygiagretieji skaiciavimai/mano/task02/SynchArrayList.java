package task02;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Laimonas Beniušis
 * Nepilnai įgyvendintas List interface, thread-safe
 * 
 */
public class SynchArrayList implements List{
    private ArrayList<Object> list;
    private ReadWriteLock lock;
    public SynchArrayList(){
        list = new ArrayList<>();
        lock = new ReadWriteLock();
    }
    @Override
    public boolean add(Object o){
        try {
            lock.lockWrite();
            this.list.add(o);
            lock.unlockWrite();
            return true;
        } catch (InterruptedException ex) {
            Logger.getLogger(SynchArrayList.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    @Override
    public void add(int i, Object o){
        try {
            lock.lockWrite();
            this.list.add(i,o);
            lock.unlockWrite();
        } catch (InterruptedException ex) {
            Logger.getLogger(SynchArrayList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public Object remove(int i){
        Object o = null;
        try {
            lock.lockWrite();
            o = this.list.remove(i);
            lock.unlockWrite();
        } catch (InterruptedException ex) {
            Logger.getLogger(SynchArrayList.class.getName()).log(Level.SEVERE, null, ex); 
        }
        return o;
    }
    
    @Override
    public Object get(int i){
        Object o = null;
        try {          
            lock.lockRead();
            o = this.list.get(i);
            lock.unlockRead();  
        } catch (InterruptedException ex) {
            Logger.getLogger(SynchArrayList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return o;
    }
    
    
    @Override
    public String toString(){
        try {
            lock.lockRead();
            ArrayList col = new ArrayList();
            col.addAll(list);
            lock.unlockRead();
            return col.toString();
        } catch (InterruptedException ex) {
            Logger.getLogger(SynchArrayList.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    @Override
    public boolean remove(Object o) {
        try {
            lock.lockWrite();
            this.list.remove(o);
            lock.unlockWrite();
            return true;
        } catch (InterruptedException ex) {
            
            Logger.getLogger(SynchArrayList.class.getName()).log(Level.SEVERE, null, ex); 
            return false;
        }
        
    }

    @Override
    public int size() {
        return -1;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return null;
    }

    @Override
    public Object[] toArray(Object[] ts) {
        return null;
    }
    
    @Override
    public boolean containsAll(Collection clctn) {
        return false;
    }

    @Override
    public boolean addAll(Collection clctn) {
        return false;
    }

    @Override
    public boolean addAll(int i, Collection clctn) {
        return false;
    }

    @Override
    public boolean removeAll(Collection clctn) {
        return false;
    }

    @Override
    public boolean retainAll(Collection clctn) {
        return false;
    }

    @Override
    public void clear() {
        
    }

    @Override
    public Object set(int i, Object e) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
         return -1;
    }

    @Override
    public ListIterator listIterator() {
        return null;
    }

    @Override
    public ListIterator listIterator(int i) {
        return null;
    }

    @Override
    public List subList(int i, int i1) {
        return null;
    }

    @Override
    public void replaceAll(UnaryOperator uo) {
    }

    @Override
    public void sort(Comparator cmprtr) {
    }

    @Override
    public Spliterator spliterator() {
        return null;
    }

    @Override
    public boolean removeIf(Predicate prdct) {
        return false;
    }

    @Override
    public Stream stream() {
        return null;
    }

    @Override
    public Stream parallelStream() {
        return null;
    }

    @Override
    public void forEach(Consumer cnsmr) {
    }
    
    
    
    
    
    
}
