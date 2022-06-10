package Controllers.TransactionsController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.TransactionsImplimentation.SlotGamesTransactionsProcessorImpl;
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
@WebServlet(urlPatterns = {"/transactions/slotgames"})
public class SlotGamesTransactionsAPI extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private String maindata;
    private JSONObject jsonobj=null;JSONArray responseObj  = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException, JSONException 
    {
        String method = req.getMethod();
        switch (method) 
        {
            case "METHOD_GET":
                doGet(req, resp);
                break;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException 
    {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = resp.getWriter(); 

        String []respo=new Utility().getDatesRange(-2);
        String fromdate=respo[0];
        String todate=respo[1];
        responseObj=new SlotGamesTransactionsProcessorImpl().getSlotGamesTransactions(fromdate,todate,"0");
        
        out.print(responseObj);
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
            
            jsonobj = new JSONObject(jb.toString());
            maindata=jsonobj.getString("data");

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

            String trans_type,trans_status;
            
            if(transtype.equals("Bet"))
            {
                trans_type="'bet'";
            }
            else if(transtype.equals("Bet Win"))
            {
                trans_type="'win'";
            }
            else if(transtype.equals("Cancel Bet"))
            {
                trans_type="'cancelbet'";
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
                responseObj=new SlotGamesTransactionsProcessorImpl().getSlotGamesTransactions(from,to,player_mobile);
            }
            else if(!trans_type.equals("All") && trans_status.equals("All"))
            {
                trans_type="Golden_Race_Trans_Type="+trans_type;
                trans_status="Acc_Status in (0,1,2)";
                responseObj=new SlotGamesTransactionsProcessorImpl().filterSlotGamesTransactions(from,to,trans_type,trans_status,player_mobile);
            }
            else if(trans_type.equals("All") && !trans_status.equals("All"))
            {
                trans_type="Golden_Race_Trans_Type in('bet','win','cancelbet')";
                trans_status="Acc_Status ="+trans_status;
                responseObj=new SlotGamesTransactionsProcessorImpl().filterSlotGamesTransactions(from,to,trans_type,trans_status,player_mobile);
            }
            else
            {
                trans_type="Golden_Race_Trans_Type ="+trans_type;
                trans_status="Acc_Status ="+trans_status;
                responseObj=new SlotGamesTransactionsProcessorImpl().filterSlotGamesTransactions(from,to,trans_type,trans_status,player_mobile);
            }
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
        out.print(responseObj);
    }

}
