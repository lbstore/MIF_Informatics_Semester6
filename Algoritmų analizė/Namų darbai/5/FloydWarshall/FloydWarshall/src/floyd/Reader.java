package floyd;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Reader {
	private Scanner scanner;
	
	public Reader(String file) {
		try {
			scanner = new Scanner(new File(file));
		} catch (FileNotFoundException e) {
			System.out.println("Failas nerastas");
			System.exit(0);
		}
	}
	
	public String getLine() {
		try {
			return scanner.nextLine();
		}
		catch (NoSuchElementException exc) {
			return null;
		}
	}
	
	public void close() {
		scanner.close();
	}
}
