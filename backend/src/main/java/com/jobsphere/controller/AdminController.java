package com.jobsphere.controller;

import com.jobsphere.entity.ContactMessage;
import com.jobsphere.entity.Feedback;
import com.jobsphere.entity.User;
import com.jobsphere.repository.ContactMessageRepository;
import com.jobsphere.repository.FeedbackRepository;
import com.jobsphere.repository.UserRepository;
import com.jobsphere.service.EmailService;
import com.jobsphere.service.TokenService;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController extends BaseController {

 private final ContactMessageRepository messages;
 private final FeedbackRepository feedback;
 private final EmailService emailService;

 public AdminController(
         TokenService tokenService,
         UserRepository userRepository,
         ContactMessageRepository messageRepository,
         FeedbackRepository feedbackRepository,
         EmailService emailService) {

  super(tokenService, userRepository);

  this.messages = messageRepository;
  this.feedback = feedbackRepository;
  this.emailService = emailService;
 }

 // =========================================================
 // ADMIN ACCESS
 // =========================================================

 private User requireAdmin(String authorizationHeader) {

  User user = currentUser(authorizationHeader);

  if (user.role != User.Role.ADMIN) {

   throw new RuntimeException(
           "Administrator access is required."
   );
  }

  return user;
 }

 // =========================================================
 // ADMIN DASHBOARD SUMMARY
 // =========================================================

 @GetMapping("/summary")
 public Map<String, Object> summary(
         @RequestHeader("Authorization") String authorizationHeader) {

  requireAdmin(authorizationHeader);

  long totalUsers =
          users.countByRole(User.Role.USER);

  long totalAdmins =
          users.countByRole(User.Role.ADMIN);

  long unreadMessages =
          messages.findAll()
                  .stream()
                  .filter(message ->
                          !Boolean.TRUE.equals(message.read))
                  .count();

  Map<String, Object> result =
          new LinkedHashMap<>();

  result.put("users", totalUsers);
  result.put("admins", totalAdmins);
  result.put("messages", messages.count());
  result.put("unreadMessages", unreadMessages);
  result.put("feedback", feedback.count());

  return result;
 }

 // =========================================================
 // USERS
 // =========================================================

 @GetMapping("/users")
 public List<Map<String, Object>> getUsers(
         @RequestHeader("Authorization") String authorizationHeader) {

  requireAdmin(authorizationHeader);

  return users.findAll()
          .stream()
          .sorted(
                  Comparator.comparing(
                          user -> user.createdAt,
                          Comparator.nullsLast(
                                  Comparator.reverseOrder()
                          )
                  )
          )
          .map(user -> {

           Map<String, Object> result =
                   new LinkedHashMap<>();

           result.put("id", user.id);
           result.put("fullName", user.fullName);
           result.put("email", user.email);
           result.put(
                   "role",
                   user.role != null
                           ? user.role.name()
                           : "USER"
           );
           result.put("active", user.active);
           result.put("createdAt", user.createdAt);

           return result;

          })
          .toList();
 }

 // =========================================================
 // UPDATE USER STATUS
 // =========================================================

 @PutMapping("/users/{id}/status")
 public Map<String, Object> updateUserStatus(
         @RequestHeader("Authorization") String authorizationHeader,
         @PathVariable Long id,
         @RequestParam boolean active) {

  requireAdmin(authorizationHeader);

  User user =
          users.findById(id)
                  .orElseThrow(() ->
                          new RuntimeException(
                                  "User not found."
                          )
                  );

  if (user.role == User.Role.ADMIN) {

   throw new RuntimeException(
           "Administrator accounts cannot be disabled here."
   );
  }

  user.active = active;

  users.save(user);

  return Map.of(
          "success",
          true,
          "message",
          active
                  ? "User activated successfully."
                  : "User deactivated successfully."
  );
 }

 // =========================================================
 // CONTACT MESSAGES
 // =========================================================

 @GetMapping("/messages")
 public List<ContactMessage> getMessages(
         @RequestHeader("Authorization") String authorizationHeader) {

  requireAdmin(authorizationHeader);

  return messages.findAll()
          .stream()
          .sorted(
                  Comparator.comparing(
                          message -> message.createdAt,
                          Comparator.nullsLast(
                                  Comparator.reverseOrder()
                          )
                  )
          )
          .toList();
 }

 // =========================================================
 // MARK MESSAGE READ
 // =========================================================

 @PutMapping("/messages/{id}/read")
 public Map<String, Object> markMessageAsRead(
         @RequestHeader("Authorization") String authorizationHeader,
         @PathVariable Long id) {

  requireAdmin(authorizationHeader);

  ContactMessage message =
          messages.findById(id)
                  .orElseThrow(() ->
                          new RuntimeException(
                                  "Message not found."
                          )
                  );

  message.read = true;

  messages.save(message);

  return Map.of(
          "success",
          true,
          "message",
          "Message marked as read."
  );
 }

 // =========================================================
 // REPLY TO MESSAGE
 // =========================================================

 @PostMapping("/messages/{id}/reply")
 public Map<String, Object> replyToMessage(
         @RequestHeader("Authorization") String authorizationHeader,
         @PathVariable Long id,
         @RequestBody Map<String, String> request) {

  requireAdmin(authorizationHeader);

  ContactMessage message =
          messages.findById(id)
                  .orElseThrow(() ->
                          new RuntimeException(
                                  "Message not found."
                          )
                  );

  String reply = request.get("reply");

  if (reply == null || reply.trim().length() < 2) {

   throw new RuntimeException(
           "Please enter a reply message."
   );
  }

  reply = reply.trim();

  if (reply.length() > 5000) {

   throw new RuntimeException(
           "Reply must be 5000 characters or fewer."
   );
  }

  message.adminReply = reply;
  message.repliedAt = LocalDateTime.now();
  message.read = true;

  messages.save(message);

  try {

   emailService.sendContactReply(
           message.email,
           message.name,
           message.subject,
           reply
   );

  } catch (Exception e) {

   throw new RuntimeException(
           "Reply was saved, but the email could not be sent. "
                   + "Please check the email configuration."
   );
  }

  return Map.of(
          "success",
          true,
          "message",
          "Reply sent successfully to "
                  + message.email
                  + "."
  );
 }

 // =========================================================
 // DELETE MESSAGE
 // =========================================================

 @DeleteMapping("/messages/{id}")
 public Map<String, Object> deleteMessage(
         @RequestHeader("Authorization") String authorizationHeader,
         @PathVariable Long id) {

  requireAdmin(authorizationHeader);

  if (!messages.existsById(id)) {

   throw new RuntimeException(
           "Message not found."
   );
  }

  messages.deleteById(id);

  return Map.of(
          "success",
          true,
          "message",
          "Message deleted successfully."
  );
 }

 // =========================================================
 // FEEDBACK
 // =========================================================

 @GetMapping("/feedback")
 public List<Feedback> getFeedback(
         @RequestHeader("Authorization") String authorizationHeader) {

  requireAdmin(authorizationHeader);

  return feedback.findAll()
          .stream()
          .sorted(
                  Comparator.comparing(
                          item -> item.createdAt,
                          Comparator.nullsLast(
                                  Comparator.reverseOrder()
                          )
                  )
          )
          .toList();
 }

 // =========================================================
 // DELETE FEEDBACK
 // =========================================================

 @DeleteMapping("/feedback/{id}")
 public Map<String, Object> deleteFeedback(
         @RequestHeader("Authorization") String authorizationHeader,
         @PathVariable Long id) {

  requireAdmin(authorizationHeader);

  if (!feedback.existsById(id)) {

   throw new RuntimeException(
           "Feedback not found."
   );
  }

  feedback.deleteById(id);

  return Map.of(
          "success",
          true,
          "message",
          "Feedback deleted successfully."
  );
 }
}
