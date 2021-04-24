package Gaming;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Database.DBManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import java.util.Base64; 
import java.util.Calendar;
import java.util.Date;
import org.json.JSONArray;

/**
 *
 * @author jac
 */
@WebServlet(urlPatterns = {"/GamingProfitLost"})
public class Gaming_ProfitLost extends HttpServlet {

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

        System.out.println("GamingProfitLost===="+jb.toString());
        jsonobj = new JSONObject(jb.toString());
        function=jsonobj.getString("function");
        maindata=jsonobj.getString("data");


    if(function.equals("getProfitLost"))
    {
        responseobj=getProfitLost("2017-12-30");
    }
   
   
    if(function.equals("filterProfitLost"))
    {
        String date=maindata;

        responseobj=getProfitLost(date);
    }
   
   
    }catch (Exception ex) { ex.getMessage();}

    PrintWriter out = resp.getWriter(); 
    out.print(responseobj);
    }
    
    
        
        
      
      
public JSONArray getProfitLost(String toDate)
{

    String[] months = new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
    String res="";
    String dataQueryTurnover = "",dataQuerySettledBetsTurn="",dataQueryWinnings="";
    JSONObject dataObj  = null;
    JSONArray dataArray = new JSONArray();

    try( Connection conn = new DBManager(type).getDBConnection();
    Statement stmt = conn.createStatement();)
    {

        ResultSet rs=null;

        ArrayList<String> dates= loopdate(toDate);
        for (int i=0;i<dates.size();i++) 
        {
            String date=dates.get(i);
            //convert String to LocalDate
            LocalDate localDate = LocalDate.parse(date);
            Month month = localDate.getMonth();

            System.out.println(month+"==="+date);
            dataQuerySettledBetsTurn = "select ifnull(sum(Play_Bet_Stake),0) from player_bets where Play_Bet_Status in(201,202) "
                + " and date(Play_Bet_Timestamp) between '"+subDate(date) +"' and '" + date + "' and  Play_Bet_Settle_Bet_Time is not null  ";

            dataQueryWinnings = "select ifnull(sum(Play_Bet_Possible_Winning),0) from player_bets where Play_Bet_Status = 202 "
                + " and  date(Play_Bet_Timestamp) between '"+subDate(date) +"' and '" + date + "' and  Play_Bet_Settle_Bet_Time is not null ";
            
            System.out.println("dataQuerySettledBetsTurn==="+dataQuerySettledBetsTurn);
            System.out.println("dataQueryWinnings==="+dataQueryWinnings);
            
            int settledBets=getCount( conn,stmt,rs,dataQuerySettledBetsTurn) ;
            int winnings=getCount( conn,stmt,rs,dataQueryWinnings) ;
            int profit=settledBets-winnings;
            
            dataObj  = new JSONObject();
            dataObj.put(String.valueOf(month), profit+"#"+winnings);
            dataArray.put(dataObj);
        }


    //rs.close();
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
        rs = (ResultSet) stmtCount.executeQuery(query);

        while (rs.next()) {
            count = rs.getInt(1);
        }
    } catch (Exception ex) {
        System.err.println("Fetch record count error .... " + ex.getMessage());
    }
    return count;
} 

        


public ArrayList<String> loopdate(String toDate)
{

    int count=1;int value=0;String newToDate="",newdate="";
    if(newToDate.equals(""))
    {
        newToDate= toDate;
    }
    else
    {
        newToDate =newdate;
    }
    
    
    ArrayList<String> dates = new ArrayList<>();
    try
    {
        
        Date todays_date = sdf.parse(newToDate);
        // convert date to calendar
        Calendar c = Calendar.getInstance();
        c.setTime(todays_date);
        // manipulate date
        while (count <= 12)
        {
    
            if(count==1)
            {
                value=0;
            }
            else
            {
                value=-1;
            }
            c.add(Calendar.MONTH, value);                        
            // convert calendar to date
            Date dt = c.getTime();
            newdate= sdf.format(dt);
            newToDate=newdate;
            
            dates.add(newdate);
            count++;
        }
        count=0;

    }
    catch (Exception ex)
    {

      ex.printStackTrace();
    }

return dates;
}
        
   

public String subDate(String date)
{

    String newdate="";
    ArrayList<String> dates = new ArrayList<>();
    try
    {
        
        Date todays_date = sdf.parse(date);
        // convert date to calendar
        Calendar c = Calendar.getInstance();
        c.setTime(todays_date);
        // manipulate date
            c.add(Calendar.MONTH, -1);                        
            // convert calendar to date
            Date dt = c.getTime();
            newdate= sdf.format(dt);

    }
    catch (Exception ex)
    {

      ex.printStackTrace();
    }

return newdate;
}

 
public String subDates(String date) 
{
    String data=null;

    try 
    {

        Date startDate = sdf.parse(date);
        long curTime = startDate.getTime();
        long interval = (24 * 1000 * 60 * 60)*30;
        curTime -= interval;
        String findate=sdf.format(new Date(curTime));

        data=findate;

    } catch (Exception ex) {

    }

return data;
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
