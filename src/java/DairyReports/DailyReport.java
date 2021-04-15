package DairyReports;

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
@WebServlet(urlPatterns = {"/DailyReport"})
public class DailyReport extends HttpServlet {

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
                   
                   System.out.println("DailyReport===="+jb.toString());
                   jsonobj = new JSONObject(jb.toString());
                   function=jsonobj.getString("function");
                   maindata=jsonobj.getString("data");
                   
                   
                   if(function.equals("getDailyReport"))
                   {
                       String []respo=initDates();
                       String fromdate=respo[0];
                       String todate=respo[1];
                       System.out.println(fromdate+"==intidates=="+todate);
                       responseobj=getDailyReport(fromdate ,todate);
                   }
                   
                   
                   if(function.equals("filterDailyReport"))
                   {
                       String[]data=maindata.split("#");
                       String from=data[0];
                       String to=data[1];    
                       
                       responseobj=getDailyReport(from ,to);
                   }
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        
        
      
      
        public JSONArray getDailyReport(String datefrom,String dateto)
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
                    System.out.println("date====="+date);
                    dataQuery = "select ifnull(sum(Acc_Amount),0) as 'deposits', " +
                    "(select ifnull(sum(Acc_Amount),0) FROM user_accounts WHERE Acc_Trans_Type =2 and DATE(Acc_Date) = '"+date+"') as'withdrawals' ," +
                    "(select  ifnull(sum(Play_Bet_Stake),0) as 'betstakes' from player_bets where DATE(Play_Bet_Timestamp)= '"+date+"' and Play_Bet_Status in (201,202,203)) as 'bet stake'," +
                    "(select  ifnull(sum(Play_Bet_Bonus_Stake),0) from player_bets where DATE(Play_Bet_Timestamp)= '"+date+"' and Play_Bet_Status in (201,202,203)) as 'bet bonus stake'," +
                    "(select ifnull(sum(Acc_Bonus_Amount),0) from user_accounts where Acc_Trans_Type=7 and DATE(Acc_Date) ='"+date+"' )as 'acc bonus amount'," +
                    "(select count(Play_Bet_ID) from player_bets where DATE(Play_Bet_Timestamp)= '"+date+"' and Play_Bet_Status  in (201,202,203)) as 'player bets'," +
                    "(select ifnull(SUM(total),0) from (select sum(Play_Bet_Stake) as 'total' from player_bets where Play_Bet_Status  in (202, 203) and DATE(Play_Bet_Timestamp) = '"+date+"' " +
                    "union ALL select -(ifnull(sum(Play_Bet_Possible_Winning),0)-(ifnull(sum(Play_Bet_Possible_Winning),0)*0.2)) as 'total' from player_bets where Play_Bet_Status = '202' and DATE(Play_Bet_Timestamp) = '"+date+"') as mytable)as 'GGR', " +
                    "(select count(id)  from player where DATE(registration_date) =  '"+date+"' ) as 'registered players'," +
                    "(select count(distinct(Play_Bet_Mobile))  from player_bets where DATE(Play_Bet_Timestamp) = '" + date + "') as 'players',"+
                    "(select count(distinct(Acc_Mobile)) from user_accounts where DATE(Acc_Date) =  '"+date+"' and Acc_Trans_Type = 1 ) as 'first deposit players'," +
                    "(select ifnull(sum(Acc_Amount),0) from user_accounts where DATE(Acc_Date) =  '"+date+"' and Acc_Trans_Type = 1 )as 'first deposit amount'" +
                    "FROM user_accounts WHERE Acc_Trans_Type =1 and DATE(Acc_Date) = '"+date+"' ";
                    System.out.println("getDailyReport==="+dataQuery);
                    
                    rs = stmt.executeQuery(dataQuery);
                    
                    
                    
                    while (rs.next())
                    {
                        String rptdate = date;
                        String deposits = rs.getString(1);
                        String withdrawals = rs.getString(2);
                        String betstake = rs.getString(3);
                        String betsbonusstake = rs.getString(4);
                        String accbonusamnt = rs.getString(5);
                        String playerbets = rs.getString(6);
                        String ngr = rs.getString(7);
                        String registeredplayers = rs.getString(8);
                        String players = rs.getString(9);
                        String firstdepositplayers = rs.getString(10);
                        String firstdepositplayersamnt = rs.getString(11);
                        
                        dataObj.put("Report_Date", rptdate);
                        dataObj.put("Deposits", deposits);
                        dataObj.put("Withdrawals", withdrawals);
                        dataObj.put("BetStake", betstake);
                        dataObj.put("BetsBonusStake", betsbonusstake);
                        dataObj.put("AccBonusAmount", accbonusamnt);
                        dataObj.put("Bets", playerbets);
                        dataObj.put("GGR", betstake);
                        dataObj.put("NGR", String.format("%.2f", Double.parseDouble(ngr)));
                        dataObj.put("RegisteredPlayers", registeredplayers);
                        dataObj.put("Players", players);
                        dataObj.put("FirstDepositPlayers", firstdepositplayers);
                        dataObj.put("FirstDepositAmount", firstdepositplayersamnt);
                    }
                    
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
        
        
        
        
        
        
        public JSONArray filterDailyReport(String datefrom,String dateto)
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
                    
