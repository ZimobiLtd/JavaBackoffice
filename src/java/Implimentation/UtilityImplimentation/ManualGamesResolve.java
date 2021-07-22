/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Implimentation.UtilityImplimentation;

import Database.DBManager;
import Utility.Utility;
import static Controllers.UtilityController.ManualResolveAPI.sdf;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
public class ManualGamesResolve {
    
    
    public ManualGamesResolve()
    {
        
    }
    
    public String getQuery(String matchID,String market)
    {
        String query="";
        String  time=sdf.format(new Date());
        
        if(market.equalsIgnoreCase("1"))
        {
            query = "INSERT INTO `bet_event_winners` ( `Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                        "VALUES " +
                        "('"+matchID+"', 'draw', '"+time+"', 1, 3, '1x2', '', '2', 0, '1.0', 0, 'Manual'),\n" +
                        "('"+matchID+"', 'Team2', '"+time+"', 1, 3, '1x2', '', '1', 0, '1.0', 0, 'Manual'),\n" +
                        "('"+matchID+"', 'Team1', '"+time+"', 1, 1, '1x2', '', '3', 0, '1.0', 0, 'Manual');";
        }
        else if(market.equalsIgnoreCase("x"))
        {
            query = "INSERT INTO `bet_event_winners` ( `Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                        "VALUES " +
                        "('"+matchID+"', 'draw', '"+time+"', 1, 1, '1x2', '', '2', 0, '1.0', 0, 'Manual'),\n" +
                        "('"+matchID+"', 'Team2', '"+time+"', 1, 3, '1x2', '', '1', 0, '1.0', 0, 'Manual'),\n" +
                        "('"+matchID+"', 'Team1', '"+time+"', 1, 3, '1x2', '', '3', 0, '1.0', 0, 'Manual');";
        }
        else if(market.equalsIgnoreCase("2"))
        {
            query = "INSERT INTO `bet_event_winners` ( `Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                        "VALUES " +
                        "('"+matchID+"', 'draw', '"+time+"', 1, 3, '1x2', '', '2', 0, '1.0', 0, 'Manual'),\n" +
                        "('"+matchID+"', 'Team2', '"+time+"', 1, 1, '1x2', '', '1', 0, '1.0', 0, 'Manual'),\n" +
                        "('"+matchID+"', 'Team1', '"+time+"', 1, 3, '1x2', '', '3', 0, '1.0', 0, 'Manual');";
        }
        else if(market.equalsIgnoreCase("gg"))
        {
            query = "INSERT INTO `bet_event_winners` (`Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`) " +
                    "VALUES" +
                    "('"+matchID+"', 'yes', '"+time+"', 29, 1, 'Both teams to score', '', '74', 0, '1.0', 0, 'Manual'),\n" +
                    "('"+matchID+"', 'no', '"+time+"', 29, 3, 'Both teams to score', '', '76', 0, '1.0', 0, 'Manual');";
        }
        else if(market.equalsIgnoreCase("ng"))
        {
            query = "INSERT INTO `bet_event_winners` (`Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`) " +
                    "VALUES" +
                    "('"+matchID+"', 'yes', '"+time+"', 29, 3, 'Both teams to score', '', '74', 0, '1.0', 0, 'Manual'),\n" +
                    "('"+matchID+"', 'no', '"+time+"', 29, 1, 'Both teams to score', '', '76', 0, '1.0', 0, 'Manual');";
        }
        else if(market.equalsIgnoreCase("o2.5"))
        {
            query = "INSERT INTO `bet_event_winners` (`Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                    "VALUES" +
                    "('"+matchID+"', 'over 2.5', '"+time+"', 18, 1, 'Total_2.5', '2.5', '12', 0, '1.0', 0, 'Manual'),\n" +
                    "('"+matchID+"', 'under 2.5', '"+time+"', 18, 3, 'Total_2.5', '2.5', '13', 0, '1.0', 0, 'Manual');";
        }
        else if(market.equalsIgnoreCase("u2.5"))
        {
            query = "INSERT INTO `bet_event_winners` (`Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                    "VALUES" +
                    "('"+matchID+"', 'over 2.5', '"+time+"', 18, 3, 'Total_2.5', '2.5', '12', 0, '1.0', 0, 'Manual'),\n" +
                    "('"+matchID+"', 'under 2.5', '"+time+"', 18, 1, 'Total_2.5', '2.5', '13', 0, '1.0', 0, 'Manual');";
        }
        else if(market.equalsIgnoreCase("x2"))
        {
            query = "INSERT INTO `bet_event_winners` (`Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                    "VALUES" +
                    "('"+matchID+"', 'draw or Team2', '"+time+"', 10, 1, 'Double chance', '', '11', 0, '1.0', 0, 'Manual'),\n" +
                    "('"+matchID+"', 'Team1 or draw', '"+time+"', 10, 3, 'Double chance', '', '9', 0, '1.0', 0, 'Manual'),\n" +
                    "('"+matchID+"', 'Team1 or Team2', '"+time+"', 10, 3, 'Double chance', '', '10', 0, '1.0', 0, 'Manual')";
        }
        else if(market.equalsIgnoreCase("x1"))
        {
            query = "INSERT INTO `bet_event_winners` (`Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                    "VALUES" +
                    "('"+matchID+"', 'draw or Team2', '"+time+"', 10, 3, 'Double chance', '', '11', 0, '1.0', 0, 'Manual'),\n" +
                    "('"+matchID+"', 'Team1 or draw', '"+time+"', 10, 1, 'Double chance', '', '9', 0, '1.0', 0, 'Manual'),\n" +
                    "('"+matchID+"', 'Team1 or Team2', '"+time+"', 10, 3, 'Double chance', '', '10', 0, '1.0', 0, 'Manual')";
        }
        else if(market.equalsIgnoreCase("12"))
        {
            query = "INSERT INTO `bet_event_winners` (`Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                    "VALUES" +
                    "('"+matchID+"', 'draw or Team2', '"+time+"', 10, 3, 'Double chance', '', '11', 0, '1.0', 0, 'Manual'),\n" +
                    "('"+matchID+"', 'Team1 or draw', '"+time+"', 10, 3, 'Double chance', '', '9', 0, '1.0', 0, 'Manual'),\n" +
                    "('"+matchID+"', 'Team1 or Team2', '"+time+"', 10, 1, 'Double chance', '', '10', 0, '1.0', 0, 'Manual')";
        }
        else if(market.equalsIgnoreCase("o1.5"))
        {
            query = "INSERT INTO `bet_event_winners` (`Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                    "VALUES" +
                    "('"+matchID+"', 'over 1.5', '"+time+"', 18, 1, 'Total_1.5', '1.5', '12', 0, '1.0', 0, 'Manual'),\n" +
                    "('"+matchID+"', 'under 1.5', '"+time+"', 18, 3, 'Total_1.5', '1.5', '13', 0, '1.0', 0, 'Manual');";
        }
        else if(market.equalsIgnoreCase("u1.5"))
        {
            query = "INSERT INTO `bet_event_winners` (`Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                    "VALUES" +
                    "('"+matchID+"', 'under 1.5', '"+time+"', 18, 1, 'Total_1.5', '1.5', '12', 0, '1.0', 0, 'Manual'),\n" +
                    "('"+matchID+"', 'over 1.5', '"+time+"', 18, 3, 'Total_1.5', '1.5', '13', 0, '1.0', 0, 'Manual');";
        }
        else if(market.equalsIgnoreCase("o3.5"))
        {
            query = "INSERT INTO `bet_event_winners` (`Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                    "VALUES" +
                    "('"+matchID+"', 'over 3.5', '"+time+"', 18, 1, 'Total_3.5', '3.5', '12', 0, '1.0', 0, 'Manual'),\n" +
                    "('"+matchID+"', 'under 3.5', '"+time+"', 18, 3, 'Total_3.5', '3.5', '13', 0, '1.0', 0, 'Manual');";
        }
        else if(market.equalsIgnoreCase("u3.5"))
        {
            query = "INSERT INTO `bet_event_winners` (`Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                    "VALUES" +
                    "('"+matchID+"', 'under 3.5', '"+time+"', 18, 1, 'Total_3.5', '3.5', '12', 0, '1.0', 0, 'Manual'),\n" +
                    "('"+matchID+"', 'over 3.5', '"+time+"', 18, 3, 'Total_3.5', '3.5', '13', 0, '1.0', 0, 'Manual');";
        }
        
        
      
        return query;
    }
    
    
    
