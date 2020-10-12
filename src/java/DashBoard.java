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
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author jac
 */
@WebServlet(urlPatterns = {"/Dashboard"})
public class DashBoard extends HttpServlet {

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

                    System.out.println("dashboard===="+jb.toString());
                    jsonobj = new JSONObject(jb.toString());
                    function=jsonobj.getString("function");
                    maindata=jsonobj.getString("data");
                   
                    String []respo=initDates();
                    String fromdate=respo[0];
                    String todate=respo[1];
                   
                    if(function.equals("getDashboardData"))
                    {
                        responseobj=getDashboardFinancialSummary(fromdate ,todate);
                    }
                    
                    if(function.equals("filterDashboardData"))
                    {
                        String []data=maindata.split("#");
                        String from=data[0];
                        String to=data[1];
                        responseobj=getDashboardFinancialSummary(from ,to);
                    }
                    
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
            PrintWriter out = resp.getWriter(); 
            
            out.print(responseobj);
            
            
        }
    
    
        
        
      
      
    // dashboard deposit values
    public JSONArray getDashboardFinancialSummary(String fromDate, String toDate) {

        String []collection=null;
        String dataQueryDeposit = "";
        String dataQueryWithdrawals = "";
        String dataQueryBalance = "";
        String dataQueryBalanceCount = "";
        String bonusbalancecount = "";
        String bonusbalancesum = "";
        String registrations="";
        String dataQueryTurnover = "";
        String dataQueryOpenBetsTurnOver = "";
        String dataQueryWinnings = "";
        String dataQuerySettledTurnOver = "";
        String dataQueryBetsByStatus="";
        String dataQueryBetsByChannel="";
        String dataQueryBetsByBetType="";
        String dataQuerySettledBetsTurnOver="";
        String dataQueryWonBetsTurnOver="";
        
        
        ResultSet rs=null;
        String depositAccBal="0";
        String depositPlayers="0";
        String withdrawAccBal="0";
        String withdrawPlayers="0";
        String playersRMBalance="0";
        String playersRMCount="0";
        String playersBMBalance="0";
        String playersBMCount="0";
        
        String totalTurnoverRM="0";
        String totalTurnoverBM="0";
        String totalOpenBetsRMTurnOver="0";
        String totalOpenBetsBMTurnOver="0";
        String settledBetsTurnoverRM="0";
        String wonBetsTurnoverRM="0";
        String totalWinnings="0";
        
        String USSD_Reg="0";
        String Web_Reg="0";
        String SMS_Reg="0";
        String App_Reg="0";
        
        String placedBets="0";
        String wonBets="0";
        String lostBets="0";
        String rejectedBets="0";
        String cancelledBets="0";
        String settledBets="0";
        
        String USSD_Bets="0";
        String Web_Bets="0";
        String SMS_Bets="0";
        String App_Bets="0";
        
        String Single_Bets="0";
        String Multi_Bets="0";
        String Jackpot_Bets="0";
        
        int GGR=0;
        int NGR=0;
        int Profit=0;
        int Loss=0;
        
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        JSONObject main = new JSONObject();
        
        dataQueryTurnover = "select ifnull(sum(Play_Bet_Stake),0), ifnull(sum(Play_Bet_Bonus_Stake),0) from player_bets where Play_Bet_Status in (201,202,203)"
                + " and  Play_Bet_Timestamp between '" + fromDate + "' and '" + toDate + "'";

        dataQueryOpenBetsTurnOver = "select ifnull(sum(Play_Bet_Stake),0), ifnull(sum(Play_Bet_Bonus_Stake),0) from player_bets where Play_Bet_Status in (201) "
                + " and  Play_Bet_Timestamp between '" + fromDate + "' and '" + toDate + "'";
        
        dataQuerySettledBetsTurnOver = "select ifnull(sum(Play_Bet_Stake),0), ifnull(sum(Play_Bet_Bonus_Stake),0) from player_bets where Play_Bet_Status in (202,203) "
                + " and  Play_Bet_Timestamp between '" + fromDate + "' and '" + toDate + "'";
        
        dataQueryWonBetsTurnOver = "select ifnull(sum(Play_Bet_Stake),0), ifnull(sum(Play_Bet_Bonus_Stake),0) from player_bets where Play_Bet_Status in (202) "
                + " and  Play_Bet_Timestamp between '" + fromDate + "' and '" + toDate + "'";

        dataQueryWinnings = "select ifnull(sum(Play_Bet_Possible_Winning),0) from player_bets where Play_Bet_Status in (202) "
                + " and  Play_Bet_Timestamp between '" + fromDate + "' and '" + toDate + "'";

        registrations="select count(id),(select count(id) from player where registration_date between  '" + fromDate + "' and '" + toDate + "' and user_channel=2), "
                    + "(select count(id) from player where registration_date between  '" + fromDate + "' and '" + toDate + "' and user_channel=3), "
                    + "(select count(id) from player where registration_date between  '" + fromDate + "' and '" + toDate + "' and user_channel=4) "
                    + "from player where registration_date between  '" + fromDate + "' and '" + toDate + "' and user_channel=1 ";

        dataQueryDeposit = "SELECT count(Acc_ID),ifnull(sum(Acc_Amount),0) FROM user_accounts "
                + "WHERE Acc_Trans_Type = 1 and  Acc_Date between '" + fromDate + "' and '" + toDate + "'";

        dataQueryWithdrawals = "SELECT  count(Acc_ID),ifnull(sum(Acc_Amount),0) FROM user_accounts "
               + "WHERE Acc_Trans_Type = 2 and  Acc_Date between '" + fromDate + "' and '" + toDate + "'";

        dataQueryBalance = "select count(id),ifnull(sum(Player_Balance),0) from player where Player_Balance > 0 and registration_date between '" + fromDate + "' and '" + toDate + "' ";

        bonusbalancesum = "select count(id),ifnull(sum(Bonus_Balance),0) from player where Bonus_Balance > 0 and registration_date between '" + fromDate + "' and '" + toDate + "' ";

        dataQueryBetsByStatus = "select Play_Bet_Status,count(Play_Bet_ID) from player_bets where Play_Bet_Timestamp between '" + fromDate + "' and '" + toDate + "' GROUP BY Play_Bet_Status";
        
        dataQueryBetsByChannel="select count(Play_Bet_Slip_ID),(select count(Play_Bet_Slip_ID) from player_bets where Play_Bet_Timestamp between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Channel = 2), "
                + "(select count(Play_Bet_Slip_ID) from player_bets where Play_Bet_Timestamp between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Channel = 3), "
                + "(select count(Play_Bet_Slip_ID) from player_bets where Play_Bet_Timestamp between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Channel = 4) "
                + "from player_bets where Play_Bet_Timestamp between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Channel = 1 " ;

        dataQueryBetsByBetType="select ifnull(Play_Bet_Type,0), ifnull(count(Play_Bet_Type),0) from player_bets  where Play_Bet_Timestamp between '" + fromDate + "' and '" + toDate + "' GROUP BY Play_Bet_Type";
        
        
        String financequeries=dataQueryDeposit+"#"+dataQueryWithdrawals+"#"+dataQueryBalance+"#"+bonusbalancesum;
        collection=financequeries.split("#");

       
            try( Connection conn = new DBManager(type).getDBConnection();
                 Statement stmt = conn.createStatement();)
            {
                
                for(int i=0;i<collection.length;i++)
                {
                    System.out.println(i+"dataQueryDeposit==="+collection[i]);
                    rs = stmt.executeQuery(collection[i]); 
                    while (rs.next()) 
                    {
                        switch (i) 
                        {
                            case 0:
                               depositPlayers=rs.getString(1);
                               depositAccBal=String.valueOf((Math.round(Double.valueOf(rs.getString(2)).intValue())));
                            break;
                            case 1:
                               withdrawPlayers=rs.getString(1);
                               withdrawAccBal=String.valueOf((Math.round(Double.valueOf(rs.getString(2)).intValue())));
                            break;
                            case 2:
                                playersRMCount=rs.getString(1); 
                                playersRMBalance=String.valueOf((Math.round(Double.valueOf(rs.getString(2)).intValue())));
                            break;
                            case 3:
                                playersBMCount=rs.getString(1); 
                                playersBMBalance=String.valueOf((Math.round(Double.valueOf(rs.getString(2)).intValue())));
                            break;
                            default:
                            break;
                        }
                        
                    }                     
                }
                
                dataObj  = new JSONObject();
                dataObj.put("DepositCount", depositPlayers);
                dataObj.put("DepositsValue", depositAccBal);
                dataObj.put("WithdrawalCount", withdrawPlayers);
                dataObj.put("WithdrawalValue", withdrawAccBal);
                dataObj.put("RMBalanceCount", playersRMCount);
                dataObj.put("RMBalanceValue", playersRMBalance);
                dataObj.put("BMBalanceCount", playersBMCount);
                dataObj.put("BMBalanceValue", playersBMBalance);
                
                main.put("Finance_Summary", dataObj);
                
                
                String profitqueries=dataQueryTurnover+"#"+dataQueryOpenBetsTurnOver+"#"+dataQueryWinnings+"#"+dataQuerySettledBetsTurnOver+"#"+dataQueryWonBetsTurnOver;
                collection=profitqueries.split("#");
                for(int i=0;i<collection.length;i++)
                {
                   System.out.println(i+"profitquery==="+collection[i]);
                    rs = stmt.executeQuery(collection[i]); 
                    while (rs.next()) 
                    {  
                        switch (i) 
                        {
                            case 0:
                               totalTurnoverRM=String.valueOf((Math.round(Double.valueOf(rs.getString(1)).intValue())));
                               totalTurnoverBM=String.valueOf((Math.round(Double.valueOf(rs.getString(2)).intValue())));
                            break;
                            case 1:
                               totalOpenBetsRMTurnOver=String.valueOf((Math.round(Double.valueOf(rs.getString(1)).intValue())));
                               totalOpenBetsBMTurnOver=String.valueOf((Math.round(Double.valueOf(rs.getString(2)).intValue())));
                            break;
                            case 2:
                               totalWinnings=rs.getString(1);
                            break;
                            case 3:
                               settledBetsTurnoverRM=String.valueOf((Math.round(Double.valueOf(rs.getString(1)).intValue())));;
                            break;
                            case 4:
                               wonBetsTurnoverRM=String.valueOf((Math.round(Double.valueOf(rs.getString(1)).intValue())));;
                            break;
                            default:
                            break;
                        }                      
                    }                     
                }
                
                dataObj  = new JSONObject();
                dataObj.put("TotalTurnover", totalTurnoverRM);
                dataObj.put("TurnoverRM", totalTurnoverRM);
                dataObj.put("TurnoverBM", totalTurnoverBM);
                dataObj.put("TotalOpenBetsRM", totalOpenBetsRMTurnOver);
                dataObj.put("TotalOpenBetsBM", totalOpenBetsBMTurnOver);
                dataObj.put("TotalWinnings", totalWinnings);
                System.out.println("totalTurnoverRM(GGR)>>>"+totalTurnoverRM+">>>totalOpenBetsRMTurnOver>>>"+totalOpenBetsRMTurnOver+">>>totalWinnings>>>"+totalWinnings);
                System.out.println("NGR>>>"+Integer.valueOf(totalTurnoverRM) +">>>"+ Integer.valueOf(totalOpenBetsRMTurnOver)+">>>"+Integer.valueOf(totalWinnings));
                GGR = Integer.valueOf(totalTurnoverRM) ;
                NGR =Integer.valueOf(totalTurnoverRM) - (Integer.valueOf(totalOpenBetsRMTurnOver) + Integer.valueOf(totalWinnings));
                Profit=Integer.valueOf(settledBetsTurnoverRM)-Integer.valueOf(wonBetsTurnoverRM);
                Loss=Integer.valueOf(totalWinnings);
                dataObj.put("GGR", String.valueOf(GGR));
                dataObj.put("NGR", String.valueOf(NGR));
                dataObj.put("Profit",String.valueOf(Profit));
                dataObj.put("Loss", String.valueOf(Loss));
                
                main.put("Profit_Summary", dataObj);
                
                
                
                System.out.println("registrations==="+registrations);
                rs = stmt.executeQuery(registrations); 
                while (rs.next()) 
                {
                    SMS_Reg=rs.getString(1);
                    USSD_Reg=rs.getString(2);
                    Web_Reg=rs.getString(3);
                    App_Reg=rs.getString(4);
                }
                dataObj  = new JSONObject();
                dataObj.put("SMS_Reg", SMS_Reg);
                dataObj.put("USSD_Reg", USSD_Reg);
                dataObj.put("Web_Reg", Web_Reg);
                dataObj.put("App_Reg", App_Reg);
                
                main.put("Registration_Summary", dataObj);
                
                
                
                
                System.out.println("dataQueryBetsByStatus==="+dataQueryBetsByStatus);
                rs = stmt.executeQuery(dataQueryBetsByStatus); 
                while (rs.next()) 
                {
                    if (rs.getInt(1) == 201) 
                    {
                        placedBets = rs.getString(2);
                    }
                    else if(rs.getInt(1) == 202) 
                    {
                        wonBets = rs.getString(2);
                    }
                    else if (rs.getInt(1) == 203) 
                    {
                        lostBets = rs.getString(2);
                    }
                    else if (rs.getInt(1) == 204) 
                    {
                        rejectedBets = rs.getString(2);
                    }
                    else if (rs.getInt(1) == 205) 
                    {
                        cancelledBets = rs.getString(2);
                    }
                    
                    settledBets = String.valueOf(Integer.valueOf(lostBets) + Integer.valueOf(wonBets));
                }
                
                
                dataObj  = new JSONObject();
                dataObj.put("Open_Bets", placedBets);
                dataObj.put("Won_Bets", wonBets);
                dataObj.put("Lost_Bets", lostBets);
                dataObj.put("Rejected_Bets", rejectedBets);
                dataObj.put("Cancelled_Bets", cancelledBets);
                dataObj.put("Settled_Bets", settledBets);
                
                main.put("BetsByStatus_Summary", dataObj);
                
                
                
                
                System.out.println("dataQueryBetsByChannel==="+dataQueryBetsByChannel);
                rs = stmt.executeQuery(dataQueryBetsByChannel); 
                while (rs.next()) 
                {
                    SMS_Bets=rs.getString(1);
                    USSD_Bets=rs.getString(2);
                    Web_Bets=rs.getString(3);
                    App_Bets=rs.getString(4);
                }
                dataObj  = new JSONObject();
                dataObj.put("SMS_Bets", SMS_Bets);
                dataObj.put("USSD_Bets", USSD_Bets);
                dataObj.put("Web_Bets", Web_Bets);
                dataObj.put("App_Bets", App_Bets);
                
                main.put("BetsByChannel_Summary", dataObj);
                
                
                
                
                System.out.println("dataQueryBetsByBetType==="+dataQueryBetsByBetType);
                rs = stmt.executeQuery(dataQueryBetsByBetType);
                while (rs.next()) 
                {
                    if(rs.getInt(1)>0)
                    {
                        switch (rs.getInt(1)) 
                        {
                            case 1:
                                Single_Bets = rs.getString(2);
                            break;
                            case 2:
                                Jackpot_Bets = rs.getString(2);
                            break;
                            case 3:
                                Multi_Bets = rs.getString(2);
                            break;
                            default:
                            break;
                        } 
                    }
                    
                }
                dataObj  = new JSONObject();
                dataObj.put("Single_Bets", Single_Bets);
                dataObj.put("Multi_Bets", Multi_Bets);
                dataObj.put("Jackpot_Bets", Jackpot_Bets);
                
                main.put("BetsByBetType_Summary", dataObj);
                
                dataArray.put(main);
              

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.print("Error...getDashboardData..." + e.getMessage());
        }

        
        return dataArray;
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
    
    public String nulldata()
    {
        String data="[\n" +
"    {\n" +
"        \"BetsByChannel_Summary\": {\n" +
"            \"USSD_Bets\": \"0\",\n" +
"            \"SMS_Bets\": \"0\",\n" +
"            \"Web_Bets\": \"0\",\n" +
"            \"App_Bets\": \"0\"\n" +
"        },\n" +
"        \"BetsByStatus_Summary\": {\n" +
"            \"Open_Bets\": \"0\",\n" +
"            \"Lost_Bets\": \"0\",\n" +
"            \"Won_Bets\": \"0\",\n" +
"            \"Cancelled_Bets\": \"0\",\n" +
"            \"Settled_Bets\": \"0\",\n" +
"            \"Rejected_Bets\": \"0\"\n" +
"        },\n" +
"        \"Registration_Summary\": {\n" +
"            \"Web_Reg\": \"0\",\n" +
"            \"USSD_Reg\": \"0\",\n" +
"            \"SMS_Reg\": \"0\",\n" +
"            \"Total_Reg\": 0,\n" +
"            \"App_Reg\": \"0\"\n" +
"        },\n" +
"        \"Profit_Summary\": {\n" +
"            \"GGR\": \"0\",\n" +
"            \"TotalWinnings\": \"0\",\n" +
"            \"TurnoverRM\": \"0\",\n" +
"            \"Loss\": \"0\",\n" +
"            \"NGR\": \"0\",\n" +
"            \"Profit\": \"0\"\n" +
"        },\n" +
"        \"Finance_Summary\": {\n" +
"            \"DepositsValue\": \"0\",\n" +
"            \"WithdrawalValue\": \"0\"\n" +
"        },\n" +
"        \"BetsByBetType_Summary\": {\n" +
"            \"Single_Bets\": \"0\",\n" +
"            \"Web_Bets\": \"0\",\n" +
"            \"Jackpot_Bets\": \"0\",\n" +
"            \"Multi_Bets\": \"0\"\n" +
"        }\n" +
"    }\n" +
"]";
        
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
