package Controllers.TransactionsController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.TransactionsImplimentation.PlayerAccountTransactionsImpl;
import Utility.Utility;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(urlPatterns = {"/transactions/playeraccount"})
public class PlayerAccountTransactionsAPI extends HttpServlet {

    private String maindata;
    private JSONObject jsonobj=null;JSONArray responseObj  = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException, JSONException 
    {
        String method = req.getMethod();
        switch (method) 
        {
            case "METHOD_POST":
                doPost(req, resp);
                break;
            default:
                String errMsg = "Method Not Supported";
                resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);
                break;
        }
    }
    
    
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException 
    {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = resp.getWriter(); 

        StringBuilder jb = new StringBuilder();
        String line = null;

        try 
        {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
            {
                jb.append(line);
            }

            System.out.println("filterPlayerTransactionsReport==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            String searchKey=jsonobj.getString("search_key");
            maindata=jsonobj.getString("data");

            switch (searchKey) 
            {
                case "mobile":
                    String[]info=maindata.split("#");
                    String player_mobile_no=info[0];
                    String []respo=new Utility().getDatesRange(-30);
                    String fromdate=respo[0];
                    String todate=respo[1];

                    if(player_mobile_no.startsWith("07") || player_mobile_no.startsWith("01"))
                    {
                       player_mobile_no="254"+player_mobile_no.substring(1);
                    }
                    responseObj=new PlayerAccountTransactionsImpl().getPlayerTransactions(fromdate,todate,player_mobile_no); 
                    break;
                case "filters":
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
                    if(transtype.equals("Deposit"))
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
                        responseObj=new PlayerAccountTransactionsImpl().getPlayerTransactions(from,to,player_mobile);
                    }
                    else if(!trans_type.equals("All") && trans_status.equals("All"))
                    {
                        trans_type="Acc_Trans_Type="+trans_type;
                        trans_status="Acc_Status in (0,1,2,3,4,8)";
                        responseObj=new PlayerAccountTransactionsImpl().filterPlayerTransactions(from,to,trans_type,trans_status,player_mobile);
                    }
                    else if(trans_type.equals("All") && !trans_status.equals("All"))
                    {
                        trans_type="Acc_Trans_Type in(0,1,2,3,4,8)";
                        trans_status="Acc_Status ="+trans_status;
                        responseObj=new PlayerAccountTransactionsImpl().filterPlayerTransactions(from,to,trans_type,trans_status,player_mobile);
                    }
                    else
                    {
                        trans_type="Acc_Trans_Type ="+trans_type;
                        trans_status="Acc_Status ="+trans_status;
                        responseObj=new PlayerAccountTransactionsImpl().filterPlayerTransactions(from,to,trans_type,trans_status,player_mobile);
                    }
                    break;
               
                default:
                    JSONObject dataObj  = new JSONObject();
                    JSONArray dataArray = new JSONArray();
                    dataObj.put("message", "Invalid search key");
                    responseObj=dataArray.put(dataObj);
                    break;
            }
            
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
        out.print(responseObj);
    }

}