    public int resolveGames(String matchID,String market) 
    {
        
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=500; 
        String query=new ManualGamesResolve().getQuery(matchID, market);

        java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());  
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        
        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
                status=200;
                System.out.println("==="+matchID+" saved===");
            }
            else
            {
                status=500;
                System.out.println("==="+matchID+" failed===");
            }

        }
        catch (SQLException ex) 
        {
            System.out.println("Error resolve1X2Games=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
        
    return status;    
    }
    
    
    
    public int checkBetEventData(String matchID,String market) 
    {
        String  todays_date=sdf.format(new Date());
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=404; int count=0;
        String query="",correctMarket="";
        if(market.equalsIgnoreCase("1"))
        {
            correctMarket="Team1";
        }
        else if(market.equalsIgnoreCase("x"))
        {
            correctMarket="draw";
        }
        else if(market.equalsIgnoreCase("2"))
        {
            correctMarket="Team2";
        }
        else if(market.equalsIgnoreCase("gg"))
        {
            correctMarket="yes";
        }
        else if(market.equalsIgnoreCase("ng"))
        {
            correctMarket="no";
        }
        else if(market.equalsIgnoreCase("o2.5"))
        {
            correctMarket="over 2.5";
        }
        else if(market.equalsIgnoreCase("u2.5"))
        {
            correctMarket="under 2.5";
        }
        else if(market.equalsIgnoreCase("x2"))
        {
            correctMarket="'draw or Team2";
        }
        else if(market.equalsIgnoreCase("x1"))
        {
            correctMarket="Team1 or draw";
        }
        else if(market.equalsIgnoreCase("12"))
        {
            correctMarket="Team1 or Team2";
        }
        
        query = "select  count(Bet_Ev_ID) from bet_event_winners where Bet_Ev_Match_ID='"+matchID+"' and Bet_Ev_Winning_Prediction in ('"+correctMarket+"') and Bet_Ev_Type=1";
        
        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) 
            {
                count = rs.getInt(1);
            }
            
            if(count > 0)
            {
                status=200;
                System.out.println("==="+matchID+" found in bet_event_winners===");
            }
            else
            {
                status=404;
                System.out.println("==="+matchID+" not found in bet_event_winners===");
            }

        }
        catch (SQLException ex) 
        {
            System.out.println("Error checkBetEventData=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
        
    return status;    
    }
    
    
    public int checkMatchID(String matchID) 
    {
        String  todays_date=sdf.format(new Date());
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=404; int count=0;
        String query="";
        
        query = "select count(Mul_ID) from multibets where Mul_Match_ID='"+matchID+"' limit 1 ";
        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) 
            {
                count = rs.getInt(1);
            }
            
            if(count > 0)
            {
                status=200;
                System.out.println("==="+matchID+" found===");
            }
            else
            {
                status=404;
                System.out.println("==="+matchID+" not found===");
            }

        }
        catch (SQLException ex) 
        {
            System.out.println("Error checkBetEventData=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
        
    return status;    
    }
    
}
