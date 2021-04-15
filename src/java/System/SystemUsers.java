package System;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Database.DBManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import java.util.Base64; 
import java.util.Date;
import org.json.JSONArray;

/**
 *
 * @author jac
 */
@WebServlet(urlPatterns = {"/SystemUsers"})
public class SystemUsers extends HttpServlet {

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
        String type="betting";JSONObject jsonobj=null;JSONArray responseobj  = null;
        public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
            resp.setContentType("text/json;charset=UTF-8");
            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Access-Control-Allow-Methods", "POST");
            
            
            StringBuffer jb = new StringBuffer();
            String line = null;

            try 
            {

                   BufferedReader reader = req.getReader();
                   while ((line = reader.readLine()) != null)
                   {
                       jb.append(line);
                   }
                   
                   System.out.println("SystemUsers===="+jb.toString());
                   jsonobj = new JSONObject(jb.toString());
                   function=jsonobj.getString("function");
                   maindata=jsonobj.getString("data");
                   
                   
                   if(function.equals("getSystemUsers"))
                   {
                        String []respo=initDates();
                        responseobj=getUsers("");
                   }
                   
                   
                   if(function.equals("filterSystemUsers"))
                   {
                       String username=maindata;
                       
                       responseobj=getUsers(username);
                       
                   }
                   
                   if(function.equals("getUser"))
                   {
                       String username=maindata;
                       
                       responseobj=getUser(username);
                       
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
                        int userStatus=Integer.valueOf(data[8]);
                        int isactive=Integer.valueOf(data[9]);
                        String role=data[10];
                        
                        int status=validateUser(username,emailaddress);
                        if(status==0)
                        {
                            int respo_id=saveSystemUser(username,emailaddress,phoneNumber,firstname,lastname,password,createdBy,modifiedBy,userStatus,isactive,role) ;
                            JSONObject dataObj  = new JSONObject();
                            JSONArray dataArray = new JSONArray();
                            if(respo_id>0)
                            {
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
                        
                       
                        int respo_id=updateSystemUser( username,emailaddress,phoneNumber,firstname,lastname,modifiedBy,userStatus,isactive,role) ;
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
                        
                       
                        int respo_id=updateUserAccess(phoneNumber,userStatus) ;
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
                       
                       int email_status=validateEmail(email);
                       if(email_status==1)
                       {
                            String code=generate_code(9); 
                            String link="We received a request to reset the password on your StarBet Back Office Account.\nKindly click on the link below.\n\n"
                                    + "http://167.86.68.102:7074/resetPass/"+code+"\n\n" +
                                     "Regards,\n" +
                                     "StartBet Team";
                            int status=savecode(email, code);

                             
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
                       
                        int status=verifyCode(code) ;
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
                        String new_pass=generate_code(7);
                        
                        JSONObject dataObj  = new JSONObject();
                        JSONArray dataArray = new JSONArray();
                        
                        int respo=changePassword(email,new_pass);
                        if(respo==200)
                        {
                            dataObj  = new JSONObject();
                            dataObj.put("message", "Password changed Successfully"); 
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
                   
                
                   
                   
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        public JSONArray getUsers(String user)
        {
                  
            String dataQuery="";

            if (user.equalsIgnoreCase("")) 
            {
                dataQuery = "select username, firstname, lastname, emailaddress, (case when blocked=1 then 'Active' when blocked=0 then 'Inactive' end ), createdon, "
                        + "(select username from users where userid = A.createdby LIMIT 1) as 'createdBy', modifiedOn,"
                        + "(select username from users where userid = A.modifiedBy LIMIT 1) as 'modifieddBy',ifnull(Role,'No Role'),phonenumber "
                        + "from users A inner join recordstatus  "
                        + "group by userid order by createdon desc,blocked desc";//on statusid = userstatus
            } 
            else
            {
                dataQuery = "select username, firstname, lastname, emailaddress, (case when blocked=1 then 'Active' when blocked=0 then 'Inactive' end), createdon, "
                        + "(select username from users where userid = A.createdby LIMIT 1) as 'createdBy', modifiedOn,"
                        + "(select username from users where userid = A.modifiedBy LIMIT 1) as 'modifieddBy',ifnull(Role,'No Role'),phonenumber "
                        + "from users A inner join recordstatus where username like '%" + user + "%' "
                        + "group by userid order by createdon desc,blocked desc";//on statusid = userstatus and
            }
            
            System.out.println("getUsers==="+dataQuery);

            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();

            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs = stmt.executeQuery(dataQuery);

                     while (rs.next())
                     {
                        String username = rs.getString(1);
                        String fname = rs.getString(2);
                        String lname = rs.getString(3);
                        String email = rs.getString(4);
                        String status = rs.getString(5);
                        String createdon = rs.getString(6);
                        String createdby = rs.getString(7);
                        String modifiedon = rs.getString(8);
                        String modifiedby = rs.getString(9);
                        String role = rs.getString(10);
                        String mobile = rs.getString(11);

                        dataObj  = new JSONObject();
                        dataObj.put("UserName", username);
                        dataObj.put("FirstName", fname);
                        dataObj.put("LastName", lname);
                        dataObj.put("Email", email);
                        dataObj.put("Status", status);
                        dataObj.put("CreatedOn", createdon);
                        dataObj.put("CreatedBy", createdby);
                        dataObj.put("ModifiedOn", modifiedon);
                        dataObj.put("ModifiedBy", modifiedby);
                        dataObj.put("Role", role);
                        dataObj.put("Mobile", mobile);
                        dataArray.put(dataObj);
                     }

                     if(dataArray.length()==0)
                     {
                        dataObj  = new JSONObject();
                        dataObj.put("UserName", "0");
                        dataObj.put("FirstName", "0");
                        dataObj.put("LastName", "0");
                        dataObj.put("Email", "0");
                        dataObj.put("Status", "0");
                        dataObj.put("CreatedOn", "0");
                        dataObj.put("CreatedBy", "0");
                        dataObj.put("ModifiedOn", "0");
                        dataObj.put("ModifiedBy", "0");
                        dataObj.put("Role", "0");
                        dataObj.put("Mobile", "0");
                        dataArray.put(dataObj);
                     }


            rs.close();
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
                    
        return dataArray;
        }
        
        
        
        
        
        
        
        public JSONArray getUser(String user)
        {
                  
            String dataQuery="";

            dataQuery = "select username, firstname, lastname, emailaddress, phonenumber, userstatus, blocked, "
                      + "ifnull(Role,'No Role') from users where username like '%" + user + "%'";
           
            System.out.println("getUser==="+dataQuery);

            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();

            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                   String username = rs.getString(1);
                   String fname = rs.getString(2);
                   String lname = rs.getString(3);
                   String email = rs.getString(4);
                   String mobile = rs.getString(5);
                   String status = rs.getString(6);
                   String isactive = rs.getString(7);
                   String role = rs.getString(8);

                   dataObj  = new JSONObject();
                   dataObj.put("UserName", username);
                   dataObj.put("FirstName", fname);
                   dataObj.put("LastName", lname);
                   dataObj.put("Email", email);
                   dataObj.put("Status", status);
                   dataObj.put("Mobile", mobile);
                   dataObj.put("IsActive", isactive);
                   dataObj.put("Role", role);
                   dataArray.put(dataObj);
                }

                if(dataArray.length()==0)
                {
                   dataObj  = new JSONObject();
                   dataObj.put("UserName", "0");
                   dataObj.put("FirstName", "0");
                   dataObj.put("LastName", "0");
                   dataObj.put("Email", "0");
                   dataObj.put("Status", "0");
                   dataObj.put("Mobile", "0");
                   dataObj.put("IsActive", "0");
                   dataObj.put("Role", "0");
                   dataArray.put(dataObj);
                }


            rs.close();
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
                    
        return dataArray;
        }
        
        
        
        
        
        public int validateUser(String username,String email)
        {
                  
            int count=0;

            String dataQuery = "select count(userid) from users where username='" + username + "'  and emailaddress='" + email + "' ";
           
            System.out.println("validateUser==="+dataQuery);

            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {
                
                ResultSet rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                   count = rs.getInt(1);
                }

            rs.close();
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
                    
        return count;
        }
        
        
        
        
        
        
        public int saveSystemUser(String username, String emailaddress, String phoneNumber, String firstname, String lastname, String password,
            Integer createdBy, Integer modifiedBy, Integer userStatus, Integer isactive, String role) 
        {
           int respo_id=0; 
            String dataQuery="";

            dataQuery = "INSERT INTO users (username,password,phonenumber,emailaddress,firstname, "
                        + "lastname,userstatus,blocked,createdby,modifiedby, modifiedon,Role) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
           
            java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());  
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            PreparedStatement ps=null;
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ps = conn.prepareStatement(dataQuery);
                ps.setString(1,username );
                ps.setString(2,password ); 
                ps.setString(3,phoneNumber ); 
                ps.setString(4,emailaddress);               
                ps.setString(5, firstname);
                ps.setString(6,lastname );
                ps.setInt(7,userStatus ); 
                ps.setInt(8,isactive ); 
                ps.setInt(9,createdBy);               
                ps.setInt(10, modifiedBy);
                ps.setTimestamp(11, timestamp);
                ps.setString(12,role );
                respo_id=ps.executeUpdate();
                
                

            ps.close();
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
        return respo_id;    
        }
        
        
        
        
        
        public int updateSystemUser(String username, String emailaddress, String phoneNumber, String firstname, String lastname,Integer modifiedBy, Integer userStatus, Integer isactive, String role) 
        {
           int respo_id=0; 
            String dataQuery="";

            java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());  
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            PreparedStatement ps=null;
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {
                
                dataQuery = "update users set username='"+username+"',emailaddress='"+emailaddress+"',emailaddress='"+emailaddress+"',firstname='"+firstname+"', "
                          + "lastname='"+lastname+"',userstatus="+userStatus+",blocked="+isactive+",modifiedby="+modifiedBy+", modifiedon='"+timestamp+"',Role='"+role+"' where phonenumber='"+phoneNumber+"' ";
                stmt.executeUpdate(dataQuery);
                
                respo_id=1;
               
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
        return respo_id;    
        }
        
        
        
        public int updateUserAccess(String phoneNumber, Integer userStatus) 
        {
           int respo_id=0; 
            String dataQuery="";

            java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());  
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            PreparedStatement ps=null;
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {
                
                dataQuery = "update users set blocked="+userStatus+" where phonenumber='"+phoneNumber+"' ";
                stmt.executeUpdate(dataQuery);
                
                respo_id=1;
               
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
        return respo_id;    
        }
        
        
        
        
        public int savecode(String email, String code)
        {

            int success_code=500;
            String query = "INSERT INTO Verification_Codes (Email,Code) values(?,?) ";

             PreparedStatement ps=null;
            try(  Connection conn = new DBManager(type).getDBConnection();
                Statement stmt=  conn.createStatement();)

            {

            ps = conn.prepareStatement(query);
            ps.setString(1, email);                
            ps.setString(2, code);
            ps.executeUpdate();
            success_code=200;

            ps.close();
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }

         return success_code;
        }
        
        
        
        
        public int verifyCode(String code) 
        {

            int count =0;
            String query = "select count(ID)  from  Verification_Codes  WHERE  Code='"+code+"' and TIMESTAMPDIFF(MINUTE,now(),`date`)>= -15   ";

            try (Connection conn = new DBManager(type).getDBConnection();
                    Statement stmt = conn.createStatement();) {
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {

            count = resultSet.getInt(1);

            }

            if(count==1)
            {
                String query1="update Verification_Codes set Status=1 WHERE  Code='"+code+"' ";
                stmt.executeUpdate(query1);
            }

            resultSet.close();
            stmt.close();
            conn.close();
            } catch (Exception e) {
                System.out.print("Error ... here ... " + e.getMessage());
            }
        return count;
        }
        
        
        
        
        public String getCodeEmail(String code) 
        {

            String email="";
            String query = "select Email  from  Verification_Codes  WHERE  Code='"+code+"' order by `date` desc limit 1 ";

            try (Connection conn = new DBManager(type).getDBConnection();
                    Statement stmt = conn.createStatement();) {
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {

            email = resultSet.getString(1);

            }
            
            if(email.equals(""))
            {
                email="0";
            }

            resultSet.close();
            stmt.close();
            conn.close();
            } catch (Exception e) {
                System.out.print("Error ... here ... " + e.getMessage());
            }
        return email;
        }
        
        
        
        
        public int changePassword(String email,String newpass)
        {

            int success_code=500;
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

            String issuccess = "UPDATE users set  password='"+newpass+"' where emailaddress='"+email+"'  ";
            stmt.executeUpdate(issuccess);

            success_code=200;

            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
        return success_code;
        }
        
        
        
        public int validateEmail(String email)
        {

            int count=0;
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

            String query = "select count(userid) from users where emailaddress='"+email+"'  ";
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {

            count = resultSet.getInt(1);

            }

            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
        return count;
        }
        
        
        
        public String generate_code(int count) 
        {

            String ALPHA_NUMERIC_STRING = "1A5B1C0D4E3F595242319283G7H4I6J5KLMNOP12QR45ST98";

            StringBuilder builder = new StringBuilder();

            while (count-- != 0) 
            {

                int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());

                builder.append(ALPHA_NUMERIC_STRING.charAt(character));
                String out = builder.toString();

            }

        return builder.toString();
        }
        
        
        
        public String[] initDates() 
        {
            String []data=null;

            try 
            {

                Date startDate = new Date();//sdf.parse(fromDate);
                long curTime = startDate.getTime();
                long interval = (24 * 1000 * 60 * 60)*5;
                curTime -= interval;
                String fromdate=sdf.format(new Date(curTime));
                String todate=sdf.format(startDate);

                data=new String[]{fromdate,todate};//fromdate+"#"+todate ;

            } catch (Exception ex) {

            }

        return data;
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
