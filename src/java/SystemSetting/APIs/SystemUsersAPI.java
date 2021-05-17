package SystemSetting.APIs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Utility.EmailSender;
import SystemSettings.SystemProcessor.SystemUsersProcessor;
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
@WebServlet(urlPatterns = {"/SystemUsers"})
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

            System.out.println("systemUsersData===="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            function=jsonobj.getString("function");
            maindata=jsonobj.getString("data");


            if(function.equals("getSystemUsers"))
            {
                 String []respo=new Utility().getDatesRange(-5);
                 responseobj=new SystemUsersProcessor().getUsers("");
            }


            if(function.equals("filterSystemUsers"))
            {
                String username=maindata;
                responseobj=new SystemUsersProcessor().getUsers(username);
            }

            if(function.equals("getUser"))
            {
                String username=maindata;
                responseobj=new SystemUsersProcessor().getUser(username);  
            }

            if(function.equals("addUser"))
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

                int status=new SystemUsersProcessor().validateUser(username,emailaddress);
                if(status==0)
                {
                    int respo_id=new SystemUsersProcessor().saveSystemUser(username,emailaddress,phoneNumber,firstname,lastname,password,createdBy,modifiedBy,userStatus,isactive,role) ;
                    JSONObject dataObj  = new JSONObject();
                    JSONArray dataArray = new JSONArray();
                    if(respo_id>0)
                    {
                        Runnable myrunnable=new Runnable()
                        {
                            public void run()
                            {
                                String txt="Welcome to Starbet Back Office: Username:"+username+" Password:"+password+"\n\n"+
                                 "Regards,\n" +
                                 "StartBet Team";
                                new EmailSender().sendEmail(txt,emailaddress, "User Account");
                            }
                        };
                        new Thread(myrunnable).start();

                        dataObj  = new JSONObject();
                        dataObj.put("message", "User created successfully"); 
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
                    JSONObject dataObj  = new JSONObject();
                    JSONArray dataArray = new JSONArray();
                    dataObj  = new JSONObject();
                    dataObj.put("error", "user exist");
                    dataArray.put(dataObj);
                    resp.setStatus(500);
                    responseobj=dataArray; 
                }


           }

           if(function.equals("updateUser"))
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


                int respo_id=new SystemUsersProcessor().updateSystemUser( username,emailaddress,phoneNumber,firstname,lastname,modifiedBy,userStatus,isactive,role) ;
                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();
                if(respo_id>0)
                {
                    dataObj  = new JSONObject();
                    dataObj.put("message", "User updated successfully"); 
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


            if(function.equals("updateUserAccess"))
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


                int respo_id=new SystemUsersProcessor().updateUserAccess(phoneNumber,userStatus) ;
                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();
                if(respo_id>0)
                {
                    dataObj  = new JSONObject();
                    dataObj.put("message", msg); 
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


            if(function.equals("sendResetPasswordInit"))
            {
                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();
                String email=maindata;

                int email_status=new SystemUsersProcessor().validateEmail(email);
                if(email_status==1)
                {
                     String code=new Utility().generateCode(7);
                     String link="We received a request to reset the password on your StarBet Back Office Account.\nKindly click on the link below.\n\n"
                             + "http://167.86.68.102:7074/resetPass/"+code+"\n\n" +
                              "Regards,\n" +
                              "StartBet Team";
                     int status=new SystemUsersProcessor().saveCode(email, code);


                      if(status>0)
                      {
                          dataObj  = new JSONObject();
                          dataObj.put("message", "Password reset link has been sent to your email"); 
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
                      Runnable myrunnable=new Runnable()
                      {
                          public void run()
                          {
                              new EmailSender().sendEmail(link,email, "Reset Password Link");
                          }
                      };
                      new Thread(myrunnable).start();
                }
                else
                {
                     dataObj  = new JSONObject();
                     dataObj.put("error", "Invalid email address");
                     dataArray.put(dataObj);
                     resp.setStatus(500);
                     responseobj=dataArray;  
                }

            }



            if(function.equals("validateCode"))
            {
                String []data=maindata.split("#");
                String code=data[0];

                int status=new SystemUsersProcessor().verifyCode(code) ;
                if(status==1)
                {
                    JSONObject dataObj  = new JSONObject();
                    JSONArray dataArray = new JSONArray();
                    dataObj  = new JSONObject();
                    dataObj.put(",message", "Valid code");
                    dataArray.put(dataObj);
                    resp.setStatus(500);
                    responseobj=dataArray;   
                }
                else
                {
                    JSONObject dataObj  = new JSONObject();
                    JSONArray dataArray = new JSONArray();

                    dataObj  = new JSONObject();
                    dataObj.put("error", "Invalid code");
                    dataArray.put(dataObj);
                    resp.setStatus(500);
                    responseobj=dataArray;   
                }
            }



            if(function.equals("resetPassword"))
            {
                String []data=maindata.split("#");
                String email=data[0];
                //String new_pass=data[1];
                //String email=getCodeEmail(code);
                String new_pass=new Utility().generateCode(7);

                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();

                int respo=new SystemUsersProcessor().changePassword(email,new_pass,0);
                if(respo==200)
                {
                    dataObj  = new JSONObject();
                    dataObj.put("message", "Password changed successfully"); 
                    dataArray.put(dataObj);
                    resp.setStatus(200);
                    responseobj=dataArray;

                    Runnable myrunnable=new Runnable()
                    {
                        public void run()
                        {
                            String txt="Hi,\nYour starbet password was changed.New password is: "+new_pass+"\n\n"+
                             "Regards,\n" +
                             "StartBet Team";
                            new EmailSender().sendEmail(txt,email, "Reset Password");
                        }
                    };
                    new Thread(myrunnable).start();
                }
                else
                {
                    dataObj  = new JSONObject();
                    dataObj.put("error", "Password change failed");
                    dataArray.put(dataObj);
                    resp.setStatus(500);
                    responseobj=dataArray;                            
                }

             }


            if(function.equals("updatePassword"))
            {
                String []data=maindata.split("#");
                String email=data[0];
                String new_pass=data[1];
                String old_pass=data[2];

                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();

                int status=new SystemUsersProcessor().validateOldPassword (email,old_pass);
                if(status == 1)
                {
                    int respo=new SystemUsersProcessor().changePassword(email,new_pass,1);
                    if(respo==200)
                    {
                        dataObj  = new JSONObject();
                        dataObj.put("message", "Password updated successfully"); 
                        dataArray.put(dataObj);
                        resp.setStatus(200);
                        responseobj=dataArray;

                        Runnable myrunnable=new Runnable()
                        {
                            public void run()
                            {
                                String txt="Hi,\nYour starbet password was changed.New password is: "+new_pass+"\n\n"+
                                 "Regards,\n" +
                                 "StartBet Team";
                                new EmailSender().sendEmail(txt,email, "Reset Password");
                            }
                        };
                        new Thread(myrunnable).start();
                    }
                    else
                    {
                        dataObj  = new JSONObject();
                        dataObj.put("error", "Password change failed");
                        dataArray.put(dataObj);
                        resp.setStatus(500);
                        responseobj=dataArray;                            
                    }
                } 
                else
                {
                    dataObj.put("error", "Password not verified");
                    dataArray.put(dataObj);
                    resp.setStatus(500);
                    responseobj=dataArray; 
                }
            }

        }catch (IOException | NumberFormatException | JSONException ex) { ex.getMessage();}

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
