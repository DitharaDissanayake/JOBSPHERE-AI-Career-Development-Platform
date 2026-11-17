package com.jobsphere.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobsphere.entity.*;
import com.jobsphere.repository.*;
import com.jobsphere.service.OllamaService;
import com.jobsphere.service.ProfileAiService;
import com.jobsphere.service.TokenService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/interview")
public class InterviewController extends BaseController {

 private final UserProfileRepository profiles;
 private final InterviewQuestionRepository questions;
 private final InterviewSessionRepository sessions;
 private final MediaRecommendationRepository media;
 private final CareerAnalysisRepository careers;
 private final ResumeRepository resumes;
 private final ResumeAnalysisRepository resumeAnalyses;
 private final OllamaService ai;

 private final ObjectMapper mapper = new ObjectMapper();

 public InterviewController(
         TokenService t,
         UserRepository u,
         UserProfileRepository p,
         InterviewQuestionRepository q,
         InterviewSessionRepository s,
         MediaRecommendationRepository m,
         CareerAnalysisRepository c,
         ResumeRepository r,
         ResumeAnalysisRepository ra,
         OllamaService a
 ) {
  super(t, u);

  profiles = p;
  questions = q;
  sessions = s;
  media = m;
  careers = c;
  resumes = r;
  resumeAnalyses = ra;
  ai = a;
 }

 // ============================================================
 // INTERVIEW HISTORY
 // ============================================================

 @GetMapping("/history")
 public Map<String, Object> history(
         @RequestHeader("Authorization") String h
 ) {

  Long id = currentId(h);

  return Map.of(
          "success", true,
          "sessions",
          sessions.findTop10ByUserIdOrderByCreatedAtDesc(id),
          "media",
          media.findTop12ByUserIdOrderByCreatedAtDesc(id)
  );
 }

 // ============================================================
 // GENERATE INTERVIEW
 // ============================================================

 @PostMapping("/generate")
 public Map<String, Object> generate(
         @RequestHeader("Authorization") String h,
         @RequestBody Map<String, String> req
 ) {

  User u = currentUser(h);

  // --------------------------------------------------------
  // 1. Load profile
  // --------------------------------------------------------

  UserProfile p = profiles.findByUserId(u.id)
          .orElseThrow(() ->
                  new RuntimeException(
                          "Complete your profile before using Interview Simulator."
                  )
          );

  String category = clean(req.get("category"));
  String position = clean(req.get("position"));

  // --------------------------------------------------------
  // 2. Load question bank
  // --------------------------------------------------------

  List<InterviewQuestion> bank = bank(category, position);

  String bankText = buildQuestionBankText(bank);

  // --------------------------------------------------------
  // 3. Load latest resume
  // --------------------------------------------------------

  String resumeText = resumes
          .findTopByUserIdOrderByUploadedAtDesc(u.id)
          .map(r -> r.extractedText == null ? "" : r.extractedText)
          .orElse("");

  // --------------------------------------------------------
  // 4. Load latest resume analysis
  // --------------------------------------------------------

  String resumeAnalysis = resumeAnalyses
          .findTopByUserIdOrderByCreatedAtDesc(u.id)
          .map(r -> r.analysisJson == null ? "" : r.analysisJson)
          .orElse("");

  // --------------------------------------------------------
  // 5. Load latest career analysis
  // --------------------------------------------------------

  String careerAnalysis = careers
          .findTopByUserIdOrderByCreatedAtDesc(u.id)
          .map(c -> c.analysisJson == null ? "" : c.analysisJson)
          .orElse("");

  // --------------------------------------------------------
  // 6. Create profile snapshot
  // --------------------------------------------------------

  String profileSnapshot = ProfileAiService.profile(u, p);

  // --------------------------------------------------------
  // 7. Generate personalized questions
  // --------------------------------------------------------

  String prompt;

  boolean selectedMode =
          !category.isBlank() || !position.isBlank();

  if (selectedMode) {

   prompt = buildSelectedPrompt(
           category,
           position,
           profileSnapshot,
           resumeText,
           resumeAnalysis,
           careerAnalysis,
           bankText
   );

  } else {

   prompt = buildPersonalizedPrompt(
           profileSnapshot,
           resumeText,
           resumeAnalysis,
           careerAnalysis,
           bankText
   );
  }

  String json = generateExactlyTen(prompt);

  // --------------------------------------------------------
  // 8. Validate final JSON
  // --------------------------------------------------------

  validateExactlyTen(json);

  // --------------------------------------------------------
  // 9. Save interview session
  // --------------------------------------------------------

  InterviewSession session = new InterviewSession();

  session.userId = u.id;
  session.category = category.isBlank()
          ? "AI Personalized"
          : category;

  session.position = position.isBlank()
          ? "AI Recommended"
          : position;

  session.sourceType = bank.isEmpty()
          ? "OLLAMA"
          : "DATABASE+OLLAMA";

  session.sessionJson = json;
  session.createdAt = LocalDateTime.now();

  sessions.save(session);

  // --------------------------------------------------------
  // 10. Save interview media
  // --------------------------------------------------------

  saveMedia(
          u.id,
          category.isBlank() ? "General" : category,
          position.isBlank() ? "General" : position
  );

  // --------------------------------------------------------
  // 11. Return result
  // --------------------------------------------------------

  return Map.of(
          "success", true,
          "sessionId", session.id,
          "data", json,
          "media",
          media.findTop12ByUserIdOrderByCreatedAtDesc(u.id)
  );
 }

