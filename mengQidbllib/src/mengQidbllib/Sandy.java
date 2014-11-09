/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mengQidbllib;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Meng
 */
public class Sandy {
    private Connection con;
    public Sandy(String dbName, String userName, String password){
        try{
        setConnection(dbName,userName, password);
        System.out.println("Connected to database successfully");
        }catch(Exception e){
        e.printStackTrace();
        return;   //why return?
        }
    }
    public void setConnection(String dbName, String userName, String password){
        String connectionUrl = "jdbc:sqlserver://localhost;"
                    + "databaseName=" + dbName + ";user=" + userName + ";password=" + password + ";";
        try {
            con = DriverManager.getConnection(connectionUrl);
        } catch (SQLException ex) {
            Logger.getLogger(Sandy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<String> readTable(String tableName){
       List<String> content = new LinkedList<>(); 
       String sql = "SELECT * FROM "+tableName;
       String header="";
       Statement s;
        try {
            s = con.createStatement();
            ResultSet rs = s.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            //first element is the header
            for(int i=1;i<=count;i++){
                header += rsmd.getColumnName(i)+"   ";
            }
            content.add(header);
            //get each row of the table
            while(rs.next()){
                String c = "";
                for(int i=1;i<=count;i++){
                    switch (rsmd.getColumnType(i)) {
                        case java.sql.Types.REAL:
                            c += rs.getFloat(i)+"   ";
                            break; // Access
                        case java.sql.Types.FLOAT:
                            c += rs.getFloat(i)+"   ";
                            break; //MSSQLSERVER
                        case java.sql.Types.DOUBLE:
                            c += rs.getDouble(i)+"   ";
                            break;
                        case java.sql.Types.VARCHAR:
                            c += fixStringFromJDBC(rs.getString(i))+"   ";
                            break; //Access
                        case java.sql.Types.CHAR:
                            c += fixStringFromJDBC(rs.getString(i))+"   ";
                            break; //MSSQLSERVER
                        default: //if all fails try string
                            c += fixStringFromJDBC(rs.getString(i))+"   ";
                        // System.out.println(rsmd.getColumnType(i));
                    }
                }
                content.add(c);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Sandy.class.getName()).log(Level.SEVERE, null, ex);
            return content;
        }
       return content;
    }
    public List<String> listPurchases(String cid){
    List<String> purchases = new LinkedList<>();
    String header = "";
    String sql = "SELECT Orders.OrderDate,Product.Pid, Product.Price, Customer.Name, LineItem.Quantity from Customer, Product, Orders, LineItem " +
"where Customer.Cid =?  and Customer.Cid = Orders.Cid\n" +
"and Orders.Oid = LineItem.Oid\n" +
"and LineItem.Pid = Product.Pid;";
        try {
            PreparedStatement ps = con.prepareCall(sql);
            ps.setInt(1, Integer.parseInt(cid));
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            //get header
            for(int i=1;i<=count;i++){
                header+=rsmd.getColumnName(i)+"   ";
            }
            purchases.add(header);
            //get each line
            while(rs.next()){
                String c="";
                  for(int i=1;i<=count;i++){
                    switch (rsmd.getColumnType(i)) {
                        case java.sql.Types.REAL:
                            c += rs.getFloat(i)+"    ";
                            break; // Access
                        case java.sql.Types.FLOAT:
                            c += rs.getFloat(i)+"    ";
                            break; //MSSQLSERVER
                        case java.sql.Types.DOUBLE:
                            c += rs.getDouble(i)+"    ";
                            break;
                        case java.sql.Types.VARCHAR:
                            c += fixStringFromJDBC(rs.getString(i))+"    ";
                            break; //Access
                        case java.sql.Types.CHAR:
                            c += fixStringFromJDBC(rs.getString(i))+"    ";
                            break; //MSSQLSERVER
                        default: //if all fails try string
                            c += fixStringFromJDBC(rs.getString(i))+"    ";
                        // System.out.println(rsmd.getColumnType(i));
                    }
                }
                purchases.add(c);
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Sandy.class.getName()).log(Level.SEVERE, null, ex);
        }
    return purchases;
    }
    /*
    public String listProduct(int productIndex){
        String pInfo="";
        String sql = "select * from Product " +
"where Product.Pid = ?";
        try {
            Statement statement = con.createStatement();
            statement.executeQuery(sql);
        } catch (SQLException ex) {
            Logger.getLogger(Sandy.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return pInfo;
    }
    */
    public void placeOrder(String customerId, String[] productIds, int[] quantities){
        String sql = "select * from Orders";
        String sql1 = "insert into Orders(Oid, OrderDate, Cid) values(?, ?, ?);";
        String sql2 = "insert into LineItem(Oid, Pid, Quantity) values(?, ?, ?);";
        int orderNum=1;
     
        try {
           // Get former information of table Orders
           Statement statement = con.createStatement();
           ResultSet rs1 = statement.executeQuery(sql);
           //Get former Order number
           //what if there is no order exist
           while(rs1.next()){
               orderNum++;
           };
     
           //Get current date
           //DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
           java.util.Date date = new java.util.Date();
           int year = date.getYear();
           int month = date.getMonth();
           int day = date.getDay();
           java.sql.Date orderDate = new java.sql.Date(year, month, day);
           PreparedStatement ps1 = con.prepareCall(sql1);
           //create a new order
           ps1.setInt(1, orderNum);
           ps1.setDate(2, orderDate);
           ps1.setInt(3, Integer.parseInt(customerId));
           ps1.executeUpdate();
           System.out.println("An order is created \n"+ orderNum+"   "+orderDate+"   "+customerId);
           ps1.close();
           rs1.close();
        } catch (SQLException ex) {
            System.out.println("Exception in creating a new order");
            Logger.getLogger(Sandy.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try{
            PreparedStatement ps2 = con.prepareCall(sql2);
            //create each line in LineItem
            for(int i =0;i<productIds.length;i++){
                ps2.setInt(1, orderNum);
                ps2.setInt(2, Integer.parseInt(productIds[i]));
                ps2.setInt(3, quantities[i]);
                ps2.executeUpdate();
                System.out.println("A LineItem is created \n"+ orderNum+"   "+productIds[i]+"   "+quantities[i]);
            }
            ps2.close();
        }catch(SQLException ex){
            System.out.println("Exception in creating LineItem" );
            ex.printStackTrace();
        }
    }
     private String fixStringFromJDBC(String s) {
        if (s == null) {
            s = "";
        }
        if (s.equals(" ")) {
            s = "";
        }
        s = s.replace('`', '\'');
        return s;
    }
}
