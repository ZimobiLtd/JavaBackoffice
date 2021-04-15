package Database;


import java.sql.Connection;
import java.sql.DriverManager;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jac
 */
public class DBManager {

    
      public DBManager(String type) {

               this.type=type; 
      }
          
        String type,data_source;
        String user ="zianahkev";
        String url1 = "jdbc:mysql://5.189.139.236:3306/supabet_inhouse";//192.168.0.88
        String user_password ="A6cMtfGAAKtmn5CW";
        String driver="com.mysql.jdbc.Driver";
    
      public Connection getDBConnection()
      {
       
            Connection connection = null;

            try {

                    Class.forName("com.mysql.jdbc.Driver");

                    if(type.equals("betting"))
                    {
                       data_source="jdbc/betting"; 
                       Context ctx = new InitialContext();
                       Context envCtx = (Context) ctx.lookup("java:comp/env");
                       DataSource ds = (DataSource)envCtx.lookup(data_source);
                       connection=ds.getConnection();
                        
                        //connection = DriverManager.getConnection(url1, user, user_password);

                    }

                    if(type.equals("0"))
                    {
                        connection = DriverManager.getConnection(url1,user,user_password); // fetch a connection
                    }

                    if (connection != null)
                    {		
                       //System.out.println("===Connection OK==="+type);   
                    }

                    }

                    catch (Exception e) 
                    {
                        e.printStackTrace();
                    } 
                
                
    return connection;
    }
    
//   public static void main(String[] args)
//    {
//        new DataBaseManager("kopakaro").getDBConnection();
//    }
   
          
          
}
