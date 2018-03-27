/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package os.iodevices;

import java.io.InputStream;
import java.util.Scanner;

/**
 *
 * @author Ugnius
 */
public class inputDevice {
    char[][] currentData;
    
    public inputDevice(){
        currentData = new char[10][4];
        currentData[0][0] = 'd';
        currentData[0][1] = 'a';
        currentData[0][2] = 'r';
        currentData[0][3] = ' ';
        currentData[1][0] = 'n';
        currentData[1][1] = 'e';
        currentData[1][2] = 'r';
        currentData[1][3] = 'a';
        currentData[2][0] = ' ';
        currentData[2][1] = 'd';
        currentData[2][2] = 'u';
        currentData[2][3] = 'o';
        currentData[3][0] = 'm';
        currentData[3][1] = 'e';
        currentData[3][2] = 'n';
        currentData[3][3] = 'u';
                
    }
    public char[][] getData(){
        Scanner in = new Scanner(System.in);
        System.out.println("INPUT");
        currentData = stringToDoubleCharArray(in.nextLine());
        return currentData;
    }
    
     private char[][] stringToDoubleCharArray(String charData) {
        return stringToDoubleCharArray(charData, 10, 4);
    }

    private char[][] stringToDoubleCharArray(String charData, int firstA, int secondA) {
        int counter = 0;
        int charDataLength = charData.length();
        char[][] data = new char[firstA][secondA];
        for (int i = charDataLength; i < 40; i++) {
            charData += " ";
        }
        for (int i = 0; i < firstA; i++) {
            charData.getChars(counter, counter + secondA, data[i], 0);
            counter += 4;
        }
//        for (int i = 0; i < firstA; i++) {
//            for (int n = 0; n < secondA; n++) {
//                charData.getChars(counter, counter+3, data[i], 0);
//                counter+=4;
//            }
//        }
        return data;
    }
}