 // ============================================================
 // SELECTED CATEGORY / POSITION PROMPT
 // ============================================================

 private String buildSelectedPrompt(
         String category,
         String position,
         String profile,
         String resume,
         String resumeAnalysis,
         String careerAnalysis,
         String bank
 ) {

  return """
                You are JOBSPHERE AI Interview Simulator.

                Generate EXACTLY 10 realistic interview questions for the candidate.

                ============================================================
                SELECTED INTERVIEW TARGET
                ============================================================

                Category:
                %s

                Position:
                %s

                ============================================================
                IMPORTANT RULES
                ============================================================

                1. Generate EXACTLY 10 questions.
                2. Every question MUST be relevant to the selected category and position.
                3. DO NOT mix unrelated career fields.
                4. DO NOT generate Banking questions for a Software Engineer.
                5. DO NOT generate Marketing questions for a Cyber Security Analyst.
                6. DO NOT generate Accounting questions for a Data Scientist.
                7. Use the candidate's profile, resume and career analysis to personalize the questions.
                8. Do NOT invent experience, projects, qualifications or skills.
                9. If the candidate has no experience, ask suitable graduate-level or scenario-based questions.
                10. Questions should resemble realistic job interviews.

                ============================================================
                QUESTION COVERAGE
                ============================================================

                Include a balanced combination of:

                - Technical knowledge
                - Practical skills
                - Problem solving
                - Role-specific scenarios
                - Behavioural questions
                - Candidate experience/projects
                - Communication
                - Career motivation
                - Responsibilities of the selected position

                ============================================================
                CANDIDATE PROFILE
                ============================================================

                %s

                ============================================================
                LATEST RESUME
                ============================================================

                %s

                ============================================================
                RESUME ANALYSIS
                ============================================================

                %s

                ============================================================
                CAREER ANALYSIS
                ============================================================

                %s

                ============================================================
                DATABASE QUESTION BANK
                ============================================================

                %s

                ============================================================
                OUTPUT FORMAT
                ============================================================

                Return ONLY valid JSON.

                Do not use Markdown.
                Do not use ```json.
                Do not add explanations outside the JSON.

                Use exactly this structure:

                {
                  "category": "%s",
                  "position": "%s",
                  "difficulty": "Mixed",
                  "questions": [
                    {
                      "number": 1,
                      "question": "...",
                      "whyAsked": "...",
                      "idealAnswer": "...",
                      "answerStructure": "...",
                      "tips": "..."
                    }
                  ]
                }

                There MUST be exactly 10 objects inside "questions".
                """.formatted(
          category,
          position,
          profile,
          resume,
          resumeAnalysis,
          careerAnalysis,
          bank,
          category,
          position
  );
 }

 // ============================================================
 // FULL AI PERSONALIZATION WHEN NOTHING IS SELECTED
 // ============================================================

