package com.jobsphere.controller;

import com.jobsphere.entity.Course;
import com.jobsphere.entity.CourseAttempt;
import com.jobsphere.entity.CourseCertificate;
import com.jobsphere.entity.User;
import com.jobsphere.repository.CourseAttemptRepository;
import com.jobsphere.repository.CourseCertificateRepository;
import com.jobsphere.repository.CourseRepository;
import com.jobsphere.repository.UserRepository;
import com.jobsphere.service.EmailService;
import com.jobsphere.service.TokenService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/courses")
public class AdminCourseController extends BaseController {

 private final CourseRepository courses;
 private final CourseAttemptRepository attempts;
 private final UserRepository userRepo;
 private final CourseCertificateRepository certificates;
 private final EmailService email;

 public AdminCourseController(
         TokenService tokenService,
         UserRepository userRepository,
         CourseRepository courseRepository,
         CourseAttemptRepository courseAttemptRepository,
         CourseCertificateRepository courseCertificateRepository,
         EmailService emailService) {

  super(tokenService, userRepository);

  this.courses = courseRepository;
  this.attempts = courseAttemptRepository;
  this.userRepo = userRepository;
  this.certificates = courseCertificateRepository;
  this.email = emailService;
 }

 // =========================================================
 // ADMIN AUTHENTICATION
 // =========================================================

 private User admin(String authorizationHeader) {

  User user = currentUser(authorizationHeader);

  if (user.role != User.Role.ADMIN) {

   throw new RuntimeException(
           "Administrator access is required."
   );
  }

  return user;
 }

 // =========================================================
 // VIEW COURSE PARTICIPANTS
 // =========================================================

 @GetMapping("/participants")
 public Map<String, Object> participants(
         @RequestHeader("Authorization") String authorizationHeader,
         @RequestParam(required = false) String courseId) {

  admin(authorizationHeader);

  List<Map<String, Object>> output =
          new ArrayList<>();

  List<CourseAttempt> rows;

  // -----------------------------------------------------
  // FILTER BY COURSE
  // -----------------------------------------------------

  if (courseId != null && !courseId.isBlank()) {

   Long selectedCourseId;

   try {

    selectedCourseId =
            Long.valueOf(courseId);

   } catch (NumberFormatException e) {

    throw new RuntimeException(
            "Invalid course ID."
    );
   }

   rows =
           attempts.findByCourseIdOrderByCompletedAtDesc(
                   selectedCourseId
           );

  } else {

   rows =
           attempts.findAll();
  }

  // -----------------------------------------------------
  // BUILD PARTICIPANT LIST
  // -----------------------------------------------------

  for (CourseAttempt attempt : rows) {

   Map<String, Object> row =
           new LinkedHashMap<>();

   row.put(
           "attemptId",
           attempt.id
   );

   row.put(
           "courseId",
           attempt.courseId
   );

   // -------------------------------------------------
   // COURSE
   // -------------------------------------------------

   Course course =
           courses.findById(
                           attempt.courseId
                   )
                   .orElse(null);

   if (course != null) {

    row.put(
            "courseName",
            course.name
    );

    row.put(
            "category",
            course.category
    );

   } else {

    row.put(
            "courseName",
            "Unknown Course"
    );

    row.put(
            "category",
            "-"
    );
   }

   // -------------------------------------------------
   // USER
   // -------------------------------------------------

   User user =
           userRepo.findById(
                           attempt.userId
                   )
                   .orElse(null);

   if (user != null) {

    row.put(
            "userId",
            user.id
    );

    row.put(
            "fullName",
            user.fullName
    );

    row.put(
            "email",
            user.email
    );

   } else {

    row.put(
            "userId",
            attempt.userId
    );

    row.put(
            "fullName",
            "Unknown User"
    );

    row.put(
            "email",
            "-"
    );
   }

   // -------------------------------------------------
   // ATTEMPT
   // -------------------------------------------------

   row.put(
           "score",
           attempt.score
   );

   row.put(
           "passed",
           Boolean.TRUE.equals(
                   attempt.passed
           )
   );

   row.put(
           "correctAnswers",
           attempt.correctAnswers
   );

   row.put(
           "totalQuestions",
           attempt.totalQuestions
   );

   row.put(
           "completedAt",
           attempt.completedAt
   );

   // -------------------------------------------------
   // CERTIFICATE
   // -------------------------------------------------

   CourseCertificate certificate =
           certificates.findByAttemptId(
                           attempt.id
                   )
                   .orElse(null);

   if (certificate != null) {

    row.put(
            "certificateId",
            certificate.id
    );

    row.put(
            "certificateSent",
            certificate.sentAt != null
    );

    row.put(
            "certificateNumber",
            certificate.certificateNumber
    );

   } else {

    row.put(
            "certificateId",
            null
    );

    row.put(
            "certificateSent",
            false
    );

    row.put(
            "certificateNumber",
            null
    );
   }

   output.add(row);
  }

  // -----------------------------------------------------
  // NEWEST FIRST
  // -----------------------------------------------------

  output.sort(
          (a, b) -> {

           Object dateA =
                   a.get("completedAt");

           Object dateB =
                   b.get("completedAt");

           if (dateA == null && dateB == null) {
            return 0;
           }

           if (dateA == null) {
            return 1;
           }

           if (dateB == null) {
            return -1;
           }

           return String.valueOf(dateB)
                   .compareTo(
                           String.valueOf(dateA)
                   );
          }
  );

  return Map.of(
          "success",
          true,

          "courses",
          courses.findByActiveTrueOrderByNameAsc(),

          "participants",
          output
  );
 }

