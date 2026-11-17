package com.jobsphere.controller;

import com.jobsphere.entity.*;
import com.jobsphere.repository.*;
import com.jobsphere.service.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController extends BaseController {

 private final ResumeRepository resumes;
 private final ResumeAnalysisRepository analyses;
 private final JobRepository jobs;
 private final OllamaService ai;

 public ResumeController(
         TokenService tokenService,
         UserRepository userRepository,
         ResumeRepository resumeRepository,
         ResumeAnalysisRepository analysisRepository,
         JobRepository jobRepository,
         OllamaService ollamaService) {

  super(tokenService, userRepository);

  this.resumes = resumeRepository;
  this.analyses = analysisRepository;
  this.jobs = jobRepository;
  this.ai = ollamaService;
 }

 // =========================================================
 // ANALYZE RESUME
 // =========================================================

 @PostMapping("/analyze")
 public Map<String, Object> analyze(
         @RequestHeader("Authorization") String authorizationHeader,
         @RequestParam("file") MultipartFile file)
         throws Exception {

  Long userId =
          currentId(authorizationHeader);

  // -----------------------------------------------------
  // CHECK FILE
  // -----------------------------------------------------

  if (file == null || file.isEmpty()) {

   throw new RuntimeException(
           "Please choose a resume PDF."
   );
  }

  String fileName =
          Objects.requireNonNullElse(
                  file.getOriginalFilename(),
                  ""
          );

  // -----------------------------------------------------
  // CHECK PDF
  // -----------------------------------------------------

  if (!"application/pdf".equalsIgnoreCase(
          file.getContentType())
          && !fileName
          .toLowerCase(Locale.ROOT)
          .endsWith(".pdf")) {

   throw new RuntimeException(
           "Only PDF resumes are supported."
   );
  }

  // -----------------------------------------------------
  // CHECK FILE SIZE
  // -----------------------------------------------------

  if (file.getSize() > 5 * 1024 * 1024) {

   throw new RuntimeException(
           "Resume must be 5 MB or smaller."
   );
  }

  // -----------------------------------------------------
  // READ PDF
  // PDFBOX 2.0.30
  // -----------------------------------------------------

  byte[] data =
          file.getBytes();

  String text;

  try (
          PDDocument document =
                  PDDocument.load(data)
  ) {

   PDFTextStripper stripper =
           new PDFTextStripper();

   text =
           stripper.getText(document);
  }

  // -----------------------------------------------------
  // CHECK EXTRACTED TEXT
  // -----------------------------------------------------

  if (text == null
          || text.trim().length() < 100) {

   throw new RuntimeException(
           "This PDF does not contain enough "
                   + "readable resume text. "
                   + "Try a text-based PDF."
   );
  }

  // -----------------------------------------------------
  // SAVE RESUME
  // -----------------------------------------------------

  Resume resume =
          new Resume();

  resume.userId =
          userId;

  resume.fileName =
          fileName;

  resume.contentType =
          "application/pdf";

  resume.fileSize =
          file.getSize();

  resume.fileData =
          data;

  resume.extractedText =
          text;

  resume.uploadedAt =
          LocalDateTime.now();

  resumes.save(resume);

  // -----------------------------------------------------
  // BUILD JOB CATALOG
  // -----------------------------------------------------

  String catalog =
          jobs.findAll()
                  .stream()
                  .map(job ->
                          job.id
                                  + " | "
                                  + job.title
                                  + " | "
                                  + job.company
                                  + " | "
                                  + job.location
                                  + " | Skills: "
                                  + job.skills
                  )
                  .reduce(
                          "",
                          (a, b) ->
                                  a + "\n" + b
                  );

  // -----------------------------------------------------
  // AI PROMPT
  // -----------------------------------------------------

  String prompt =
          "You are an expert ATS resume reviewer. "

                  + "Analyze ONLY the uploaded resume text. "

                  + "Do not invent information. "

                  + "Return ONLY valid JSON with these keys: "

                  + "resumeQualityScore(number 0-100), "

                  + "atsCompatibility(number 0-100), "

                  + "matchingJobs(array of objects with "
                  + "title, company, matchScore, reason), "

                  + "aiResumeAnalysis(string), "

                  + "skillMatchingAnalysis(array of strings), "

                  + "missingSections(array of strings), "

                  + "improvements(array of strings). "

                  + "Assess contact/header, summary, education, "
                  + "skills, experience, projects, "
                  + "certifications, measurable achievements, "
                  + "ATS formatting and keyword alignment. "

                  + "Use the job catalog for matching. "

                  + "\n\nRESUME TEXT:\n"
                  + text

                  + "\n\nJOB CATALOG:\n"
                  + catalog;

  // -----------------------------------------------------
  // CALL OLLAMA
  // -----------------------------------------------------

  String json =
          ai.generate(prompt);

  // -----------------------------------------------------
  // SAVE ANALYSIS
  // -----------------------------------------------------

  ResumeAnalysis analysis =
          new ResumeAnalysis();

  analysis.resumeId =
          resume.id;

  analysis.userId =
          userId;

  analysis.analysisJson =
          json;

  analysis.createdAt =
          LocalDateTime.now();

  analyses.save(analysis);

  // -----------------------------------------------------
  // RESPONSE
  // -----------------------------------------------------

  return Map.of(
          "success",
          true,

          "id",
          analysis.id,

          "resumeId",
          resume.id,

          "fileName",
          resume.fileName,

          "createdAt",
          analysis.createdAt,

          "analysis",
          json
  );
 }

 // =========================================================
 // RESUME ANALYSIS HISTORY
 // =========================================================

 @GetMapping("/history")
 public List<Map<String, Object>> history(
         @RequestHeader("Authorization") String authorizationHeader) {

  Long userId =
          currentId(authorizationHeader);

  return analyses.findAll()
          .stream()

          .filter(
                  analysis ->
                          userId.equals(
                                  analysis.userId
                          )
          )

          .sorted(
                  Comparator.comparing(
                          analysis ->
                                  analysis.createdAt,
                          Comparator.reverseOrder()
                  )
          )

          .map(
                  analysis -> {

                   Map<String, Object> result =
                           new LinkedHashMap<>();

                   result.put(
                           "id",
                           analysis.id
                   );

                   result.put(
                           "resumeId",
                           analysis.resumeId
                   );

                   result.put(
                           "createdAt",
                           analysis.createdAt
                   );

                   result.put(
                           "analysis",
                           analysis.analysisJson
                   );

                   return result;
                  }
          )

          .toList();
 }

 // =========================================================
 // VIEW RESUME FILE
 // =========================================================

 @GetMapping("/{id}/file")
 public ResponseEntity<byte[]> file(
         @RequestHeader("Authorization") String authorizationHeader,
         @PathVariable Long id) {

  Long userId =
          currentId(authorizationHeader);

  Resume resume =
          resumes.findById(id)
                  .orElseThrow(
                          () -> new RuntimeException(
                                  "Resume not found."
                          )
                  );

  // -----------------------------------------------------
  // SECURITY CHECK
  // -----------------------------------------------------

  if (!userId.equals(resume.userId)) {

   throw new RuntimeException(
           "You can only access your own resume."
   );
  }

  // -----------------------------------------------------
  // SAFE FILE NAME
  // -----------------------------------------------------

  String safeFileName =
          resume.fileName
                  .replace("\"", "");

  // -----------------------------------------------------
  // RETURN PDF
  // -----------------------------------------------------

  return ResponseEntity
          .ok()
          .contentType(
                  MediaType.APPLICATION_PDF
          )
          .header(
                  HttpHeaders.CONTENT_DISPOSITION,
                  "inline; filename=\"" +
                          safeFileName +
                          "\""
          )
          .body(
                  resume.fileData
          );
 }
}