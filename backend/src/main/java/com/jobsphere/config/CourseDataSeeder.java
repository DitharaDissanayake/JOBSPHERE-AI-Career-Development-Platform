package com.jobsphere.config;

import com.jobsphere.entity.Course;
import com.jobsphere.entity.CourseQuestion;
import com.jobsphere.repository.CourseRepository;
import com.jobsphere.repository.CourseQuestionRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CourseDataSeeder implements CommandLineRunner {

 private final CourseRepository courses;
 private final CourseQuestionRepository questions;

 public CourseDataSeeder(CourseRepository c, CourseQuestionRepository q) {
  courses = c;
  questions = q;
 }

 @Override
 public void run(String... args) {

  // Prevent duplicate course insertion
  if (courses.count() > 0) {
   return;
  }

  // =========================================================
  // 1. AI & GENERATIVE AI
  // =========================================================

  seed(
          "AI & Generative AI for the Workplace",
          "Artificial Intelligence",
          "Build practical AI literacy, prompt skills and responsible AI habits for modern workplaces.",
          "AI, artificial intelligence, machine learning, prompt engineering, data, technology, software",

          "[{\"title\":\"AI foundations\",\"minutes\":7,\"theory\":\"Artificial intelligence enables systems to perform tasks that normally require human judgement. Generative AI creates new text, code, images or summaries from learned patterns.\"},{\"title\":\"Prompt engineering\",\"minutes\":8,\"theory\":\"A useful prompt gives the role, context, task, constraints and desired output. Iterative prompting improves reliability.\"},{\"title\":\"Responsible workplace AI\",\"minutes\":7,\"theory\":\"Human review, privacy, source checking, bias awareness and clear disclosure reduce AI-related business risk.\"},{\"title\":\"Industry practice\",\"minutes\":8,\"theory\":\"Professionals can use AI for research, drafting, coding support, meeting summaries and workflow automation while keeping final accountability.\"}]",

          "Use AI to accelerate research and drafting, create first-pass documents, explain technical concepts and automate repetitive work. Never paste confidential customer data into an unapproved AI tool and always verify important outputs.",

          new String[][]{
                  {
                          "Which prompt is most useful?",
                          "Write something",
                          "Act as a project analyst. Summarize these requirements in five risks and mitigations.",
                          "AI please",
                          "Do it fast",
                          "B"
                  },
                  {
                          "What should happen before using an AI output for an important decision?",
                          "Publish immediately",
                          "Delete the output",
                          "Human review and verification",
                          "Trust the model completely",
                          "C"
                  },
                  {
                          "What is generative AI designed to do?",
                          "Only store files",
                          "Generate new content from learned patterns",
                          "Replace every employee",
                          "Only calculate taxes",
                          "B"
                  },
                  {
                          "A strong prompt normally includes:",
                          "Only one word",
                          "Context, task and constraints",
                          "A password",
                          "A random emoji",
                          "B"
                  },
                  {
                          "Why should confidential data be protected?",
                          "It improves fonts",
                          "It can create privacy and security risks",
                          "It makes Wi-Fi faster",
                          "It changes screen size",
                          "B"
                  },
                  {
                          "What is hallucination in AI?",
                          "A network cable fault",
                          "A confident but incorrect generated answer",
                          "A printer error",
                          "A type of database",
                          "B"
                  },
                  {
                          "Which is a responsible AI practice?",
                          "Skip verification",
                          "Hide AI use when policy requires disclosure",
                          "Check sources and protect sensitive data",
                          "Share passwords with AI",
                          "C"
                  },
                  {
                          "AI is most useful at work when it:",
                          "Supports human productivity and judgement",
                          "Always makes final decisions",
                          "Removes all controls",
                          "Never needs context",
                          "A"
                  },
                  {
                          "What improves an AI response?",
                          "Clear context and examples",
                          "Less information always",
                          "No task description",
                          "Random instructions",
                          "A"
                  },
                  {
                          "Who remains accountable for an important business decision?",
                          "The AI model",
                          "The human decision-maker",
                          "The keyboard",
                          "The internet",
                          "B"
                  }
          }
  );

  // =========================================================
  // 2. DATA ANALYTICS
  // =========================================================

  seed(
          "Data Analytics & Business Intelligence",
          "Data & Analytics",
          "Learn how to turn business data into useful insights, dashboards and decisions.",
          "data analytics, excel, sql, power bi, statistics, business intelligence",

          "[{\"title\":\"Analytics lifecycle\",\"minutes\":7,\"theory\":\"Business analytics starts with a question, collects suitable data, cleans and validates it, analyzes patterns and communicates an actionable result.\"},{\"title\":\"Data quality\",\"minutes\":7,\"theory\":\"Accuracy, completeness, consistency, validity and timeliness affect whether an analysis can be trusted.\"},{\"title\":\"KPIs and visualization\",\"minutes\":8,\"theory\":\"A KPI measures progress toward a business objective. Good charts make comparisons, trends and exceptions easy to understand.\"},{\"title\":\"Industry practice\",\"minutes\":8,\"theory\":\"Analysts combine spreadsheets, SQL and BI tools to produce repeatable reports and recommendations for stakeholders.\"}]",

          "In industry, start with the business decision, clean data before analysis, document assumptions and present only the metrics that help a stakeholder act.",

          new String[][]{
                  {
                          "What should an analyst clarify first?",
                          "The business question",
                          "Chart colors",
                          "Laptop brand",
                          "Office size",
                          "A"
                  },
                  {
                          "Which is a data-quality issue?",
                          "Missing values",
                          "Good documentation",
                          "A clear KPI",
                          "A validated source",
                          "A"
                  },
                  {
                          "A KPI is:",
                          "A random number",
                          "A key measure linked to an objective",
                          "A password",
                          "A file type",
                          "B"
                  },
                  {
                          "SQL is commonly used to:",
                          "Query and manipulate relational data",
                          "Design logos",
                          "Send emails only",
                          "Edit videos",
                          "A"
                  },
                  {
                          "Why validate data?",
                          "To increase uncertainty",
                          "To detect errors before trusting results",
                          "To remove all numbers",
                          "To change hardware",
                          "B"
                  },
                  {
                          "A useful dashboard should:",
                          "Support decisions",
                          "Contain every possible chart",
                          "Hide assumptions",
                          "Avoid labels",
                          "A"
                  },
                  {
                          "Correlation means:",
                          "One variable definitely causes another",
                          "Variables are associated",
                          "Data is always correct",
                          "A chart is complete",
                          "B"
                  },
                  {
                          "What should a recommendation contain?",
                          "Evidence and a practical action",
                          "Only decoration",
                          "No context",
                          "A password",
                          "A"
                  },
                  {
                          "Data cleaning may include:",
                          "Handling duplicates and missing values",
                          "Deleting every row",
                          "Changing facts to fit a target",
                          "Ignoring errors",
                          "A"
                  },
                  {
                          "The best chart depends mainly on:",
                          "The question and data",
                          "The monitor size",
                          "The keyboard",
                          "Random choice",
                          "A"
                  }
          }
  );

  // =========================================================
  // 3. PROFESSIONAL COMMUNICATION
  // =========================================================

  seed(
          "Professional Communication & Workplace Success",
          "Professional Skills",
          "Strengthen communication, teamwork, presentation and workplace problem-solving skills.",
          "communication, teamwork, presentation, leadership, soft skills, business",

          "[{\"title\":\"Clear communication\",\"minutes\":7,\"theory\":\"Effective workplace communication is clear, concise, audience-aware and action-oriented. Active listening reduces misunderstanding.\"},{\"title\":\"Team collaboration\",\"minutes\":7,\"theory\":\"Strong teams use role clarity, respectful feedback, shared goals and reliable follow-through.\"},{\"title\":\"Presentation skills\",\"minutes\":8,\"theory\":\"A professional presentation has a clear purpose, logical structure, useful visuals and confident delivery.\"},{\"title\":\"Industry practice\",\"minutes\":8,\"theory\":\"Use concise emails, meeting agendas, status updates, stakeholder summaries and evidence-based problem solving in real work.\"}]",

          "Apply these skills through structured emails, meeting notes, stakeholder updates, presentations, constructive feedback and clear escalation of risks.",

          new String[][]{
                  {
                          "Active listening means:",
                          "Waiting silently",
                          "Understanding, clarifying and responding to the speaker",
                          "Interrupting often",
                          "Preparing your reply only",
                          "B"
                  },
                  {
                          "A professional email should usually be:",
                          "Clear and action-oriented",
                          "Very vague",
                          "Without a subject",
                          "All caps",
                          "A"
                  },
                  {
                          "Constructive feedback should focus on:",
                          "The person",
                          "Observable behaviour and improvement",
                          "Rumours",
                          "Personal attacks",
                          "B"
                  },
                  {
                          "A meeting agenda helps:",
                          "Keep discussion focused",
                          "Hide decisions",
                          "Increase confusion",
                          "Avoid preparation",
                          "A"
                  },
                  {
                          "A good presentation starts with:",
                          "A clear purpose",
                          "Random slides",
                          "Long paragraphs",
                          "No audience consideration",
                          "A"
                  },
                  {
                          "When a deadline is at risk, you should:",
                          "Hide it",
                          "Communicate early with impact and recovery plan",
                          "Blame someone",
                          "Wait until failure",
                          "B"
                  },
                  {
                          "Teamwork requires:",
                          "Shared goals and accountability",
                          "Competition only",
                          "No communication",
                          "No ownership",
                          "A"
                  },
                  {
                          "Stakeholder communication should be adapted to:",
                          "Audience needs",
                          "Your favourite font",
                          "Random timing",
                          "Only technical jargon",
                          "A"
                  },
                  {
                          "A useful status update includes:",
                          "Progress, risks and next steps",
                          "Only greetings",
                          "Personal opinions",
                          "No dates",
                          "A"
                  },
                  {
                          "Problem solving should begin by:",
                          "Defining the problem",
                          "Choosing a solution randomly",
                          "Ignoring evidence",
                          "Assigning blame",
                          "A"
                  }
          }
  );

  // =========================================================
  // 4. CYBERSECURITY
  // =========================================================

  seed(
          "Cybersecurity Fundamentals for Professionals",
          "Cybersecurity",
          "Understand core cyber risks, secure behaviour, identity protection and incident awareness.",
          "cyber security, cybersecurity, networking, privacy, authentication, risk",

          "[{\"title\":\"Threats and risk\",\"minutes\":7,\"theory\":\"Cyber risk combines threats, vulnerabilities and potential impact. Common threats include phishing, malware, credential theft and social engineering.\"},{\"title\":\"Identity and access\",\"minutes\":7,\"theory\":\"Strong unique passwords, multi-factor authentication and least privilege reduce unauthorized access.\"},{\"title\":\"Safe data handling\",\"minutes\":8,\"theory\":\"Sensitive information should be accessed only when needed, stored securely and shared through approved channels.\"},{\"title\":\"Industry practice\",\"minutes\":8,\"theory\":\"Employees should report suspicious activity quickly, preserve evidence and follow organizational incident procedures.\"}]",

          "Use MFA, password managers, secure sharing, software updates and phishing awareness. If a suspicious event occurs, report it through the approved security channel instead of investigating recklessly.",

          new String[][]{
                  {
                          "Phishing commonly tries to:",
                          "Steal information through deception",
                          "Improve battery life",
                          "Clean a screen",
                          "Increase storage",
                          "A"
                  },
                  {
                          "MFA provides:",
                          "An additional authentication factor",
                          "Free internet",
                          "Automatic backups only",
                          "A faster CPU",
                          "A"
                  },
                  {
                          "Least privilege means:",
                          "Everyone gets admin rights",
                          "Users receive only access needed for their role",
                          "No one can log in",
                          "Passwords are shared",
                          "B"
                  },
                  {
                          "A suspicious login should be:",
                          "Ignored",
                          "Reported and investigated through procedure",
                          "Posted publicly",
                          "Deleted from logs",
                          "B"
                  },
                  {
                          "A strong password should be:",
                          "Unique and difficult to guess",
                          "Shared with coworkers",
                          "Your name",
                          "The same everywhere",
                          "A"
                  },
                  {
                          "Software updates often include:",
                          "Security fixes",
                          "Only wallpapers",
                          "Passwords",
                          "Job offers",
                          "A"
                  },
                  {
                          "Sensitive data should be shared:",
                          "Through approved secure channels",
                          "In public chats",
                          "With anyone who asks",
                          "On social media",
                          "A"
                  },
                  {
                          "Social engineering targets:",
                          "Human behaviour and trust",
                          "Only hard drives",
                          "Printer ink",
                          "Screen brightness",
                          "A"
                  },
                  {
                          "If you click a suspicious link, the best response is to:",
                          "Report it promptly and follow incident guidance",
                          "Hide it",
                          "Forward it",
                          "Enter more data",
                          "A"
                  },
                  {
                          "Cybersecurity is:",
                          "Only an IT department concern",
                          "A shared organizational responsibility",
                          "Only a manager task",
                          "Optional",
                          "B"
                  }
          }
  );

  // =========================================================
  // 5. AGILE PROJECT MANAGEMENT
  // =========================================================

  seed(
          "Agile Project Management & Scrum",
          "Project Management",
          "Learn Agile thinking, Scrum roles, ceremonies and practical delivery techniques used by teams.",
          "agile, scrum, project management, software, teamwork, kanban",

          "[{\"title\":\"Agile mindset\",\"minutes\":7,\"theory\":\"Agile emphasizes iterative delivery, customer feedback, collaboration and responding to change rather than relying only on a fixed plan.\"},{\"title\":\"Scrum framework\",\"minutes\":7,\"theory\":\"Scrum uses Product Owner, Scrum Master and Developers, with a Product Backlog and increments delivered through Sprints.\"},{\"title\":\"Planning and feedback\",\"minutes\":8,\"theory\":\"Teams refine work, plan a Sprint, inspect progress and adapt based on evidence and stakeholder feedback.\"},{\"title\":\"Industry practice\",\"minutes\":8,\"theory\":\"Use user stories, acceptance criteria, backlog prioritization, daily coordination and retrospectives to improve delivery.\"}]",

          "In industry, convert requirements into user stories, prioritize by value, define acceptance criteria, review increments with stakeholders and improve the process through retrospectives.",

          new String[][]{
                  {
                          "Agile primarily values:",
                          "Responding to change and customer collaboration",
                          "No feedback",
                          "Permanent plans",
                          "Longer documents only",
                          "A"
                  },
                  {
                          "Who maximizes product value in Scrum?",
                          "Product Owner",
                          "Customer support only",
                          "Finance clerk",
                          "Network admin",
                          "A"
                  },
                  {
                          "A Sprint is:",
                          "A time-boxed period for creating an increment",
                          "A yearly report",
                          "A database",
                          "A job interview",
                          "A"
                  },
                  {
                          "The Product Backlog contains:",
                          "Ordered work for the product",
                          "Employee passwords",
                          "Office expenses only",
                          "Completed payroll",
                          "A"
                  },
                  {
                          "A retrospective focuses on:",
                          "How the team can improve",
                          "Customer billing only",
                          "Hiring only",
                          "Passwords",
                          "A"
                  },
                  {
                          "Acceptance criteria define:",
                          "Conditions a work item must satisfy",
                          "Team salaries",
                          "Office rules",
                          "Wi-Fi settings",
                          "A"
                  },
                  {
                          "Daily Scrum is mainly for:",
                          "Inspecting progress and planning near-term work",
                          "A long client presentation",
                          "Payroll",
                          "Marketing",
                          "A"
                  },
                  {
                          "Kanban commonly emphasizes:",
                          "Visualizing flow and limiting work in progress",
                          "Fixed yearly releases",
                          "Passwords",
                          "Recruitment",
                          "A"
                  },
                  {
                          "Agile teams should:",
                          "Use feedback to adapt",
                          "Avoid stakeholders",
                          "Never change scope",
                          "Hide problems",
                          "A"
                  },
                  {
                          "A user story should describe:",
                          "A user need and desired outcome",
                          "A database password",
                          "A random task",
                          "A meeting room",
                          "A"
                  }
          }
  );

  // =========================================================
  // 6. DIGITAL MARKETING
  // =========================================================

  seed(
          "Digital Marketing & Personal Branding",
          "Digital Marketing",
          "Build job-ready knowledge of digital channels, content strategy, analytics and professional branding.",
          "marketing, digital, social media, branding, seo, content, analytics",

          "[{\"title\":\"Digital marketing funnel\",\"minutes\":7,\"theory\":\"Digital marketing connects awareness, consideration, conversion and retention activities to measurable goals.\"},{\"title\":\"Content and SEO\",\"minutes\":7,\"theory\":\"Useful content should match audience intent. SEO improves discoverability through relevant topics, structure and technical quality.\"},{\"title\":\"Analytics\",\"minutes\":8,\"theory\":\"Metrics such as reach, engagement, conversion rate and cost per acquisition help evaluate campaign performance.\"},{\"title\":\"Industry practice\",\"minutes\":8,\"theory\":\"Professionals plan content calendars, test messages, track campaign results and build consistent professional profiles across channels.\"}]",

          "Use audience research, a content calendar, measurable campaign objectives, analytics dashboards and a consistent professional identity. Avoid misleading claims and protect customer data.",

          new String[][]{
                  {
                          "A marketing funnel commonly begins with:",
                          "Awareness",
                          "Payroll",
                          "Database backup",
                          "Recruitment",
                          "A"
                  },
                  {
                          "SEO mainly helps:",
                          "Improve search discoverability",
                          "Encrypt passwords",
                          "Repair hardware",
                          "Write contracts",
                          "A"
                  },
                  {
                          "Conversion rate measures:",
                          "The proportion completing a desired action",
                          "Screen size",
                          "Employee attendance",
                          "File storage",
                          "A"
                  },
                  {
                          "A content strategy should be based on:",
                          "Audience needs and business goals",
                          "Random posts",
                          "No research",
                          "Personal preference only",
                          "A"
                  },
                  {
                          "A/B testing compares:",
                          "Two variants to learn which performs better",
                          "Two passwords",
                          "Two employees",
                          "Two databases only",
                          "A"
                  },
                  {
                          "A professional personal brand should be:",
                          "Consistent, credible and evidence-based",
                          "Misleading",
                          "Anonymous everywhere",
                          "Copied from others",
                          "A"
                  },
                  {
                          "Engagement can include:",
                          "Meaningful interactions with content",
                          "Only page size",
                          "Printer speed",
                          "Password length",
                          "A"
                  },
                  {
                          "Analytics are useful because they:",
                          "Support evidence-based decisions",
                          "Guarantee sales",
                          "Replace strategy",
                          "Remove all uncertainty",
                          "A"
                  },
                  {
                          "A content calendar helps:",
                          "Plan and coordinate publishing",
                          "Delete analytics",
                          "Hide campaigns",
                          "Create passwords",
                          "A"
                  },
                  {
                          "Good digital marketing should protect:",
                          "Customer privacy and trust",
                          "Only logos",
                          "Nothing",
                          "Passwords in public",
                          "A"
                  }
          }
  );
 }

 // =============================================================
 // SEED METHOD
 // =============================================================

 private void seed(
         String name,
         String category,
         String desc,
         String tags,
         String modules,
         String application,
         String[][] qs
 ) {

  // Create Course
  Course c = new Course();

  // Course fields are PUBLIC in your Course entity,
  // therefore direct access is correct.
  c.name = name;
  c.category = category;
  c.durationMinutes = 30;
  c.shortDescription = desc;

  c.introduction =
          "This is a focused 30-minute JOBSPHERE career-learning course. " +
                  "Complete the four learning sections, apply the ideas to an industry scenario, " +
                  "then answer 10 MCQs. Your score is calculated automatically.";

  c.eligibility =
          "Open to JOBSPHERE users with an active account. A completed career profile " +
                  "is recommended because course recommendations use your education, skills, " +
                  "experience, interests and career goals.";

  c.outcomes =
          "Understand practical concepts, connect theory to workplace situations, " +
                  "answer knowledge checks and identify skills to improve for the job market.";

  c.modulesJson = modules;
  c.industryApplication = application;
  c.tags = tags;
  c.active = true;

  // Save Course first so the database generates the ID.
  courses.save(c);

  // Create questions
  for (String[] q : qs) {

   CourseQuestion x = new CourseQuestion();

   // IMPORTANT:
   // CourseQuestion.courseId is PRIVATE.
   // Therefore use its setter.
   //
   // Course.id is PUBLIC.
   // Therefore c.id is valid.
   x.setCourseId(c.id);

   x.setQuestion(q[0]);
   x.setOptionA(q[1]);
   x.setOptionB(q[2]);
   x.setOptionC(q[3]);
   x.setOptionD(q[4]);
   x.setCorrectOption(q[5]);

   x.setExplanation(
           "Review the course theory and apply it to a realistic workplace situation."
   );

   x.setActive(true);

   questions.save(x);
  }
 }
}