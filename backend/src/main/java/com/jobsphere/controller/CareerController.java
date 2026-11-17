package com.jobsphere.controller;

import com.jobsphere.entity.CareerAnalysis;
import com.jobsphere.entity.User;
import com.jobsphere.entity.UserProfile;
import com.jobsphere.repository.CareerAnalysisRepository;
import com.jobsphere.repository.JobRepository;
import com.jobsphere.repository.UserProfileRepository;
import com.jobsphere.repository.UserRepository;
import com.jobsphere.service.OllamaService;
import com.jobsphere.service.ProfileAiService;
import com.jobsphere.service.TokenService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/career")
public class CareerController extends BaseController {

 private final UserProfileRepository profiles;
 private final CareerAnalysisRepository analyses;
 private final OllamaService ai;
 private final JobRepository jobs;

 public CareerController(
         TokenService tokenService,
         UserRepository userRepository,
         UserProfileRepository profileRepository,
         CareerAnalysisRepository analysisRepository,
         OllamaService ollamaService,
         JobRepository jobRepository) {

  super(tokenService, userRepository);

  this.profiles = profileRepository;
  this.analyses = analysisRepository;
  this.ai = ollamaService;
  this.jobs = jobRepository;
 }

 /**
  * Analyze the currently logged-in user's career profile.
  */
 @PostMapping("/analyze")
 public Map<String, Object> analyze(
         @RequestHeader("Authorization") String authorizationHeader) {

  User user = currentUser(authorizationHeader);

  UserProfile profile = profiles
          .findByUserId(user.id)
          .orElseThrow(() ->
                  new RuntimeException(
                          "Complete your profile before using the AI Career Advisor."
                  )
          );

  /*
   * Build the current job catalogue.
   */
  String catalog = jobs.findAll()
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
          .reduce("", (first, second) ->
                  first + "\n" + second
          );

  /*
   * Create a snapshot of THIS user's profile.
   */
  String snapshot =
          ProfileAiService.profile(user, profile);

  /*
   * Ollama prompt.
   *
   * The AI is instructed to analyse only the
   * logged-in user's profile.
   */
  String prompt =
          "You are a professional career advisor. "
                  + "Analyze ONLY this candidate profile and compare it "
                  + "with the provided job catalog. "
                  + "Return ONLY valid JSON. "
                  + "Exactly two careerPaths are required. "

                  + "JSON keys: "
                  + "overallMatchScore(number 0-100), "
                  + "technicalSkillLevel(string), "
                  + "growthPotential(string), "
                  + "summary(string), "
                  + "strengths(array), "
                  + "weaknesses(array), "
                  + "careerPaths(array exactly 2). "

                  + "Each careerPath must contain: "
                  + "title, "
                  + "matchScore(number 0-100), "
                  + "whyFit, "
                  + "skillGaps(array), "
                  + "roadmap(array exactly 3 stages). "

                  + "Also include a top-level "
                  + "careerDevelopmentRoadmap array of exactly 4 practical steps. "

                  + "Do not invent candidate experience. "
                  + "Use the catalog for realistic matching jobs "
                  + "and company/location references where appropriate. "

                  + "\nCANDIDATE:\n"
                  + snapshot

                  + "\nJOB CATALOG:\n"
                  + catalog;

  /*
   * Ask Ollama for the personalized analysis.
   */
  String json = ai.generate(prompt);

  /*
   * Save every analysis as a NEW record.
   *
   * This means if the user edits their profile and
   * analyzes again, the previous analysis remains
   * available in history.
   */
  CareerAnalysis analysis = new CareerAnalysis();

  analysis.userId = user.id;
  analysis.profileSnapshot = snapshot;
  analysis.analysisJson = json;
  analysis.createdAt = LocalDateTime.now();

  analyses.save(analysis);

  /*
   * Return the newly-created analysis.
   */
  Map<String, Object> result =
          new LinkedHashMap<>();

  result.put("success", true);
  result.put("id", analysis.id);
  result.put("createdAt", analysis.createdAt);
  result.put("analysis", json);

  return result;
 }

 /**
  * Get the logged-in user's previous career analyses.
  */
 @GetMapping("/history")
 public List<Map<String, Object>> history(
         @RequestHeader("Authorization") String authorizationHeader) {

  Long userId = currentId(authorizationHeader);

  return analyses.findAll()
          .stream()

          /*
           * IMPORTANT:
           * Only return analyses belonging to
           * the currently logged-in user.
           */
          .filter(analysis ->
                  userId.equals(analysis.userId)
          )

          /*
           * Newest analysis first.
           */
          .sorted(
                  Comparator.comparing(
                          analysis -> analysis.createdAt,
                          Comparator.reverseOrder()
                  )
          )

          /*
           * Explicitly create Map<String,Object>
           * instead of using Map.of().
           */
          .map(analysis -> {

           Map<String, Object> result =
                   new LinkedHashMap<>();

           result.put("id", analysis.id);
           result.put(
                   "createdAt",
                   analysis.createdAt
           );
           result.put(
                   "analysis",
                   analysis.analysisJson
           );

           return result;
          })

          .toList();
 }
}
