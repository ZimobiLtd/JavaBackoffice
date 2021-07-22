package Controllers.GamingController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.GamingImplimentation.GamesExposerImpl;
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
@WebServlet(urlPatterns = {"/games/exposure"})
public class GamesExposerAPI extends HttpServlet {

    
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

            System.out.println("getGamesExposure===="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            maindata=jsonobj.getString("data");

            String []respo=new Utility().getDatesRange(-7);
            String fromdate=respo[0];
            String todate=respo[1];
            responseObj=new GamesExposerImpl().getPlayersLiability(fromdate,todate);
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
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

            System.out.println("filterGamesExposure===="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            maindata=jsonobj.getString("data");

            String[]data=maindata.split("#");
            String fromdate=data[0];
            String todate=data[1];
            responseObj=new GamesExposerImpl().getPlayersLiability(fromdate,todate);
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
        out.print(responseObj);
    }
}
