/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author Lemmin
 */
public class CSVparser {
    public static ArrayList<Vector> readFile(File input) throws FileNotFoundException, Exception{
        // Open a Scanner
        Scanner inFile = new Scanner(input);
        inFile.useDelimiter("\n");

        // Treat the first line as a header and use it
        // to determine the number of columns
        int numColumns = 0;
        String currentLine = inFile.next();
        Scanner parser = new Scanner(currentLine);
        parser.useDelimiter(",");
        while(parser.hasNext()){
            parser.next();
            numColumns++;
        }
        // There should be at least two columns
        if(numColumns < 2){
            throw new Exception("The file should have at least two columns.");
        }
        // Now read the data
        double[] currentRow;
        ArrayList<Vector> vectors = new ArrayList<>();
        while(inFile.hasNext()){
            currentLine = inFile.next();
            parser = new Scanner(currentLine);

            parser.useDelimiter(",");
            try {
                    currentRow = new double[numColumns];
                    // Ignore data in rows with more entries than in the header
                    for(int i = 0; i < numColumns; i++){
                            currentRow[i] = Double.parseDouble(parser.next());
                    }
                    vectors.add(Vector.asVector(currentRow));
            }
            // Ensure the data are parsed to numeric
            catch(NumberFormatException nfe){
                    throw new NumberFormatException("The file should contain only numeric data.");
            }
            // Ensure a non-jagged array
            catch(NoSuchElementException nsee){
                    throw new NoSuchElementException(" Jagged arrays");
            }
        }

        // Ensure # rows >= # cols
        if(vectors.size() < numColumns){
            System.out.println(vectors.size()+" "+numColumns);
            throw new Exception("There must be at least as many data rows as columns in the file.");
                
        }
        return vectors;

    }
}
