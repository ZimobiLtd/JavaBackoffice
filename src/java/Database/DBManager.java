package Database;


import Utility.LoggerClass;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
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
    private static DBManager dbManager=new DBManager();
    private static final String DRIVER="com.mysql.cj.jdbc.Driver";
    private static final String LOOKUP="java:comp/env";
    
    private DBManager() 
    {
    }
    
    
    public static DBManager getInstance()
    {
        if (dbManager == null)
        {
            dbManager = new DBManager();
        }

        return dbManager;
    }
    
    
        
    public Connection getDBConnection(String type)
    {
        String datasource;
        if(type.equals("read"))
        {
            datasource="jdbc/starbet_read"; 
        }
        else if (type.equals("write"))
        {
            datasource="jdbc/starbet_write";
        }
        else
        {
            datasource="jdbc/startbet";  
        }
        
        DataSource dataSource = null;
        Connection connection=null;
        try 
        {
            Class.forName(DRIVER);
            Context ctx = new InitialContext();
            Context envCtx = (Context) ctx.lookup(LOOKUP);
            dataSource = (DataSource)envCtx.lookup(datasource);
            connection=dataSource.getConnection();
        }
        catch (SQLException | ClassNotFoundException | NamingException ex) 
        {
            LoggerClass.buildLog(Thread.currentThread().getStackTrace()[1],Level.SEVERE, "<<<ClassNotFoundException | NamingException>>> "+ex.getMessage());
        }
        
        return connection;
    }
    
      
}