 private String buildPersonalizedPrompt(
         String profile,
         String resume,
         String resumeAnalysis,
         String careerAnalysis,
         String bank
 ) {

  return """
                You are JOBSPHERE AI Interview Simulator.

                The candidate did NOT select an interview category or position.

                Your job is to analyze the candidate's complete career information
                and automatically determine the most relevant career direction.

                ============================================================
                STEP 1 - ANALYZE THE CANDIDATE
                ============================================================

                Carefully analyze:

                - Education
                - Degree
                - University
                - Skills
                - Technical skills
                - Soft skills
                - Experience
                - Projects
                - Certifications
                - Career interests
                - Career goals
                - Resume
                - Resume analysis
                - Career analysis

                Determine the candidate's strongest and most realistic career direction.

                Examples:

                Software Engineering
                Data Analytics
                Data Science
                Cyber Security
                Networking
                Banking
                Finance
                Accounting
                Marketing
                Human Resources
                Business Analysis
                Project Management
                UI/UX Design
                Education
                Research
                Supply Chain
                Sales
                Customer Service
                or another clearly supported career direction.

                ============================================================
                STEP 2 - CHOOSE THE MOST RELEVANT POSITION
                ============================================================

                Identify the single most appropriate job position based on
                the candidate's information.

                Do NOT randomly select a position.

                The selected position must be supported by the candidate's:

                - Skills
                - Education
                - Experience
                - Projects
                - Career interests
                - Career goals
                - Resume
                - Career analysis

                ============================================================
                STEP 3 - GENERATE INTERVIEW QUESTIONS
                ============================================================

                Generate EXACTLY 10 questions for the selected career direction.

                The questions must be highly relevant to the candidate.

                DO NOT mix unrelated career fields.

                For example:

                If the candidate is clearly suited for Software Engineering,
                do NOT generate Banking, Marketing, HR or Accounting questions.

                If the candidate is clearly suited for Cyber Security,
                focus on Cyber Security.

                If the candidate is clearly suited for Data Analytics,
                focus on Data Analytics.

                ============================================================
                PERSONALIZATION
                ============================================================

                Where appropriate, refer to the candidate's actual:

                - Skills
                - Projects
                - Experience
                - Education
                - Certifications
                - Career goals

                Do NOT invent information that is not present.

                ============================================================
                QUESTION COVERAGE
                ============================================================

                Include a balanced combination of:

                1. Technical knowledge
                2. Practical skills
                3. Problem solving
                4. Scenario-based questions
                5. Behavioural questions
                6. Project or experience questions
                7. Communication
                8. Career motivation
                9. Role responsibilities
                10. One challenging question appropriate to the candidate

                ============================================================
                CANDIDATE PROFILE
                ============================================================

                %s

                ============================================================
                LATEST RESUME
                ============================================================

                %s

                ============================================================
                RESUME ANALYSIS
                ============================================================

                %s

                ============================================================
                CAREER ANALYSIS
                ============================================================

                %s

                ============================================================
                DATABASE QUESTION BANK
                ============================================================

                %s

                ============================================================
                OUTPUT FORMAT
                ============================================================

                Return ONLY valid JSON.

                Do not use Markdown.
                Do not use ```json.
                Do not add explanations outside the JSON.

                Use exactly this structure:

                {
                  "category": "AI identified category",
                  "position": "AI identified position",
                  "difficulty": "Mixed",
                  "questions": [
                    {
                      "number": 1,
                      "question": "...",
                      "whyAsked": "...",
                      "idealAnswer": "...",
                      "answerStructure": "...",
                      "tips": "..."
                    }
                  ]
                }

                There MUST be exactly 10 objects inside "questions".
                """.formatted(
          profile,
          resume,
          resumeAnalysis,
          careerAnalysis,
          bank
  );
 }

 // ============================================================
 // FORCE EXACTLY 10 QUESTIONS
 // ============================================================

 private String generateExactlyTen(String prompt) {

  String json = ai.generate(prompt);

  if (isExactlyTen(json)) {
   return json;
  }

  // --------------------------------------------------------
  // Second attempt
  // --------------------------------------------------------

  String retryPrompt = """
                You are a JSON correction and interview generation engine.

                The previous AI response did not contain exactly 10 valid
                interview questions.

                Generate a NEW response.

                Requirements:

                - EXACTLY 10 questions.
                - Do not generate 9.
                - Do not generate 11.
                - Questions must remain relevant to the candidate.
                - Do not invent candidate experience.
                - Return ONLY valid JSON.
                - No Markdown.
                - No ```json.

                Required structure:

                {
                  "category": "...",
                  "position": "...",
                  "difficulty": "Mixed",
                  "questions": [
                    {
                      "number": 1,
                      "question": "...",
                      "whyAsked": "...",
                      "idealAnswer": "...",
                      "answerStructure": "...",
                      "tips": "..."
                    }
                  ]
                }

                There MUST be exactly 10 question objects.

                Previous response:

                %s
                """.formatted(json);

  String retry = ai.generate(retryPrompt);

  if (isExactlyTen(retry)) {
   return retry;
  }

  throw new RuntimeException(
          "The AI could not generate exactly 10 interview questions. Please try again."
  );
 }

 // ============================================================
 // VALIDATE 10 QUESTIONS
 // ============================================================

 private boolean isExactlyTen(String json) {

  try {

   JsonNode root = mapper.readTree(json);

   JsonNode list = root.get("questions");

   return list != null
           && list.isArray()
           && list.size() == 10;

  } catch (Exception e) {

   return false;
  }
 }