                    dataQuery = "select ifnull(sum(Acc_Amount),0) as 'deposits', " +
                    "(select ifnull(sum(Acc_Amount),0) FROM user_accounts WHERE Acc_Trans_Type =2 and DATE(Acc_Date) = '"+date+"') as'withdrawals' ," +
                    "(select  ifnull(sum(Play_Bet_Stake),0) as 'betstakes' from player_bets where DATE(Play_Bet_Timestamp)= '"+date+"' and Play_Bet_Status in (201,202,203)) as 'bet stake'," +
                    "(select  '0' from player_bets where DATE(Play_Bet_Timestamp)= '"+date+"' and Play_Bet_Status in (201,202,203)) as 'bet bonus stake'," +
                    "(select ifnull(sum(Acc_Bonus_Amount),0) from user_accounts where Acc_Trans_Type=7 and DATE(Acc_Date) ='"+date+"' )as 'acc bonus amount'," +
                    "(select count(Play_Bet_ID) from player_bets where DATE(Play_Bet_Timestamp)= '"+date+"' and Play_Bet_Status  in (201,202,203)) as 'player bets'," +
                    "(select ifnull(SUM(total),0) from (select sum(Play_Bet_Stake) as 'total' from player_bets where Play_Bet_Status  in (202, 203) and DATE(Play_Bet_Timestamp) = '"+date+"' " +
                    "union ALL select -(ifnull(sum(Play_Bet_Possible_Winning),0)-(ifnull(sum(Play_Bet_Possible_Winning),0)*0.2)) as 'total' from player_bets where Play_Bet_Status = '202' and DATE(Play_Bet_Timestamp) = '"+date+"') as mytable)as 'GGR', " +
                    "(select count(id)  from player where DATE(registration_date) =  '"+date+"' ) as 'registered players'," +
                    "(select count(distinct(Acc_Mobile)) from user_accounts where DATE(Acc_Date) =  '"+date+"' and Acc_Trans_Type = 1 ) as 'first deposit players'," +
                    "(select ifnull(sum(Acc_Amount),0) from user_accounts where DATE(Acc_Date) =  '"+date+"' and Acc_Trans_Type = 1 )as 'first deposit amount'" +
                    "FROM user_accounts WHERE Acc_Trans_Type =1 and DATE(Acc_Date) = '"+date+"' ";
                    rs = stmt.executeQuery(dataQuery);
                    
                    
                    while (rs.next())
                    {
                       String rptdate = date;
                        String deposits = rs.getString(1);
                        String withdrawals = rs.getString(2);
                        String betstake = rs.getString(3);
                        String betsbonusstake = rs.getString(4);
                        String accbonusamnt = rs.getString(5);
                        String playerbets = rs.getString(6);
                        String ggr = rs.getString(7);
                        String registeredplayers = rs.getString(8);
                        String firstdepositplayers = rs.getString(9);
                        String firstdepositplayersamnt = rs.getString(10);
                        
                        dataObj.put("Report_Date", rptdate);
                        dataObj.put("Deposits", deposits);
                        dataObj.put("Withdrawals", withdrawals);
                        dataObj.put("BetStake", betstake);
                        dataObj.put("BetsBonusStake", betsbonusstake);
                        dataObj.put("AccBonusAmount", accbonusamnt);
                        dataObj.put("PlayerBets", playerbets);
                        dataObj.put("GGR", String.format("%.2f", Double.parseDouble(ggr)));
                        dataObj.put("RegisteredPlayers", registeredplayers);
                        dataObj.put("FirstDepositPlayers", firstdepositplayers);
                        dataObj.put("FirstDepositAmount", firstdepositplayersamnt);
                    }
                    
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
                while (curTime>=endTime) 
                {
                    System.out.println("==="+new Date(curTime));
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

            String fromdate=LocalDate.now().plusDays(-30).toString();

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
