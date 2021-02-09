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
import java.text.DecimalFormat;
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
@WebServlet(urlPatterns = {"/BettingReport"})
public class BettingReport extends HttpServlet {

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
        public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
                   
                   System.out.println("BettingReport===="+jb.toString());
                   jsonobj = new JSONObject(jb.toString());
                   function=jsonobj.getString("function");
                   maindata=jsonobj.getString("data");
                   
                   
                   if(function.equals("getBettingReport"))
                   {
                       String []respo=initDates();
                       String fromdate=respo[0];
                       String todate=respo[1];
                       
                       responseobj=getBettingReport("2017-10-23" ,"2020-10-25");
                   }
                   
                   
                   if(function.equals("filterBettingReport"))
                   {
                       String[]data=maindata.split("#");
                       String from=data[0];
                       String to=data[1];  
                       
                       responseobj=getBettingReport(from ,to);
                   }
                   
                   
                   if(function.equals("getPlayerBettingReport"))
                   {
                       String[]data=maindata.split("#");
                       String player_id=data[0];
                       String player_mobile=data[1];
                       
                       if(player_mobile.startsWith("07") || player_mobile.startsWith("01"))
                       {
                          player_mobile="254"+player_mobile.substring(1);
                       }
                       
                       responseobj=getPlayerBettingReport("","",player_mobile);
                   }
                   
                   if(function.equals("filterPlayerBettingReport"))
                   {
                       String[]data=maindata.split("#");
                       String player_id=data[0];
                       String player_mobile=data[1];
                       String from=data[2];
                       String to=data[3];  
                       
                       if(player_mobile.startsWith("07") || player_mobile.startsWith("01"))
                       {
                          player_mobile="254"+player_mobile.substring(1);
                       }
                       
                       responseobj=getPlayerBettingReport(from,to,player_mobile);
                   }
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        
        
      
      
