
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        try {
            RM rm = new RM();
            GUI gui = new GUI(rm);
            gui.setStepButtonEnabled(false);
            gui.setVisible(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String command = reader.readLine();
            while (!command.equalsIgnoreCase("x")) {
                try {
                    if (command.startsWith("load")) {
                        command = command.substring(5).trim();
                        try {
                            int i = rm.loadProgram(command);
                            System.out.println("Program loaded successfully with index " + i);
                        } catch (LoaderParseException ex) {
                            System.out.println("Loading failed. Could not parse specified file.");
                        } catch (OutOfMemoryException ex){
                            System.out.println("Loading failed. Not enough memory.");
                        } catch (MemoryException ex) {
                            System.out.println("Unexpected memory failure. Loading failed.");
                        }
                    } else if (command.startsWith("unload")) {
                        command = command.substring(7).trim();
                        try {
                            rm.unloadProgram(Integer.parseInt(command));
                            System.out.println("Program unloaded successfully.");
                        } catch (MemoryException ex) {
                            System.out.println("Program failed to unload.");
                        }
                    } else if (command.startsWith("run")) {
                        command = command.substring(3).trim();
                        if (rm.selectProgram(Integer.parseInt(command))) {
                            int stop = 0;
                            while (stop == 0) {
                                stop = rm.nextStep(); //TODO handle end of program
                                gui.updateRegisters();
                            }
                        } else {
                            System.out.println("Runing failed. Virtual machine with given index was not loaded.");
                        }
                    } else if (command.startsWith("stepping")) {
                        command = command.substring(8).trim();
                        if (rm.selectProgram(Integer.parseInt(command))) {
                            gui.setStepButtonEnabled(true);
                            synchronized (rm) {
                                try {
                                    rm.wait();
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            gui.setStepButtonEnabled(false);
                            System.out.println("\n");
                        } else {
                            System.out.println("Stepping failed. Virtual machine with given index was not loaded.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Failed parsing index.");
                }

                command = reader.readLine();
            }

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