 // =========================================================
 // SEND COURSE CERTIFICATE
 // =========================================================

 @PostMapping("/certificates/{attemptId}/send")
 public Map<String, Object> sendCertificate(
         @RequestHeader("Authorization") String authorizationHeader,
         @PathVariable Long attemptId)
         throws Exception {

  admin(authorizationHeader);

  // -----------------------------------------------------
  // FIND ATTEMPT
  // -----------------------------------------------------

  CourseAttempt attempt =
          attempts.findById(attemptId)
                  .orElseThrow(
                          () ->
                                  new RuntimeException(
                                          "Course attempt not found."
                                  )
                  );

  // -----------------------------------------------------
  // CHECK SCORE
  // -----------------------------------------------------

  if (!Boolean.TRUE.equals(attempt.passed)
          || attempt.score == null
          || attempt.score < 70) {

   throw new RuntimeException(
           "A certificate can only be issued "
                   + "for a score of 70% or higher."
   );
  }

  // -----------------------------------------------------
  // FIND USER
  // -----------------------------------------------------

  User user =
          userRepo.findById(attempt.userId)
                  .orElseThrow(
                          () ->
                                  new RuntimeException(
                                          "User not found."
                                  )
                  );

  // -----------------------------------------------------
  // CHECK EMAIL
  // -----------------------------------------------------

  if (user.email == null
          || user.email.isBlank()) {

   throw new RuntimeException(
           "The user does not have a valid email address."
   );
  }

  // -----------------------------------------------------
  // FIND COURSE
  // -----------------------------------------------------

  Course course =
          courses.findById(attempt.courseId)
                  .orElseThrow(
                          () ->
                                  new RuntimeException(
                                          "Course not found."
                                  )
                  );

  // -----------------------------------------------------
  // FIND OR CREATE CERTIFICATE
  // -----------------------------------------------------

  CourseCertificate certificate =
          certificates.findByAttemptId(
                          attempt.id
                  )
                  .orElseGet(
                          CourseCertificate::new
                  );

  // -----------------------------------------------------
  // CREATE CERTIFICATE
  // -----------------------------------------------------

  if (certificate.id == null) {

   certificate.courseId =
           course.id;

   certificate.userId =
           user.id;

   certificate.attemptId =
           attempt.id;

   certificate.certificateNumber =
           "JS-"
                   + LocalDateTime.now()
                   .getYear()
                   + "-"
                   + String.format(
                   "%06d",
                   attempt.id
           );

   certificate.issuedAt =
           LocalDateTime.now();

   certificate.fileName =
           "JOBSPHERE-"
                   + certificate.certificateNumber
                   + ".pdf";

   certificate.contentType =
           "application/pdf";

   certificate.fileData =
           createCertificatePdf(
                   user,
                   course,
                   attempt,
                   certificate.certificateNumber
           );

   certificates.save(certificate);
  }

  // -----------------------------------------------------
  // SEND EMAIL
  // -----------------------------------------------------

  email.sendCourseCertificate(
          user.email,
          user.fullName,
          course.name,
          attempt.score,
          certificate.certificateNumber,
          certificate.fileData,
          certificate.fileName
  );

  // -----------------------------------------------------
  // MARK CERTIFICATE AS SENT
  // -----------------------------------------------------

  certificate.sentAt =
          LocalDateTime.now();

  certificates.save(certificate);
  attempt.certificateSent = true;
  attempts.save(attempt);

  return Map.of(
          "success",
          true,

          "message",
          "Professional certificate sent successfully to "
                  + user.email
                  + ".",

          "certificateNumber",
          certificate.certificateNumber,

          "certificateId",
          certificate.id
  );
 }

