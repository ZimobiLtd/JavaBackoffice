package Controllers.SportingController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.SportingImplimentation.LeaguesImpl;
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
@WebServlet(urlPatterns = {"/sporting/leagues"})
public class LeaguesAPI extends HttpServlet {

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

        System.out.println("getLeagues===");

        responseObj=new LeaguesImpl().getLeagues();
        
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
            
            System.out.println("setLeaguesStatus==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            maindata=jsonobj.getString("data");

            String[]data=maindata.split("#");
            String leagueID=data[0];
            String leagueStatus=data[1];  
            
            JSONObject dataObj;
            JSONArray dataArray = new JSONArray();
            int status=new LeaguesImpl().updateLeagueStatus(leagueID, leagueStatus);
            if(status == 200)
            {
                dataObj  = new JSONObject();
                dataObj.put("message", "League status changed"); 
                dataArray.put(dataObj);
                resp.setStatus(200);
                responseObj=dataArray;
            }
            else
            {
                dataObj  = new JSONObject();
                dataObj.put("error", "League status change failed"); 
                dataArray.put(dataObj);
                resp.setStatus(500);
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