        public JSONArray getBettingReport(String fromDate,String toDate)
        {
                  
            String res="0.00";
            String dataQuery = "";
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs=null;
                
                dataQuery = "select Play_Bet_Timestamp, Play_Bet_Slip_ID, (case when Play_Bet_Status=200 then 'Pending'  when Play_Bet_Status=201 then 'Placed' when Play_Bet_Status=202 then 'Win' when Play_Bet_Status=203 then 'Loss' when Play_Bet_Status=204 then 'Rejected' when Play_Bet_Status=205 then 'Cancelled' end)as 'bet_status', "+
                            " Chan_Mode_Name , Play_Bet_Mobile, Play_Bet_Stake, Play_Bet_Bonus_Stake , Play_Bet_Possible_Winning, " +
                            "Play_Bet_Group_ID, Play_Bet_Possible_BonusWinning  from player_bets ,channels_used where  Chan_Table_ID = play_bet_Channel and  date(Play_Bet_Timestamp) between   '"+fromDate+"' and '"+toDate+"'  " +
                            "and play_Bet_Type in (0,1,2,3)  and Play_Bet_Status in (201, 202, 203) order by Play_Bet_Timestamp desc ";
                
                System.out.println("getBettingReport==="+dataQuery);
                
                rs = stmt.executeQuery(dataQuery);
  
                while (rs.next())
                {
                    dataObj  = new JSONObject();

                    String betdate = sdf.format(rs.getTimestamp(1));
                    String betslip_id = rs.getString(2);
                    String betstatus = rs.getString(3);
                    String betchannel = rs.getString(4);
                    String betmobile = rs.getString(5);
                    String betstake = rs.getString(6);//bet rm 
                    String betbonusstake = rs.getString(7);//bet bm 
                    String betpossiblewinning = rs.getString(8);//bet rm possible win
                    String betgroup_id = rs.getString(9);
                    String betbonuspossiblewinning = rs.getString(10);//bet rm possible win


                    String settled_rm="0.00",settled_bm="0.00",openbet_bm="0.00",ggr_rm="0.00",ngr_rm="0.00",ngrtax_rm="0.00",taxedngr_rm="0.00",bonusamountopen="0.00",bonusmoneysettled="0.00",openbet_rm="0.00",winamount_rm="0.00",winamount_bm="0.00",
                    ggr_bm="0.00",ngr_bm="0.00",refund_rm="0.00",taxedwinamount_rm="0.00",refund_bm="0.00",winning_tax="0.00",ngr_tax="0.00";

                    if (betstatus.equalsIgnoreCase("Placed")) 
                    {
                        openbet_rm = String.valueOf(betstake); // open rm
                        settled_rm = "0.00"; // settled rm
                        winamount_rm = "0.00"; // win rm
                        ggr_rm = "0.00"; // ggr rm
                        openbet_bm = String.valueOf(betbonusstake); // open bm
                        settled_bm = "0.00"; // settled bm
                        winamount_bm = "0.00"; // win bm
                        ggr_bm = "0.00"; // ggr bm
                        ngr_rm = "0.00"; // ngr 
                        refund_rm = "0.00"; // rm refund
                        refund_bm = "0.00"; // bm refund
                    } 
                    else if (betstatus.equalsIgnoreCase("Win")) 
                    {
                        openbet_rm = "0.00"; // open rm
                        settled_rm = String.valueOf(betstake); // settled rm
                        
                        double win_tax=Integer.valueOf(betpossiblewinning)*0.2;
                        double taxedamount_won=Double.valueOf(betpossiblewinning)-win_tax;
                        
                        taxedwinamount_rm=String.format("%.2f", taxedamount_won);
                        winning_tax=String.format("%.2f", win_tax);// win_tax rm
                        winamount_rm =betpossiblewinning; // win rm
                        ggr_rm = String.valueOf(Integer.valueOf(betstake)); // ggr rm
                        openbet_bm = "0.00"; // open bm
                        settled_bm = String.valueOf(betbonusstake); // settled bm
                        winamount_bm = String.valueOf(betbonuspossiblewinning); // win bm
                        ggr_bm = String.valueOf(Integer.valueOf(betbonusstake)); // ggr bm
                        refund_rm = "0.00"; // rm refund
                        refund_bm = "0.00"; // bm refund
                    } 
                    else if (betstatus.equalsIgnoreCase("Loss"))
                    {
                        openbet_rm = "0.00"; // open rm
                        settled_rm = String.valueOf(betstake); // settled rm
                        winamount_rm = "0.00"; // win rm
                        ggr_rm = String.valueOf(settled_rm); // ggr rm
                        
                        double NGR=Double.valueOf(ggr_rm) - Double.valueOf(winamount_rm);
                        double NGR_Tax=NGR*0.15;
                        double fin_TaxedNGR=NGR-NGR_Tax;
                        
                        
                        ngr_tax=String.format("%.2f", NGR_Tax);// ngr_tax
                        ngr_rm = String.format("%.2f", NGR); // ngr 
                        taxedngr_rm = String.format("%.2f", fin_TaxedNGR); // ngr 
                        openbet_bm = "0.00"; // open bm
                        settled_bm = String.valueOf(betbonusstake); // settled bm
                        winamount_bm = "0.00"; // win bm
                        ggr_bm = String.valueOf(betbonusstake); // ggr bm
                        refund_rm = "0.00"; // rm refund
                        refund_bm = "0.00"; // bm refund
                    } 
                    else if (betstatus.equalsIgnoreCase("Rejected"))
                    {
                        openbet_rm = "0.00"; // open rm
                        settled_rm = "0.00"; // settled rm
                        winamount_rm = "0.00"; // win rm
                        ggr_rm = "0.00"; // ggr rm
                        openbet_bm = "0.00"; // open bm
                        settled_bm = "0.00"; // settled bm
                        winamount_bm = "0.00"; // win bm
                        ggr_bm = "0.00"; // ggr bm
                        ngr_rm = "0.00"; // ngr 
                        refund_rm = String.valueOf(betstake); // rm refund
                        refund_bm = String.valueOf(betbonusstake);  // bm refund
                    } 
                    else if (betstatus.equalsIgnoreCase("Cancelled")) 
                    {
                        openbet_rm = "0.00"; // open rm
                        settled_rm = "0.00"; // settled rm
                        winamount_rm = "0.00"; // win rm
                        ggr_rm = "0.00"; // ggr rm
                        openbet_bm = "0.00"; // open bm
                        settled_bm = "0.00"; // settled bm
                        winamount_bm = "0.00"; // win bm
                        ggr_bm = "0.00"; // ggr bm
                        ngr_rm = "0.00"; // ngr 
                        refund_rm = String.valueOf(betstake); // rm refund
                        refund_bm = String.valueOf(betbonusstake);  // bm refund
                    } 
                    else if (betstatus.equalsIgnoreCase("Pending")) 
                    {
                        openbet_rm = "0.00"; // open rm
                        settled_rm = String.valueOf(betstake); // settled rm
                        winamount_rm = "0.00"; // win rm
                        ggr_rm = "0.00"; // ggr rm
                        openbet_bm = "0.00"; // open bm
                        settled_bm = String.valueOf(betstake); // settled bm
                        winamount_bm = "0.00"; // win bm
                        ggr_bm = "0.00"; // ggr bm
                        ngr_rm = "0.00"; // ngr 
                        refund_rm = String.valueOf(betstake); // rm refund
                        refund_bm = String.valueOf(betbonusstake);  // bm refund
                    }

                    dataObj.put("BetDate", betdate);
                    dataObj.put("BetSlipID", betslip_id);
                    dataObj.put("BetStatus", betstatus);
                    dataObj.put("BetChannel", betchannel);
                    dataObj.put("BetMobile", betmobile);
                    dataObj.put("RMOpen", openbet_rm);
                    dataObj.put("RMSettled", settled_rm);
                    dataObj.put("RMWinAmountTax", winning_tax);
                    dataObj.put("RMWinAmount", winamount_rm);
                    dataObj.put("RMTaxedWinAmount", taxedwinamount_rm);
                    dataObj.put("GGRRM", ggr_rm);
                    dataObj.put("NGRRM", ngr_rm);
                    dataObj.put("NGRRMTax", ngr_tax);
                    dataObj.put("TaxedNGRRM", taxedngr_rm);
                    dataObj.put("RefundRM", refund_rm);
                    dataObj.put("BMOpen", openbet_bm);
                    dataObj.put("BMSettled", settled_bm);
                    dataObj.put("BMWinAmount", winamount_bm);
                    dataObj.put("GGRBM", ggr_bm);
                    dataObj.put("RefundBM", refund_bm);

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
        
        
        
        
        public JSONArray getPlayerBettingReport(String fromDate,String toDate,String mobile)
        {
                  
            String res="0.00";
            String dataQuery = "";
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            JSONArray respoArray = new JSONArray();
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            int totalBetStake=0;
            int totalWinnings=0;
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs=null;
                
                if(fromDate.equals("")&& fromDate.equals(""))
                {
                    dataQuery = "select Play_Bet_Timestamp, Play_Bet_Slip_ID, (case when Play_Bet_Status=200 then 'Pending'  when Play_Bet_Status=201 then 'Placed' when Play_Bet_Status=202 then 'Win' when Play_Bet_Status=203 then 'Loss' when Play_Bet_Status=204 then 'Rejected' when Play_Bet_Status=205 then 'Cancelled' end)as 'bet_status', "+
                            " Chan_Mode_Name , Play_Bet_Mobile, Play_Bet_Stake, Play_Bet_Bonus_Stake , Play_Bet_Possible_Winning, " +
                            "Play_Bet_Group_ID, Play_Bet_Possible_BonusWinning  from player_bets ,channels_used where  Chan_Table_ID = play_bet_Channel  " +
                            "and play_Bet_Type in (0,1,2,3)  and Play_Bet_Status in (201, 202, 203) and  player_bets.Play_Bet_Mobile='"+mobile+"' order by Play_Bet_Timestamp desc ";        
                }
                else
                {
                    dataQuery = "select Play_Bet_Timestamp, Play_Bet_Slip_ID, (case when Play_Bet_Status=200 then 'Pending'  when Play_Bet_Status=201 then 'Placed' when Play_Bet_Status=202 then 'Win' when Play_Bet_Status=203 then 'Loss' when Play_Bet_Status=204 then 'Rejected' when Play_Bet_Status=205 then 'Cancelled' end)as 'bet_status', "+
                            " Chan_Mode_Name , Play_Bet_Mobile, Play_Bet_Stake, Play_Bet_Bonus_Stake , Play_Bet_Possible_Winning, " +
                            "Play_Bet_Group_ID, Play_Bet_Possible_BonusWinning  from player_bets ,channels_used where  Chan_Table_ID = play_bet_Channel and  date(Play_Bet_Timestamp) between   '"+fromDate+"' and '"+toDate+"'  " +
                            "and play_Bet_Type in (0,1,2,3)  and Play_Bet_Status in (201, 202, 203) and  player_bets.Play_Bet_Mobile='"+mobile+"' order by Play_Bet_Timestamp desc ";                
                }
                
               
                System.out.println("getPlayerBettingReport==="+dataQuery);
                
                rs = stmt.executeQuery(dataQuery);
  
                while (rs.next())
                {
                    dataObj  = new JSONObject();

                    String betdate = sdf.format(rs.getTimestamp(1));
                    String betslip_id = rs.getString(2);
                    String betstatus = rs.getString(3);
                    String betchannel = rs.getString(4);
                    String betmobile = rs.getString(5);
                    String betstake = rs.getString(6);//bet rm 
                    String betbonusstake = rs.getString(7);//bet bm 
                    String betpossiblewinning = rs.getString(8);//bet rm possible win
                    String betgroup_id = rs.getString(9);
                    String betbonuspossiblewinning = rs.getString(10);//bet rm possible win


                    String settled_rm="0.00",settled_bm="0.00",openbet_bm="0.00",ggr_rm="0.00",ngr_rm="0.00",ngrtax_rm="0.00",taxedngr_rm="0.00",bonusamountopen="0.00",bonusmoneysettled="0.00",openbet_rm="0.00",winamount_rm="0.00",winamount_bm="0.00",
                    ggr_bm="0.00",ngr_bm="0.00",refund_rm="0.00",taxedwinamount_rm="0.00",refund_bm="0.00",winning_tax="0.00",ngr_tax="0.00";

                    if (betstatus.equalsIgnoreCase("Placed")) 
                    {
                        openbet_rm = String.valueOf(betstake); // open rm
                        settled_rm = "0.00"; // settled rm
                        winamount_rm = "0.00"; // win rm
                        ggr_rm = "0.00"; // ggr rm
                        openbet_bm = String.valueOf(betbonusstake); // open bm
                        settled_bm = "0.00"; // settled bm
                        winamount_bm = "0.00"; // win bm
                        ggr_bm = "0.00"; // ggr bm
                        ngr_rm = "0.00"; // ngr 
                        refund_rm = "0.00"; // rm refund
                        refund_bm = "0.00"; // bm refund
                        
                        totalBetStake+=Integer.valueOf(betstake);
                    } 
                    else if (betstatus.equalsIgnoreCase("Win")) 
                    {
                        openbet_rm = "0.00"; // open rm
                        settled_rm = String.valueOf(betstake); // settled rm
                        
                        double win_tax=Integer.valueOf(betpossiblewinning)*0.2;
                        double taxedamount_won=Double.valueOf(betpossiblewinning)-win_tax;
                        
                        taxedwinamount_rm=String.format("%.2f", taxedamount_won);
                        winning_tax=String.format("%.2f", win_tax);// win_tax rm
                        winamount_rm =betpossiblewinning; // win rm
                        ggr_rm = String.valueOf(Integer.valueOf(betstake)); // ggr rm
                        openbet_bm = "0.00"; // open bm
                        settled_bm = String.valueOf(betbonusstake); // settled bm
                        winamount_bm = String.valueOf(betbonuspossiblewinning); // win bm
                        ggr_bm = String.valueOf(Integer.valueOf(betbonusstake)); // ggr bm
                        refund_rm = "0.00"; // rm refund
                        refund_bm = "0.00"; // bm refund
                        
                        totalWinnings+=(int) taxedamount_won;
                    } 
                    else if (betstatus.equalsIgnoreCase("Loss"))
                    {
                        openbet_rm = "0.00"; // open rm
                        settled_rm = String.valueOf(betstake); // settled rm
                        winamount_rm = "0.00"; // win rm
                        ggr_rm = String.valueOf(settled_rm); // ggr rm
                        
                        double NGR=Double.valueOf(ggr_rm) - Double.valueOf(winamount_rm);
                        double NGR_Tax=NGR*0.15;
                        double fin_TaxedNGR=NGR-NGR_Tax;
                        
                        
                        ngr_tax=String.format("%.2f", NGR_Tax);// ngr_tax
                        ngr_rm = String.format("%.2f", NGR); // ngr 
                        taxedngr_rm = String.format("%.2f", fin_TaxedNGR); // ngr 
                        openbet_bm = "0.00"; // open bm
                        settled_bm = String.valueOf(betbonusstake); // settled bm
                        winamount_bm = "0.00"; // win bm
                        ggr_bm = String.valueOf(betbonusstake); // ggr bm
                        refund_rm = "0.00"; // rm refund
                        refund_bm = "0.00"; // bm refund
                    } 
                    else if (betstatus.equalsIgnoreCase("Rejected"))
                    {
                        openbet_rm = "0.00"; // open rm
                        settled_rm = "0.00"; // settled rm
                        winamount_rm = "0.00"; // win rm
                        ggr_rm = "0.00"; // ggr rm
                        openbet_bm = "0.00"; // open bm
                        settled_bm = "0.00"; // settled bm
                        winamount_bm = "0.00"; // win bm
                        ggr_bm = "0.00"; // ggr bm
                        ngr_rm = "0.00"; // ngr 
                        refund_rm = String.valueOf(betstake); // rm refund
                        refund_bm = String.valueOf(betbonusstake);  // bm refund
                    } 
                    else if (betstatus.equalsIgnoreCase("Cancelled")) 
                    {
                        openbet_rm = "0.00"; // open rm
                        settled_rm = "0.00"; // settled rm
                        winamount_rm = "0.00"; // win rm
                        ggr_rm = "0.00"; // ggr rm
                        openbet_bm = "0.00"; // open bm
                        settled_bm = "0.00"; // settled bm
                        winamount_bm = "0.00"; // win bm
                        ggr_bm = "0.00"; // ggr bm
                        ngr_rm = "0.00"; // ngr 
                        refund_rm = String.valueOf(betstake); // rm refund
                        refund_bm = String.valueOf(betbonusstake);  // bm refund
                    } 
                    else if (betstatus.equalsIgnoreCase("Pending")) 
                    {
                        openbet_rm = "0.00"; // open rm
                        settled_rm = String.valueOf(betstake); // settled rm
                        winamount_rm = "0.00"; // win rm
                        ggr_rm = "0.00"; // ggr rm
                        openbet_bm = "0.00"; // open bm
                        settled_bm = String.valueOf(betstake); // settled bm
                        winamount_bm = "0.00"; // win bm
                        ggr_bm = "0.00"; // ggr bm
                        ngr_rm = "0.00"; // ngr 
                        refund_rm = String.valueOf(betstake); // rm refund
                        refund_bm = String.valueOf(betbonusstake);  // bm refund
                    }

                    dataObj.put("BetDate", betdate);
                    dataObj.put("BetSlipID", betslip_id);
                    dataObj.put("BetStatus", betstatus);
                    dataObj.put("BetChannel", betchannel);
                    dataObj.put("BetMobile", betmobile);
                    dataObj.put("RMOpen", openbet_rm);
                    dataObj.put("RMSettled", settled_rm);
                    dataObj.put("RMWinAmountTax", winning_tax);
                    dataObj.put("RMWinAmount", winamount_rm);
                    dataObj.put("RMTaxedWinAmount", taxedwinamount_rm);
                    dataObj.put("GGRRM", ggr_rm);
                    dataObj.put("NGRRM", ngr_rm);
                    dataObj.put("NGRRMTax", ngr_tax);
                    dataObj.put("TaxedNGRRM", taxedngr_rm);
                    dataObj.put("RefundRM", refund_rm);
                    dataObj.put("BMOpen", openbet_bm);
                    dataObj.put("BMSettled", settled_bm);
                    dataObj.put("BMWinAmount", winamount_bm);
                    dataObj.put("GGRBM", ggr_bm);
                    dataObj.put("RefundBM", refund_bm);

                    dataArray.put(dataObj);
                }
                
                
                JSONObject dataObj1  = new JSONObject();
                JSONArray dataArray1 = new JSONArray();
                String query1=" select ifnull(sum(Acc_Amount),0) ,registration_date  from user_accounts,player where Acc_Mobile='"+mobile+"' and msisdn='"+mobile+"'  group by Acc_Mobile";
                rs = stmt.executeQuery(query1);
                while (rs.next())
                {
                    dataObj1.put("Balance", rs.getString(1));
                    dataObj1.put("RegistrationDate", rs.getString(2));
                    dataObj1.put("TotalBetStake", String.valueOf(totalBetStake));
                    dataObj1.put("TotalWinnings", String.valueOf(totalWinnings));
                    
                    dataArray1.put(dataObj1);
                    
                }
                 
                respoArray.put(dataArray);
                respoArray.put(dataArray1);
                
                   

            rs.close();
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
                    
        return respoArray;
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
