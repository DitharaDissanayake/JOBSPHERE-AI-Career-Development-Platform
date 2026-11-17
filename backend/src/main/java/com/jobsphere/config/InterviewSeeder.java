package com.jobsphere.config;

import com.jobsphere.entity.InterviewQuestion;
import com.jobsphere.repository.InterviewQuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class InterviewSeeder {

 @Bean
 CommandLineRunner seedInterviewQuestions(InterviewQuestionRepository repo) {

  return args -> {

   if (repo.count() > 0) {
    return;
   }

   repo.saveAll(List.of(

           // =========================================================
           // SOFTWARE ENGINEERING
           // =========================================================

           q(
                   "Software Engineering",
                   "Software Engineer",
                   "Tell me about yourself.",
                   "Give a 60–90 second summary of your education, relevant projects, technical strengths and the role you want next.",
                   "Tests communication and relevance.",
                   "Use present → evidence → future."
           ),

           q(
                   "Software Engineering",
                   "Software Engineer",
                   "Describe a software project you are proud of.",
                   "Explain the problem, your responsibility, technologies, an important challenge and measurable result.",
                   "Tests ownership and technical depth.",
                   "Use STAR and clearly explain your contribution."
           ),

           q(
                   "Software Engineering",
                   "Software Engineer",
                   "How do you debug a production issue?",
                   "Reproduce safely, inspect logs and metrics, isolate the failing component, form hypotheses, test the smallest change, validate and document the root cause.",
                   "Tests systematic troubleshooting.",
                   "Mention monitoring, rollback and prevention."
           ),

           q(
                   "Software Engineering",
                   "Software Engineer",
                   "Explain REST APIs.",
                   "REST APIs expose resources through HTTP methods such as GET, POST, PUT/PATCH and DELETE, using clear URLs, status codes and representations such as JSON.",
                   "Tests web fundamentals.",
                   "Give one practical example."
           ),

           q(
                   "Software Engineering",
                   "Software Engineer",
                   "What is database normalization?",
                   "It organizes relational data to reduce redundancy and update anomalies by separating related facts into appropriate tables and relationships.",
                   "Tests database fundamentals.",
                   "Mention 1NF, 2NF and 3NF briefly."
           ),

           q(
                   "Software Engineering",
                   "Software Engineer",
                   "How do you handle a code review comment you disagree with?",
                   "Understand the concern, discuss the trade-off with evidence, propose alternatives and follow the team decision respectfully.",
                   "Tests collaboration.",
                   "Focus on the technical issue rather than the person."
           ),

           q(
                   "Software Engineering",
                   "Software Engineer",
                   "What is your approach to learning a new technology?",
                   "Start with documentation, build a small proof of concept, apply it to a real task, test edge cases and record lessons learned.",
                   "Tests adaptability.",
                   "Connect your answer to a real example."
           ),

           // =========================================================
           // DATA ANALYTICS
           // =========================================================

           q(
                   "Data",
                   "Data Analyst",
                   "How would you approach a new analytics problem?",
                   "Clarify the business question, identify data sources, clean and validate data, explore patterns, analyze, visualize and communicate actionable findings.",
                   "Tests analytical thinking.",
                   "Start with the decision the analysis supports."
           ),

           q(
                   "Data",
                   "Data Analyst",
                   "How do you handle missing data?",
                   "Understand why values are missing, quantify the impact, then choose deletion, imputation or an explicit missing category based on context.",
                   "Tests data quality judgement.",
                   "Explain why your choice is appropriate."
           ),

           q(
                   "Data",
                   "Data Analyst",
                   "What is the difference between correlation and causation?",
                   "Correlation means two variables are associated, while causation means a change in one variable produces a change in another.",
                   "Tests statistical understanding.",
                   "Give a simple real-world example."
           ),

           q(
                   "Data",
                   "Data Analyst",
                   "What is a dashboard?",
                   "A dashboard is a visual interface that presents important metrics and trends so users can monitor performance and make decisions.",
                   "Tests data visualization knowledge.",
                   "Mention tools such as Power BI, Tableau or Excel."
           ),

           // =========================================================
           // BUSINESS MANAGEMENT
           // =========================================================

           q(
                   "Business Management",
                   "Business Manager",
                   "What makes an effective manager?",
                   "An effective manager communicates clearly, sets goals, supports employees, makes informed decisions and takes responsibility for results.",
                   "Tests leadership understanding.",
                   "Give an example of leadership experience."
           ),

           q(
                   "Business Management",
                   "Business Manager",
                   "How do you handle a conflict between employees?",
                   "Listen to both sides, identify the root cause, focus on facts, encourage respectful discussion and agree on a practical solution.",
                   "Tests conflict management.",
                   "Remain neutral and professional."
           ),

           q(
                   "Business Management",
                   "Business Manager",
                   "How do you prioritize tasks?",
                   "Evaluate urgency, importance, business impact and deadlines, then organize tasks based on their priority.",
                   "Tests organizational ability.",
                   "Mention tools such as task lists or project management software."
           ),

           q(
                   "Business Management",
                   "Business Manager",
                   "How do you measure business performance?",
                   "Use relevant KPIs such as revenue, profit, customer satisfaction, productivity, retention and operational efficiency.",
                   "Tests business knowledge.",
                   "Choose KPIs that match business objectives."
           ),

           q(
                   "Business Management",
                   "Business Manager",
                   "How would you improve an underperforming team?",
                   "Identify the causes of poor performance, communicate expectations, provide support and training, set measurable goals and monitor progress.",
                   "Tests leadership and problem solving.",
                   "Avoid blaming employees."
           ),

           // =========================================================
           // BUSINESS ANALYSIS
           // =========================================================

           q(
                   "Business Analysis",
                   "Business Analyst",
                   "How do you gather requirements?",
                   "Identify stakeholders, understand the business goal, conduct interviews or workshops, document requirements and validate them.",
                   "Tests requirements practice.",
                   "Use user stories or acceptance criteria."
           ),

           q(
                   "Business Analysis",
                   "Business Analyst",
                   "What is a functional requirement?",
                   "A functional requirement describes what a system should do, such as allowing users to register, log in or generate a report.",
                   "Tests requirements knowledge.",
                   "Give a practical example."
           ),

           q(
                   "Business Analysis",
                   "Business Analyst",
                   "How do you handle conflicting stakeholder requirements?",
                   "Understand each stakeholder's objectives, identify conflicts, evaluate business value and constraints, and work toward an agreed priority.",
                   "Tests negotiation and analysis.",
                   "Use evidence and business objectives."
           ),

           // =========================================================
           // BANKING
           // =========================================================

           q(
                   "Banking",
                   "Banking Officer",
                   "Why do you want to work in banking?",
                   "Explain your interest in financial services, customer service, analytical work and the opportunity to develop a professional career in banking.",
                   "Tests motivation.",
                   "Connect your skills with banking."
           ),

           q(
                   "Banking",
                   "Banking Officer",
                   "What is KYC?",
                   "KYC means Know Your Customer. It is the process of verifying customer identity and understanding customer information to reduce financial crime risks.",
                   "Tests banking compliance knowledge.",
                   "Mention identity verification and compliance."
           ),

           q(
                   "Banking",
                   "Banking Officer",
                   "What is a credit score?",
                   "A credit score is a numerical measure used to assess a person's creditworthiness based on factors such as repayment history and existing credit obligations.",
                   "Tests financial knowledge.",
                   "Explain why it matters when granting credit."
           ),

           q(
                   "Banking",
                   "Banking Officer",
                   "How would you handle an angry customer?",
                   "Listen carefully, remain calm, acknowledge the concern, verify the issue and provide an appropriate solution or escalation.",
                   "Tests customer service.",
                   "Never argue with the customer."
           ),

           q(
                   "Banking",
                   "Banking Officer",
                   "What is interest?",
                   "Interest is the cost of borrowing money or the return earned for depositing or investing money.",
                   "Tests basic financial knowledge.",
                   "Explain both borrowing and saving examples."
           ),

           // =========================================================
           // FINANCE
           // =========================================================

           q(
                   "Finance",
                   "Financial Analyst",
                   "What does a financial analyst do?",
                   "A financial analyst evaluates financial information, business performance, investments and market conditions to support financial decisions.",
                   "Tests role understanding.",
                   "Mention financial statements and forecasting."
           ),

           q(
                   "Finance",
                   "Financial Analyst",
                   "What is financial forecasting?",
                   "Financial forecasting estimates future revenue, expenses, cash flows and financial performance using historical and expected business information.",
                   "Tests financial planning knowledge.",
                   "Mention assumptions and scenarios."
           ),

           q(
                   "Finance",
                   "Financial Analyst",
                   "What is cash flow?",
                   "Cash flow represents money entering and leaving a business during a specific period.",
                   "Tests financial fundamentals.",
                   "Explain operating, investing and financing cash flows."
           ),

           q(
                   "Finance",
                   "Financial Analyst",
                   "What is ROI?",
                   "ROI, or Return on Investment, measures the return generated by an investment compared with its cost.",
                   "Tests financial analysis.",
                   "Give a simple investment example."
           ),

           // =========================================================
           // ACCOUNTING
           // =========================================================

           q(
                   "Accounting",
                   "Accountant",
                   "What are the main financial statements?",
                   "The main financial statements are the income statement, balance sheet, cash flow statement and statement of changes in equity.",
                   "Tests accounting knowledge.",
                   "Briefly explain the purpose of each."
           ),

           q(
                   "Accounting",
                   "Accountant",
                   "What is the difference between assets and liabilities?",
                   "Assets are resources controlled by a business, while liabilities are obligations that the business owes to others.",
                   "Tests accounting fundamentals.",
                   "Give examples such as cash and loans."
           ),

           q(
                   "Accounting",
                   "Accountant",
                   "What is a balance sheet?",
                   "A balance sheet presents a company's assets, liabilities and equity at a specific point in time.",
                   "Tests financial reporting knowledge.",
                   "Mention the accounting equation."
           ),

           q(
                   "Accounting",
                   "Accountant",
                   "How do you ensure accuracy in accounting work?",
                   "Use supporting documents, reconcile accounts, review entries, follow accounting standards and perform regular checks.",
                   "Tests attention to detail.",
                   "Mention reconciliation and documentation."
           ),

           // =========================================================
           // MARKETING
           // =========================================================

           q(
                   "Marketing",
                   "Marketing Executive",
                   "What is digital marketing?",
                   "Digital marketing promotes products or services through online channels such as search engines, social media, websites, email and digital advertising.",
                   "Tests marketing fundamentals.",
                   "Mention measurable campaigns."
           ),

           q(
                   "Marketing",
                   "Marketing Executive",
                   "What is SEO?",
                   "SEO, or Search Engine Optimization, improves a website's visibility in search engine results through relevant content, technical improvements and authority building.",
                   "Tests digital marketing knowledge.",
                   "Mention keywords, content and technical SEO."
           ),

           q(
                   "Marketing",
                   "Marketing Executive",
                   "How would you measure a marketing campaign?",
                   "Measure KPIs such as reach, engagement, traffic, conversion rate, customer acquisition cost and return on investment.",
                   "Tests analytical marketing ability.",
                   "Choose metrics based on campaign objectives."
           ),

           q(
                   "Marketing",
                   "Marketing Executive",
                   "What is a target market?",
                   "A target market is a specific group of customers that a business aims to serve based on characteristics such as needs, demographics and behavior.",
                   "Tests marketing strategy.",
                   "Give a practical example."
           ),

           // =========================================================
           // HUMAN RESOURCES
           // =========================================================

           q(
                   "Human Resources",
                   "HR Executive",
                   "What is the role of Human Resources?",
                   "HR manages activities such as recruitment, employee development, performance management, employee relations and workplace policies.",
                   "Tests HR knowledge.",
                   "Mention both employee and organizational needs."
           ),

           q(
                   "Human Resources",
                   "HR Executive",
                   "How would you conduct an interview?",
                   "Review the job requirements, prepare relevant questions, create a comfortable environment, evaluate evidence and document the assessment objectively.",
                   "Tests recruitment skills.",
                   "Avoid discriminatory or irrelevant questions."
           ),

           q(
                   "Human Resources",
                   "HR Executive",
                   "How would you handle an employee complaint?",
                   "Listen confidentially, document the concern, investigate fairly and follow organizational policies before deciding on appropriate action.",
                   "Tests employee relations.",
                   "Remain objective and confidential."
           ),

           q(
                   "Human Resources",
                   "HR Executive",
                   "What makes a good employee onboarding process?",
                   "It introduces employees to the organization, role expectations, systems, policies, colleagues and resources needed to become productive.",
                   "Tests HR process knowledge.",
                   "Mention structured orientation and follow-up."
           ),

           // =========================================================
           // SCIENCE
           // =========================================================

           q(
                   "Science",
                   "Research Scientist",
                   "What is the scientific method?",
                   "The scientific method involves observation, asking a question, forming a hypothesis, conducting experiments, analyzing evidence and drawing conclusions.",
                   "Tests scientific reasoning.",
                   "Explain the importance of evidence."
           ),

           q(
                   "Science",
                   "Research Scientist",
                   "How do you design a scientific experiment?",
                   "Define the research question, identify variables, establish controls, select a suitable method, collect data and analyze results.",
                   "Tests research methodology.",
                   "Mention independent and dependent variables."
           ),

           q(
                   "Science",
                   "Research Scientist",
                   "Why is reproducibility important in science?",
                   "Reproducibility allows other researchers to repeat methods and verify findings, strengthening confidence in scientific results.",
                   "Tests research integrity.",
                   "Mention clear methods and documentation."
           ),

           q(
                   "Science",
                   "Laboratory Scientist",
                   "How do you maintain accuracy in laboratory work?",
                   "Follow standard procedures, calibrate equipment, use controls, record observations carefully and verify unexpected results.",
                   "Tests laboratory discipline.",
                   "Emphasize safety and documentation."
           ),

           // =========================================================
           // ENVIRONMENTAL SCIENCE
           // =========================================================

           q(
                   "Environmental Science",
                   "Environmental Scientist",
                   "What is climate change?",
                   "Climate change refers to long-term changes in Earth's climate patterns, with recent warming largely driven by human activities that increase greenhouse gas concentrations.",
                   "Tests environmental awareness.",
                   "Explain causes and impacts briefly."
           ),

           q(
                   "Environmental Science",
                   "Environmental Scientist",
                   "How can organizations reduce environmental impact?",
                   "Organizations can improve energy efficiency, reduce waste, use renewable energy, manage resources responsibly and measure environmental performance.",
                   "Tests sustainability knowledge.",
                   "Give practical business examples."
           ),

           // =========================================================
           // HEALTHCARE
           // =========================================================

           q(
                   "Healthcare",
                   "Healthcare Professional",
                   "Why is communication important in healthcare?",
                   "Clear communication helps professionals understand patient needs, explain information, coordinate care and reduce misunderstandings.",
                   "Tests communication skills.",
                   "Mention empathy and confidentiality."
           ),

           q(
                   "Healthcare",
                   "Healthcare Administrator",
                   "How would you handle a difficult patient?",
                   "Remain calm, listen carefully, acknowledge concerns, communicate clearly and follow appropriate procedures or escalation channels.",
                   "Tests professionalism.",
                   "Show empathy without compromising policy."
           ),

           q(
                   "Healthcare",
                   "Healthcare Administrator",
                   "Why is patient confidentiality important?",
                   "Patient confidentiality protects personal information and maintains trust between patients and healthcare organizations.",
                   "Tests professional ethics.",
                   "Mention privacy and authorized access."
           ),

           // =========================================================
           // CYBER SECURITY
           // =========================================================

           q(
                   "Cyber Security",
                   "Cyber Security Analyst",
                   "How would you respond to a suspicious login alert?",
                   "Validate the alert, check identity and device context, inspect logs, contain the account or device if necessary, investigate scope, recover safely and document lessons learned.",
                   "Tests incident response thinking.",
                   "Mention escalation and evidence preservation."
           ),

           q(
                   "Cyber Security",
                   "Cyber Security Analyst",
                   "What is phishing?",
                   "Phishing is a social engineering technique where attackers attempt to trick users into revealing information or performing unsafe actions through deceptive messages.",
                   "Tests security awareness.",
                   "Give an email example."
           ),

           q(
                   "Cyber Security",
                   "Cyber Security Analyst",
                   "What is multi-factor authentication?",
                   "Multi-factor authentication requires users to provide two or more different types of authentication factors to access an account.",
                   "Tests security fundamentals.",
                   "Mention password plus authenticator or biometric."
           ),

           q(
                   "Cyber Security",
                   "Cyber Security Analyst",
                   "What is the principle of least privilege?",
                   "Users and systems should receive only the access necessary to perform their required tasks.",
                   "Tests access-control knowledge.",
                   "Explain how it reduces security risk."
           ),

           // =========================================================
           // DEVOPS
           // =========================================================

           q(
                   "DevOps",
                   "DevOps Engineer",
                   "What is CI/CD?",
                   "Continuous integration validates code changes through automated build and test pipelines; continuous delivery or deployment automates release with appropriate controls.",
                   "Tests delivery fundamentals.",
                   "Connect it to quality and fast feedback."
           ),

           q(
                   "DevOps",
                   "DevOps Engineer",
                   "What is Docker?",
                   "Docker is a platform for packaging applications and their dependencies into containers so they can run consistently across environments.",
                   "Tests containerization knowledge.",
                   "Explain the difference between containers and virtual machines."
           ),

           q(
                   "DevOps",
                   "DevOps Engineer",
                   "How do you monitor a production application?",
                   "Use logs, metrics, traces and alerts to monitor availability, performance, errors and resource usage.",
                   "Tests operational knowledge.",
                   "Mention proactive alerting."
           ),

           // =========================================================
           // UI/UX DESIGN
           // =========================================================

           q(
                   "Design",
                   "UI/UX Designer",
                   "How do you validate a design?",
                   "Combine user research, prototypes, usability testing and analytics to validate whether users can complete important tasks efficiently.",
                   "Tests user-centred design.",
                   "Talk about evidence, not personal taste."
           ),

           q(
                   "Design",
                   "UI/UX Designer",
                   "What is the difference between UI and UX?",
                   "UI focuses on the visual and interactive interface, while UX focuses on the overall user experience, usability and satisfaction.",
                   "Tests design fundamentals.",
                   "Give a simple application example."
           ),

           q(
                   "Design",
                   "UI/UX Designer",
                   "What is a wireframe?",
                   "A wireframe is a simplified visual representation of a screen or webpage used to plan layout, content and functionality before detailed design.",
                   "Tests design process knowledge.",
                   "Mention low-fidelity prototypes."
           ),

           // =========================================================
           // NETWORKING
           // =========================================================

           q(
                   "Networking",
                   "Network Engineer",
                   "What is an IP address?",
                   "An IP address identifies a device on an IP network and allows network traffic to be delivered to the appropriate destination.",
                   "Tests networking fundamentals.",
                   "Mention IPv4 and IPv6."
           ),

           q(
                   "Networking",
                   "Network Engineer",
                   "What is the difference between LAN and WAN?",
                   "A LAN connects devices within a relatively small area such as an office, while a WAN connects networks across larger geographical areas.",
                   "Tests network concepts.",
                   "Give an office and branch-office example."
           ),

           q(
                   "Networking",
                   "Network Engineer",
                   "How would you troubleshoot a network connection?",
                   "Check physical connectivity, IP configuration, gateway, DNS, routing, firewall rules and connectivity tests in a logical sequence.",
                   "Tests troubleshooting ability.",
                   "Explain your troubleshooting order."
           ),

           // =========================================================
           // EDUCATION
           // =========================================================

           q(
                   "Education",
                   "Teacher",
                   "How do you handle students with different learning abilities?",
                   "Use differentiated instruction, varied learning materials, flexible activities and individual support based on student needs.",
                   "Tests teaching adaptability.",
                   "Give a practical classroom example."
           ),

           q(
                   "Education",
                   "Teacher",
                   "How do you evaluate student learning?",
                   "Use assessments, assignments, observation, participation and feedback to measure understanding and identify areas for improvement.",
                   "Tests assessment knowledge.",
                   "Mention formative and summative assessment."
           ),

           q(
                   "Education",
                   "Lecturer",
                   "How can technology improve education?",
                   "Technology can provide interactive learning resources, personalized learning, online collaboration, digital assessment and access to educational content.",
                   "Tests educational technology awareness.",
                   "Give examples of useful tools."
           ),

           // =========================================================
           // PROJECT MANAGEMENT
           // =========================================================

           q(
                   "Project Management",
                   "Project Manager",
                   "What is project management?",
                   "Project management involves planning, organizing and controlling resources and activities to achieve defined project objectives within constraints.",
                   "Tests project management fundamentals.",
                   "Mention scope, time, cost and quality."
           ),

           q(
                   "Project Management",
                   "Project Manager",
                   "How do you manage project risks?",
                   "Identify risks, assess their probability and impact, prioritize them and create mitigation or contingency plans.",
                   "Tests risk management.",
                   "Give a practical project example."
           ),

           q(
                   "Project Management",
                   "Project Manager",
                   "How do you handle a missed project deadline?",
                   "Identify the cause, assess the impact, communicate with stakeholders, reprioritize work and create a realistic recovery plan.",
                   "Tests problem solving and communication.",
                   "Take responsibility rather than hiding the issue."
           ),

           q(
                   "Project Management",
                   "Project Manager",
                   "How do you manage stakeholders?",
                   "Identify stakeholders, understand their interests, communicate appropriately and keep them informed about progress, risks and decisions.",
                   "Tests stakeholder management.",
                   "Adjust communication to different audiences."
           ),

           // =========================================================
           // SALES
           // =========================================================

           q(
                   "Sales",
                   "Sales Executive",
                   "What makes a successful salesperson?",
                   "A successful salesperson understands customer needs, communicates value, builds relationships, handles objections and follows up consistently.",
                   "Tests sales fundamentals.",
                   "Focus on solving customer problems."
           ),

           q(
                   "Sales",
                   "Sales Executive",
                   "How do you handle a customer who says the price is too high?",
                   "Understand the customer's concern, explain the value and benefits, explore alternatives where appropriate and avoid immediately reducing the price.",
                   "Tests negotiation skills.",
                   "Sell value rather than simply lowering price."
           ),

           // =========================================================
           // ENTREPRENEURSHIP
           // =========================================================

           q(
                   "Entrepreneurship",
                   "Entrepreneur",
                   "How would you identify a business opportunity?",
                   "Identify customer problems, research market demand, analyze competitors and determine whether a sustainable solution can be created.",
                   "Tests entrepreneurial thinking.",
                   "Start with a real customer problem."
           ),

           q(
                   "Entrepreneurship",
                   "Entrepreneur",
                   "What is a business model?",
                   "A business model explains how an organization creates value for customers and generates revenue sustainably.",
                   "Tests business understanding.",
                   "Mention customers, value proposition and revenue."
           ),

           // =========================================================
           // SUPPLY CHAIN
           // =========================================================

           q(
                   "Supply Chain",
                   "Supply Chain Executive",
                   "What is supply chain management?",
                   "Supply chain management coordinates sourcing, procurement, production, inventory, logistics and delivery to efficiently meet customer demand.",
                   "Tests supply chain fundamentals.",
                   "Mention cost, quality and delivery."
           ),

           q(
                   "Supply Chain",
                   "Supply Chain Executive",
                   "How would you manage an inventory shortage?",
                   "Identify the cause, assess demand and available stock, communicate with suppliers and stakeholders, prioritize critical orders and create a replenishment plan.",
                   "Tests operational problem solving.",
                   "Consider both immediate and long-term solutions."
           ),

           // =========================================================
           // CUSTOMER SERVICE
           // =========================================================

           q(
                   "Customer Service",
                   "Customer Service Executive",
                   "What is good customer service?",
                   "Good customer service means understanding customer needs, responding professionally, solving problems efficiently and creating a positive experience.",
                   "Tests service orientation.",
                   "Give an example from your experience."
           ),

           q(
                   "Customer Service",
                   "Customer Service Executive",
                   "How do you handle multiple customers at the same time?",
                   "Prioritize urgent issues, communicate expected response times, stay organized and ensure every customer receives appropriate attention.",
                   "Tests multitasking and organization.",
                   "Do not sacrifice accuracy for speed."
           ),

           // =========================================================
           // RESEARCH
           // =========================================================

           q(
                   "Research",
                   "Research Assistant",
                   "How do you select a research topic?",
                   "Identify a meaningful problem, review existing literature, find a research gap and ensure the topic is feasible with available resources and time.",
                   "Tests research planning.",
                   "Explain the importance of a research gap."
           ),

           q(
                   "Research",
                   "Research Assistant",
                   "What is a literature review?",
                   "A literature review systematically examines existing research to understand what is known, identify gaps and establish the context for a study.",
                   "Tests academic research knowledge.",
                   "Compare and synthesize sources rather than simply listing them."
           ),

           // =========================================================
           // GENERAL / GRADUATE
           // =========================================================

           q(
                   "General",
                   "Graduate",
                   "Why should we hire you?",
                   "Explain the combination of your relevant skills, education, projects, attitude and willingness to learn, connecting them directly to the requirements of the role.",
                   "Tests self-awareness and communication.",
                   "Focus on evidence instead of making unsupported claims."
           ),

           q(
                   "General",
                   "Graduate",
                   "What is your greatest strength?",
                   "Choose a strength relevant to the position and support it with a specific example showing how that strength produced a useful result.",
                   "Tests self-awareness.",
                   "Use evidence rather than simply naming an ability."
           ),

           q(
                   "General",
                   "Graduate",
                   "What is your biggest weakness?",
                   "Choose a genuine but manageable weakness and explain the specific steps you are taking to improve it.",
                   "Tests self-awareness and growth mindset.",
                   "Do not say you have no weaknesses."
           ),

           q(
                   "General",
                   "Graduate",
                   "Where do you see yourself in five years?",
                   "Describe a realistic direction showing professional growth, increasing responsibility and continued development of relevant skills.",
                   "Tests career planning.",
                   "Connect your goals with the position."
           ),

           q(
                   "General",
                   "Graduate",
                   "How do you handle pressure?",
                   "Break work into priorities, stay organized, communicate early about risks and focus on controllable actions.",
                   "Tests resilience and organization.",
                   "Give a real example if possible."
           )

   ));

  };
 }

 private static InterviewQuestion q(
         String category,
         String position,
         String question,
         String idealAnswer,
         String explanation,
         String tips
 ) {

  InterviewQuestion x = new InterviewQuestion();

  x.category = category;
  x.position = position;
  x.question = question;
  x.idealAnswer = idealAnswer;
  x.explanation = explanation;
  x.tips = tips;
  x.active = true;

  return x;
 }
}