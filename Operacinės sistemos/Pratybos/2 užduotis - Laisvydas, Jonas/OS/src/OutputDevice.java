
import javax.swing.JTextArea;


public class OutputDevice {
    
    private JTextArea monitor;
    
    public void putByte(byte b) {
        //monitor.append(String.valueOf((char)b));
        //monitor.setText(String.valueOf((char)b));
        System.out.print((char)b);
    }
    
    public void putString(String string) {
        //monitor.append(string+"\n");
        System.out.println(string);
    }

    void setMonitor(JTextArea monitor) {
        this.monitor = monitor;
    }
    
}
