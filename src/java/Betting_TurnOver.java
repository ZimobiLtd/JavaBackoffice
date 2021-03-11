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
@WebServlet(urlPatterns = {"/BettingTurnOver"})
public class Betting_TurnOver extends HttpServlet {

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
        public static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
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
                   
                   
                   if(function.equals("getBettingTurnOver"))
                   {
                       String []respo=initDates();
                       String fromdate=respo[0];
                       String todate=respo[1];
                       responseobj=getAllBetting_Turnover(fromdate ,todate);
                   }
                   
                   
                   if(function.equals("filterBettingTurnOver"))
                   {
                       String[]data=maindata.split("#");
                       String from=data[0];
                       String to=data[1];
                       String bettype=data[2];
                       String betstatus=data[3];
                       
                       
                       String bet_type="0.00",bet_status="0.00";
                       if(bettype.equals("Singlebet"))
                       {
                           bet_type="1";
                       }
                       else if(bettype.equals("Multibet"))
                       {
                           bet_type="3";
                       }
                       else if(bettype.equals("Jackpot"))
                       {
                           bet_type="2";
                       }
                       else
                       {
                          bet_type="Any"; 
                       }
                       
                       
                       if(betstatus.equalsIgnoreCase("Pending"))
                       {
                           bet_status="200";
                       }
                       else if(betstatus.equalsIgnoreCase("Placed"))
                       {
                           bet_status="201";
                       }
                       else if(betstatus.equalsIgnoreCase("Won"))
                       {
                           bet_status="202";
                       }
                       else if(betstatus.equalsIgnoreCase("Lost"))
                       {
                           bet_status="203";
                       }
                       else if(betstatus.equalsIgnoreCase("Rejected"))
                       {
                           bet_status="204";
                       }
                       else if(betstatus.equalsIgnoreCase("Cancelled"))
                       {
                           bet_status="205";
                       }
                       else
                       {
                          bet_status="Any"; 
                       }
                       
                       
                       
                       
                       if(bet_type.equals("Any") && bet_status.equals("Any"))
                       {
                           responseobj=getAllBetting_Turnover(from ,to);
                       }
                       else if(!bet_type.equals("Any") && bet_status.equals("Any"))
                       {
                           bet_type="Play_Bet_Type="+bet_type;
                           bet_status="Play_Bet_Status in (201, 202, 203, 204, 205, 206)";
                           responseobj=filterAllBetting_Turnover(from, to, bet_type,bet_status) ;
                       }
                       else if(bet_type.equals("Any") && !bet_status.equals("Any"))
                       {
                           bet_type="Play_Bet_Type in(1,2,3)";
                           bet_status="Play_Bet_Status ="+bet_status;
                           responseobj=filterAllBetting_Turnover(from, to, bet_type,bet_status) ;
                       }
                       else
                       {
                           bet_type="Play_Bet_Type ="+bet_type;
                           bet_status="Play_Bet_Status ="+bet_status;
                           responseobj=filterAllBetting_Turnover(from, to, bet_type,bet_status) ;
                       }
                       
                       System.out.println(responseobj);
                   }
                   
                   
                   
