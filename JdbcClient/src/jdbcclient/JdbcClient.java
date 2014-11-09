/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdbcclient;


import ccdblb.ccdb;
import java.util.*;

/**
 *
 * @author seemab
 */
public class JdbcClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner cin = new Scanner(System.in);
        int count = 0;
        
        ccdb db = null;
        db = new ccdb("CreditCardAccounts", "ism6236", "ism6236bo");
       
        List<String> aclist = db.read("Select AccountNo, Balance, Limit, expirationDate, name from Account order by AccountNo");
        for (int i = 0; i < aclist.size(); i++) {
            System.out.println(aclist.get(i));
        }

        System.out.println();
        System.out.println("Enter U to update an account credit limit, T to verify a transaction, L to list transactions, Q to quit");
        System.out.flush();
        String input = cin.nextLine();
        boolean quit = false;

        while (!quit) {
            int c = input.charAt(0);
            switch (c) {
                case 'u':
                case 'U':
                    System.out.println("Enter Account No: ");
                    System.out.flush();
                    String accno = cin.nextLine();
                    System.out.println("Enter the new amount: ");
                    System.out.flush();
                    String amt = cin.nextLine();
                    //int n = db.update(accno,amt);
                    int n = db.setLimit(accno, amt);
                            
                    System.out.println(n + " records got updated");
                    break;
                case 'T':
                case 't':
                    System.out.println("Enter Account No: ");
                    System.out.flush();
                    accno = cin.nextLine();
                    System.out.println("Enter the amount: ");
                    System.out.flush();
                    amt = cin.nextLine();
                    String appcode = db.charge(accno, amt);

                    System.out.println("The approval code is " + appcode);

                    break;
                case 'L':
                case 'l':
                    System.out.println("Enter Account No: ");
                    System.out.flush();
                    accno = cin.nextLine();
                    List<String> l = (List<String>) db.list(accno);
                    for (int i = 0; i < l.size(); i++) {
                        System.out.println(l.get(i));
                    }

                    break;
                default:
                    quit = true;

            }

            if (!quit) {
                System.out.println("Enter U to update an account credit limit, T to verify a transaction, L to list transactions, Q to quit");
                System.out.flush();
                input = cin.nextLine();
            }

        }
    }
    
}
