package SystemSetting.APIs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import SystemSettings.SystemProcessor.SettingsProcessor;
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
@WebServlet(urlPatterns = {"/SystemSettings"})
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
        Connection conn;
        String response,username ,password,function,maindata;
        JSONObject jsonobj=null;JSONArray responseobj  = null;
        public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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

            System.out.println("systemSettingsData===="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            function=jsonobj.getString("function");
            maindata=jsonobj.getString("data");


            if(function.equals("getRoles"))
            {
                responseobj=new SettingsProcessor().getSecurityRoles();
            }


            if(function.equals("getUserRoles"))
            {
                String username=maindata;
                responseobj=new SettingsProcessor().getUserRoles(username);
            }

            if(function.equals("getSystemSettings"))
            {
                responseobj=new SettingsProcessor().getSystemSettings();
            }

            if(function.equals("viewSystemSettings"))
            {
                String id=maindata;
                responseobj=new SettingsProcessor().getSystemSettingsByID(id);
            }

            if(function.equals("saveSystemSettings"))
            {
                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();

                String []data=maindata.split("#");
                String name=data[0];
                String value=data[1];
                String description=data[2];
                String createdby=data[3];
                String modifiedby=data[4];

                int status=new SettingsProcessor().validateSettings(name);
                if(status==0)
                {
                    int respo_id=new SettingsProcessor().saveSystemSettings(name,value,description,createdby,modifiedby) ;
                    if(respo_id>0)
                    {
                        dataObj  = new JSONObject();
                        dataObj.put("message", "Setting saved successfully"); 
                        dataArray.put(dataObj);
                        resp.setStatus(200);
                        responseobj=dataArray;
                    }
                    else
                    {
                        dataObj  = new JSONObject();
                        dataObj.put("error", "request failed");
                        dataArray.put(dataObj);
                        resp.setStatus(500);
                        responseobj=dataArray;                            
                    }
                }
                else
                {
                    dataObj  = new JSONObject();
                    dataObj.put("error", "setting already exist");
                    dataArray.put(dataObj);
                    resp.setStatus(500);
                    responseobj=dataArray;
                }
            }

            if(function.equals("editSystemSettings"))
            {
                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();

                String []data=maindata.split("#");
                String id=data[0];
                String name=data[1];
                String value=data[2];
                String description=data[3];
                String modifiedby=data[4];

                /*int status=validateSettings(name);
                if(status==0)
                {*/
                    int respo_id=new SettingsProcessor().editSystemSettings(id,name,value,description,modifiedby) ;
                    if(respo_id>0)
                    {
                        dataObj  = new JSONObject();
                        dataObj.put("message", "Setting changed successfully"); 
                        dataArray.put(dataObj);
                        resp.setStatus(200);
                        responseobj=dataArray;
                    }
                    else
                    {
                        dataObj  = new JSONObject();
                        dataObj.put("error", "request failed");
                        dataArray.put(dataObj);
                        resp.setStatus(500);
                    }
                /*}
                else
                {
                    dataObj  = new JSONObject();
                    dataObj.put("error", "setting already exist");
                    dataArray.put(dataObj);
                    resp.setStatus(500);
                    responseobj=dataArray;
                } */   
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
