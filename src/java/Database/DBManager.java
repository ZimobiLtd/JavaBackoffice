package Database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
public class DBManager 
{
    String type,data_source;
    String user ="mysqld_user";
    String url1 = "jdbc:mysql://62.171.191.3:3306/starbet?useSSL=false";//192.168.0.88
    String user_password ="+q4LY9.F:29:3b(q";
    String driver="com.mysql.cj.jdbc.Driver";
    
    public DBManager() 
    {
        this.type="betting"; 
    }
        
    public Connection getDBConnection()
    {
        Connection connection = null;

        try 
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
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
               System.out.println("===Connection OK==="+type);   
            }

        }
        catch (ClassNotFoundException | SQLException | NamingException ex) 
        {
            System.out.println("Error getDBConnection==="+ex.getMessage());
        } 
                
    return connection;
    }
      
}