 // =========================================================
 // DOWNLOAD CERTIFICATE
 // =========================================================

 @GetMapping("/certificates/{id}")
 public ResponseEntity<byte[]> downloadCertificate(
         @RequestHeader("Authorization") String authorizationHeader,
         @PathVariable Long id) {

  admin(authorizationHeader);

  CourseCertificate certificate =
          certificates.findById(id)
                  .orElseThrow(
                          () ->
                                  new RuntimeException(
                                          "Certificate not found."
                                  )
                  );

  String fileName =
          certificate.fileName;

  if (fileName == null
          || fileName.isBlank()) {

   fileName =
           "JOBSPHERE-Certificate-"
                   + certificate.id
                   + ".pdf";
  }

  return ResponseEntity
          .ok()
          .contentType(
                  MediaType.APPLICATION_PDF
          )
          .header(
                  HttpHeaders.CONTENT_DISPOSITION,
                  "inline; filename=\""
                          + fileName.replace("\"", "")
                          + "\""
          )
          .body(
                  certificate.fileData
          );
 }

 // =========================================================
 // CREATE PDF CERTIFICATE
 // PDFBOX 2.0.30
 // =========================================================

 private byte[] createCertificatePdf(
         User user,
         Course course,
         CourseAttempt attempt,
         String certificateNumber)
         throws Exception {

  ByteArrayOutputStream output = new ByteArrayOutputStream();

  try (PDDocument document = new PDDocument()) {
   PDRectangle landscape = new PDRectangle(
           PDRectangle.A4.getHeight(),
           PDRectangle.A4.getWidth()
   );
   PDPage page = new PDPage(landscape);
   document.addPage(page);

   PDType1Font titleFont = PDType1Font.TIMES_ROMAN;
   PDType1Font nameFont = PDType1Font.TIMES_ITALIC;
   PDType1Font boldFont = PDType1Font.HELVETICA_BOLD;
   PDType1Font regularFont = PDType1Font.HELVETICA;
   PDType1Font italicFont = PDType1Font.HELVETICA_OBLIQUE;

   final int GOLD_R = 218, GOLD_G = 183, GOLD_B = 105;
   final int PALE_GOLD_R = 245, PALE_GOLD_G = 224, PALE_GOLD_B = 167;
   final int NAVY_R = 20, NAVY_G = 18, NAVY_B = 67;
   final int PURPLE_R = 48, PURPLE_G = 15, PURPLE_B = 62;
   final int CREAM_R = 255, CREAM_G = 252, CREAM_B = 232;
   final int INK_R = 35, INK_G = 31, INK_B = 26;
   final int MUTED_R = 105, MUTED_G = 96, MUTED_B = 78;

   float w = landscape.getWidth();
   float h = landscape.getHeight();

   try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
    // Deep plum-to-navy style background, as in the supplied reference.
    stream.setNonStrokingColor(PURPLE_R, PURPLE_G, PURPLE_B);
    stream.addRect(0, 0, w, h);
    stream.fill();

    // Dark navy header panel.
    stream.setNonStrokingColor(NAVY_R, NAVY_G, NAVY_B);
    stream.addRect(0, h - 270, w, 270);
    stream.fill();

    // Warm ivory lower panel with a broad curved upper edge.
    stream.setNonStrokingColor(CREAM_R, CREAM_G, CREAM_B);
    stream.moveTo(0, 0);
    stream.lineTo(w, 0);
    stream.lineTo(w, h - 305);
    stream.curveTo(w - 125, h - 285, w - 235, h - 300, w - 355, h - 320);
    stream.curveTo(w - 510, h - 347, 230, h - 338, 0, h - 305);
    stream.closePath();
    stream.fill();

    // Gold wave accents tracing the curved transition.
    stream.setStrokingColor(GOLD_R, GOLD_G, GOLD_B);
    stream.setLineWidth(3.2f);
    stream.moveTo(0, h - 296);
    stream.curveTo(190, h - 326, 280, h - 328, 425, h - 318);
    stream.curveTo(590, h - 307, 700, h - 284, w, h - 296);
    stream.stroke();

    stream.setStrokingColor(PALE_GOLD_R, PALE_GOLD_G, PALE_GOLD_B);
    stream.setLineWidth(1.0f);
    stream.moveTo(0, h - 305);
    stream.curveTo(190, h - 335, 285, h - 337, 425, h - 327);
    stream.curveTo(590, h - 316, 700, h - 293, w, h - 305);
    stream.stroke();

    // Elegant double gold frame.
    stream.setStrokingColor(GOLD_R, GOLD_G, GOLD_B);
    stream.setLineWidth(2.0f);
    stream.addRect(24, 24, w - 48, h - 48);
    stream.stroke();

    stream.setStrokingColor(PALE_GOLD_R, PALE_GOLD_G, PALE_GOLD_B);
    stream.setLineWidth(0.65f);
    stream.addRect(31, 31, w - 62, h - 62);
    stream.stroke();

    // Minimal corner details.
    stream.setStrokingColor(GOLD_R, GOLD_G, GOLD_B);
    stream.setLineWidth(1.1f);
    stream.moveTo(42, h - 42);
    stream.lineTo(70, h - 42);
    stream.lineTo(70, h - 70);
    stream.stroke();
    stream.moveTo(w - 42, 42);
    stream.lineTo(w - 70, 42);
    stream.lineTo(w - 70, 70);
    stream.stroke();

    // Main heading in the dark header.
    drawCenteredText(stream, "C E R T I F I C A T E",
            titleFont, 22, h - 91,
            PALE_GOLD_R, PALE_GOLD_G, PALE_GOLD_B);
    drawCenteredText(stream, "O F   C O M P L E T I O N",
            boldFont, 6.8f, h - 110,
            255, 247, 225);

    // Small gold separator under the heading.
    stream.setStrokingColor(GOLD_R, GOLD_G, GOLD_B);
    stream.setLineWidth(0.8f);
    stream.moveTo(w / 2f - 88, h - 123);
    stream.lineTo(w / 2f + 88, h - 123);
    stream.stroke();

    // Central premium award medallion, overlapping the colour boundary.
    float cx = w / 2f;
    float cy = h - 286;
    stream.setNonStrokingColor(34, 22, 65);
    stream.setStrokingColor(GOLD_R, GOLD_G, GOLD_B);
    stream.setLineWidth(2.2f);
    circlePath(stream, cx, cy, 31);
    stream.fillAndStroke();

    stream.setStrokingColor(PALE_GOLD_R, PALE_GOLD_G, PALE_GOLD_B);
    stream.setLineWidth(0.8f);
    circlePath(stream, cx, cy, 25);
    stream.stroke();

    drawCenteredTextAt(stream, "JOBSPHERE", boldFont, 5.2f,
            cx, cy + 7, PALE_GOLD_R, PALE_GOLD_G, PALE_GOLD_B);
    drawCenteredTextAt(stream, "PREMIUM", boldFont, 7.0f,
            cx, cy - 2, 255, 247, 225);
    drawCenteredTextAt(stream, "AWARD", boldFont, 6.0f,
            cx, cy - 11, PALE_GOLD_R, PALE_GOLD_G, PALE_GOLD_B);

    // Fine rays around the medallion.
    stream.setStrokingColor(GOLD_R, GOLD_G, GOLD_B);
    stream.setLineWidth(0.7f);
    for (int i = 0; i < 12; i++) {
     double angle = Math.PI * 2.0 * i / 12.0;
     float x1 = cx + (float) Math.cos(angle) * 34f;
     float y1 = cy + (float) Math.sin(angle) * 34f;
     float x2 = cx + (float) Math.cos(angle) * 39f;
     float y2 = cy + (float) Math.sin(angle) * 39f;
     stream.moveTo(x1, y1);
     stream.lineTo(x2, y2);
    }
    stream.stroke();

    // Learner and course details on the light panel.
    drawCenteredText(stream, "P R O U D L Y   P R E S E N T E D   T O",
            boldFont, 7.0f, h - 354,
            INK_R, INK_G, INK_B);

    String learnerName = safeText(user.fullName);
    if (learnerName.length() > 42) {
     learnerName = learnerName.substring(0, 39) + "...";
    }
    drawCenteredText(stream, learnerName, nameFont, 25,
            h - 389, 148, 112, 54);

    String courseName = safeText(course.name);
    if (courseName.length() > 76) {
     courseName = courseName.substring(0, 73) + "...";
    }
    drawCenteredText(stream, "HAS SUCCESSFULLY COMPLETED",
            boldFont, 7.2f, h - 408,
            INK_R, INK_G, INK_B);
    drawCenteredText(stream, courseName, boldFont, 11.0f,
            h - 427, NAVY_R, NAVY_G, NAVY_B);

    drawCenteredText(stream,
            "Assessment Score: " + attempt.score + "%",
            regularFont, 7.4f, h - 443,
            MUTED_R, MUTED_G, MUTED_B);

    drawCenteredText(stream,
            "Certificate No. " + certificateNumber,
            regularFont, 6.2f, h - 457,
            MUTED_R, MUTED_G, MUTED_B);

    // Date and signature lines, like the reference design.
    float lineY = 65f;
    stream.setStrokingColor(125, 111, 83);
    stream.setLineWidth(0.65f);
    stream.moveTo(105, lineY);
    stream.lineTo(225, lineY);
    stream.moveTo(w - 225, lineY);
    stream.lineTo(w - 105, lineY);
    stream.stroke();

    drawCenteredTextAt(stream, LocalDateTime.now().toLocalDate().toString(),
            regularFont, 6.5f, 165, lineY + 8,
            INK_R, INK_G, INK_B);
    drawCenteredTextAt(stream, "DATE", boldFont, 5.8f,
            165, lineY - 11, MUTED_R, MUTED_G, MUTED_B);

    drawCenteredTextAt(stream, "JOBSPHERE ADMINISTRATION",
            regularFont, 6.5f, w - 165, lineY + 8,
            INK_R, INK_G, INK_B);
    drawCenteredTextAt(stream, "SIGNATURE",
            boldFont, 5.8f, w - 165, lineY - 11,
            MUTED_R, MUTED_G, MUTED_B);
   }

   document.save(output);
  }