                   if(function.equals("searchBettingTurnOver"))
                   {
                       String data=maindata;
                       String res="0.00";
                       if(data.startsWith("07"))
                       {
                           res="Play_Bet_Mobile="+"254"+data.substring(1);
                       }
                       else if(data.startsWith("07"))
                       {
                           res="Play_Bet_Mobile='"+data+"'";
                       }
                       else
                       {
                           res="Play_Bet_Slip_ID='"+data+"'";
                       }
                       
                       responseobj=searchAllBetting_Turnover(res) ;
                           
                   }   
                   
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
      
      
        public JSONArray getAllBetting_Turnover(String fromDate, String toDate) 
        {
                  
            String res="0.00";
            String dataQuery = "";
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            String betstatus="0.00",taxedbetpossiblewinningamount="0.00",possiblewinningamounttax="0.00",taxedbetwonamount_rm="0.00",betpossiblewinningamount="0.00",betsettledtime="0.00",openbet_rm="0.00",rejectedbet_rm="0.00",cancellebet_rm="0.00",betwonamount_rm="0.00",betbonusmoneyprocessed="0.00",
            openbetbonus_bm="0.00",betbonusrejected="0.00",betbonuscancelled="0.00",betbonuswinningamount="0.00",betbonusamount="0.00",betwonamount_rm_tax="0.00",bettype="0.00",betrealmoneyprocessed="0.00",bonusachieved="0.00";
             
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs=null;
                
                dataQuery = "SELECT Play_Bet_ID,Play_Bet_Slip_ID, Play_Bet_Mobile,Play_Bet_Timestamp,Play_Bet_Stake,Play_Bet_Status,Play_Bet_Possible_Winning, ifnull(Play_Bet_Settle_Bet_Time,'0000-00-00 00:00'), "+
                "Play_Bet_Bonus_Stake, (case when Play_Bet_Type=1 then'Singlebet' when Play_Bet_Type=2 then 'Jackpot' when Play_Bet_Type=3 then 'Multibet' end) as 'bet _type', "+
                "Play_Bet_Bonus_Winning FROM player_bets where date(Play_Bet_Timestamp) between '"+fromDate+"' and '"+toDate+"'   and play_Bet_Type in (1,2,3) and "+
                "Play_Bet_Status in (201, 202, 203, 204, 205, 206) order by Play_Bet_Timestamp desc";              
                  
                rs = stmt.executeQuery(dataQuery);

                System.out.println("getAllBetting_Turnover==="+dataQuery);

                while (rs.next())
                {
                 
                    dataObj  = new JSONObject();
                    
                    String bet_id = rs.getString(1);
                    String betslip_id = rs.getString(2);
                    String bet_mobile = rs.getString(3);//player
                    String betdate =  sdf.format(rs.getTimestamp(4));//bet date
                    String betamount = rs.getString(5);//bet real money amount
                    int status = Integer.valueOf(rs.getString(6));//bet status code
                    
                    
                    if(status==201)
                    {
                        betstatus="Placed";//bet status
                        betpossiblewinningamount = rs.getString(7);//bet possible winning amount
                        double winning_tax=(Double.valueOf(betpossiblewinningamount)-Double.valueOf(betamount))*0.2;
                        taxedbetpossiblewinningamount=String.format("%.2f", (Double.valueOf(betpossiblewinningamount)-winning_tax)); 
                        possiblewinningamounttax=String.format("%.2f", winning_tax);
                        betsettledtime = rs.getString(8);// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = rs.getString(5);  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = rs.getString(9); // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = rs.getString(9);//bet bonus amount
                        bettype = rs.getString(10);
                        betrealmoneyprocessed = "0.00"; // rm processed
                        betbonusmoneyprocessed = "0.00"; // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    else if(status==202)
                    {
                        betstatus="Won"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = rs.getString(8);// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = rs.getString(5);  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = rs.getString(7); // rm win
                        
                        double winning_tax=(Double.valueOf(betwonamount_rm)-Double.valueOf(betamount))*0.2;
                        taxedbetwonamount_rm=String.format("%.2f", Double.valueOf(betwonamount_rm)-winning_tax);
                        betwonamount_rm_tax=String.format("%.2f", winning_tax);
                        
                        openbetbonus_bm = rs.getString(9); // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = rs.getString(11); // bm win
                        betbonusamount = rs.getString(9);//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed =  rs.getString(5); // rm processed
                        betbonusmoneyprocessed = rs.getString(9); // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    else if(status==203)
                    {
                        betstatus="Lost"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = rs.getString(8);// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = "0.00";  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = "0.00"; // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = "0.00";//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed =  rs.getString(5); // rm processed
                        betbonusmoneyprocessed = rs.getString(9); // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    
                    else if(status==204)
                    {
                        betstatus="Rejected"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = rs.getString(8);// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = "0.00";  // real money(rm) open
                        rejectedbet_rm = rs.getString(5); // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = "0.00"; // bonus money (bm) open
                        betbonusrejected = rs.getString(9); // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = "0.00";//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed = "0.00"; // rm processed
                        betbonusmoneyprocessed = "0.00"; // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    
                    else if(status==205)
                    {
                        betstatus="Cancelled"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = rs.getString(8);// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = "0.00";  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = rs.getString(5); // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = "0.00"; // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = rs.getString(9); //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = "0.00";//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed = "0.00"; // rm processed
                        betbonusmoneyprocessed = "0.00"; // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    
                    else if(status==200)
                    {
                        betstatus="Pending"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = rs.getString(8);// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = "0.00";  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = "0.00"; // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = "0.00";//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed = "0.00"; // rm processed
                        betbonusmoneyprocessed = "0.00"; // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    
                    if(bet_mobile.startsWith("254"))
                    {
                        bet_mobile="0"+bet_mobile.substring(3);
                    }
                    
                    dataObj.put("BetID", bet_id);
                    dataObj.put("BetSlipID", betslip_id);
                    dataObj.put("BetMobile", bet_mobile);
                    dataObj.put("BetDate", betdate);
                    dataObj.put("BetAmountRM", betamount);
                    dataObj.put("BetStatus", betstatus);
                    dataObj.put("BetPossibleWinning", betpossiblewinningamount);
                    dataObj.put("TaxedBetPossibleWinning", taxedbetpossiblewinningamount);
                    dataObj.put("BetPossibleWinningTax", possiblewinningamounttax);
                    dataObj.put("BetLastChangeDate", betsettledtime);
                    dataObj.put("BetOpenRM", openbet_rm);
                    dataObj.put("BetRejectedRM", rejectedbet_rm);
                    dataObj.put("BetCancelledRM", cancellebet_rm);
                    dataObj.put("BetWonAmount",betwonamount_rm );
                    
                    dataObj.put("TaxedBetWonAmount", taxedbetwonamount_rm);
                    dataObj.put("BetWonAmountTax", betwonamount_rm_tax);
                    
                    dataObj.put("BetBonusOpenBM", openbetbonus_bm);
                    dataObj.put("BetBonusRejectedBM", betbonusrejected);
                    dataObj.put("BetBonusCancelledBM", betbonuscancelled);
                    dataObj.put("BetBonusPossibleWinningBM", betbonuswinningamount);
                    dataObj.put("BetAmountBM", betbonusamount);
                    dataObj.put("BetType", bettype);
                    dataObj.put("BetRMProcessed", betrealmoneyprocessed);
                    dataObj.put("BetBMProcessed", betbonusmoneyprocessed);
                    dataObj.put("BetBonusAchieved", bonusachieved);
                    
                    dataArray.put(dataObj);
                }

                
                if(dataArray.length()==0)
                {
                    dataObj  = new JSONObject();
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
        
        
        
        
        public JSONArray filterAllBetting_Turnover(String fromDate, String toDate,String bet_type, String bet_status) 
        {
                  
            String res="0.00";
            String dataQuery = "";
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            
            String betstatus="0.00",taxedbetpossiblewinningamount="0.00",possiblewinningamounttax="0.00",taxedbetwonamount_rm="0.00",betpossiblewinningamount="0.00",betsettledtime="0.00",openbet_rm="0.00",rejectedbet_rm="0.00",cancellebet_rm="0.00",betwonamount_rm="0.00",betbonusmoneyprocessed="0.00",
            openbetbonus_bm="0.00",betbonusrejected="0.00",betbonuscancelled="0.00",betbonuswinningamount="0.00",betbonusamount="0.00",betwonamount_rm_tax="0.00",bettype="0.00",betrealmoneyprocessed="0.00",bonusachieved="0.00";
                      
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs=null;
                
                dataQuery = "SELECT Play_Bet_ID,Play_Bet_Slip_ID, Play_Bet_Mobile,Play_Bet_Timestamp,Play_Bet_Stake,Play_Bet_Status,Play_Bet_Possible_Winning, ifnull(ifnull(Play_Bet_Settle_Bet_Time,'0000-00-00 00:00'),'0000-00-00 00:00'), "+
                "Play_Bet_Bonus_Stake, (case when Play_Bet_Type=1 then'Singlebet' when Play_Bet_Type=2 then 'Jackpot' when Play_Bet_Type=3 then 'Multibet' end) as 'bet _type', "+
                "Play_Bet_Bonus_Winning FROM player_bets where date(Play_Bet_Timestamp) between '"+fromDate+"' and '"+toDate+"'  and "+bet_type+" and "+bet_status+"  order by Play_Bet_Timestamp desc";              
                  
                rs = stmt.executeQuery(dataQuery);

                System.out.println("getAllBetting_Turnover==="+dataQuery);

                
                    
                while (rs.next())
                {
                 
                    dataObj  = new JSONObject();
                    
                    String bet_id = rs.getString(1);
                    String betslip_id = rs.getString(2);
                    String bet_mobile = rs.getString(3);//player
                    String betdate =  sdf.format(rs.getTimestamp(4));//bet date
                    String betamount = rs.getString(5);//bet real money amount
                    String val=rs.getString(6);
                    int status =0;
                    if(!val.equals(""))
                    {
                        status=Integer.valueOf(val);//bet status code
                    }
                    
                    
                      
                    
                   
                    if(status==201)
                    {
                        betstatus="Placed";//bet status
                        betpossiblewinningamount = rs.getString(7);//bet possible winning amount
                        double winning_tax=(Double.valueOf(betpossiblewinningamount)-Double.valueOf(betamount))*0.2;
                        taxedbetpossiblewinningamount=String.format("%.2f", (Double.valueOf(betpossiblewinningamount)-winning_tax)); 
                        possiblewinningamounttax=String.format("%.2f", winning_tax);
                        betsettledtime = sdf.format(rs.getTimestamp(8));// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = rs.getString(5);  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = rs.getString(9); // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = rs.getString(9);//bet bonus amount
                        bettype = rs.getString(10);
                        betrealmoneyprocessed = "0.00"; // rm processed
                        betbonusmoneyprocessed = "0.00"; // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    else if(status==202)
                    {
                        betstatus="Won"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = sdf.format(rs.getTimestamp(8));// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = rs.getString(5);  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = rs.getString(7); // rm win
                        
                        double winning_tax=(Double.valueOf(betwonamount_rm)-Double.valueOf(betamount))*0.2;
                        taxedbetwonamount_rm=String.format("%.2f", Double.valueOf(betwonamount_rm)-winning_tax);
                        betwonamount_rm_tax=String.format("%.2f", winning_tax);
                        
                        openbetbonus_bm = rs.getString(9); // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = rs.getString(11); // bm win
                        betbonusamount = rs.getString(9);//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed =  rs.getString(5); // rm processed
                        betbonusmoneyprocessed = rs.getString(9); // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    else if(status==203)
                    {
                        betstatus="Lost"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = sdf.format(rs.getTimestamp(8));// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = "0.00";  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = "0.00"; // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = "0.00";//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed =  rs.getString(5); // rm processed
                        betbonusmoneyprocessed = rs.getString(9); // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    
                    else if(status==204)
                    {
                        betstatus="Rejected"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = sdf.format(rs.getTimestamp(8));// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = "0.00";  // real money(rm) open
                        rejectedbet_rm = rs.getString(5); // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = "0.00"; // bonus money (bm) open
                        betbonusrejected = rs.getString(9); // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = "0.00";//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed = "0.00"; // rm processed
                        betbonusmoneyprocessed = "0.00"; // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    
                    else if(status==205)
                    {
                        betstatus="Cancelled"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = sdf.format(rs.getTimestamp(8));// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = "0.00";  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = rs.getString(5); // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = "0.00"; // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = rs.getString(9); //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = "0.00";//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed = "0.00"; // rm processed
                        betbonusmoneyprocessed = "0.00"; // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    
                    else if(status==200)
                    {
                        betstatus="Pending"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = sdf.format(rs.getTimestamp(8));// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = "0.00";  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = "0.00"; // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = "0.00";//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed = "0.00"; // rm processed
                        betbonusmoneyprocessed = "0.00"; // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    

                    if(bet_mobile.startsWith("254"))
                    {
                        bet_mobile="0"+bet_mobile.substring(3);
                    }
                    
                    dataObj.put("BetID", bet_id);
                    dataObj.put("BetSlipID", betslip_id);
                    dataObj.put("BetMobile", bet_mobile);
                    dataObj.put("BetDate", betdate);
                    dataObj.put("BetAmountRM", betamount);
                    dataObj.put("BetStatus", betstatus);
                    dataObj.put("BetPossibleWinning", betpossiblewinningamount);
                    dataObj.put("TaxedBetPossibleWinning", taxedbetpossiblewinningamount);
                    dataObj.put("BetPossibleWinningTax", possiblewinningamounttax);
                    dataObj.put("BetLastChangeDate", betsettledtime);
                    dataObj.put("BetOpenRM", openbet_rm);
                    dataObj.put("BetRejectedRM", rejectedbet_rm);
                    dataObj.put("BetCancelledRM", cancellebet_rm);
                    dataObj.put("BetWonAmount",betwonamount_rm );
                    
                    dataObj.put("TaxedBetWonAmount", taxedbetwonamount_rm);
                    dataObj.put("BetWonAmountTax", betwonamount_rm_tax);
                    
                    dataObj.put("BetBonusOpenBM", openbetbonus_bm);
                    dataObj.put("BetBonusRejectedBM", betbonusrejected);
                    dataObj.put("BetBonusCancelledBM", betbonuscancelled);
                    dataObj.put("BetBonusPossibleWinningBM", betbonuswinningamount);
                    dataObj.put("BetAmountBM", betbonusamount);
                    dataObj.put("BetType", bettype);
                    dataObj.put("BetRMProcessed", betrealmoneyprocessed);
                    dataObj.put("BetBMProcessed", betbonusmoneyprocessed);
                    dataObj.put("BetBonusAchieved", bonusachieved);

                    dataArray.put(dataObj); 
                    
                     
                }
                
                if(dataArray.length()==0)
                {
                    dataObj  = new JSONObject();
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
        
        
        
        
        public JSONArray searchAllBetting_Turnover(String data) 
        {
                  
            String res="0.00";
            String dataQuery = "";
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            String betstatus="0.00",taxedbetpossiblewinningamount="0.00",possiblewinningamounttax="0.00",taxedbetwonamount_rm="0.00",betpossiblewinningamount="0.00",betsettledtime="0.00",openbet_rm="0.00",rejectedbet_rm="0.00",cancellebet_rm="0.00",betwonamount_rm="0.00",betbonusmoneyprocessed="0.00",
            openbetbonus_bm="0.00",betbonusrejected="0.00",betbonuscancelled="0.00",betbonuswinningamount="0.00",betbonusamount="0.00",betwonamount_rm_tax="0.00",bettype="0.00",betrealmoneyprocessed="0.00",bonusachieved="0.00";
             
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs=null;
                
                dataQuery = "SELECT Play_Bet_ID,Play_Bet_Slip_ID, Play_Bet_Mobile,Play_Bet_Timestamp,Play_Bet_Stake,Play_Bet_Status,Play_Bet_Possible_Winning, ifnull(Play_Bet_Settle_Bet_Time,'0000-00-00 00:00'), "+
                "Play_Bet_Bonus_Stake, (case when Play_Bet_Type=1 then'Singlebet' when Play_Bet_Type=2 then 'Jackpot' when Play_Bet_Type=3 then 'Multibet' end) as 'bet _type', "+
                "Play_Bet_Bonus_Winning FROM player_bets where "+data+"  and play_Bet_Type in (1,2,3) and "+
                "Play_Bet_Status in (201, 202, 203, 204, 205, 206) order by Play_Bet_Timestamp desc";              
                  
                rs = stmt.executeQuery(dataQuery);

                System.out.println("getAllBetting_Turnover==="+dataQuery);

                while (rs.next())
                {
                 
                    dataObj  = new JSONObject();
                    
                    String bet_id = rs.getString(1);
                    String betslip_id = rs.getString(2);
                    String bet_mobile = rs.getString(3);//player
                    String betdate =  sdf.format(rs.getTimestamp(4));//bet date
                    String betamount = rs.getString(5);//bet real money amount
                    int status = Integer.valueOf(rs.getString(6));//bet status code
                    
                    
                    if(status==201)
                    {
                        betstatus="Placed";//bet status
                        betpossiblewinningamount = rs.getString(7);//bet possible winning amount
                        double winning_tax=(Double.valueOf(betpossiblewinningamount)-Double.valueOf(betamount))*0.2;
                        taxedbetpossiblewinningamount=String.format("%.2f", (Double.valueOf(betpossiblewinningamount)-winning_tax)); 
                        possiblewinningamounttax=String.format("%.2f", winning_tax);
                        betsettledtime =sdf.format(rs.getTimestamp(8));// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = rs.getString(5);  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = rs.getString(9); // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = rs.getString(9);//bet bonus amount
                        bettype = rs.getString(10);
                        betrealmoneyprocessed = "0.00"; // rm processed
                        betbonusmoneyprocessed = "0.00"; // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    else if(status==202)
                    {
                        betstatus="Won"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = sdf.format(rs.getTimestamp(8));// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = rs.getString(5);  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = rs.getString(7); // rm win
                        
                        double winning_tax=(Double.valueOf(betwonamount_rm)-Double.valueOf(betamount))*0.2;
                        taxedbetwonamount_rm=String.format("%.2f", Double.valueOf(betwonamount_rm)-winning_tax);
                        betwonamount_rm_tax=String.format("%.2f", winning_tax);
                        
                        openbetbonus_bm = rs.getString(9); // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = rs.getString(11); // bm win
                        betbonusamount = rs.getString(9);//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed =  rs.getString(5); // rm processed
                        betbonusmoneyprocessed = rs.getString(9); // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    else if(status==203)
                    {
                        betstatus="Lost"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = sdf.format(rs.getTimestamp(8));// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = "0.00";  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = "0.00"; // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = "0.00";//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed =  rs.getString(5); // rm processed
                        betbonusmoneyprocessed = rs.getString(9); // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    
                    else if(status==204)
                    {
                        betstatus="Rejected"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = sdf.format(rs.getTimestamp(8));// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = "0.00";  // real money(rm) open
                        rejectedbet_rm = rs.getString(5); // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = "0.00"; // bonus money (bm) open
                        betbonusrejected = rs.getString(9); // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = "0.00";//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed = "0.00"; // rm processed
                        betbonusmoneyprocessed = "0.00"; // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    
                    else if(status==205)
                    {
                        betstatus="Cancelled"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = sdf.format(rs.getTimestamp(8));// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = "0.00";  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = rs.getString(5); // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = "0.00"; // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = rs.getString(9); //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = "0.00";//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed = "0.00"; // rm processed
                        betbonusmoneyprocessed = "0.00"; // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    
                    else if(status==200)
                    {
                        betstatus="Pending"; //bet status
                        betpossiblewinningamount = "0.00"; //rs.getString(7);
                        betsettledtime = sdf.format(rs.getTimestamp(8));// bet settled  date  sdf.format(rs.getTimestamp(8));
                        openbet_rm = "0.00";  // real money(rm) open
                        rejectedbet_rm = "0.00"; // rm rejected
                        cancellebet_rm = "0.00"; // rm cancelled
                        betwonamount_rm = "0.00"; // rm win
                        openbetbonus_bm = "0.00"; // bonus money (bm) open
                        betbonusrejected = "0.00"; // bm rejected
                        betbonuscancelled = "0.00"; //bm cancelled
                        betbonuswinningamount = "0.00"; // bm win
                        betbonusamount = "0.00";//bet bonus amount
                        bettype = rs.getString(10);//bet type
                        betrealmoneyprocessed = "0.00"; // rm processed
                        betbonusmoneyprocessed = "0.00"; // bm processed
                        bonusachieved = "0.00"; // bonus achieved
                    }
                    
                    
                    if(bet_mobile.startsWith("254"))
                    {
                        bet_mobile="0"+bet_mobile.substring(3);
                    }

                    dataObj.put("BetID", bet_id);
                    dataObj.put("BetSlipID", betslip_id);
                    dataObj.put("BetMobile", bet_mobile);
                    dataObj.put("BetDate", betdate);
                    dataObj.put("BetAmountRM", betamount);
                    dataObj.put("BetStatus", betstatus);
                    dataObj.put("BetPossibleWinning", betpossiblewinningamount);
                    dataObj.put("TaxedBetPossibleWinning", taxedbetpossiblewinningamount);
                    dataObj.put("BetPossibleWinningTax", possiblewinningamounttax);
                    dataObj.put("BetLastChangeDate", betsettledtime);
                    dataObj.put("BetOpenRM", openbet_rm);
                    dataObj.put("BetRejectedRM", rejectedbet_rm);
                    dataObj.put("BetCancelledRM", cancellebet_rm);
                    dataObj.put("BetWonAmount",betwonamount_rm );
                    
                    dataObj.put("TaxedBetWonAmount", taxedbetwonamount_rm);
                    dataObj.put("BetWonAmountTax", betwonamount_rm_tax);
                    
                    dataObj.put("BetBonusOpenBM", openbetbonus_bm);
                    dataObj.put("BetBonusRejectedBM", betbonusrejected);
                    dataObj.put("BetBonusCancelledBM", betbonuscancelled);
                    dataObj.put("BetBonusPossibleWinningBM", betbonuswinningamount);
                    dataObj.put("BetAmountBM", betbonusamount);
                    dataObj.put("BetType", bettype);
                    dataObj.put("BetRMProcessed", betrealmoneyprocessed);
                    dataObj.put("BetBMProcessed", betbonusmoneyprocessed);
                    dataObj.put("BetBonusAchieved", bonusachieved);
                    
                    dataArray.put(dataObj);
                }

                
                
                if(dataArray.length()==0)
                {
                    dataObj  = new JSONObject();
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
                while (endTime <= curTime) 
                {
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

            String todate=LocalDate.now().toString();

            String fromdate=LocalDate.now().plusDays(-60).toString();

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
