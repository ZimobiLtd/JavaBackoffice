package Controllers.PlayersController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.PlayersImplimentation.DormantPlayersImpl;
import Implimentation.PlayersImplimentation.PlayerPasswordImpl;
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
@WebServlet(urlPatterns = {"/player/password"})
public class PlayerPasswordAPI extends HttpServlet {

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

        StringBuilder jb = new StringBuilder();
        String line = null;

        try 
        {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
            {
                jb.append(line);
            }
            
            System.out.println("getPlayerPassword==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            maindata=jsonobj.getString("data");
            String mobile=maindata;
                
            if(mobile.startsWith("07") || mobile.startsWith("01"))
            {
               mobile="254"+mobile.substring(1);
            }
            String newMobile=mobile;

            String password=new PlayerPasswordImpl().getPlayerPassword(newMobile);
            JSONObject dataObj  = new JSONObject();
            JSONArray dataArray = new JSONArray();
            dataObj.put("message", "PLAYER PASSWORD: "+password);
            dataArray.put(dataObj);
            responseObj=dataArray;
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
        out.print(responseObj);
    }
    
    

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
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

            System.out.println("resetPlayerPassword==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            maindata=jsonobj.getString("data");

            String mobile=maindata;
            String pin=new Utility().generateCode(4);

            if(mobile.startsWith("07") || mobile.startsWith("01"))
            {
               mobile="254"+mobile.substring(1);
            }
            String newMobile=mobile;
            int status=new PlayerPasswordImpl().updatePlayerPassword(mobile,pin);
            String sms="Dear customer, your new StarBet password is: "+pin+".Visit www.starbet.co.ke. Call: 0709758000 or Whatsapp 0114029659 for Assistance ";
            if(status== 200)
            {
                Thread thread=new Thread(() -> {
                    new Utility().sendSMS(newMobile,sms);
                });
                thread.start();

                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();
                dataObj.put("message", "Password reset successful");
                dataArray.put(dataObj);
                responseObj=dataArray;
            }
            else
            {
                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();
                dataObj.put("error", "Password reset failed");
                dataArray.put(dataObj);
                responseObj=dataArray;
            }
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
        out.print(responseObj);
    }
    
    
    

}
