
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jac
 */
public class Test {
    
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public static void main(String args[])
    {
        
        try {
            //loopdate("2020-08-01" ,"2020-08-05");
            Date startDate = sdf.parse("2020-08-01");
            Date endDate = sdf.parse("2020-08-05");
            System.out.println(startDate+"===="+endDate);
            /*for (String currentDate : loopdate("2020-08-01" ,"2020-08-05"))
            {
            System.out.println("test date==="+currentDate);
            }*/
        } catch (ParseException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(int i=0;i<5;i++)
        {
            System.out.println( i+"===");
        }
    }
    
    
    
     public static ArrayList<String> loopdate(String fromDate, String toDate) 
        {
        ArrayList<String> dates = new ArrayList<>();
        try {
            System.out.println(fromDate+"*********"+toDate);
           
            Date startDate = sdf.parse(fromDate);
            Date endDate = sdf.parse(toDate);
            System.out.println(startDate+"===="+endDate);
            long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
            
            long endTime = startDate.getTime(); // create your endtime here, possibly using Calendar or Date
            long curTime = endDate.getTime();
            while (curTime >= endTime) {
                System.out.println("date===="+new java.util.Date(curTime).toString());
                dates.add(new java.util.Date(curTime).toString());
                curTime -= interval;
            }

        } catch (Exception ex) {

        }

        return dates;

        }
}
