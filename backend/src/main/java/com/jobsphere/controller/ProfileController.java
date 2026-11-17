package com.jobsphere.controller;

import com.jobsphere.entity.Certificate;
import com.jobsphere.entity.DashboardInsight;
import com.jobsphere.entity.User;
import com.jobsphere.entity.UserProfile;
import com.jobsphere.repository.CertificateRepository;
import com.jobsphere.repository.UserProfileRepository;
import com.jobsphere.repository.UserRepository;
import com.jobsphere.service.ProfileAiService;
import com.jobsphere.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController extends BaseController {

 private final UserProfileRepository profiles;
 private final CertificateRepository certs;
 private final ProfileAiService ai;

 public ProfileController(
         TokenService tokenService,
         UserRepository userRepository,
         UserProfileRepository profileRepository,
         CertificateRepository certificateRepository,
         ProfileAiService profileAiService) {

  super(tokenService, userRepository);

  this.profiles = profileRepository;
  this.certs = certificateRepository;
  this.ai = profileAiService;
 }

 /**
  * Get the currently logged-in user's profile.
  */
 @GetMapping
 public Map<String, Object> get(
         @RequestHeader("Authorization") String authorizationHeader) {

  User user = currentUser(authorizationHeader);

  Optional<UserProfile> profile =
          profiles.findByUserId(user.id);

  Map<String, Object> userData =
          new LinkedHashMap<>();

  userData.put("id", user.id);
  userData.put("fullName", user.fullName);
  userData.put("email", user.email);
  userData.put("role", user.role.name());

  Map<String, Object> result =
          new LinkedHashMap<>();

  result.put("exists", profile.isPresent());
  result.put("user", userData);
  result.put(
          "profile",
          profile.orElseGet(UserProfile::new)
  );
  result.put(
          "completeness",
          profile.map(this::complete).orElse(0)
  );

  return result;
 }

 /**
  * Create or update the current user's profile.
  */
 @PutMapping
 public Map<String, Object> save(
         @RequestHeader("Authorization") String authorizationHeader,
         @RequestBody UserProfile input) {

  User user = currentUser(authorizationHeader);

  validate(input);

  UserProfile profile =
          profiles.findByUserId(user.id)
                  .orElseGet(UserProfile::new);

  profile.userId = user.id;

  profile.phone = input.phone;
  profile.location = input.location;
  profile.university = input.university;
  profile.degree = input.degree;
  profile.graduationYear = input.graduationYear;
  profile.employmentStatus = input.employmentStatus;
  profile.headline = input.headline;
  profile.education = input.education;
  profile.skills = input.skills;
  profile.softSkills = input.softSkills;
  profile.experience = input.experience;
  profile.projects = input.projects;
  profile.certifications = input.certifications;
  profile.careerInterest = input.careerInterest;
  profile.careerGoal = input.careerGoal;
  profile.portfolioUrl = input.portfolioUrl;
  profile.linkedinUrl = input.linkedinUrl;
  profile.githubUrl = input.githubUrl;
  profile.updatedAt = LocalDateTime.now();

  profiles.save(profile);

  /*
   * After saving the profile, ask Ollama to create
   * personalized dashboard information.
   */
  try {

   DashboardInsight dashboardInsight =
           ai.analyze(user, profile);

   Map<String, Object> result =
           new LinkedHashMap<>();

   result.put("success", true);
   result.put(
           "message",
           "Profile saved and your personalized AI dashboard has been refreshed."
   );
   result.put("aiUpdated", true);
   result.put(
           "dashboardInsight",
           dashboardInsight.insightJson
   );

   return result;

  } catch (Exception exception) {

   /*
    * The profile has already been saved successfully.
    * Ollama being unavailable should not make the
    * profile update fail.
    */
   Map<String, Object> result =
           new LinkedHashMap<>();

   result.put("success", true);
   result.put(
           "message",
           "Profile saved. Start Ollama to refresh your personalized AI dashboard."
   );
   result.put("aiUpdated", false);
   result.put(
           "aiMessage",
           exception.getMessage()
   );

   return result;
  }
 }

 /**
  * Upload a certificate PDF.
  */
 @PostMapping("/certificates")
 public Map<String, Object> upload(
         @RequestHeader("Authorization") String authorizationHeader,
         @RequestParam("file") MultipartFile file)
         throws Exception {

  Long userId = currentId(authorizationHeader);

  if (file == null || file.isEmpty()) {
   throw new RuntimeException(
           "Please choose a certificate PDF."
   );
  }

  String originalFileName =
          Objects.requireNonNullElse(
                  file.getOriginalFilename(),
                  "certificate.pdf"
          );

  String lowerFileName =
          originalFileName.toLowerCase(Locale.ROOT);

  if (!"application/pdf".equalsIgnoreCase(
          file.getContentType())
          && !lowerFileName.endsWith(".pdf")) {

   throw new RuntimeException(
           "Only PDF certificates are supported."
   );
  }

  if (file.getSize() > 5 * 1024 * 1024) {
   throw new RuntimeException(
           "Certificate must be 5 MB or smaller."
   );
  }

  Certificate certificate =
          new Certificate();

  certificate.userId = userId;
  certificate.fileName = originalFileName;
  certificate.contentType = "application/pdf";
  certificate.fileSize = file.getSize();
  certificate.fileData = file.getBytes();
  certificate.uploadedAt = LocalDateTime.now();

  certs.save(certificate);

  Map<String, Object> result =
          new LinkedHashMap<>();

  result.put("success", true);
  result.put("id", certificate.id);
  result.put(
          "fileName",
          certificate.fileName
  );

  return result;
 }

 /**
  * List only the certificates belonging to
  * the currently logged-in user.
  */
 @GetMapping("/certificates")
 public List<Map<String, Object>> list(
         @RequestHeader("Authorization") String authorizationHeader) {

  Long userId = currentId(authorizationHeader);

  return certs.findAll()
          .stream()
          .filter(certificate ->
                  userId.equals(certificate.userId)
          )
          .sorted(
                  Comparator.comparing(
                          certificate ->
                                  certificate.uploadedAt,
                          Comparator.reverseOrder()
                  )
          )
          .map(certificate -> {

           /*
            * Explicit Map<String,Object> avoids
            * the Map.of() generic inference error.
            */
           Map<String, Object> result =
                   new LinkedHashMap<>();

           result.put(
                   "id",
                   certificate.id
           );

           result.put(
                   "fileName",
                   certificate.fileName
           );

           result.put(
                   "fileSize",
                   certificate.fileSize
           );

           result.put(
                   "uploadedAt",
                   certificate.uploadedAt
           );

           return result;
          })
          .toList();
 }

 /**
  * View/download a certificate PDF.
  */
 @GetMapping("/certificates/{id}")
 public ResponseEntity<byte[]> pdf(
         @RequestHeader("Authorization") String authorizationHeader,
         @PathVariable Long id) {

  Long userId =
          currentId(authorizationHeader);

  Certificate certificate =
          certs.findById(id)
                  .orElseThrow(() ->
                          new RuntimeException(
                                  "Certificate not found."
                          )
                  );

  /*
   * Users can access only their own certificates.
   */
  if (!userId.equals(certificate.userId)) {
   throw new RuntimeException(
           "You can only access your own certificate."
   );
  }

  return ResponseEntity
          .ok()
          .contentType(MediaType.APPLICATION_PDF)
          .header(
                  HttpHeaders.CONTENT_DISPOSITION,
                  "inline; filename=\"" +
                          certificate.fileName
                                  .replace("\"", "") +
                          "\""
          )
          .body(certificate.fileData);
 }

 /**
  * Delete a certificate.
  */
 @DeleteMapping("/certificates/{id}")
 public Map<String, Object> delete(
         @RequestHeader("Authorization") String authorizationHeader,
         @PathVariable Long id) {

  Long userId =
          currentId(authorizationHeader);

  Certificate certificate =
          certs.findById(id)
                  .orElseThrow(() ->
                          new RuntimeException(
                                  "Certificate not found."
                          )
                  );

  /*
   * Prevent users from deleting another user's
   * certificate.
   */
  if (!userId.equals(certificate.userId)) {
   throw new RuntimeException(
           "Unauthorized certificate access."
   );
  }

  certs.delete(certificate);

  return Map.of(
          "success",
          true,
          "message",
          "Certificate deleted successfully."
  );
 }

 /**
  * Validate profile information.
  */
 private void validate(UserProfile profile) {

  if (profile == null) {
   throw new RuntimeException(
           "Profile data is required."
   );
  }

  /*
   * Sri Lankan mobile number:
   * 07XXXXXXXX
   */
  if (profile.phone != null
          && !profile.phone.isBlank()
          && !profile.phone.matches("07\\d{8}")) {

   throw new RuntimeException(
           "Phone number must contain exactly 10 digits and start with 07."
   );
  }

  /*
   * Graduation year validation.
   */
  if (profile.graduationYear != null
          && (profile.graduationYear < 1950
          || profile.graduationYear > 2100)) {

   throw new RuntimeException(
           "Graduation year must be between 1950 and 2100."
   );
  }

  /*
   * Portfolio URL validation.
   */
  if (profile.portfolioUrl != null
          && !profile.portfolioUrl.isBlank()
          && !profile.portfolioUrl.matches(
          "https?://.+")) {

   throw new RuntimeException(
           "Portfolio URL must start with http:// or https://."
   );
  }

  /*
   * LinkedIn URL validation.
   */
  if (profile.linkedinUrl != null
          && !profile.linkedinUrl.isBlank()
          && !profile.linkedinUrl.matches(
          "https?://.+")) {

   throw new RuntimeException(
           "LinkedIn URL must start with http:// or https://."
   );
  }

  /*
   * GitHub URL validation.
   */
  if (profile.githubUrl != null
          && !profile.githubUrl.isBlank()
          && !profile.githubUrl.matches(
          "https?://.+")) {

   throw new RuntimeException(
           "GitHub URL must start with http:// or https://."
   );
  }
 }

 /**
  * Calculate profile completeness.
  */
 private int complete(UserProfile profile) {

  String[] fields = {
          profile.phone,
          profile.location,
          profile.university,
          profile.degree,
          String.valueOf(
                  profile.graduationYear == null
                          ? ""
                          : profile.graduationYear
          ),
          profile.education,
          profile.skills,
          profile.experience,
          profile.projects,
          profile.careerInterest,
          profile.careerGoal
  };

  int completed = 0;

  for (String value : fields) {

   if (value != null
           && !value.isBlank()
           && !"null".equals(value)) {

    completed++;
   }
  }

  return Math.round(
          completed * 100f / fields.length
  );
 }
}