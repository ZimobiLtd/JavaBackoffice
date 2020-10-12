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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
@WebServlet(urlPatterns = {"/Bonus"})
public class Bonus extends HttpServlet {

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
                   
                   System.out.println("Bonus===="+jb.toString());
                   jsonobj = new JSONObject(jb.toString());
                   function=jsonobj.getString("function");
                   maindata=jsonobj.getString("data");
                   
                   
                   if(function.equals("getBonus"))
                   {
                       responseobj=getBonus("");
                   }
                   
                   
                   if(function.equals("filterBonus"))
                   {
                       String bonusname=maindata;
                       responseobj=getBonus(bonusname);
                   }
                   
                   if(function.equals("createBonus"))
                   {
                       String []data=maindata.split("#");
                       int bonusstatus =Integer.valueOf(data [0]);
                       String bonusname =data [1];
                       String description =data [2];
                       String bonustype =data [3];
                       int availableonweb =Integer.valueOf(data [4]);
                       int availableonsms =Integer.valueOf(data [5]);
                       int availableonussd =Integer.valueOf(data [6]);
                       int availableapp =Integer.valueOf(data [7]);
                       String activationdate =data [8];
                       String deactivationdate =data [9];
                       int expirydays =Integer.valueOf(data [10]);
                       int turnover =Integer.valueOf(data [11]);
                       int amount =Integer.valueOf(data [12]);
                       int iscash =Integer.valueOf(data [13]);
                       int isticky =Integer.valueOf(data [14]);
                       int minrelease =Integer.valueOf(data [15]);
                       int maxrelease =Integer.valueOf(data [16]);
                       int removeillegalwinnings =Integer.valueOf(data [17]);
                       int createdby =Integer.valueOf(data [18]);
                       int modifiedby =Integer.valueOf(data [19]);
                       
                       
                       String respo=createBonus(bonusstatus,bonusname,description,bonustype,availableonweb,availableonsms,availableonussd,availableapp,
                                    activationdate,deactivationdate,expirydays,turnover,amount,iscash,isticky,minrelease,maxrelease,removeillegalwinnings,createdby,modifiedby);
                     
                       if(respo.equals("200"))
                       {
                            JSONObject dataObj  = new JSONObject();
                            JSONArray dataArray = new JSONArray();
                            
                            dataObj.put("message", "Bonus added successfully");
                            dataArray.put(dataObj);
                            responseobj=dataArray;
                       }
                       else
                       {
                            JSONObject dataObj  = new JSONObject();
                            JSONArray dataArray = new JSONArray();
                            
                            dataObj.put("message", "request failed");
                            dataArray.put(dataObj);
                            responseobj=dataArray;
                       }
                       
                   }
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        
        
      
      
        public JSONArray getBonus(String bonusName)
        {
                  
            String res="";
            String dataQuery = "";
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs=null;
                    
                if (bonusName.equalsIgnoreCase("")) 
                {
                    dataQuery = "select Bon_ID, Bon_Name, Bon_Description, Bon_Type, Bon_ActivationDate, "
                    + "Bon_DeactivationDate, (case when Bon_Status=0 then 'Inactive' when Bon_Status=1 then'Active' end), B.username, Bon_CreatedOn,  "
                    + "B.username, Bon_ModifiedOn from Bonus_settings  "
                    + "inner join users B on Bon_CreatedBy = B.userid "
                    + "inner join users C on Bon_ModifiedBy = C.userid";
                } 
                else 
                {
                    dataQuery = "select Bon_Code, Bon_Name, Bon_Description, Bon_Type, Bon_ActivationDate, "
                    + "Bon_DeactivationDate,  (case when Bon_Status=0 then 'Inactive' when Bon_Status=1 then 'Active' end), B.username, Bon_CreatedOn,  "
                    + "B.username, Bon_ModifiedOn from Bonus_settings  "
                    + "inner join users B on Bon_CreatedBy = B.userid "
                    + "inner join users C on Bon_ModifiedBy = C.userid where Bon_Name like '%" + bonusName + "%'";
                }   
                
                
                System.out.println("getBonus==="+dataQuery);

                rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                    dataObj  = new JSONObject();

                    String bonusid = rs.getString(1);
                    String bonusname = rs.getString(2);
                    String description = rs.getString(3);
                    String type = rs.getString(4);
                    String validfrom = rs.getString(5);
                    String validto = rs.getString(6);
                    String status = rs.getString(7);
                    String createdon = rs.getString(8);
                    String createdby = rs.getString(9);
                    String modifiedby = rs.getString(10);
                    String modifiedon = rs.getString(11);

                    dataObj.put("ID", bonusid);
                    dataObj.put("BonusName", bonusname);
                    dataObj.put("Description", description);
                    dataObj.put("Type", type);
                    dataObj.put("ValidFrom", validfrom);
                    dataObj.put("ValidTo", validto);
                    dataObj.put("Status", status);
                    dataObj.put("CreateOn", createdon);
                    dataObj.put("CreatedBy", createdby);
                    dataObj.put("ModifiedBy", modifiedby);
                    dataObj.put("ModifiedOn", modifiedon);

                    dataArray.put(dataObj);
                }
                   
                  
                //System.out.println("\n\n\n"+dataArray);
                   
