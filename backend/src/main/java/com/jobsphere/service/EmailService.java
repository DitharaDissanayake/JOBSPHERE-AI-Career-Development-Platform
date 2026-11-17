
        package com.jobsphere.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

 private final JavaMailSender mailSender;

 public EmailService(JavaMailSender mailSender) {
  this.mailSender = mailSender;
 }

 // =========================================================
 // COMMON SIMPLE EMAIL METHOD
 // =========================================================
 private void send(String to, String subject, String body) {

  SimpleMailMessage message = new SimpleMailMessage();

  message.setTo(to);
  message.setSubject(subject);
  message.setText(body);

  mailSender.send(message);
 }


 // =========================================================
 // REGISTRATION SUCCESS
 // =========================================================
 public void sendRegistrationSuccess(
         String email,
         String name) {

  String body =
          "Dear " + name + ",\n\n" +
          "Welcome to JOBSPHERE!\n\n" +
          "Your account has been registered successfully. " +
          "You can now sign in and complete your career profile to receive " +
          "personalized AI career guidance, learning recommendations and interview preparation.\n\n" +
          "Best regards,\n" +
          "JOBSPHERE Team";

  send(email, "JOBSPHERE | Registration Successful", body);
 }

 // =========================================================
 // COURSE PAYMENT INVOICE
 // =========================================================
 public void sendCoursePaymentInvoice(
         String email,
         String name,
         String course,
         String orderId,
         String paymentId,
         String amount,
         byte[] data,
         String fileName) throws Exception {

  MimeMessage message = mailSender.createMimeMessage();
  MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
  helper.setTo(email);
  helper.setSubject("JOBSPHERE | Course Payment Receipt - " + course);

  String body =
          "Dear " + name + ",\n\n" +
          "Thank you for your payment to JOBSPHERE.\n\n" +
          "Course: " + course + "\n" +
          "Amount: LKR " + amount + "\n" +
          "Order ID: " + orderId + "\n" +
          "Payment ID: " + (paymentId == null || paymentId.isBlank() ? "JOBSPHERE Demo Payment" : paymentId) + "\n\n" +
          "Your payment receipt is attached as a PDF. The advanced 7-day course is now available in your JOBSPHERE account.\n\n" +
          "Best regards,\n" +
          "JOBSPHERE Administration";

  helper.setText(body);
  helper.addAttachment(fileName, new ByteArrayResource(data));
  mailSender.send(message);
 }

 // =========================================================
 // CONTACT REPLY
 // =========================================================
 public void sendContactReply(
         String email,
         String name,
         String subject,
         String reply) {

  String emailSubject;

  if (subject == null || subject.isBlank()) {
   emailSubject = "Reply from JOBSPHERE";
  } else {
   emailSubject = "JOBSPHERE - Re: " + subject;
  }

  String body =
          "Dear " + name + ",\n\n" +
                  "Thank you for contacting JOBSPHERE.\n\n" +
                  "Our administrator has replied:\n\n" +
                  reply + "\n\n" +
                  "Best regards,\n" +
                  "JOBSPHERE Administration";

  send(email, emailSubject, body);
 }

 // =========================================================
 // PASSWORD RESET OTP
 // =========================================================
 public void sendPasswordOtp(
         String email,
         String name,
         String otp) {

  String body =
          "Dear " + name + ",\n\n" +
                  "Your JOBSPHERE password reset code is: " + otp + "\n\n" +
                  "This code expires in 10 minutes.\n\n" +
                  "If you did not request this password reset, you can safely " +
                  "ignore this email.\n\n" +
                  "JOBSPHERE Security Team";

  send(
          email,
          "JOBSPHERE Password Reset OTP",
          body
  );
 }

 // =========================================================
 // LOGIN ALERT
 // =========================================================
 public void sendLoginAlert(
         String email,
         String name) {

  String body =
          "Dear " + name + ",\n\n" +
                  "You have successfully signed in to your JOBSPHERE account " +
                  "using this email address.\n\n" +
                  "If this was not you, please reset your password immediately.\n\n" +
                  "JOBSPHERE Security Team";

  send(
          email,
          "JOBSPHERE - Successful Sign In",
          body
  );
 }

 // =========================================================
 // COURSE CERTIFICATE EMAIL
 // =========================================================
 public void sendCourseCertificate(
         String email,
         String name,
         String course,
         int score,
         String number,
         byte[] data,
         String fileName) throws Exception {

  MimeMessage message = mailSender.createMimeMessage();

  MimeMessageHelper helper =
          new MimeMessageHelper(
                  message,
                  true,
                  "UTF-8"
          );

  helper.setTo(email);

  helper.setSubject(
          "JOBSPHERE | Course Certificate - " + course
  );

  String body =
          "Dear " + name + ",\n\n" +

                  "Congratulations!\n\n" +

                  "You have successfully completed the JOBSPHERE course " +
                  "\"" + course + "\" " +
                  "with an assessment score of " +
                  score + "%.\n\n" +

                  "Your professional certificate is attached to this email.\n\n" +

                  "Certificate No: " + number + "\n\n" +

                  "Keep learning and continue building job-ready skills " +
                  "with JOBSPHERE.\n\n" +

                  "Best regards,\n" +
                  "JOBSPHERE Administration";

  helper.setText(body);

  // Attach generated PDF certificate
  helper.addAttachment(
          fileName,
          new ByteArrayResource(data)
  );

  mailSender.send(message);
 }
}

