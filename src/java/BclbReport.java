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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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
@WebServlet(urlPatterns = {"/BCLPReport"})
public class BclbReport extends HttpServlet {

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
                   
                   System.out.println("BCLBReport===="+jb.toString());
                   jsonobj = new JSONObject(jb.toString());
                   function=jsonobj.getString("function");
                   maindata=jsonobj.getString("data");
                   
                   
                   if(function.equals("getBCLBReport"))
                   {
                       String []respo=initDates();
                       String fromdate=respo[0];
                       String todate=respo[1];
                       responseobj=getBCLBReport(fromdate ,todate);
                   }
                   
                   
                   if(function.equals("filterBCLBReport"))
                   {
                       String[]data=maindata.split("#");
                       String from=data[0];
                       String to=data[1];    
                       
                       responseobj=getBCLBReport(from ,to);
                   }
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        
        
      
      
        public JSONArray getBCLBReport(String datefrom,String dateto)
        {
                  
            String res="";
            String dataQuery = "";
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs=null;
                for (String date : loopdate(datefrom ,dateto)) 
                {
                    dataObj  = new JSONObject();
                    
                    dataQuery = " select ifnull(sum(Play_Bet_Stake),0) as 'betstakes' ,count(Play_Bet_ID) ,"
                                + "(select ifnull(sum(Play_Bet_Stake),0) from player_bets where DATE(Play_Bet_Timestamp) =  '"+date+"'  and Play_Bet_Status  in (202)) as 'payouts' " +
                                "from player_bets where DATE(Play_Bet_Timestamp) =  '"+date+"'  and Play_Bet_Status in (202,203)  ";
                    System.out.println("getBCLBReport==="+dataQuery);
                    
                    rs = stmt.executeQuery(dataQuery);
                    while (rs.next())
                    {
                        String rptdate = date;
                        String totalsales = rs.getString(1);
                        String tickets = rs.getString(2);
                        String payouts = rs.getString(3);
                        
                        
                        String wins = String .valueOf(Integer.valueOf(totalsales)-Integer.valueOf(payouts));
                        double betting_tax=0.15*Double.valueOf(Integer.valueOf(totalsales)-Integer.valueOf(payouts));
                        String bettingTax = String.format("%.2f", betting_tax);
                        
                        dataObj.put("Report_Date", rptdate);
                        dataObj.put("Tickets", tickets);
                        dataObj.put("TotalSales", totalsales);
                        dataObj.put("NetSales", totalsales);
                        dataObj.put("Win_Loss", wins);
                        dataObj.put("BettingTax", bettingTax);
                        //dataObj.put("WitholdingTax", "0.00");
                    }
                    double []taxData=getTaxData(conn,stmt,date);
                     String totalPayout = String.format("%.2f", taxData[0]);
                    String withholdingtax = String.format("%.2f", taxData[1]);
                    dataObj.put("Payout", totalPayout);
                    dataObj.put("WitholdingTax", withholdingtax);
                    
                    dataArray.put(dataObj);
                }
                
                
                   
                  
                //System.out.println("\n\n\n"+dataArray);
                   

            rs.close();
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
                    
        return dataArray;
        }
        
        
        
        public double[] getTaxData(Connection conn,Statement stmt,String date)
        {
            String dataQuery = "";double [] respo=null;
            double betStake=0.0,possibleWinning=0.0,taxedpossibleWinning=0.0,withholdingTax=0.0;
            
            try
            {

                ResultSet rs=null;
                    dataQuery = " select  Play_Bet_Stake,Play_Bet_Possible_Winning,Play_Bet_Possible_Winning - (Play_Bet_Possible_Winning - Play_Bet_Stake)*0.2, sum((Play_Bet_Possible_Winning - Play_Bet_Stake)*0.2) "
                            + " from player_bets where DATE(Play_Bet_Timestamp) =  '"+date+"'  and Play_Bet_Status  in (202)  group by Play_Bet_ID";
                    System.out.println("getBCLBReport==="+dataQuery);
                    
                    rs = stmt.executeQuery(dataQuery);
                    while (rs.next())
                    {
                        betStake += Double.valueOf( rs.getString(1));
                        possibleWinning += Double.valueOf( rs.getString(2));
                        taxedpossibleWinning += Double.valueOf( rs.getString(3));
                        withholdingTax += Double.valueOf( rs.getString(4));
                    }
                    double finPossibleWinning=possibleWinning-withholdingTax;
                    respo=new double[] {finPossibleWinning,withholdingTax};
                    
            rs.close();
            }
            catch(Exception ex)
            {
                System.out.println("Error getTaxData==="+ex.getMessage());
            }
                    
        return respo;
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
                while (curTime >= endTime) 
                {
                    //System.out.println("==="+new Date(endTime));
                    dates.add(sdf.format(new Date(curTime)));
                    curTime -= interval;
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

            String todate=LocalDate.now().toString();

            String fromdate=LocalDate.now().plusDays(-6).toString();

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
