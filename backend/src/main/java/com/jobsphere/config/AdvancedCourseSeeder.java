package com.jobsphere.config;

import com.jobsphere.entity.Course;
import com.jobsphere.entity.CourseQuestion;
import com.jobsphere.repository.CourseQuestionRepository;
import com.jobsphere.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Order(10)
public class AdvancedCourseSeeder implements CommandLineRunner {
    private final CourseRepository courses;
    private final CourseQuestionRepository questions;

    public AdvancedCourseSeeder(CourseRepository courses, CourseQuestionRepository questions) {
        this.courses = courses;
        this.questions = questions;
    }

    record Seed(String name, String category, String desc, String tags, String intro,
                String outcomes, String modules) {}

    @Override
    public void run(String... args) {
        List<Seed> seeds = List.of(
                new Seed(
                        "Advanced Software Engineering Career Accelerator",
                        "Software Engineering",
                        "A structured seven-day deep dive into engineering practices, APIs, databases, testing and professional delivery.",
                        "software, developer, java, spring, programming, backend, frontend, api, database, engineering, coding",
                        "This seven-day programme is designed for learners who already understand basic programming and want to move toward professional software engineering. Each day contains a focused lesson, practical thinking prompts and industry examples. Study one day at a time and complete the assessment only after reviewing all seven days.",
                        "Strengthen software design thinking; understand REST API and database practices; improve testing and debugging; learn Git and delivery workflows; communicate technical decisions professionally.",
                        moduleJson(
                                "Day 1 - Engineering Mindset",
                                "Professional software engineering begins with understanding the problem before writing code. A strong developer clarifies requirements, identifies constraints, thinks about users and considers maintainability. Good engineering is not only about making a program work once; it is about creating software that can be understood, tested, changed and supported by other people.\n\nIn a real team, developers balance delivery speed with reliability. They break large requirements into smaller tasks, identify risks early and document decisions. When a requirement is unclear, asking a precise question is usually better than making a hidden assumption.\n\nA useful daily habit is to write down the problem, expected outcome, assumptions and acceptance criteria before implementation.",
                                "Day 2 - Clean Code and Design",
                                "Clean code communicates intent. Meaningful names, small focused methods, sensible classes and consistent conventions reduce cognitive load for the next developer. Code should be simple enough that another person can understand the main flow without reverse engineering every line.\n\nObject-oriented design works best when responsibilities are separated. A class should have a clear purpose, dependencies should be explicit, and duplication should be reduced without creating unnecessary abstraction. Design decisions should be driven by the problem rather than by patterns used only for decoration.\n\nBefore refactoring, protect behaviour with tests or another reliable verification method. Refactor in small steps and verify after each change.",
                                "Day 3 - APIs and Integration",
                                "REST APIs allow applications to communicate through HTTP. A professional API uses meaningful resource-oriented URLs, appropriate methods, clear status codes and predictable JSON representations. Validation belongs at the boundary, while business rules should remain in the service layer.\n\nAuthentication and authorization are different concerns. Authentication identifies a user; authorization determines what that user may access. Sensitive operations should never rely only on frontend controls. The backend must check ownership and permissions on every protected request.\n\nWhen integrating an external service, use timeouts, error handling, logging and configuration rather than hard-coding secrets or URLs.",
                                "Day 4 - Databases and Persistence",
                                "Relational databases store structured data in tables connected through keys and constraints. Normalization reduces unnecessary duplication, while indexes improve lookup performance when chosen carefully. A developer should understand transactions because a business operation may update several records and must remain consistent if one step fails.\n\nUse parameterized queries or ORM mechanisms safely, validate input and avoid storing sensitive information that is not needed. Database migrations should be repeatable so that another developer can create the same structure.\n\nPerformance problems should be measured rather than guessed. Query plans, indexes and realistic test data help explain slow operations.",
                                "Day 5 - Testing and Debugging",
                                "Testing provides confidence that software behaves as intended. Unit tests focus on small pieces of logic, integration tests verify collaboration between components, and end-to-end tests validate important user journeys. A useful test checks both expected behaviour and meaningful edge cases.\n\nDebugging is a structured process: reproduce the problem, collect evidence, isolate the failing area, form a hypothesis, make the smallest safe change and verify the result. Logs and error messages should be informative but must not expose passwords, tokens or personal data.\n\nAfter fixing a defect, consider a regression test so the same problem is less likely to return.",
                                "Day 6 - Git, CI/CD and Delivery",
                                "Git gives teams a history of changes and supports collaborative development. Small commits with meaningful messages make reviews easier. Pull requests should explain what changed, why it changed and how it was tested.\n\nCI automatically builds and tests changes so problems are found early. CD extends this workflow toward repeatable releases. A mature pipeline may include tests, security checks, packaging, deployment and rollback controls.\n\nA developer should be comfortable reading build failures, resolving merge conflicts and communicating release risks. Delivery is a team responsibility, not simply a final button click.",
                                "Day 7 - Professional Engineering Practice",
                                "Senior-level growth comes from combining technical skill with communication, ownership and judgement. Engineers explain trade-offs in language that product owners and other stakeholders can understand. When a decision has risk, communicate the impact, options and recommendation instead of silently accepting the risk.\n\nCareer development also requires evidence. Keep a portfolio of meaningful projects, describe your contribution clearly, quantify outcomes where possible and be ready to explain why you selected a technology or architecture.\n\nBefore finishing this course, review each day and write three actions you can apply to your next project.",
                                7, 40
                        )),
                new Seed(
                        "Advanced Data Analytics & Business Intelligence",
                        "Data & Analytics",
                        "Seven days of practical analytics covering data preparation, SQL, statistics, dashboards and business storytelling.",
                        "data, analytics, sql, excel, power bi, statistics, business intelligence, dashboard, reporting, python",
                        "This advanced seven-day course takes learners from a business question to a defensible analytical recommendation. The lessons emphasise data quality, SQL thinking, statistics, dashboard design and communication with decision-makers.",
                        "Define analytical questions; prepare and validate datasets; use SQL and descriptive statistics; build meaningful dashboards; interpret relationships carefully; communicate evidence-based recommendations.",
                        moduleJson(
                                "Day 1 - Business Questions and Data",
                                "Good analysis begins with a decision, not a chart. A business question should identify what needs to be understood, who will use the answer and what action could follow. Analysts translate vague requests into measurable questions and define success criteria.\n\nData sources may include databases, spreadsheets, APIs, surveys and operational systems. Before analysis, document where the data came from, the period covered and important limitations.\n\nA useful data dictionary records field names, meanings, units, allowed values and missing-value conventions. This prevents analysts from making inconsistent assumptions.",
                                "Day 2 - Data Cleaning and Quality",
                                "Data cleaning is the process of detecting and resolving errors such as duplicates, missing values, inconsistent formats and invalid ranges. The correct treatment depends on why the problem exists. Deleting rows can be dangerous if missingness is systematic.\n\nQuality checks should be repeatable. For example, verify row counts, uniqueness of keys, date ranges, category values and relationships between totals. Keep an audit trail of important transformations so results can be reproduced.\n\nNever change a value simply because it looks inconvenient. Investigate the source and record the decision.",
                                "Day 3 - SQL and Analytical Queries",
                                "SQL is a core analytical skill because it allows structured questions to be answered close to the data. SELECT, WHERE, GROUP BY, JOIN, CASE and window functions support common reporting tasks. Analysts should understand how joins affect row counts and avoid accidental duplication.\n\nA strong query is readable and testable. Start with a small sample, verify joins, compare totals against trusted reports and then optimise if needed.\n\nUse clear aliases and document complex logic. If a metric is used repeatedly, consider creating a reusable view or governed dataset.",
                                "Day 4 - Statistics for Decisions",
                                "Descriptive statistics summarise data using measures such as mean, median, standard deviation and proportions. The median can be more representative when extreme values exist. Analysts should also distinguish association from causation.\n\nSampling creates uncertainty. Confidence intervals and hypothesis tests help quantify evidence, but statistical significance alone does not establish business importance. Consider effect size, context and practical consequences.\n\nVisual exploration should come before final interpretation. Outliers may represent errors, rare events or valuable business signals.",
                                "Day 5 - Dashboards and KPIs",
                                "A dashboard should answer a small set of important questions quickly. KPIs need a clear definition, owner, target, time period and data source. Too many charts reduce attention and make the dashboard harder to use.\n\nChoose visual encodings that match the question. Line charts work well for trends, bars for comparisons and tables for precise values. Use consistent labels and highlight exceptions rather than decorating every metric.\n\nA dashboard is a product. Test it with real users and revise based on the decisions they need to make.",
                                "Day 6 - Insight and Storytelling",
                                "An insight connects evidence to meaning. Instead of saying that a metric increased, explain the size of the change, where it occurred and why it matters. A recommendation should state the proposed action, supporting evidence, expected benefit and key risk.\n\nGood analytical storytelling has a logical sequence: context, finding, explanation, implication and action. Tailor technical detail to the audience.\n\nWhen evidence is weak, say so. Trust increases when analysts communicate uncertainty rather than presenting every result as certain.",
                                "Day 7 - Analytics Project and Professional Practice",
                                "A professional analytics project has a defined question, governed data, reproducible transformations, validated metrics, clear visualisations and a decision-oriented conclusion. Version-control important queries and document assumptions.\n\nBefore delivery, perform a quality review: verify totals, filters, dates, calculations, labels and permissions. Ask another person to review the logic when the decision is important.\n\nFinish the course by creating a one-page summary of the business question, data, method, main finding, recommendation and limitations.",
                                7, 40
                        )),
                new Seed(
                        "Advanced Business Analysis & Product Strategy",
                        "Business",
                        "Seven days of requirements, process analysis, stakeholder management, prioritisation and product decision-making.",
                        "business analyst, business, requirements, product, strategy, stakeholder, process, agile, product manager",
                        "This programme develops the ability to understand business problems, work with stakeholders and turn needs into clear requirements and prioritised product decisions.",
                        "Gather and validate requirements; model processes; manage stakeholders; write user stories and acceptance criteria; prioritise work; support data-informed product decisions.",
                        moduleJson(
                                "Day 1 - Problem Framing",
                                "A business analyst starts by understanding the problem rather than jumping to a solution. Clarify the current situation, desired outcome, users affected, constraints and evidence. A problem statement should be specific enough to guide investigation.\n\nSeparate symptoms from root causes. Five Whys, process observation and evidence review can help reveal underlying issues.\n\nDocument assumptions and questions so stakeholders can validate them early.",
                                "Day 2 - Requirements Engineering",
                                "Requirements describe what a solution needs to achieve. Functional requirements explain behaviours, while non-functional requirements cover qualities such as performance, security, usability and availability.\n\nGood requirements are clear, testable and traceable to a business objective. User stories can describe a need from the user's perspective, while acceptance criteria define observable conditions for completion.\n\nAvoid ambiguous words such as fast or easy unless they are measurable.",
                                "Day 3 - Process Analysis",
                                "Process analysis maps how work happens today and identifies delays, duplication, handoffs and control points. A current-state model should represent reality, including exceptions, not an idealised process.\n\nThe future-state design should explain what changes, why the change is useful and what risks it creates. Consider people, process, technology, policy and data together.\n\nValidate process maps with people who perform the work; they often know exceptions that documents miss.",
                                "Day 4 - Stakeholders and Communication",
                                "Stakeholders have different interests, influence and levels of technical knowledge. A stakeholder map helps determine who needs detailed involvement, who needs regular updates and who only needs key decisions.\n\nUse interviews, workshops, surveys and observation appropriately. Active listening is essential: paraphrase the requirement, confirm priorities and record decisions.\n\nWhen stakeholders disagree, expose the trade-off rather than hiding it. Use evidence, objectives and agreed criteria to support a decision.",
                                "Day 5 - Agile and Prioritisation",
                                "Agile delivery works through short feedback cycles. Product backlogs contain work that is refined and prioritised based on value, risk, dependencies and effort. Techniques such as MoSCoW or value-versus-effort matrices make priorities visible.\n\nA story is not complete because it is written; the team needs enough shared understanding to build and test it. Acceptance criteria reduce ambiguity.\n\nPriorities should be revisited when evidence or business conditions change.",
                                "Day 6 - Product Metrics and Decisions",
                                "Product metrics connect user behaviour to business outcomes. Examples include activation, retention, conversion, task success and support volume. Metrics should have definitions and avoid vanity measurement.\n\nA/B tests and experiments can help evaluate changes, but results require careful interpretation. Avoid changing multiple important variables without a plan to distinguish effects.\n\nCombine quantitative evidence with user feedback to understand not only what happened but why.",
                                "Day 7 - Strategy and Professional Delivery",
                                "Strategy connects a desired future state with choices about where to focus resources. A strong analyst can explain the current state, opportunity, options, recommendation and risks in a concise decision document.\n\nProfessional delivery means keeping requirements traceable, communicating changes, recording decisions and escalating blockers early.\n\nComplete the course by preparing a short product recommendation with problem, evidence, options, recommendation, success metrics and risks.",
                                7, 40
                        )),
                new Seed(
                        "Advanced Cybersecurity & Security Operations",
                        "Cybersecurity",
                        "Seven days covering identity, network security, secure behaviour, incident response, risk and security operations.",
                        "cybersecurity, security, network, soc, incident response, risk, authentication, cloud, privacy",
                        "This advanced course builds practical security awareness for technology and business professionals. It focuses on reducing risk, recognising incidents and responding through controlled processes.",
                        "Understand common attack paths; strengthen identity controls; interpret security events; support incident response; apply risk thinking; communicate security issues responsibly.",
                        moduleJson(
                                "Day 1 - Threats, Vulnerabilities and Risk",
                                "A threat is a potential cause of harm, while a vulnerability is a weakness that can be exploited. Risk combines likelihood and impact in a particular context.\n\nSecurity teams prioritise controls based on business importance. A public website, payment system and internal prototype may have very different risk profiles.\n\nRisk registers should describe assets, threats, existing controls, gaps, owners and treatment plans.",
                                "Day 2 - Identity and Access Management",
                                "Identity controls begin with strong authentication and continue through authorization, provisioning, review and deprovisioning. Multi-factor authentication reduces the impact of stolen passwords.\n\nLeast privilege limits damage by ensuring accounts have only the access they need. Privileged accounts require stronger monitoring and careful use.\n\nAccess reviews should be regular and linked to role changes. Former users and unnecessary permissions are common sources of exposure.",
                                "Day 3 - Network and Application Security",
                                "Network security uses segmentation, firewalls, secure protocols and monitoring to reduce attack paths. Applications should validate input, protect sessions and enforce authorization server-side.\n\nTransport encryption protects data in transit, but it does not make an application automatically secure. Secrets should be stored in protected configuration rather than source code.\n\nSecurity testing should include realistic misuse cases while respecting authorization and scope.",
                                "Day 4 - Detection and Incident Response",
                                "Security monitoring looks for unusual behaviour such as repeated failed logins, unexpected locations, suspicious processes or abnormal data transfer. Alerts require context to avoid both missed incidents and excessive false positives.\n\nIncident response usually includes preparation, identification, containment, eradication, recovery and lessons learned. Evidence should be preserved and actions documented.\n\nDo not improvise destructive actions during an incident. Follow the approved escalation process.",
                                "Day 5 - Phishing and Human Risk",
                                "Social engineering exploits trust, urgency and authority. A suspicious email may contain a fake login page, malicious attachment or request for payment or credentials.\n\nUsers should verify unexpected requests through an independent channel. Security awareness should teach practical behaviours rather than relying only on warnings.\n\nIf a mistake happens, prompt reporting is valuable because it gives defenders more time to contain the risk.",
                                "Day 6 - Security Governance and Privacy",
                                "Security governance aligns controls with laws, policies, contracts and business objectives. Data classification helps determine how information should be stored, shared and retained.\n\nPrivacy principles encourage collecting only necessary data, using it for defined purposes and protecting it from unauthorized access. Documentation supports accountability.\n\nSecurity decisions should consider people, process and technology together.",
                                "Day 7 - Security Operations Career Practice",
                                "Security professionals need technical curiosity, careful documentation and strong communication. A SOC analyst may investigate alerts, while a security engineer designs controls and a risk analyst evaluates business exposure.\n\nBuild a portfolio through safe labs, documented projects and incident-analysis exercises. Never test systems without permission.\n\nFinish by creating a simple incident report describing detection, evidence, impact, containment, recovery and prevention.",
                                7, 40
                        )),
                new Seed(
                        "Advanced Digital Marketing & Personal Branding",
                        "Digital Marketing",
                        "Seven days of practical digital marketing covering audience research, content, SEO, campaigns, analytics and professional branding.",
                        "marketing, digital marketing, social media, seo, content, branding, analytics, advertising, communication",
                        "This seven-day course develops a practical approach to building digital campaigns and a credible professional brand. Learners work from audience needs through content, distribution, measurement and improvement.",
                        "Research audiences; plan content; understand SEO basics; design campaign journeys; interpret marketing metrics; build a professional personal brand.",
                        moduleJson(
                                "Day 1 - Audience and Positioning",
                                "Marketing starts with understanding a specific audience and the problem a product or professional can help solve. Personas should be based on evidence rather than stereotypes.\n\nPositioning explains who the offer is for, what value it provides and why it is different. A clear value proposition helps keep content focused.\n\nFor personal branding, identify a target professional audience, your strongest evidence and the topics you can discuss credibly.",
                                "Day 2 - Content Strategy",
                                "Content should have a purpose such as awareness, education, consideration, conversion or retention. A content calendar helps balance topics, formats and publishing frequency.\n\nStrong content gives the audience useful information rather than simply promoting a service. Use clear headlines, examples, proof and calls to action appropriate to the stage of the journey.\n\nRepurpose valuable ideas into multiple formats while keeping the message consistent.",
                                "Day 3 - SEO and Discoverability",
                                "Search engine optimisation helps useful content become easier to discover. Start with search intent: understand what the user is trying to accomplish, then create content that genuinely answers the need.\n\nUse descriptive titles, headings, internal links and readable copy. Technical quality and mobile usability also matter.\n\nAvoid keyword stuffing or manipulative tactics. Long-term discoverability comes from relevance, quality and a trustworthy site.",
                                "Day 4 - Campaign Planning",
                                "A campaign should define objective, audience, message, channel, budget, timeline and measurement plan. The customer journey should connect awareness content to a clear next step.\n\nUse consistent creative assets and landing-page messaging. Test one meaningful variable at a time where possible.\n\nBudget decisions should be based on expected value and evidence, not simply the largest possible audience.",
                                "Day 5 - Social Media and Community",
                                "Social media works best when it supports conversation and community rather than broadcasting constantly. Respond respectfully, protect private information and use a consistent professional voice.\n\nCommunity feedback can reveal objections, language and content opportunities. Escalate complaints that require private support instead of arguing publicly.\n\nProfessional personal branding should show real work, learning and contribution rather than exaggerated claims.",
                                "Day 6 - Analytics and Optimisation",
                                "Marketing analytics connects activity to outcomes. Metrics such as impressions, click-through rate, conversion rate, cost per acquisition and retention should be interpreted in context.\n\nA metric moving upward is not automatically positive. For example, traffic may increase while conversion quality falls. Segment results by audience, channel and campaign where useful.\n\nUse a test-learn-improve cycle and document what changed so successful approaches can be repeated.",
                                "Day 7 - Personal Brand Portfolio",
                                "A strong professional brand is supported by evidence: projects, case studies, certificates, recommendations and useful public contributions. Your profile should communicate a clear direction without claiming skills you cannot demonstrate.\n\nCreate a simple portfolio structure with introduction, expertise, selected work, results, learning and contact information. Keep examples current and make them easy to scan.\n\nFinish the course by writing a 30-day content and networking plan aligned with your target role.",
                                7, 40
                        )),
                new Seed(
                        "Advanced Professional Communication & Leadership",
                        "Professional Skills",
                        "Seven days focused on executive communication, teamwork, negotiation, leadership, presentations and career confidence.",
                        "communication, leadership, teamwork, presentation, negotiation, management, soft skills, career",
                        "This seven-day programme helps learners communicate with greater clarity and confidence in meetings, presentations, stakeholder discussions and career conversations.",
                        "Improve executive-style communication; handle difficult conversations; present ideas clearly; negotiate professionally; lead collaborative work; build career confidence.",
                        moduleJson(
                                "Day 1 - Clear Professional Communication",
                                "Professional communication starts with purpose and audience. Before sending a message, decide what the reader needs to know, decide or do. Put the main point early and remove unnecessary detail.\n\nEmail, chat and documents have different expectations. Use concise subject lines, clear structure and appropriate tone. For complex decisions, a short summary followed by supporting detail works well.\n\nGood communication is measured by understanding, not by the number of words used.",
                                "Day 2 - Presentation and Storytelling",
                                "A strong presentation has a clear opening, logical flow, evidence and a conclusion. Slides should support the speaker rather than become a script of paragraphs.\n\nUse examples and stories when they clarify a problem, decision or outcome. Rehearse timing and anticipate likely questions.\n\nAfter presenting, capture questions and feedback. They can reveal where the audience needs clearer evidence or explanation.",
                                "Day 3 - Teamwork and Collaboration",
                                "Effective teams have shared goals, clear roles and reliable communication. Collaboration includes disagreement; the goal is to challenge ideas without attacking people.\n\nUse meeting agendas, action owners and deadlines to turn discussion into progress. When responsibilities overlap, clarify ownership instead of assuming someone else will act.\n\nTrust grows when commitments are kept and problems are communicated early.",
                                "Day 4 - Difficult Conversations and Negotiation",
                                "Difficult conversations should focus on facts, impact and the desired improvement. Prepare examples and choose a private, appropriate setting.\n\nNegotiation works best when both sides understand interests, constraints and alternatives. Separate the person from the issue and look for trade-offs that preserve the important outcome.\n\nDo not promise what you cannot deliver. Record important agreements and follow up.",
                                "Day 5 - Leadership and Decision Making",
                                "Leadership is the ability to create direction, enable people and take responsibility. It does not require a formal title. Good leaders clarify outcomes, remove blockers and create space for others to contribute.\n\nDecision making should distinguish reversible from difficult-to-reverse choices. Use evidence, seek relevant input and decide within the available time.\n\nWhen a decision is made, communicate what was decided, why, who owns the next step and when it will be reviewed.",
                                "Day 6 - Career Conversations and Networking",
                                "Career conversations become more useful when you prepare specific questions. Ask about skills, team culture, common entry routes, current challenges and advice for someone at your stage.\n\nNetworking is relationship building, not collecting contacts. Be respectful of time, follow up with thanks and offer useful context about your interests.\n\nWhen speaking with an industry professional, do not ask for confidential company information. Focus on knowledge, experience and publicly discussable career topics.",
                                "Day 7 - Leadership Portfolio and Personal Growth",
                                "Career confidence grows from evidence and reflection. Keep a record of projects, responsibilities, feedback and outcomes. Use these examples in interviews and performance conversations.\n\nSet development goals that are specific and measurable. Choose a small number of behaviours to practise repeatedly rather than trying to improve everything at once.\n\nFinish with a 30-day communication and leadership plan that includes one presentation, one networking conversation and one measurable workplace improvement.",
                                7, 40
                        ))
        );

        for (Seed seed : seeds) {
            Course c = courses.findByNameIgnoreCase(seed.name()).orElse(null);
            if (c == null) {
                c = new Course();
                c.name = seed.name();
            }
            c.category = seed.category();
            c.durationMinutes = 7 * 24 * 60;
            c.shortDescription = seed.desc();
            c.introduction = seed.intro();
            c.eligibility = "Suitable for learners whose profile matches this career area and who are ready to study one focused lesson per day for seven days.";
            c.outcomes = seed.outcomes();
            c.modulesJson = seed.modules();
            c.industryApplication = "Practical industry application for " + seed.category() + ".";
            c.tags = seed.tags();
            c.active = true;
            c.courseLevel = "ADVANCED";
            c.durationDays = 7;
            c.price = new BigDecimal("250.00");
            c = courses.save(c);

            if (questions.findByCourseIdAndActiveTrueOrderByIdAsc(c.id).size() < 10) {
                for (String[] q : questionsFor(seed.category())) {
                    CourseQuestion cq = new CourseQuestion();
                    cq.setCourseId(c.id);
                    cq.setQuestion(q[0]);
                    cq.setOptionA(q[1]);
                    cq.setOptionB(q[2]);
                    cq.setOptionC(q[3]);
                    cq.setOptionD(q[4]);
                    cq.setCorrectOption(q[5]);
                    cq.setExplanation("Review the relevant lesson before attempting the question again.");
                    cq.setActive(true);
                    questions.save(cq);
                }
            }
        }
    }

