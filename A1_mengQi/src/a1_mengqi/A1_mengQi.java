/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package a1_mengqi;

import a1_mengqi.gui.mainGUI;
import java.util.List;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import mengQidbllib.Sandy;

/**
 *
 * @author Meng
 */
public class A1_mengQi {
    private static Sandy sandy=null;
    /**
     * @param args the command line arguments
     */
     public static void main(String[] args) {
        // TODO code application logic here

        sandy = new Sandy("OrdersDB", "ism6236", "ism6236bo");
        List<String> list = sandy.readTable("Customer");
        for(int i = 0;i<list.size();i++){
            System.out.println(list.get(i));
        }
        mainGUI main = new mainGUI(); 
       main.setVisible(true);
        main.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }
    public static Sandy getSandy(){
        return sandy;
    }
    
}
