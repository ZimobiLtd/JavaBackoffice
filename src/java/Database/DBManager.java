package Database;


import java.sql.Connection;
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
        Connection connection=null;
        try 
        {
            Class.forName(DRIVER);
            Context ctx = new InitialContext();
            Context envCtx = (Context) ctx.lookup(LOOKUP);
            DataSource dataSource = (DataSource)envCtx.lookup("jdbc/ushindibet");
            connection=dataSource.getConnection();
        }
        catch (SQLException | ClassNotFoundException | NamingException ex) 
        {
            System.out.println("<<<ClassNotFoundException | NamingException>>> "+ex.getMessage());
        }
        
        return connection;
    }
    
      
}
