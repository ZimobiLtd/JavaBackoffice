/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.*;
/**
 *
 * @author jac
 */
public class LoggerClass {
    private static LoggerClass instance=new LoggerClass();
    String className;
    static String methodName;
    static int lineNumber;
    static String callerClassName;
    static final String PATH="/root/jac_tomcat/BOlogs.log";
    /**private static final String PATH="/home/jac/Desktop/sgr_connect_operator_app.log";
    private static FileHandler handler;
    private static final Logger logger = Logger.getLogger("sgr-connect-Logger");
    static FileWriter fileWriter;
    static  PrintWriter printWriter;*/
    
    LoggerClass()
    {
        this.className=LoggerClass.class.getName();
    }
    
    static
    {
        try
        {
            /**File file=new File(PATH+".29");
            file.delete();
            handler = new FileHandler(PATH,1000000000,1,true); 
            handler.setFormatter(new MyFormatter());
            logger.addHandler(handler);
            logger.setUseParentHandlers(false);
            fileWriter = new FileWriter(PATH, true); //Set true for append mode
            printWriter = new PrintWriter(fileWriter);*/
        } 
        catch (SecurityException ex)
        {
            Logger.getLogger(LoggerClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     *
     * @return
     */
    public static LoggerClass getInstance()
    {
        return instance;
    }
    
    /**
     *
     * @param element
     * @param leval
     * @param log
     */
    public static void buildLog(StackTraceElement element,java.util.logging.Level leval,String log)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fullClassName = element.getClassName();
            LoggerClass.callerClassName= fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            LoggerClass.lineNumber = element.getLineNumber();
            LoggerClass.methodName = element.getMethodName();
            String msg="[" + formatter.format(new Date()) + "]-[" + callerClassName + "]-[" + methodName+"-Line "+lineNumber + "]-[" + leval.toString()+ "]" + log;
            printWriter(msg);
        } catch (IOException ex)
        {
            Logger.getLogger(LoggerClass.class.getName()).log(Level.SEVERE, null, ex);
        }

    } 
    
    /**
     *
     * @param text
     */
    public static void printWriter(String text) throws IOException 
    {
        FileWriter fileWriter = new FileWriter(PATH, true); //Set true for append mode
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(text);
        printWriter.close();
    }
    
   /**static class MyFormatter extends Formatter 
   {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        
        @Override
        public String format(LogRecord myRecord)
        {

            return "\n[" + sdf.format(new Date(myRecord.getMillis())) + "]-[" + callerClassName + "]-[" + methodName+"-Line "+lineNumber + "]-[" + myRecord.getLevel()
                    + "]===" + myRecord.getMessage() + "===";
        }
    }*/
       
}
