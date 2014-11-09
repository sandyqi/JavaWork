/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jdbc01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Meng
 */
public class JDBC01 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
  
            // TODO code application logic here
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("Driver loaded");
            Connection connection = DriverManager.getConnection
            ("jdbc:sqlserver://localhost;databaseName=CreditCardAccounts;user=ism6236;password=ism6236bo;");    
            System.out.println("Database connected");
            String sql = "Select AccountNo, Balance, Limit, expirationDate, name from Account order by name";
            Statement s = connection.createStatement();
            System.out.println(); 
            ResultSet rs = s.executeQuery(sql);
            printAll(rs);
            System.out.println("Execute inserting");
         PreparedStatement pst = connection.prepareStatement(
"Insert Into PurchaseRequests(AccountNo,Amount,Appcode,CodeType) Values (?, ?, ?, ?);");
pst.setString(1, "0920");
pst.setFloat(2, 100);
pst.setInt(3, 3);
pst.setString(4, "A");
int n = pst.executeUpdate(); 
//fixStringFromJDBC("DD");
           
            
    }
    public static void printAll(ResultSet rs) throws SQLException{
             ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            System.out.println("Show the information of the table");
           for(int i =1;i<=columnCount;i++){
           System.out.println(i+"\t"+rsmd.getTableName(i)+"\t"+rsmd.getColumnName(i)+"\t"+rsmd.getColumnTypeName(i));
           }
           System.out.println("Show the content of the table");
            while(rs.next()){
            System.out.println(rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"
                    +rs.getString(4)+"\t"+rs.getString(5)+"\t"+rs.getRow());
            }
    }
    
}
