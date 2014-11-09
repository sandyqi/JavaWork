/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccdblb;
import java.sql.*;
import java.util.*;
/**
 *
 * @author seemab
 */
public class ccdb {
    private Connection mcn;

    public ccdb(String dbname, String uid, String pass) {
        try {
            setConnection(dbname, uid, pass);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public boolean isConnected() {
        return (mcn != null ? true : false);
    }

    public void setConnection(String dbname, String uid, String pass) throws ClassNotFoundException {
        try {
           //  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String connectionUrl = "jdbc:sqlserver://localhost;"
                    + "databaseName=" + dbname + ";user=" + uid + ";password=" + pass + ";";
            mcn = DriverManager.getConnection(connectionUrl);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<String> read(String sql) {

        List<String> retval = new LinkedList<String>();
        try {
            Statement s = mcn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            ResultSetMetaData rsmd = rs.getMetaData();
            int ncols = rsmd.getColumnCount();
            String header = "";
            for (int i = 1; i <= ncols; i++) {
                header += rsmd.getColumnName(i) + "   ";
            }
            retval.add(header);
            while (rs.next()) {
                String line = "";
                for (int i = 1; i <= ncols; i++) {
                    switch (rsmd.getColumnType(i)) {
                        case java.sql.Types.REAL:
                            line += rs.getFloat(i);
                            break; // Access
                        case java.sql.Types.FLOAT:
                            line += rs.getFloat(i);
                            break; //MSSQLSERVER
                        case java.sql.Types.DOUBLE:
                            line += rs.getDouble(i);
                            break;
                        case java.sql.Types.VARCHAR:
                            line += fixStringFromJDBC(rs.getString(i));
                            break; //Access
                        case java.sql.Types.CHAR:
                            line += fixStringFromJDBC(rs.getString(i));
                            break; //MSSQLSERVER
                        default: //if all fails try string
                            line += fixStringFromJDBC(rs.getString(i));
                        // System.out.println(rsmd.getColumnType(i));
                    }
                    line += "   ";
                }
                retval.add(line);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            return retval;
        }
        return retval;
    }

    public List<String> list(String accno) {
        List<String> rv = new ArrayList<String>();
        try {

            PreparedStatement st = mcn.prepareStatement(
                    "SELECT Amount, Appcode, CodeType FROM PurchaseRequests WHERE AccountNo=?;");
            st.setString(1, accno);

            ResultSet rs = st.executeQuery();
            String line;
            while (rs.next()) {
                float amt = rs.getFloat(1);
                int appcode = rs.getInt(2);
                String codetype = rs.getString(3);
                rv.add(String.format("%-15.2f %-4d %-4s", amt, appcode, codetype));
            }
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rv;
    }

    public int setLimit(String accno, String amount) {
        int n = 0;

        try {
            CallableStatement cst = mcn.prepareCall("{call SetLimit1(?,?,?)}");
            float amt = Float.parseFloat(amount);

            cst.setString(1, accno);
            cst.setFloat(2, amt);
            cst.setInt(3, n);
            cst.registerOutParameter(3, java.sql.Types.INTEGER);
            cst.executeUpdate();
            n = cst.getInt(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n;
    } //end setlimit
    
    public String charge(String accno, String amount) {
        String codetype = "";
        int appcode = -1;
        try {
            PreparedStatement pst = mcn.prepareStatement(
                    "SELECT Balance,Limit FROM Account WHERE AccountNo = ?");
            pst.setString(1, accno);
            ResultSet rs = pst.executeQuery();
            rs.next();
            float balance = rs.getFloat(1);
            float limit = rs.getFloat(2);
            float fAmt = Float.valueOf(amount.trim()).floatValue();

            String q = String.format("SELECT Max(AppCode) FROM PurchaseRequests WHERE AccountNo = '%s'", accno);
            Statement s = mcn.createStatement();
            rs = s.executeQuery(q);

            String[] sql;
            if (rs.next()) {
                appcode = rs.getInt(1) + 1;
            } else {
                appcode = 1;
            }
            s.close();
            pst.close();

            if (balance + fAmt <= limit) {
                codetype = "A";
                sql = new String[2];
                sql[0] = String.format("Insert Into PurchaseRequests(AccountNo,Amount,Appcode,CodeType) Values ('%s', %6.2f,%d, '%s');", accno, fAmt, appcode, codetype);
                sql[1] = String.format("Update Account Set Balance = Balance + %6.2f WHERE AccountNo = '%s';", fAmt, accno);

            } else {
                codetype = "R";
                sql = new String[1];
                sql[0] = String.format("Insert Into PurchaseRequests(AccountNo,Amount,Appcode,CodeType) Values ('%s', %6.2f,%d, '%s');", accno, fAmt, appcode, codetype);
            }

            transactSQL(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("%s%d", codetype, appcode);
    } // end charge

    public int update(String accno, String amount) {
        String[] q = new String[1];
        q[0] = String.format("Update Account Set Limit = %s where AccountNo = '%s'", amount, accno);
        int n = transactSQL(q);
        //System.out.println("Number of records updated = " + n);
        return n;

    }

    private int transactSQL(String[] sql) {
        Statement st = null;
        int n = 0;
        try {
            st = mcn.createStatement();
            mcn.setAutoCommit(false);
            for (int i = 0; i < sql.length; i++) {
                n = st.executeUpdate(sql[i]);
            }
            mcn.commit();
            //System.out.println(mcn.getAutoCommit());
            
        } catch (SQLException ex) {
            try {
                //rollback if there is an error
                mcn.rollback();
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mcn.setAutoCommit(true);
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return n;
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
