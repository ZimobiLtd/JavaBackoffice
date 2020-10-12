/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
@WebServlet(urlPatterns = {"/GamingSummary"})
public class Gaming_Summary extends HttpServlet {

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
String type="betting";JSONObject jsonobj=null;JSONArray responseobj  = null;
public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
throws ServletException, IOException {
    resp.setContentType("text/json;charset=UTF-8");
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Methods", "POST");


    StringBuffer jb = new StringBuffer();
    String line = null;

    try 
    {

    BufferedReader reader = req.getReader();
    while ((line = reader.readLine()) != null)
    {
        jb.append(line);
    }

        System.out.println("GamingSummary===="+jb.toString());
        jsonobj = new JSONObject(jb.toString());
        function=jsonobj.getString("function");
        maindata=jsonobj.getString("data");


    if(function.equals("getGamingSummary"))
    {
        responseobj=getGamingSummary();
    }
   
   
   
    }catch (Exception ex) { ex.getMessage();}

    PrintWriter out = resp.getWriter(); 
    out.print(responseobj);
    }
 


// most played 
public JSONArray getGamingSummary()
{

    JSONObject dataObj  = null;
    JSONArray dataArray = new JSONArray();

    try( Connection conn = new DBManager(type).getDBConnection();
    Statement stmt = conn.createStatement();)
    {

        ResultSet rs=null;
        
        String reg_today = "select count(id) from player where DATE(registration_date) = curdate() ";
        
        String reg_played_today = "select count(distinct(Play_Bet_Mobile)) from player inner join player_bets on msisdn = Play_Bet_Mobile where DATE(registration_date) = curdate() ";
        
        String playerstoday = "select count(distinct(Play_Bet_Mobile)) from player_bets where DATE(Play_Bet_Timestamp) = curdate() ";
        
        String turnovertoday = "select ifnull(sum(Play_Bet_Stake),0) from player_bets where DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status in (200,201,202,203) ";
        
        String turnoverweekly = "select ifnull(sum(Play_Bet_Stake),0) from player_bets where Play_Bet_Timestamp between  DATE_SUB(curdate(),INTERVAL 7 DAY) and curdate() "
                                + "and Play_Bet_Status in (200,201,202,203) ";
        
        String turnovermonthly = "select ifnull(sum(Play_Bet_Stake),0) from player_bets where Play_Bet_Timestamp between  DATE_SUB(curdate(),INTERVAL 30 DAY) and curdate() "
                                + "and Play_Bet_Status in (200,201,202,203)";
        
        String revenuetoday = "SELECT ifnull(sum(totals),0) FROM (SELECT SUM(Play_Bet_Stake) totals FROM player_bets where " +
                                "DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status in (200,201,202,203) union all " +
                                "SELECT SUM(-Play_Bet_Possible_Winning) totals FROM player_bets where " +
                                "DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status='202') a ";
        
        String revenueweekly = "SELECT ifnull(sum(totals),0) FROM (SELECT SUM(Play_Bet_Stake) totals FROM player_bets where " +
                                "DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status in (200,201,202,203) union all " +
                                "SELECT SUM(-Play_Bet_Possible_Winning) totals FROM player_bets where " +
                                "DATE(Play_Bet_Timestamp)  between  DATE_SUB(curdate(),INTERVAL 7 DAY) and curdate() and Play_Bet_Status='202') a ";        
          
        String revenuemonthly = "SELECT ifnull(sum(totals),0) FROM (SELECT SUM(Play_Bet_Stake) totals FROM player_bets where " +
                                "DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status in (200,201,202,203) union all " +
                                "SELECT SUM(-Play_Bet_Possible_Winning) totals FROM player_bets where " +
                                "DATE(Play_Bet_Timestamp)  between  DATE_SUB(curdate(),INTERVAL 30 DAY) and curdate() and Play_Bet_Status='202') a ";
                
        String betstoday = "select count(Play_Bet_ID) from player_bets where DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status = '201' ";
        
        String settledbetstoday = "select count(Play_Bet_ID) from player_bets where DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status in (202, 203) ";
        
        String wonbetstoday = "select count(Play_Bet_ID) from player_bets where DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status in (202) ";
        
        
        
        dataObj  = new JSONObject();

        int registrations_today =0;// getData(conn,stmt,rs,reg_today);
        int registered_played_today = getData(conn,stmt,rs,reg_played_today);
        int players_today = getData(conn,stmt,rs,playerstoday);
        int turnover_today = getData(conn,stmt,rs,turnovertoday);
        int turnover_weekly = getData(conn,stmt,rs,turnoverweekly);
        int turnover_monthly = getData(conn,stmt,rs,turnovermonthly);
        int revenue_today = getData(conn,stmt,rs,revenuetoday);
        int revenue_weekly = getData(conn,stmt,rs,revenueweekly);
        int revenue_monthly = getData(conn,stmt,rs,revenuemonthly);
        int bets_today = getData(conn,stmt,rs,betstoday);
        int settledbets_today = getData(conn,stmt,rs,settledbetstoday);
        int wonbets_today = getData(conn,stmt,rs,wonbetstoday);

        dataObj.put("RegistrationsToday", registrations_today);
        dataObj.put("Registered_Payed_Today", registered_played_today);
        dataObj.put("PlayersToday", players_today);
        dataObj.put("TurnOverToday", revenue_today);
        dataObj.put("TurnoverWeekly", revenue_weekly);
        dataObj.put("TurnoverMonthly", revenue_monthly);
        dataObj.put("BetStakeToday", turnover_today);
        dataObj.put("BetStakeWeekly", turnover_weekly);
        dataObj.put("BetStakeMonthly", turnover_monthly);
        dataObj.put("PendingBets", bets_today);
        dataObj.put("SettledBetsToday", settledbets_today);
        dataObj.put("WonBetsToday", wonbets_today);

        dataArray.put(dataObj);
      
    stmt.close();
    conn.close();
    }
    catch(Exception e)
    {

    }

return dataArray;
}




        
        
        
// return record count in a result set
public int getData( Connection conn,Statement stmt,ResultSet rs, String query) 
{
    int count = 0;
    
    try 
    {

        Statement stmtCount = (Statement) conn.createStatement();
        rs = (ResultSet) stmtCount.executeQuery(query);

        while (rs.next()) 
        {
            count = rs.getInt(1);
        }
        
    rs.close();
    } catch (Exception ex) {
        System.err.println("get data error .... " + ex.getMessage());
    }
    
    return count;
}        
        
        
        
public  ArrayList<String> loopdate(String fromDate, String toDate) 
{
    ArrayList<String> dates = new ArrayList<>();
    try 
    {

        Date startDate = sdf.parse(fromDate);
        Date endDate = sdf.parse(toDate);
        long interval = 24 * 1000 * 60 * 60; // 1 hour in millis

        long endTime = startDate.getTime(); // create your endtime here, possibly using Calendar or Date
        long curTime = endDate.getTime();
        while (endTime <= curTime) 
        {
System.out.println("==="+new Date(endTime));
dates.add(sdf.format(new Date(endTime)));
endTime += interval;
        }

    } catch (Exception ex) {

    }

return dates;
}




public String[] initDates() 
{
    String []data=null;

    try 
    {

        Date startDate = new Date();//sdf.parse(fromDate);
        long curTime = startDate.getTime();
        long interval = (24 * 1000 * 60 * 60)*5;
        curTime -= interval;
        String fromdate=sdf.format(new Date(curTime));
        String todate=sdf.format(startDate);

        data=new String[]{fromdate,todate};//fromdate+"#"+todate ;

    } catch (Exception ex) {

    }

return data;
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
