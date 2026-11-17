
        package com.jobsphere.controller;

import com.jobsphere.entity.*;
import com.jobsphere.repository.*;
import com.jobsphere.service.EmailService;
import com.jobsphere.service.TokenService;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class PublicController {

 private final ContactMessageRepository messages;
 private final FeedbackRepository feedback;
 private final TokenService tokens;
 private final EmailService emailService;

 public PublicController(
         ContactMessageRepository m,
         FeedbackRepository f,
         TokenService t,
         EmailService e
 ) {
  messages = m;
  feedback = f;
  tokens = t;
  emailService = e;
 }


 // ============================================================
 // USER CONTACT MESSAGE
 // ============================================================

 @PostMapping("/contact")
 public Map<String, Object> contact(
         @RequestBody ContactMessage m
 ) {

  // -----------------------------
  // NAME
  // -----------------------------

  String name = m.name == null
          ? ""
          : m.name.trim();

  if (name.isEmpty()) {
   throw new RuntimeException(
           "Please enter your name."
   );
  }

  if (name.length() < 2) {
   throw new RuntimeException(
           "Name must contain at least 2 characters."
   );
  }

  if (name.length() > 120) {
   throw new RuntimeException(
           "Name must be 120 characters or fewer."
   );
  }

  if (!name.matches("[\\p{L}][\\p{L} .'-]*")) {
   throw new RuntimeException(
           "Please enter a valid name. Use letters, spaces, apostrophes or hyphens only."
   );
  }


  // -----------------------------
  // EMAIL
  // -----------------------------

  String email = m.email == null
          ? ""
          : m.email.trim();

  if (email.isEmpty()) {
   throw new RuntimeException(
           "Please enter your email address."
   );
  }

  if (email.length() > 160) {
   throw new RuntimeException(
           "Email address must be 160 characters or fewer."
   );
  }

  if (!email.matches(
          "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
  )) {
   throw new RuntimeException(
           "Please enter a valid email address."
   );
  }


  // -----------------------------
  // SUBJECT
  // -----------------------------

  String subject = m.subject == null
          ? null
          : m.subject.trim();

  if (subject != null && subject.length() > 255) {
   throw new RuntimeException(
           "Subject must be 255 characters or fewer."
   );
  }


  // -----------------------------
  // MESSAGE
  // -----------------------------

  String message = m.message == null
          ? ""
          : m.message.trim();

  if (message.isEmpty()) {
   throw new RuntimeException(
           "Please enter your message."
   );
  }

  if (message.length() < 5) {
   throw new RuntimeException(
           "Please enter a message of at least 5 characters."
   );
  }

  if (message.length() > 3000) {
   throw new RuntimeException(
           "Message must be 3000 characters or fewer."
   );
  }


  // -----------------------------
  // SAVE
  // -----------------------------

  m.id = null;

  m.name = name;

  m.email = email.toLowerCase(
          Locale.ROOT
  );

  m.subject = subject;

  m.message = message;

  m.read = false;

  m.replied = false;

  m.adminReply = null;

  m.repliedAt = null;

  m.createdAt = LocalDateTime.now();

  messages.save(m);


  return Map.of(
          "success", true,
          "message",
          "Thank you. Your message has been sent to JOBSPHERE."
  );
 }


 // ============================================================
 // ADMIN REPLY TO CONTACT MESSAGE
 // ============================================================

 @PostMapping("/admin/contact/{id}/reply")
 public Map<String, Object> replyToContact(
         @RequestHeader("Authorization") String authorization,
         @PathVariable Long id,
         @RequestBody Map<String, String> request
 ) {

  // -----------------------------
  // CHECK LOGIN
  // -----------------------------

  if (authorization == null ||
          authorization.isBlank()) {

   throw new RuntimeException(
           "Please sign in as an administrator."
   );
  }

  String token = authorization.replaceFirst(
          "^Bearer\\s*",
          ""
  );

  Long adminId = tokens.user(token);

  if (adminId == null) {
   throw new RuntimeException(
           "Your session has expired. Please sign in again."
   );
  }


  // -----------------------------
  // FIND MESSAGE
  // -----------------------------

  ContactMessage contactMessage =
          messages.findById(id)
                  .orElseThrow(() ->
                          new RuntimeException(
                                  "Contact message not found."
                          )
                  );


  // -----------------------------
  // GET REPLY
  // -----------------------------

  String reply = request.get("reply");

  if (reply == null) {
   reply = "";
  }

  reply = reply.trim();


  // -----------------------------
  // VALIDATE REPLY
  // -----------------------------

  if (reply.isEmpty()) {
   throw new RuntimeException(
           "Please enter a reply."
   );
  }

  if (reply.length() < 2) {
   throw new RuntimeException(
           "Reply must contain at least 2 characters."
   );
  }

  if (reply.length() > 5000) {
   throw new RuntimeException(
           "Reply must be 5000 characters or fewer."
   );
  }


  // -----------------------------
  // SEND EMAIL
  // -----------------------------

  try {

   emailService.sendContactReply(
           contactMessage.email,
           contactMessage.name,
           contactMessage.subject,
           reply
   );

  } catch (Exception e) {

   throw new RuntimeException(
           "The reply could not be sent by email. Please check the email configuration."
   );
  }


  // -----------------------------
  // SAVE REPLY
  // -----------------------------

  contactMessage.adminReply = reply;

  contactMessage.replied = true;

  contactMessage.repliedAt =
          LocalDateTime.now();

  contactMessage.read = true;

  messages.save(contactMessage);


  return Map.of(
          "success", true,
          "message",
          "Reply sent successfully to " +
                  contactMessage.email + "."
  );
 }


 // ============================================================
 // FEEDBACK
 // ============================================================

 @PostMapping("/feedback")
 public Map<String, Object> feedback(
         @RequestHeader("Authorization") String h,
         @RequestBody Feedback f
 ) {

  Long uid = tokens.user(
          h.replaceFirst(
                  "^Bearer\\s*",
                  ""
          )
  );

  if (uid == null) {
   throw new RuntimeException(
           "Please sign in to submit feedback."
   );
  }

  if (f.rating == null ||
          f.rating < 1 ||
          f.rating > 5) {

   throw new RuntimeException(
           "Please select a rating from 1 to 5 stars."
   );
  }

  if (f.comment != null &&
          f.comment.length() > 1000) {

   throw new RuntimeException(
           "Feedback comment must be 1000 characters or fewer."
   );
  }

  f.id = null;

  f.userId = uid;

  f.createdAt =
          LocalDateTime.now();

  feedback.save(f);


  return Map.of(
          "success", true,
          "message",
          "Thank you for helping us improve JOBSPHERE."
  );
 }

// ============================================================
// ADMIN - VIEW ALL CONTACT MESSAGES
// ============================================================

 @GetMapping("/admin/contact")
 public List<ContactMessage> getContactMessages(
         @RequestHeader("Authorization") String authorization
 ) {

  if (authorization == null ||
          authorization.isBlank()) {

   throw new RuntimeException(
           "Please sign in as an administrator."
   );
  }

  String token = authorization.replaceFirst(
          "^Bearer\\s*",
          ""
  );

  Long adminId = tokens.user(token);

  if (adminId == null) {
   throw new RuntimeException(
           "Your session has expired. Please sign in again."
   );
  }

  return messages.findAll();
 }
// ============================================================
// ADMIN - DELETE CONTACT MESSAGE
// ============================================================

 @DeleteMapping("/admin/contact/{id}")
 public Map<String, Object> deleteContactMessage(
         @RequestHeader("Authorization") String authorization,
         @PathVariable Long id
 ) {

  if (authorization == null ||
          authorization.isBlank()) {

   throw new RuntimeException(
           "Please sign in as an administrator."
   );
  }

  String token = authorization.replaceFirst(
          "^Bearer\\s*",
          ""
  );

  Long adminId = tokens.user(token);

  if (adminId == null) {

   throw new RuntimeException(
           "Your session has expired. Please sign in again."
   );
  }

  ContactMessage message =
          messages.findById(id)
                  .orElseThrow(() ->
                          new RuntimeException(
                                  "Contact message not found."
                          )
                  );

  messages.delete(message);

  return Map.of(
          "success",
          true,
          "message",
          "Contact message deleted successfully."
  );
 }

}
