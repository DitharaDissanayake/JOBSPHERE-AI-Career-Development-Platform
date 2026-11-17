package com.jobsphere.controller;

import com.jobsphere.entity.User;
import com.jobsphere.entity.UserSettings;
import com.jobsphere.repository.UserRepository;
import com.jobsphere.repository.UserSettingsRepository;
import com.jobsphere.service.TokenService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/settings")
public class SettingsController extends BaseController {

 private final UserRepository userRepository;
 private final UserSettingsRepository settings;
 private final PasswordEncoder passwordEncoder;

 public SettingsController(
         TokenService tokenService,
         UserRepository userRepository,
         UserSettingsRepository settings,
         PasswordEncoder passwordEncoder
 ) {
  super(tokenService, userRepository);

  this.userRepository = userRepository;
  this.settings = settings;
  this.passwordEncoder = passwordEncoder;
 }

 // =========================================================
 // GET SETTINGS
 // =========================================================

 @GetMapping
 public Map<String, Object> getSettings(
         @RequestHeader("Authorization") String authorization
 ) {

  Long userId = currentId(authorization);
  User user = currentUser(authorization);

  UserSettings userSettings =
          settings.findByUserId(userId)
                  .orElseGet(() -> {
                   UserSettings newSettings = defaults(userId);
                   return settings.save(newSettings);
                  });

  Map<String, Object> userData = new HashMap<>();

  userData.put("id", user.id);
  userData.put("fullName", user.fullName);
  userData.put("email", user.email);
  userData.put("role", user.role.toString());

  Map<String, Object> response = new HashMap<>();

  response.put("success", true);
  response.put("user", userData);
  response.put("settings", userSettings);

  return response;
 }

 // =========================================================
 // SAVE SETTINGS
 // =========================================================

 @PutMapping
 public Map<String, Object> saveSettings(
         @RequestHeader("Authorization") String authorization,
         @RequestBody UserSettings input
 ) {

  Long userId = currentId(authorization);

  UserSettings userSettings =
          settings.findByUserId(userId)
                  .orElseGet(() -> defaults(userId));

  userSettings.emailNotifications =
          Boolean.TRUE.equals(input.emailNotifications);

  userSettings.courseReminders =
          Boolean.TRUE.equals(input.courseReminders);

  userSettings.careerRecommendations =
          Boolean.TRUE.equals(input.careerRecommendations);

  userSettings.profileVisibility =
          Boolean.TRUE.equals(input.profileVisibility);

  if (input.theme != null &&
          Set.of("system", "light", "dark")
                  .contains(input.theme.toLowerCase())) {

   userSettings.theme =
           input.theme.toLowerCase();
  }

  if (input.language != null &&
          !input.language.trim().isEmpty()) {

   userSettings.language =
           input.language.trim();
  }

  userSettings.updatedAt =
          LocalDateTime.now();

  settings.save(userSettings);

  Map<String, Object> response =
          new HashMap<>();

  response.put("success", true);

  response.put(
          "message",
          "Your settings have been saved successfully."
  );

  response.put(
          "settings",
          userSettings
  );

  return response;
 }

 // =========================================================
 // CHANGE PASSWORD
 // =========================================================

 @PutMapping("/change-password")
 public Map<String, Object> changePassword(
         @RequestHeader("Authorization") String authorization,
         @RequestBody ChangePasswordRequest request
 ) {

  User user = currentUser(authorization);

  if (request == null) {
   return error("Password information is required.");
  }

  // Current password
  if (request.currentPassword == null ||
          request.currentPassword.isBlank()) {

   return error(
           "Current password is required."
   );
  }

  // New password
  if (request.newPassword == null ||
          request.newPassword.isBlank()) {

   return error(
           "New password is required."
   );
  }

  // Confirm password
  if (request.confirmPassword == null ||
          request.confirmPassword.isBlank()) {

   return error(
           "Please confirm your new password."
   );
  }

  // Minimum length
  if (request.newPassword.length() < 8) {

   return error(
           "New password must contain at least 8 characters."
   );
  }

  // Uppercase
  if (!request.newPassword.matches(".*[A-Z].*")) {

   return error(
           "New password must contain at least one uppercase letter."
   );
  }

  // Lowercase
  if (!request.newPassword.matches(".*[a-z].*")) {

   return error(
           "New password must contain at least one lowercase letter."
   );
  }

  // Number
  if (!request.newPassword.matches(".*[0-9].*")) {

   return error(
           "New password must contain at least one number."
   );
  }

  // Confirm password
  if (!request.newPassword.equals(
          request.confirmPassword)) {

   return error(
           "New password and confirmation password do not match."
   );
  }

  // New password must be different
  if (request.currentPassword.equals(
          request.newPassword)) {

   return error(
           "New password must be different from your current password."
   );
  }

  // Check current password
  if (!passwordEncoder.matches(
          request.currentPassword,
          user.passwordHash)) {

   return error(
           "Your current password is incorrect."
   );
  }

  // Encrypt new password
  user.passwordHash =
          passwordEncoder.encode(
                  request.newPassword
          );

  // Save user
  userRepository.save(user);

  Map<String, Object> response =
          new HashMap<>();

  response.put("success", true);

  response.put(
          "message",
          "Your password has been changed successfully."
  );

  return response;
 }

 // =========================================================
 // DEFAULT SETTINGS
 // =========================================================

 private UserSettings defaults(Long userId) {

  UserSettings userSettings =
          new UserSettings();

  userSettings.userId = userId;

  userSettings.emailNotifications = true;

  userSettings.courseReminders = true;

  userSettings.careerRecommendations = true;

  userSettings.profileVisibility = true;

  userSettings.theme = "system";

  userSettings.language = "English";

  userSettings.updatedAt =
          LocalDateTime.now();

  return userSettings;
 }

 // =========================================================
 // ERROR RESPONSE
 // =========================================================

 private Map<String, Object> error(String message) {

  Map<String, Object> response =
          new HashMap<>();

  response.put("success", false);

  response.put(
          "message",
          message
  );

  return response;
 }

 // =========================================================
 // PASSWORD REQUEST
 // =========================================================

 public static class ChangePasswordRequest {

  public String currentPassword;

  public String newPassword;

  public String confirmPassword;
 }
}