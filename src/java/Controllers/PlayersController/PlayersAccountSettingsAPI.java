package Controllers.PlayersController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.PlayersImplimentation.DormantPlayersImpl;
import Implimentation.PlayersImplimentation.PlayersRegistrationImpl;
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
@WebServlet(urlPatterns = {"/player/account/settings"})
public class PlayersAccountSettingsAPI extends HttpServlet {

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

            System.out.println("getPlayerAccountNarration==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            maindata=jsonobj.getString("data");

            String []data=maindata.split("#");
            String mobile=data[0];
            String mobile_no;

            if(mobile.startsWith("07") || mobile.startsWith("01"))
            {
                mobile_no="254"+mobile.substring(1);
            }
            else
            {
                mobile_no=mobile;
            }

            responseObj=new PlayersRegistrationImpl().getDeactivationNarration(mobile_no);
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
            
            System.out.println("updatePlayerAccount==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            String action=jsonobj.getString("action");
            maindata=jsonobj.getString("data");

            if(action.equals("deactivate"))
            {
                String []data=maindata.split("#");
                String mobile=data[0];
                String narration=data[1];
                String deactivatedBy=data[2];
                String mobile_no;

                if(mobile.startsWith("07") || mobile.startsWith("01"))
                {
                    mobile_no="254"+mobile.substring(1);
                }
                else
                {
                    mobile_no=mobile;
                }
                responseObj=new PlayersRegistrationImpl().setDeactivatePlayer(mobile_no,narration,deactivatedBy);  
            }
            if(action.equals("activate"))
            {
                String []data=maindata.split("#");
                String mobile=data[0];
                String narration=data[1];
                String activatedBy=data[2];
                String mobile_no;

                if(mobile.startsWith("07") || mobile.startsWith("01"))
                {
                    mobile_no="254"+mobile.substring(1);
                }
                else
                {
                    mobile_no=mobile;
                }
                responseObj=new PlayersRegistrationImpl().setActivatePlayer(mobile_no,activatedBy);
            }
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
        out.print(responseObj);
    }

}
