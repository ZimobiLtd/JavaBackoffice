/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

/**
 *
 * @author jac
 */
import java.security.Security; 
import java.util.Properties; 

import javax.mail.Message; 
import javax.mail.MessagingException; 
import javax.mail.PasswordAuthentication; 
import javax.mail.Session; 
import javax.mail.Transport; 
import javax.mail.internet.InternetAddress; 
import javax.mail.internet.MimeMessage; 

public class testemail { 

/** 
* @param args 
*/ 
private static final String SMTP_HOST_NAME = "smtp.zoho.com"; 
private static final String SMTP_PORT = "587"; 
private static final String emailMsgTxt = "Test Message Contents"; 
private static final String emailSubjectTxt = "A test from gmail"; 
private static final String emailFromAddress = "password@starbet.co.ke"; 
private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory"; 
private static final String[] sendTo = { "jacmgitau@gmail.com"}; 


public static void main(String args[]) throws Exception { 
Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider()); 

new testemail().sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt, emailFromAddress); 
System.out.println("Sucessfully mail to All Users"); 
} 

public void sendSSLMessage(String recipients[], String subject, 
String message, String from) throws MessagingException 
{ 
    boolean debug = true; 
    Properties props = new Properties(); 
    props.put("mail.smtp.host", "smtp.zoho.com"); 

    props.put("mail.smtp.auth", "true"); 
    props.put("mail.smtp.port", "587"); 


    //props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
    //props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
    //props.setProperty("mail.smtp.socketFactory.fallback", "false"); 
    //props.setProperty("mail.smtp.socketFactory.port", "587"); 
    props.put("mail.smtp.starttls.enable", "true"); 




    Session session = Session.getInstance(props, 
    new javax.mail.Authenticator() { 
    protected PasswordAuthentication getPasswordAuthentication() { 
    return new PasswordAuthentication(emailFromAddress, "St@rbet1234"); 
    } 
    }); 

    session.setDebug(debug); 

    Message msg = new MimeMessage(session); 
    InternetAddress addressFrom = new InternetAddress(from); 
    msg.setFrom(addressFrom); 

    InternetAddress[] addressTo = new InternetAddress[recipients.length]; 
    for (int i = 0; i < recipients.length; i++) { 
    addressTo[i] = new InternetAddress(recipients[i]); 
    } 
    msg.setRecipients(Message.RecipientType.TO, addressTo); 

    // Setting the Subject and Content Type 
    msg.setSubject(subject); 
    msg.setContent(message, "text/plain"); 
    Transport transport = session.getTransport("smtps"); 
    //transport.send(msg); 
    transport.connect(SMTP_HOST_NAME, 587, emailFromAddress, "St@rbet1234"); 
    transport.sendMessage(msg, addressTo); 
    transport.close(); 


    } 

} 
