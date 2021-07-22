package Controllers.BettingReportController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.BettingReportImplimentation.BettingReportImpl;
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
@WebServlet(urlPatterns = {"/betting/player/report"})
public class PlayerBettingReportAPI extends HttpServlet {

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

            System.out.println("filterPlayerReport==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            String searchKey=jsonobj.getString("search_key");
            maindata=jsonobj.getString("data");

            switch (searchKey) 
            {
                case "mobile":
                    String mobile=maindata;
                    if(mobile.startsWith("07") || mobile.startsWith("01"))
                    {
                        mobile="254"+mobile.substring(3);
                    }   responseObj=new BettingReportImpl().getPlayerBettingReportByMobile(mobile);
                    break;
                case "betslip":
                    String betSlip=maindata;
                    responseObj=new BettingReportImpl().getBettingReportByBetslipID(betSlip);
                    break;
                case "mobile_date":
                    String[]data=maindata.split("#");
                    String player_mobile=data[0];
                    String from=data[1];
                    String to=data[2];
                    if(player_mobile.startsWith("07") || player_mobile.startsWith("01"))
                    {
                        player_mobile="254"+player_mobile.substring(1);
                    }   responseObj=new BettingReportImpl().getPlayerBettingReport(from,to,player_mobile);
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
