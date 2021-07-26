package Controllers.SportingController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.SportingImplimentation.EventsImpl;
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
@WebServlet(urlPatterns = {"/sporting/events"})
public class EventsAPI extends HttpServlet {

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

        System.out.println("getEvents===");

        String []respo=new Utility().getDatesRange(0);
        String fromdate=respo[0];
        String todate=respo[1];
        responseObj=new EventsImpl().getEvents(fromdate ,todate);
        
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
            
            System.out.println("filterEvents==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            maindata=jsonobj.getString("data");

            String filters="";
            String[]data=maindata.split("#");
            String from=data[0];
            String to=data[1];  
            String sport=data[2].trim();
            String tornament=data[3].trim();

            if (sport.equalsIgnoreCase("any")&& tornament.equalsIgnoreCase("any"))
            {
              responseObj=new EventsImpl().getEvents(from ,to);  
            }
            else if (sport.equalsIgnoreCase("any")&& !tornament.equalsIgnoreCase("any"))
            {
                filters = " and Torna_Name like '" + tornament + "%' ";
                responseObj=new EventsImpl().filterEvents(from ,to,filters);
            }
            else if (!sport.equalsIgnoreCase("any")&& tornament.equalsIgnoreCase("any"))
            {
                filters = " and Torna_Sport_Name like '" + sport + "%' ";
                responseObj=new EventsImpl().filterEvents(from ,to,filters);
            }
            else
            {
                if (!sport.equalsIgnoreCase("null")&& !tornament.equalsIgnoreCase("null")) 
                {
                    filters = " and Torna_Name like '" + tornament + "%' and Torna_Sport_Name like '" + sport + "%' ";
                }
                else if (!sport.equalsIgnoreCase("null")&& tornament.equalsIgnoreCase("null") ) 
                {
                    filters = " and Torna_Sport_Name like '" + sport + "%' ";
                }
                else if (sport.equalsIgnoreCase("null")&& !tornament.equalsIgnoreCase("null") ) 
                {
                    filters = " and Torna_Name like '" + tornament + "%' ";
                }

                responseObj=new EventsImpl().filterEvents(from ,to,filters);
            } 
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
        out.print(responseObj);
    }

}
