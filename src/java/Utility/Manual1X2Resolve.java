package Utility;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Database.DBManager;
import static Utility.TestClass.sdf;
import Utility.Utility;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import java.util.Base64; 
import java.util.Date;
import org.json.JSONArray;

/**
 *
 * @author jac
 */
@WebServlet(urlPatterns = {"/resolve1x2"})
public class Manual1X2Resolve extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    Connection conn;
    String response,username ,password,function,maindata;
    JSONObject jsonobj=null;JSONArray responseobj  = null;
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        resp.setContentType("text/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST");
      
        String matchID=req.getParameter("match_id").trim();
        String market=req.getParameter("market").trim();
        
        int dataStatus=checkBetEventData(matchID);
        if(dataStatus ==404)
        {
            int status=resolve1X2Games(matchID,market);
            if(status ==200)
            {
                response="======\n\n "+matchID+" 1x2 Market Saved Successfully \n\n===========================================================";
            }
            else
            {
                response="======\n\n "+matchID+" Failed To Save 1x2 Market \n\n===========================================================";
            }
        }
        else
        {
            response="======\n\n "+matchID+" 1x2 Market Already Exist In Bet_Event_Winers \n\n===========================================================";
        }
        

        PrintWriter out = resp.getWriter(); 
        out.print(response);
    }

    
    
    public int resolve1X2Games(String matchID,String winningTeam) 
    {
        String  todays_date=sdf.format(new Date());
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=500; 
        String query="";
        
        if(winningTeam.equalsIgnoreCase("1"))
        {
            query = "INSERT INTO `bet_event_winners` ( `Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                        "VALUES " +
                        "('"+matchID+"', 'draw', '"+todays_date+"', 1, 3, '1x2', '', '2', 0, '1.0', 0, 'Manual'),\n" +
                        "('"+matchID+"', 'Team2', '"+todays_date+"', 1, 3, '1x2', '', '1', 0, '1.0', 0, 'Manual'),\n" +
                        "('"+matchID+"', 'Team1', '"+todays_date+"', 1, 1, '1x2', '', '3', 0, '1.0', 0, 'Manual');";
        }
        else if(winningTeam.equalsIgnoreCase("x"))
        {
            query = "INSERT INTO `bet_event_winners` ( `Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                        "VALUES " +
                        "('"+matchID+"', 'draw', '"+todays_date+"', 1, 1, '1x2', '', '2', 0, '1.0', 0, 'Manual'),\n" +
                        "('"+matchID+"', 'Team2', '"+todays_date+"', 1, 3, '1x2', '', '1', 0, '1.0', 0, 'Manual'),\n" +
                        "('"+matchID+"', 'Team1', '"+todays_date+"', 1, 3, '1x2', '', '3', 0, '1.0', 0, 'Manual');";
        }
        else if(winningTeam.equalsIgnoreCase("2"))
        {
            query = "INSERT INTO `bet_event_winners` ( `Bet_Ev_Match_ID`, `Bet_Ev_Winning_Prediction`, `Bet_Ev_Timestamp`, `Bet_Ev_Market_ID`, `Bet_Ev_Type`, `Bet_Ev_Market_Name`, `Bet_Ev_Market_Specifier`, `Bet_Ev_Outcome_ID`, `Bet_Ev_Void_Factor`, `Bet_Ev_Dead_Beat_Void_Factor`, `Bet_Process_Status`, `Bet_Settlement_Mode`)\n" +
                        "VALUES " +
                        "('"+matchID+"', 'draw', '"+todays_date+"', 1, 3, '1x2', '', '2', 0, '1.0', 0, 'Manual'),\n" +
                        "('"+matchID+"', 'Team2', '"+todays_date+"', 1, 1, '1x2', '', '1', 0, '1.0', 0, 'Manual'),\n" +
                        "('"+matchID+"', 'Team1', '"+todays_date+"', 1, 3, '1x2', '', '3', 0, '1.0', 0, 'Manual');";
        }

        
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
    
    
    
    public int checkBetEventData(String matchID) 
    {
        String  todays_date=sdf.format(new Date());
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=404; int count=0;
        String query="";
        
        query = "select  count(Bet_Ev_ID) from bet_event_winners where Bet_Ev_Match_ID='"+matchID+"' order by Bet_Ev_Timestamp desc limit 3 ";

        
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

    
    
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
   @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