 private void validateExactlyTen(String json) {

  if (!isExactlyTen(json)) {

   throw new RuntimeException(
           "Interview generation failed. The AI response must contain exactly 10 questions."
   );
  }
 }

 // ============================================================
 // QUESTION BANK
 // ============================================================

 private List<InterviewQuestion> bank(
         String category,
         String position
 ) {

  // Both selected
  if (!category.isBlank() && !position.isBlank()) {

   List<InterviewQuestion> result =
           questions
                   .findTop20ByActiveTrueAndCategoryContainingIgnoreCaseAndPositionContainingIgnoreCase(
                           category,
                           position
                   );

   if (!result.isEmpty()) {
    return result;
   }
  }

  // Category selected
  if (!category.isBlank()) {

   List<InterviewQuestion> result =
           questions
                   .findTop20ByActiveTrueAndCategoryContainingIgnoreCase(
                           category
                   );

   if (!result.isEmpty()) {
    return result;
   }
  }

  // Position selected
  if (!position.isBlank()) {

   List<InterviewQuestion> result =
           questions
                   .findTop20ByActiveTrueAndPositionContainingIgnoreCase(
                           position
                   );

   if (!result.isEmpty()) {
    return result;
   }
  }

  // Nothing selected
  return questions.findTop20ByActiveTrue();
 }

 // ============================================================
 // QUESTION BANK TEXT
 // ============================================================

 private String buildQuestionBankText(
         List<InterviewQuestion> bank
 ) {

  if (bank == null || bank.isEmpty()) {
   return "No database questions available.";
  }

  StringBuilder text = new StringBuilder();

  for (InterviewQuestion q : bank) {

   text.append("\n");

   text.append("ID: ")
           .append(q.id)
           .append("\n");

   text.append("Category: ")
           .append(q.category)
           .append("\n");

   text.append("Position: ")
           .append(q.position)
           .append("\n");

   text.append("Question: ")
           .append(q.question)
           .append("\n");

   text.append("Ideal Answer: ")
           .append(q.idealAnswer)
           .append("\n");

   text.append("-----------------------------\n");
  }

  return text.toString();
 }

 // ============================================================
 // MEDIA
 // ============================================================

 private void saveMedia(
         Long userId,
         String category,
         String position
 ) {

  List<MediaRecommendation> all = new ArrayList<>();

  List<String[]> seeds = new ArrayList<>();

  seeds.add(
          new String[]{
                  "VIDEO",
                  "How to Answer Tell Me About Yourself in an Interview",
                  "https://www.youtube.com/results?search_query=how+to+answer+tell+me+about+yourself+interview",
                  "https://www.youtube.com/embed?listType=search&list=how+to+answer+tell+me+about+yourself+interview",
                  "Interview preparation for introductions"
          }
  );

  seeds.add(
          new String[]{
                  "VIDEO",
                  "STAR Method Interview Answers",
                  "https://www.youtube.com/results?search_query=STAR+method+interview+answers",
                  "https://www.youtube.com/embed?listType=search&list=STAR+method+interview+answers",
                  "Learn a structured way to answer behavioural questions"
          }
  );

  String searchTerm =
          (category + " " + position)
                  .trim()
                  .replace(" ", "+");

  seeds.add(
          new String[]{
                  "VIDEO",
                  category + " " + position + " Interview Preparation",
                  "https://www.youtube.com/results?search_query="
                          + searchTerm
                          + "+interview+preparation",
                  "https://www.youtube.com/embed?listType=search&list="
                          + searchTerm
                          + "+interview+preparation",
                  "Role-specific interview preparation"
          }
  );

  seeds.add(
          new String[]{
                  "PODCAST",
                  "Career Growth & Job Search Podcast",
                  "https://www.youtube.com/results?search_query=career+growth+job+search+podcast",
                  "https://www.youtube.com/embed?listType=search&list=career+growth+job+search+podcast",
                  "Career development discussions and practical advice"
          }
  );

  for (String[] x : seeds) {

   MediaRecommendation m =
           new MediaRecommendation();

   m.userId = userId;
   m.mediaType = x[0];
   m.title = x[1];
   m.url = x[2];
   m.embedUrl = x[3];
   m.description = x[4];
   m.source = "YouTube";

   m.category = category;
   m.position = position;

   m.createdAt = LocalDateTime.now();

   all.add(m);
  }

  media.saveAll(all);
 }

 // ============================================================
 // CLEAN INPUT
 // ============================================================

 private String clean(String s) {

  return s == null
          ? ""
          : s.trim();
 }
}