            rs.close();
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
                    
        return dataArray;
        }
        
        
        
        
        public String createBonus(int bonusstatus,String name,String description,String bonustype,int availableonweb,int availableonsms,int availableonussd,int availableapp,
                String activationdate,String deactivationdate,int expirydays,int turnover,int amount,int iscash,int isticky,int minrelease,int maxrelease,int removeillegalwinnings,
                int createdby,int modifiedby)
        {

            String status="",success_code="500";
            String query = "insert into Bonus_settings(Bon_Status,Bon_Name, Bon_Description, Bon_Type, Bon_AvailableonWeb, Bon_AvailableonSMS, Bon_AvailableonUSSD, "
                    + "Bon_AvailableonAndroidApp, Bon_ActivationDate,Bon_DeactivationDate,Bon_Expiry_Days,Bon_Turnover,Bon_Amount,Bon_IsCash, "
                    + "Bon_Sticky,Bon_MinimumRelease,Bon_MaximumRelease, Bon_RemoveIllegalWinnings, Bon_CreatedBy,Bon_CreatedOn,Bon_ModifiedBy,Bon_ModifiedOn) "
                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

             PreparedStatement ps=null;
            try(Connection conn = new DBManager(type).getDBConnection();
                Statement stmt=  conn.createStatement();)

            {
                
            java.sql.Date activationtimestamp = new java.sql.Date(sdf.parse(activationdate).getTime());  
            java.sql.Date deactivationtimestamp = new java.sql.Date(sdf.parse(deactivationdate).getTime()); 
            java.sql.Timestamp createdontimestamp = new java.sql.Timestamp(new Date().getTime()); 
            java.sql.Timestamp modifiedontimestamp = new java.sql.Timestamp(new Date().getTime()); 

            ps = conn.prepareStatement(query);
            ps.setInt(1,bonusstatus );
            ps.setString(2,name ); 
            ps.setString(3,description ); 
            ps.setString(4,bonustype);               
            ps.setInt(5, availableonweb);
            ps.setInt(6,availableonsms );
            ps.setInt(7,availableonussd ); 
            ps.setInt(8,availableapp ); 
            ps.setDate(9,activationtimestamp);               
            ps.setDate(10, deactivationtimestamp);
            ps.setInt(11,expirydays );
            ps.setInt(12,turnover ); 
            ps.setInt(13,amount ); 
            ps.setInt(14,iscash);               
            ps.setInt(15, isticky);            
            ps.setInt(16,minrelease );
            ps.setInt(17,maxrelease ); 
            ps.setInt(18,removeillegalwinnings ); 
            ps.setInt(19,createdby);               
            ps.setTimestamp(20, createdontimestamp);
            ps.setInt(21,modifiedby );
            ps.setTimestamp(22, modifiedontimestamp);
            
            int i=ps.executeUpdate();
            if(i>0)
            {
                success_code="200";                
            }

            ps.close();
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }

        return success_code;
       }
        
        
        
        public  ArrayList<String> loopdate(String fromDate, String toDate) 
        {
            ArrayList<String> dates = new ArrayList<>();
            try 
            {

                Date startDate = sdf.parse(fromDate);
                Date endDate = sdf.parse(toDate);
                long interval = 24 * 1000 * 60 * 60; // 1 hour in millis

                long endTime = startDate.getTime(); // create your endtime here, possibly using Calendar or Date
                long curTime = endDate.getTime();
                while (endTime <= curTime) 
                {
                    System.out.println("==="+new Date(endTime));
                    dates.add(sdf.format(new Date(endTime)));
                    endTime += interval;
                }

            } catch (Exception ex) {

            }

        return dates;
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
