package Controllers.SystemSettings;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.SystemSettingsImplimentation.SettingsImpl;
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
@WebServlet(urlPatterns = {"/system/settings"})
public class SystemSettingsAPI extends HttpServlet {

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

        responseObj=new SettingsImpl().getSystemSettings();
        
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
            String action=jsonobj.getString("action");
            maindata=jsonobj.getString("data");

            if(action.equals("add_setting"))
            {
                System.out.println("filterSystemSettingsByID==="+jb.toString());
                String id=maindata;
                responseObj=new SettingsImpl().getSystemSettingsByID(id);
            }
            else if(action.equals("edit_setting"))
            {
                JSONObject dataObj;
                JSONArray dataArray = new JSONArray();

                String []data=maindata.split("#");
                String name=data[0];
                String value=data[1];
                String description=data[2];
                String createdby=data[3];
                String modifiedby=data[4];

                int status=new SettingsImpl().validateSettings(name);
                if(status==0)
                {
                    int respo_id=new SettingsImpl().saveSystemSettings(name,value,description,createdby,modifiedby) ;
                    if(respo_id>0)
                    {
                        dataObj  = new JSONObject();
                        dataObj.put("message", "Setting saved successfully"); 
                        dataArray.put(dataObj);
                        resp.setStatus(200);
                        responseObj=dataArray;
                    }
                    else
                    {
                        dataObj  = new JSONObject();
                        dataObj.put("error", "request failed");
                        dataArray.put(dataObj);
                        resp.setStatus(500);
                        responseObj=dataArray;                            
                    }
                }
                else
                {
                    dataObj  = new JSONObject();
                    dataObj.put("error", "setting already exist");
                    dataArray.put(dataObj);
                    resp.setStatus(500);
                    responseObj=dataArray;
                }
            }
            else if(action.equals("edit_setting"))
            {
                JSONObject dataObj;
                JSONArray dataArray = new JSONArray();

                String []data=maindata.split("#");
                String id=data[0];
                String name=data[1];
                String value=data[2];
                String description=data[3];
                String modifiedby=data[4];

                int respo_id=new SettingsImpl().editSystemSettings(id,name,value,description,modifiedby) ;
                if(respo_id>0)
                {
                    dataObj  = new JSONObject();
                    dataObj.put("message", "Setting changed successfully"); 
                    dataArray.put(dataObj);
                    resp.setStatus(200);
                    responseObj=dataArray;
                }
                else
                {
                    dataObj  = new JSONObject();
                    dataObj.put("error", "request failed");
                    dataArray.put(dataObj);
                    resp.setStatus(500);
                } 
            }
            
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
        out.print(responseObj);
    }

}
