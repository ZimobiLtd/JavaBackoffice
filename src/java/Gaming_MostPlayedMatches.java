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
@WebServlet(urlPatterns = {"/GamingMostPlayed"})
public class Gaming_MostPlayedMatches extends HttpServlet {

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

        System.out.println("GamingMostPlayed===="+jb.toString());
        jsonobj = new JSONObject(jb.toString());
        function=jsonobj.getString("function");
        maindata=jsonobj.getString("data");


    if(function.equals("getMostPlayed"))
    {
        String []respo=initDates();
        String fromdate=respo[0];
        String todate=respo[1];
        System.out.println(fromdate+"==intidates=="+todate);
        responseobj=getMostPlayed("2017-09-30" ,"2020-10-31");
    }
                   
                   
    if(function.equals("filterMostPlayed"))
    {
        String[]data=maindata.split("#");
        String from=data[0];
        String to=data[1]; 
        responseobj=getMostPlayed(from ,to);
    }
                   
                   
    }catch (Exception ex) { ex.getMessage();}

    PrintWriter out = resp.getWriter(); 
    out.print(responseobj);
    }
    


// most played 
public JSONArray getMostPlayed(String dateFrom,String dateTo)
{

    String res="";
    String dataQuery = "",settledQuery="",placedQuery="";
    JSONObject dataObj  = null;
    JSONArray dataArray = new JSONArray();

    try( Connection conn = new DBManager(type).getDBConnection();
    Statement stmt = conn.createStatement();)
    {

        ResultSet rs=null;

        dataQuery = "select A.Mul_Match_ID, A.Mul_EventTime, A.Mul_Sportname, A.Mul_Tournament, A.Mul_Event,sum(A.Mul_Bet_Odd * B.Play_Bet_Stake),ifnull(A.Mul_Finished_Timestamp,'0') " +
                    "from multibets A,player_bets B where A.Mul_Bet_Status = '201'  and  B.Play_Bet_Group_ID = A.Mul_Group_ID and date(B.Play_Bet_Timestamp) between '" + dateFrom + "' and '" + dateTo + "' GROUP BY A.Mul_Match_ID  " +
                    "order by count(A.Mul_Match_ID) desc limit 20 ";
        
        
        rs = stmt.executeQuery(dataQuery);

        System.out.println("getMostPlayed==="+dataQuery);

        while (rs.next())
        {
            dataObj  = new JSONObject();
            
            String matchid = rs.getString(1);
            String eventdate = sdf.format(rs.getTimestamp(2));
            String sport = rs.getString(3);
            String tornament = rs.getString(4);
            String event = rs.getString(5);
            String totalstake = rs.getString(6);
            String betsettleddate = rs.getString(7);
            settledQuery = "select count(B.Mul_Match_ID) from player_bets A inner join multibets B on A.Play_Bet_Group_ID = B.Mul_Group_ID "
                         + "where B.Mul_Match_ID = '" + matchid + "' and B.Mul_Bet_Status in ('202','203')  ";
            String settledBets = String.valueOf(getCount(conn,stmt,rs,settledQuery));
            placedQuery = "select count(B.Mul_Match_ID) from player_bets A inner join multibets B on A.Play_Bet_Group_ID = B.Mul_Group_ID "
                        + "where B.Mul_Match_ID = '" + matchid + "' and B.Mul_Bet_Status = '201'  ";
            String placedBets = String.valueOf(getCount(conn,stmt,rs,placedQuery));

            dataObj.put("MatchID", matchid);
            dataObj.put("EventDate", eventdate);
            dataObj.put("BetsSettledDate", betsettleddate);
            dataObj.put("Sport", sport);
            dataObj.put("Tornament", tornament);
            dataObj.put("Event", event);
            dataObj.put("TotalStake", totalstake);
            dataObj.put("SettledBets", settledBets);
            dataObj.put("placedBets", placedBets);

            dataArray.put(dataObj);
        }
                   

    rs.close();
    stmt.close();
    conn.close();
    }
    catch(Exception e)
    {

    }

return dataArray;
}



// most played 
public JSONArray filterMostPlayed(String dateFrom,String dateTo)
{

    String res="";
    String dataQuery = "",settledQuery="",placedQuery="";
    JSONObject dataObj  = null;
    JSONArray dataArray = new JSONArray();

    try( Connection conn = new DBManager(type).getDBConnection();
    Statement stmt = conn.createStatement();)
    {

        ResultSet rs=null;

        dataQuery = "select A.Mul_Match_ID, A.Mul_EventTime, A.Mul_Sportname, A.Mul_Tournament, A.Mul_Event,sum(A.Mul_Bet_Odd * B.Play_Bet_Stake),ifnull(A.Mul_Finished_Timestamp,'0') " +
                    "from multibets A,player_bets B where A.Mul_Bet_Status = '201'  and  B.Play_Bet_Group_ID = A.Mul_Group_ID  and date(B.Play_Bet_Timestamp) between '" + dateFrom + "' and '" + dateTo + "'  GROUP BY A.Mul_Match_ID  " +
                    "order by count(A.Mul_Match_ID) desc";
        
        

        rs = stmt.executeQuery(dataQuery);

        System.out.println("filterMostPlayed==="+dataQuery);

        while (rs.next())
        {
            dataObj  = new JSONObject();
            
            String matchid = rs.getString(1);
            String eventdate = sdf.format(rs.getTimestamp(2));
            String sport = rs.getString(3);
            String tornament = rs.getString(4);
            String event = rs.getString(5);
            String totalstake = rs.getString(6);
            String betsettleddate = rs.getString(7);
            settledQuery = "select count(B.Mul_Match_ID) from player_bets A inner join multibets B on A.Play_Bet_Group_ID = B.Mul_Group_ID "
                         + "where B.Mul_Match_ID = '" + matchid + "' and B.Mul_Bet_Status in ('202','203')  ";
            String settledBets = String.valueOf(getCount(conn,stmt,rs,settledQuery));
            placedQuery = "select count(B.Mul_Match_ID) from player_bets A inner join multibets B on A.Play_Bet_Group_ID = B.Mul_Group_ID "
                        + "where B.Mul_Match_ID = '" + matchid + "' and B.Mul_Bet_Status = '201'  ";
            String placedBets = String.valueOf(getCount(conn,stmt,rs,placedQuery));

            dataObj.put("MatchID", matchid);
            dataObj.put("EventDate", eventdate);
            dataObj.put("BetsSettledDate", betsettleddate);
            dataObj.put("Sport", sport);
            dataObj.put("Tornament", tornament);
            dataObj.put("Event", event);
            dataObj.put("TotalStake", totalstake);
            dataObj.put("SettledBets", settledBets);
            dataObj.put("placedBets", placedBets);

            dataArray.put(dataObj);
        }
                   

    rs.close();
    stmt.close();
    conn.close();
    }
    catch(Exception e)
    {

    }

return dataArray;
}
        
        
        
// return record count in a result set
public int getCount( Connection conn,Statement stmt,ResultSet rs, String query) 
{
    int count = 0;
    
    try {

        Statement stmtCount = (Statement) conn.createStatement();
        ResultSet resultSetCount = (ResultSet) stmtCount.executeQuery(query);

        while (resultSetCount.next()) {
            count = resultSetCount.getInt(1);
        }
    } catch (Exception ex) {
        System.err.println("Fetch record count error .... " + ex.getMessage());
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
