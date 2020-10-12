/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
@WebServlet(urlPatterns = {"/SystemSettings"})
public class SystemSettings extends HttpServlet {

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
        public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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

                System.out.println("SystemSettings===="+jb.toString());
                jsonobj = new JSONObject(jb.toString());
                function=jsonobj.getString("function");
                maindata=jsonobj.getString("data");


                if(function.equals("getRoles"))
                {
                    responseobj=getSecurityRoles();
                }


                if(function.equals("getUserRoles"))
                {
                    String username=maindata;

                    responseobj=getUserRoles(username);
                }
                
                if(function.equals("getSystemSettings"))
                {

                    responseobj=getSystemSettings();
                }
                
                if(function.equals("viewSystemSettings"))
                {
                    String id=maindata;
                    responseobj=getSystemSettingsByID(id);
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
                    
                    int status=validateSettings(name);
                    if(status==0)
                    {
                        int respo_id=saveSystemSettings(name,value,description,createdby,modifiedby) ;
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
                        int respo_id=editSystemSettings(id,name,value,description,modifiedby) ;
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
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        
        
        
        
        public JSONArray getSecurityRoles()
        {
                  
            String dataQuery="";

            dataQuery = "select id, name from securityrole where rolestatus = 1";
           
            System.out.println("getSecurityRoles==="+dataQuery);

            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();

            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                   String id = rs.getString(1);
                   String name = rs.getString(2);

                   dataObj  = new JSONObject();
                   dataObj.put("RoleID", id);
                   dataObj.put("Name", name);
                   dataArray.put(dataObj);
                }

                if(dataArray.length()==0)
                {
                   dataObj  = new JSONObject();
                   dataObj.put("RoleID", "0");
                   dataObj.put("Name", "0");
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
        
        
        
        
        public JSONArray getUserRoles(String user)
        {
                  
            String dataQuery="";

            dataQuery = "select Role from users where username like '%" + user + "%'";
           
            System.out.println("getUserRoles==="+dataQuery);

            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();

            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                   String name = rs.getString(1);

                   dataObj  = new JSONObject();
                   dataObj.put("Name", name);
                   dataArray.put(dataObj);
                }

                if(dataArray.length()==0)
                {
                   dataObj  = new JSONObject();
                   dataObj.put("Name", "0");
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
        
        
        
        
        public JSONArray getSystemSettings()
        {
                  
            String dataQuery="";

            dataQuery = "select id,settingname, settingvalue, description,createdon, modifiedon,createdby,modifiedby, "
                    + "(select username from users where userid = systemsetting.createdby LIMIT 1) as 'createdBy'," 
                        + "(select username from users where userid = systemsetting.modifiedby LIMIT 1) as 'modifieddBy'  from systemsetting order by createdon desc";
            
            System.out.println("getSystemSettings==="+dataQuery);

            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();

            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                    String id = rs.getString(1);
                   String name = rs.getString(2);
                   String value = rs.getString(3);
                   String description = rs.getString(4);
                   String createdon = rs.getString(5);
                   String modifiedon = rs.getString(6);
                   String createdby = rs.getString(7);
                   String modifiedby = rs.getString(8);
                   String createdbyuser = rs.getString(9);
                   String modifiedbyuser = rs.getString(10);
                   
                   dataObj  = new JSONObject();
                   dataObj.put("ID", id);
                   dataObj.put("Name", name);
                   dataObj.put("Value", value);
                   dataObj.put("Description", description);
                   dataObj.put("Createdon", createdon);
                   dataObj.put("Modifiedon", modifiedon);
                   dataObj.put("CreatedBy", createdby);
                   dataObj.put("ModifiedBy", modifiedby);
                   dataObj.put("CreatedByUser", createdbyuser);
                   dataObj.put("ModifiedByUser", modifiedbyuser);
                   
                   dataArray.put(dataObj);
                }

                if(dataArray.length()==0)
                {
                   dataObj  = new JSONObject();
                   dataObj.put("ID", "0");
                   dataObj.put("Name", "0");
                   dataObj.put("Value", "0");
                   dataObj.put("Description", "0");
                   dataObj.put("Createdon", "0");
                   dataObj.put("Modifiedon", "0");
                   dataObj.put("CreatedBy", "0");
                   dataObj.put("ModifiedBy", "0");
                   dataObj.put("CreatedByUser", "0");
                   dataObj.put("ModifiedByUser", "0");
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
        
        
        
        
        public JSONArray getSystemSettingsByID(String id)
        {
                  
            String dataQuery="";

            dataQuery = "select id,settingname, settingvalue, description from systemsetting where id=" + id + " ";
           
            System.out.println("getSystemSettingsByName==="+dataQuery);

            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();

            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                   String rec_id = rs.getString(1);
                   String name = rs.getString(2);
                   String value = rs.getString(3);
                   String description = rs.getString(4);
                   
                   dataObj  = new JSONObject();
                   dataObj.put("ID", rec_id);
                   dataObj.put("Name", name);
                   dataObj.put("Value", value);
                   dataObj.put("Description", description);
                   
                   dataArray.put(dataObj);
                }

                if(dataArray.length()==0)
                {
                   dataObj  = new JSONObject();
                   dataObj.put("ID", "0");
                   dataObj.put("Name", "0");
                   dataObj.put("Value", "0");
                   dataObj.put("Description", "0");
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
        
    
        
        
        public int saveSystemSettings(String name, String value, String descrition, String createdby, String modifiedby) 
        {
           int respo_id=0; 
            String dataQuery="";

            dataQuery = "INSERT INTO systemsetting (settingname,settingvalue,description,createdon, modifiedon,createdby, modifiedby) VALUES(?,?,?,?,?,?,?)";
           
            java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());  
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            PreparedStatement ps=null;
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ps = conn.prepareStatement(dataQuery);
                ps.setString(1,name );
                ps.setString(2,value ); 
                ps.setString(3,descrition ); 
                ps.setTimestamp(4, timestamp);
                ps.setTimestamp(5, timestamp);
                ps.setInt(6,Integer.valueOf(createdby) ); 
                ps.setInt(7,Integer.valueOf(modifiedby) ); 
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
        
        
        
        
        public int editSystemSettings(String id,String name, String value, String description,String modifiedby) 
        {
           int respo_id=0; 
            String dataQuery="";
            
            java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());
            dataQuery = "update systemsetting set settingname='"+name+"',settingvalue='"+value+"',description='"+description+"',modifiedon='"+timestamp+"',modifiedby="+modifiedby+" where id= "+id+" ";
            System.out.println("editSystemSettings==="+dataQuery);
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            PreparedStatement ps=null;
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

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
        
        
        
        public int validateSettings(String name)
        {
                  
            int count=0;

            String dataQuery = "select count(id) from systemsetting where settingname like '%" + name + "%' ";
           
            System.out.println("validateSettings==="+dataQuery);

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
