package Controllers.SystemSettings;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.SystemSettingsImplimentation.SystemUsersImpl;
import Utility.EmailSender;
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
@WebServlet(urlPatterns = {"/system/users"})
public class SystemUsersAPI extends HttpServlet {

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
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
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
            case "METHOD_PUT":
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

        System.out.println("getSystemUsers===");
            
        responseObj=new SystemUsersImpl().getUsers("");
        
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
            
            System.out.println("getSystemUsers==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            String action=jsonobj.getString("action");
            maindata=jsonobj.getString("data");

            if(action.equals("get_user"))
            {
                String username=maindata;
                responseObj=new SystemUsersImpl().getUsers(username);
            }
            else if(action.equals("add_user"))
            {
                String []data=maindata.split("#");
                String username=data[0];
                String emailaddress=data[1];
                String phoneNumber=data[2];
                String firstname=data[3];
                String lastname=data[4];
                String password=data[5];
                int createdBy=Integer.valueOf(data[6]);
                int modifiedBy=Integer.valueOf(data[7]);
                int userStatus=2;//Integer.valueOf(data[8]);
                int isactive=Integer.valueOf(data[9]);
                String role=data[10];

                String userMobile="0";
                if(phoneNumber.startsWith("07") || phoneNumber.startsWith("01"))
                {
                   userMobile="254"+phoneNumber.substring(1);
                }
                else
                {
                    userMobile=phoneNumber;
                }
                
                int status=new SystemUsersImpl().validateUser(username,emailaddress);
                if(status==0)
                {
                    int respo_id=new SystemUsersImpl().saveSystemUser(username,emailaddress,userMobile,firstname,lastname,password,createdBy,modifiedBy,userStatus,isactive,role) ;
                    JSONObject dataObj  = new JSONObject();
                    JSONArray dataArray = new JSONArray();
                    if(respo_id>0)
                    {
                        Runnable myrunnable=() -> {
                            String text="Welcome to Starbet Back Office: Username:"+username+" Password:"+password+"<br><br>"+
                                        "Regards,<br>" +
                                        "StartBet Team";
                            new EmailSender().postEmail(emailaddress,text);
                        };
                        new Thread(myrunnable).start();

                        dataObj.put("message", "User created successfully"); 
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
                    JSONObject dataObj  = new JSONObject();
                    JSONArray dataArray = new JSONArray();
                    dataObj.put("error", "user exist");
                    dataArray.put(dataObj);
                    resp.setStatus(500);
                    responseObj=dataArray; 
                }
            }
            else if(action.equals("update_user"))
            {
                String []data=maindata.split("#");
                String username=data[0];
                String emailaddress=data[1];
                String phoneNumber=data[2];
                String firstname=data[3];
                String lastname=data[4];
                String password=data[5];
                int createdBy=Integer.valueOf(data[6]);
                int modifiedBy=Integer.valueOf(data[7]);
                int userStatus=Integer.valueOf(data[8]);
                int isactive=Integer.valueOf(data[9]);
                String role=data[10];

                int status=new SystemUsersImpl().validateUser(username,emailaddress);
                if(status==0)
                {
                    int respo_id=new SystemUsersImpl().updateSystemUser(username,emailaddress,phoneNumber,firstname,lastname,modifiedBy,userStatus,isactive,role) ;
                    JSONObject dataObj  = new JSONObject();
                    JSONArray dataArray = new JSONArray();
                    if(respo_id>0)
                    {
                        Runnable myrunnable=() -> {
                            String text="Welcome to Starbet Back Office: Username:"+username+" Password:"+password+"<br><br>"+
                                        "Regards,<br>" +
                                        "StartBet Team";
                            new EmailSender().postEmail(emailaddress,text);
                        };
                        new Thread(myrunnable).start();

                        dataObj.put("message", "User created successfully"); 
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
                    JSONObject dataObj  = new JSONObject();
                    JSONArray dataArray = new JSONArray();
                    dataObj.put("error", "user exist");
                    dataArray.put(dataObj);
                    resp.setStatus(500);
                    responseObj=dataArray; 
                }
            }
            else if(action.equals("update_user_access"))
            {
                String []data=maindata.split("#");
                String phoneNumber=data[0];
                int userStatus=Integer.valueOf(data[1]);
                String msg="";
                if(userStatus==0)
                {
                   msg="User deactivated" ;
                }
                else if(userStatus==1)
                {
                   msg="User activated" ;
                }
                
                String userMobile="0";
                if(phoneNumber.startsWith("07") || phoneNumber.startsWith("01"))
                {
                   userMobile="254"+phoneNumber.substring(1);
                }
                else
                {
                    userMobile=phoneNumber;
                }

                int respo_id=new SystemUsersImpl().updateUserAccess(userMobile,userStatus) ;
                JSONObject dataObj;
                JSONArray dataArray = new JSONArray();
                if(respo_id>0)
                {
                    dataObj  = new JSONObject();
                    dataObj.put("message", msg); 
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
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
        out.print(responseObj);
    }
    
   
}