  return output.toByteArray();
 }

 // =========================================================
 // CENTER TEXT
 // =========================================================

 private void drawCenteredText(
         PDPageContentStream stream,
         String text,
         PDType1Font font,
         float fontSize,
         float y)
         throws Exception {
  drawCenteredText(stream, text, font, fontSize, y, 9, 33, 63);
 }

 private void drawCenteredText(
         PDPageContentStream stream,
         String text,
         PDType1Font font,
         float fontSize,
         float y,
         int r,
         int g,
         int b)
         throws Exception {

  String safe = safeText(text);

  float pageWidth = PDRectangle.A4.getHeight();
  float textWidth = font.getStringWidth(safe) / 1000 * fontSize;
  float x = (pageWidth - textWidth) / 2;

  stream.beginText();
  stream.setNonStrokingColor(r, g, b);
  stream.setFont(font, fontSize);
  stream.newLineAtOffset(x, y);
  stream.showText(safe);
  stream.endText();
 }

 private void drawCenteredTextAt(
         PDPageContentStream stream,
         String text,
         PDType1Font font,
         float fontSize,
         float centerX,
         float y,
         int r,
         int g,
         int b)
         throws Exception {

  String safe = safeText(text);
  float textWidth = font.getStringWidth(safe) / 1000 * fontSize;
  float x = centerX - textWidth / 2f;

  stream.beginText();
  stream.setNonStrokingColor(r, g, b);
  stream.setFont(font, fontSize);
  stream.newLineAtOffset(x, y);
  stream.showText(safe);
  stream.endText();
 }



 private void circlePath(
         PDPageContentStream stream,
         float cx,
         float cy,
         float radius)
         throws Exception {

  final float k = 0.5522848f * radius;

  stream.moveTo(cx + radius, cy);
  stream.curveTo(cx + radius, cy + k, cx + k, cy + radius, cx, cy + radius);
  stream.curveTo(cx - k, cy + radius, cx - radius, cy + k, cx - radius, cy);
  stream.curveTo(cx - radius, cy - k, cx - k, cy - radius, cx, cy - radius);
  stream.curveTo(cx + k, cy - radius, cx + radius, cy - k, cx + radius, cy);
  stream.closePath();
 }

 // =========================================================
 // SAFE PDF TEXT
 // =========================================================

 private String safeText(String value) {

  if (value == null
          || value.isBlank()) {

   return "JOBSPHERE Learner";
  }

  return value
          .replace("\n", " ")
          .replace("\r", " ")
          .replace("–", "-")
          .replace("—", "-")
          .replace("“", "\"")
          .replace("”", "\"")
          .replace("‘", "'")
          .replace("’", "'");
 }
}