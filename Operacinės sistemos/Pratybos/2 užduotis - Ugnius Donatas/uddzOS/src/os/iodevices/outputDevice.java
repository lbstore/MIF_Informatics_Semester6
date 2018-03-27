/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package os.iodevices;

/**
 *
 * @author Ugnius
 */
public class outputDevice {
    public void sendData(char[][] data){
        String s="";
        for (int i=0; i<data.length; i++){
            for (int n = 0; n <data[i].length; n++){
                s+=data[i][n];
            }
        }
        System.out.println("OUTPUT:" + s);
    }
}