    private static String[][] questionsFor(String category) {
        String c = category == null ? "this career area" : category;
        return new String[][]{
                {"What is the main purpose of professional learning in " + c + "?", "To apply knowledge to real problems", "To avoid practice", "To memorise without understanding", "To ignore feedback", "A"},
                {"Which approach is most useful when solving a problem in " + c + "?", "Define the problem and gather evidence", "Start without requirements", "Ignore constraints", "Choose the first idea immediately", "A"},
                {"Why is clear communication important in " + c + "?", "It helps teams understand decisions and responsibilities", "It removes the need for teamwork", "It guarantees every decision is correct", "It replaces technical knowledge", "A"},
                {"What should a professional do when a requirement is unclear?", "Ask a focused question and confirm the expected outcome", "Make a hidden assumption", "Ignore the requirement", "Delete the task", "A"},
                {"Which practice supports continuous improvement?", "Review results and use feedback to improve", "Never review completed work", "Avoid measuring outcomes", "Repeat the same process regardless of results", "A"},
                {"What is a good professional response to a mistake?", "Report it, understand the cause and take corrective action", "Hide it", "Blame another person immediately", "Delete the evidence", "A"},
                {"Why should professional decisions be supported by evidence?", "Evidence helps explain and evaluate the decision", "Evidence is unnecessary", "It makes decisions slower in every situation", "It prevents discussion", "A"},
                {"Which behaviour is most valuable in a workplace team?", "Reliability, respect and clear collaboration", "Withholding information", "Avoiding responsibility", "Ignoring agreed deadlines", "A"},
                {"What is an appropriate way to develop a career in " + c + "?", "Build skills through projects, learning, feedback and relevant experience", "Wait without practising", "Use only a CV without evidence", "Avoid industry knowledge", "A"},
                {"What should a learner do after completing an advanced course?", "Apply the learning in a practical project and continue improving", "Stop learning permanently", "Ignore feedback", "Avoid real-world practice", "A"}
        };
    }

    private static String moduleJson(String t1, String th1, String t2, String th2, String t3, String th3,
                                     String t4, String th4, String t5, String th5, String t6, String th6,
                                     String t7, String th7, int days, int minutes) {
        String[] titles={t1,t2,t3,t4,t5,t6,t7};
        String[] theory={th1,th2,th3,th4,th5,th6,th7};
        StringBuilder b=new StringBuilder("[");
        for(int i=0;i<7;i++){
            if(i>0)b.append(",");
            b.append("{\"title\":\"").append(json(titles[i])).append("\",\"minutes\":").append(minutes)
                    .append(",\"day\":").append(i+1).append(",\"theory\":\"").append(json(theory[i])).append("\"}");
        }
        return b.append("]").toString();
    }

    private static String json(String s) {
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\r","").replace("\n","\\n");
    }
}
