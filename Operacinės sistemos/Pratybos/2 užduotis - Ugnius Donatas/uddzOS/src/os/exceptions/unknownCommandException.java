/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package os.exceptions;

/**
 *
 * @author Ugnius
 */
public class unknownCommandException extends Exception {
        String failedCommand;

    public unknownCommandException(String error) {
        failedCommand = error;
    }
        
}
