/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package helloejbclient;

import helloejb1.HelloRemote;
import javax.naming.InitialContext;

/**
 *
 * @author Meng
 */
public class HelloEJBClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       //find the EJB
        HelloRemote hr;
        try{
            InitialContext ic = new InitialContext();
            hr =(HelloRemote)ic.lookup("java:global/HelloEJB1/Hello!helloejb1.HelloRemote");
            System.out.println(hr.sayHello("SANDY"));
        }catch(Exception e){
            e.printStackTrace();
    }
    
}
}