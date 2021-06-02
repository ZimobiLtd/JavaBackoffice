package Utility;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jac
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import javax.mail.internet.*;
public class EmailSender {
    
    
    
    public EmailSender()
    {
        
    }
    
    
    public void sendEmail(String text,String to, String subject) 
    {
        String mailSmtpHost = "smtp.zoho.com";
        String userName="password@starbet.com";
        String password="St@rbet1234";
        String mailFrom = "password@starbet.com";
        try {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailSmtpHost);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.startssl.enable", "true");
        Session emailSession = Session.getInstance(properties,
        new javax.mail.Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() 
        {
            return new PasswordAuthentication(userName, password);
         }
       });

        Message emailMessage = new MimeMessage(emailSession);
        emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        //emailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
        emailMessage.setFrom(new InternetAddress(mailFrom));
        emailMessage.setSubject(subject);
        emailMessage.setText(text);

        //emailSession.setDebug(true);

        Transport.send(emailMessage);


        } catch (AddressException e) {
                e.printStackTrace();
        } catch (MessagingException e) {
                e.printStackTrace();
        }
    }
    
    
    
    public void sendMail (String msg ,String to,String subject) 
    {
        final String username = "instapayremit@zimobi.co.ke";
        final String password = "Sendmoney";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.host", "zimobi.co.ke");
        props.put("mail.smtp.port", "587");

        // Get the Session object.
        Session session = Session.getInstance(props,
           new javax.mail.Authenticator() {
              protected PasswordAuthentication getPasswordAuthentication() {
                 return new PasswordAuthentication(username, password);
              }
           });

        try {
           // Create a default MimeMessage object.
           Message message = new MimeMessage(session);
           // Set From: header field of the header.
           message.setFrom(new InternetAddress("instapayremit@zimobi.co.ke"));
           // Set To: header field of the header.
           message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
           // Set Subject: header field
           message.setSubject(subject);
           // Create the message part
           BodyPart messageBodyPart = new MimeBodyPart();
           // Now set the actual message
           messageBodyPart.setText(msg);
           // Create a multipar message
           Multipart multipart = new MimeMultipart();
           // Set text message part
           multipart.addBodyPart(messageBodyPart);
          /* // Part two is attachment
           messageBodyPart = new MimeBodyPart();
           String filename = "/root/kopakaro_dist/KopaKaro_Loan_Application_Form.pdf";
           // String filename = "/home/jac/Desktop/KopaKaro_Loan_Application_Form.pdf";
           DataSource source = new FileDataSource(filename);
           messageBodyPart.setDataHandler(new DataHandler(source));
           messageBodyPart.setFileName(filename);
           multipart.addBodyPart(messageBodyPart);*/
           // Send the complete message parts
           message.setContent(multipart);
           // Send message
           Transport.send(message);

           System.out.println("===Email Sent===");

        } catch (MessagingException e) {
           throw new RuntimeException(e);
        }
    } 
    
    
    
    public void postEmail(String receiverEmail, String text)
    {
        String respo="";
        String url="http://62.171.191.3:7076/send-email";
        String postParams = "{\"email\": \""+receiverEmail+"\", \"message\": \""+text+"\"}";
        System.out.println(postParams);
        StringBuffer response = null;

        try
        {
            String charset = "UTF-8";
            HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();

            connection.setDoOutput(true);
            connection.setRequestProperty("cache-control", "no-cache");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("Content-type", "application/json");
            //connection.setRequestProperty("Authorization", "Bearer ukdpyoliwya4lfch");
            //connection.setRequestProperty("Content-Length", "" + Integer.toString(data.getBytes().length));

            OutputStream output = connection.getOutputStream();
            output.write(postParams.getBytes("UTF-8"));
            output.close();


            int responseCode=connection.getResponseCode();
            if(responseCode==200)
            {
                InputStream inputStream = connection.getInputStream();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                response = new StringBuffer();
                String inputLine;
                while ((inputLine = responseReader.readLine()) != null)
                {
                  response.append(inputLine);
                }
                System.out.println("Email Sent>>>" + response.toString());
                respo=response.toString();
                responseReader.close();
            }
            else
            {
                respo="Email Failed>>>";
            }
        }
        catch (IOException ex)
        {
            System.out.println("Error postEmail=== "+ex.getMessage());
        }
    }
    
    

 
  public static void main(String[] args) 
  {
      
       new EmailSender().sendEmail("Hello coder","jacmgitau@gmail.com", "Test") ;
      
  }
    
}
