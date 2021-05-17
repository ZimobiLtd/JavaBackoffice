package Players.PlayersAPIs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Players.PlayersProcessor.BettingReportProcessor;
import Players.PlayersProcessor.PlayerTransactionsProcessor;
import Utility.Utility;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author jac
 */
@WebServlet(urlPatterns = {"/BettingReport"})
public class BettingReportAPI extends HttpServlet {

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
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        resp.setContentType("text/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST");


        StringBuilder jb = new StringBuilder();
        String line = null;

        try 
        {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
            {
                jb.append(line);
            }

            System.out.println("getBettingReportData===="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            function=jsonobj.getString("function");
            maindata=jsonobj.getString("data");


            if(function.equals("getBettingReport"))
            {
                String []respo=new Utility().getDatesRange(0);
                String fromdate=respo[0];
                String todate=respo[1];
                responseobj=new BettingReportProcessor().getAllBettingReport(fromdate ,todate);
            }


            if(function.equals("filterBettingReport"))
            {
                String[]data=maindata.split("#");
                String fromdate=data[0];
                String todate=data[1];  
                responseobj=new BettingReportProcessor().getAllBettingReport(fromdate ,todate);
            }


            if(function.equals("getPlayerBettingReport"))
            {
                String[]data=maindata.split("#");
                String player_mobile=data[0];
                String []respo=new Utility().getDatesRange(0);
                String fromdate=respo[0];
                String todate=respo[1]; 

                if(player_mobile.startsWith("07") || player_mobile.startsWith("01"))
                {
                   player_mobile="254"+player_mobile.substring(1);
                }

                responseobj=new BettingReportProcessor().getPlayerBettingReport("","",player_mobile);
            }


            if(function.equals("getPlayerBettingReportByMobile"))
            {
                String mobile=maindata;
                if(mobile.startsWith("07") || mobile.startsWith("01"))
                {
                   mobile="254"+mobile.substring(1);
                }
                responseobj=new BettingReportProcessor().getPlayerBettingReportByMobile(mobile);
            }


            if(function.equals("getPlayerBettingReportByBetSlipID"))
            {
                String betSlip=maindata;
                responseobj=new BettingReportProcessor().getBettingReportByBetslipID(betSlip);
            }


            if(function.equals("filterPlayerBettingReport"))
            {
                String[]data=maindata.split("#");
                String player_mobile=data[0];
                String from=data[1];
                String to=data[2];  

                if(player_mobile.startsWith("07") || player_mobile.startsWith("01"))
                {
                   player_mobile="254"+player_mobile.substring(1);
                }

                responseobj=new BettingReportProcessor().getPlayerBettingReport(from,to,player_mobile);
            }


            if(function.equals("getPlayerTransactionsReport"))
            {
                String[]data=maindata.split("#");
                String player_mobile=data[0];

                String []respo=new Utility().getDatesRange(0);
                String fromdate=respo[0];
                String todate=respo[1];

                if(player_mobile.startsWith("07") || player_mobile.startsWith("01"))
                {
                   player_mobile="254"+player_mobile.substring(1);
                }

                responseobj=new PlayerTransactionsProcessor().getPlayerTransactions(fromdate,todate,player_mobile);
            }

            if(function.equals("filterPlayerTransactionsReport"))
            {   
                String[]data=maindata.split("#");
                String from=data[0];
                String to=data[1];
                String transtype=data[2];
                String transstatus=data[3]; 
                String player_mobile=data[4];
                if(player_mobile.startsWith("07") || player_mobile.startsWith("01"))
                {
                   player_mobile="254"+player_mobile.substring(1);
                }
                String trans_type="",trans_status="";
                //Acc_Trans_Type =1 then 'Deposit' when Acc_Trans_Type=2 then 'User Withdrawal'  when Acc_Trans_Type=8 then 'Withdrawal Charge' 
                //when Acc_Trans_Type=3 then 'Bet WIN' when Acc_Trans_Type=4 then 'Bet Withdrawal'  end) as 'Trans_Type'
                if(transtype.equals("Deposit"))//Deposit,User Withdrawal,Withdrawal Charge,GoldenRace Bet Withdrawal,Bet Withdrawal
                {
                    trans_type="1";
                }
                else if(transtype.equals("User Withdrawal"))
                {
                    trans_type="2";
                }
                else if(transtype.equals("Withdrawal Charge"))
                {
                    trans_type="8";
                }
                else if(transtype.equals("GoldenRace Bet Withdrawal"))
                {
                    trans_type="3";
                }
                else if(transtype.equals("Bet Withdrawal"))
                {
                    trans_type="4";
                }
                else
                {
                   trans_type="All"; 
                }


                if(transstatus.equalsIgnoreCase("Processed"))
                {
                    trans_status="0";
                }
                else if(transstatus.equalsIgnoreCase("Pending"))
                {
                    trans_status="1";
                }
                else if(transstatus.equalsIgnoreCase("Failed"))
                {
                    trans_status="2";
                }
                else
                {
                   trans_status="All"; 
                }




                if(trans_type.equals("All") && trans_status.equals("All"))
                {
                    responseobj=new PlayerTransactionsProcessor().getPlayerTransactions(from,to,player_mobile);
                }
                else if(!trans_type.equals("All") && trans_status.equals("All"))
                {
                    trans_type="Acc_Trans_Type="+trans_type;
                    trans_status="Acc_Status in (0,1,2,3,4,8)";
                    responseobj=new PlayerTransactionsProcessor().filterPlayerTransactions(from,to,trans_type,trans_status,player_mobile);
                }
                else if(trans_type.equals("All") && !trans_status.equals("All"))
                {
                    trans_type="Acc_Trans_Type in(0,1,2,3,4,8)";
                    trans_status="Acc_Status ="+trans_status;
                    responseobj=new PlayerTransactionsProcessor().filterPlayerTransactions(from,to,trans_type,trans_status,player_mobile);
                }
                else
                {
                    trans_type="Acc_Trans_Type ="+trans_type;
                    trans_status="Acc_Status ="+trans_status;
                    responseobj=new PlayerTransactionsProcessor().filterPlayerTransactions(from,to,trans_type,trans_status,player_mobile);
                }

            }

        }catch (IOException | JSONException ex) { ex.getMessage();}

    PrintWriter out = resp.getWriter(); 
    out.print(responseobj);
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
