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
import java.util.Date;
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
    
    
    /*public void sendEmail(String text,String to, String subject) 
    {
        String mailSmtpHost ="smtp.zoho.com";
        String userName="password@starbet.co.ke";
        String password="St@rbet1234";
        String mailFrom ="password@starbet.co.ke";
        try {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailSmtpHost);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "587");
        //properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        Session emailSession = Session.getInstance(properties,
        new javax.mail.Authenticator() {
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

        System.out.println("==="+subject+" email sent===");

        } catch (AddressException e) {
                e.printStackTrace();
        } catch (MessagingException e) {
                e.printStackTrace();
        }
    }*/
    
    /*public void sendEmail(String text,String to, String subject) 
    {
        String mailSmtpHost = "smtp.zoho.com";
        String userName="password@starbet.co.ke";
        String password="St@rbet1234";
        String mailFrom = "password@starbet.co.ke";
        try {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailSmtpHost);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        Session emailSession = Session.getInstance(properties,
        new javax.mail.Authenticator() {
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

        System.out.println("==="+subject+" email sent===");

        } catch (AddressException e) {
                e.printStackTrace();
        } catch (MessagingException e) {
                e.printStackTrace();
        }
    }*/
    
    
    
     public void sendEmail(String text,String mailTo, String subject) 
     {
        final String GMAIL_HOST = "smtp.gmail.com";
        final String ZOHO_HOST = "starbet.co.ke";
        final String TLS_PORT = "587";

        final String SENDER_EMAIL = "password@starbet.co.ke";
        final String SENDER_USERNAME ="password@starbet.co.ke";
        final String SENDER_PASSWORD = "St@rbet1234";

        // protocol properties
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", ZOHO_HOST); // change to GMAIL_HOST for gmail for gmail
        props.setProperty("mail.smtp.port", TLS_PORT);
        props.setProperty("mail.smtp.starttls.enable", "true");
        /*props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.port", "25");*/
        props.setProperty("mail.smtps.auth", "true");
        // close connection upon quit being sent
        props.put("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, null);

        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        try {
            // create the message
            final MimeMessage msg = new MimeMessage(session);

            // set recipients and content
            msg.setFrom(new InternetAddress(SENDER_EMAIL));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo, false));
            msg.setSubject(subject);
            //msg.setText(text);
            msg.setText("Message Sent via JavaMail", "utf-8", "html");
            msg.setSentDate(new Date());

            // this means you do not need socketFactory properties
            Transport transport = session.getTransport("smtps");

            // send the mail
            transport.connect(ZOHO_HOST, SENDER_USERNAME, SENDER_PASSWORD);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();

        } catch (MessagingException ex)
        {
            System.out.println("send mail==="+ex.getMessage());
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
    
    

 
  public static void main(String[] args) 
  {
      
    new EmailSender().sendEmail("Hello coder","jacmgitau@gmail.com", "Test") ;
      
  }
    
}
