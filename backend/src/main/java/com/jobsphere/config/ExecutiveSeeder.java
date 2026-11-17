package com.jobsphere.config;

import com.jobsphere.entity.Executive;
import com.jobsphere.repository.ExecutiveRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExecutiveSeeder implements CommandLineRunner {

    private final ExecutiveRepository repo;

    public ExecutiveSeeder(ExecutiveRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {

        /*
         * ============================================================
         * JOBSPHERE DEMO EXECUTIVE / MENTOR DATA
         * ============================================================
         *
         * These are SAMPLE profiles created for development,
         * testing and academic demonstration purposes.
         *
         * All six profiles use the same WhatsApp-enabled test number:
         *
         * Local number:
         * 0711262422
         *
         * International format:
         * +94711262422
         *
         * Before production, replace these demo profiles with
         * approved company professionals who have agreed to be
         * contacted through the platform.
         * ============================================================
         */

        /*
         * Do not insert duplicate records every time the
         * Spring Boot application starts.
         */
        if (repo.count() > 0) {
            return;
        }

        List<Executive> data = List.of(

                // ====================================================
                // 1. SOFTWARE ENGINEERING
                // ====================================================

                mentor(
                        "Nadeesha Perera",
                        "TechNova Solutions",
                        "Senior Software Engineering Manager",
                        "+94711262422",
                        "Software Engineering",

                        "software,java,spring,spring boot,developer,programming,"
                                + "backend,frontend,full stack,software engineering,"
                                + "technical interview,career",

                        "Engineering leadership, software development careers, "
                                + "Java, Spring Boot, backend development, "
                                + "technical interviews, project guidance and "
                                + "software engineering career development",

                        "Experienced software engineering professional who provides "
                                + "practical career guidance for students and graduates. "
                                + "Can help learners understand software engineering "
                                + "career paths, improve technical skills, build "
                                + "practical projects, prepare for technical interviews "
                                + "and develop a strong professional portfolio. "
                                + "This is a demo mentor profile created for JOBSPHERE "
                                + "academic testing."
                ),

                // ====================================================
                // 2. DATA & ANALYTICS
                // ====================================================

                mentor(
                        "Kasun Fernando",
                        "DataBridge Analytics",
                        "Lead Data & BI Consultant",
                        "+94711262422",
                        "Data & Analytics",

                        "data,analytics,data analytics,sql,python,power bi,"
                                + "statistics,business intelligence,visualization,"
                                + "data science,reporting,career",

                        "Data Analytics, SQL, Python, Power BI, statistics, "
                                + "business intelligence, data visualization, "
                                + "portfolio development and analytics careers",

                        "Data and business intelligence professional who provides "
                                + "career guidance for learners interested in "
                                + "Data & Analytics. Can help students understand "
                                + "industry expectations, improve SQL and Python "
                                + "skills, create Power BI dashboards, develop "
                                + "analytics projects and build a strong portfolio. "
                                + "This is a demo mentor profile created for "
                                + "JOBSPHERE academic testing."
                ),

                // ====================================================
                // 3. DIGITAL MARKETING
                // ====================================================

                mentor(
                        "Shenali Wijesinghe",
                        "Lanka Digital Studio",
                        "Head of Digital Strategy",
                        "+94711262422",
                        "Digital Marketing",

                        "marketing,digital marketing,seo,social media,branding,"
                                + "content,content marketing,digital strategy,"
                                + "campaign,advertising,personal branding",

                        "Digital marketing, SEO, social media, personal branding, "
                                + "content strategy, digital campaigns and marketing "
                                + "career development",

                        "Digital strategy professional who provides practical "
                                + "career guidance for learners interested in "
                                + "digital marketing. Can help students understand "
                                + "digital campaigns, improve social media skills, "
                                + "develop content strategies, understand SEO and "
                                + "build a professional personal brand. "
                                + "This is a demo mentor profile created for "
                                + "JOBSPHERE academic testing."
                ),

                // ====================================================
                // 4. CYBERSECURITY
                // ====================================================

                mentor(
                        "Dulanjan Silva",
                        "SecureCore Lanka",
                        "Security Operations Lead",
                        "+94711262422",
                        "Cybersecurity",

                        "cybersecurity,security,network,networking,cloud,"
                                + "privacy,risk,soc,security operations,"
                                + "information security,ethical hacking",

                        "Cybersecurity careers, Security Operations Centre "
                                + "work, networking, cloud security, privacy, "
                                + "risk management and security fundamentals",

                        "Cybersecurity professional who provides career "
                                + "guidance for students interested in "
                                + "information security and security operations. "
                                + "Can help learners understand cybersecurity "
                                + "career paths, security fundamentals, networking, "
                                + "SOC operations, cloud security and professional "
                                + "development. This is a demo mentor profile "
                                + "created for JOBSPHERE academic testing."
                ),

                // ====================================================
                // 5. BUSINESS ANALYSIS
                // ====================================================

                mentor(
                        "Tharushi Jayawardena",
                        "BusinessFlow Consulting",
                        "Senior Business Analyst",
                        "+94711262422",
                        "Business",

                        "business,business analyst,analysis,requirements,"
                                + "requirements gathering,product,process,"
                                + "agile,strategy,stakeholder,documentation",

                        "Business analysis, requirements gathering, product "
                                + "thinking, process improvement, Agile, "
                                + "stakeholder communication and business strategy",

                        "Business analysis professional who helps learners "
                                + "understand how business requirements are "
                                + "identified, analysed and communicated. "
                                + "Can provide guidance on requirements gathering, "
                                + "process analysis, stakeholder communication, "
                                + "Agile practices, product thinking and business "
                                + "analysis career development. This is a demo "
                                + "mentor profile created for JOBSPHERE "
                                + "academic testing."
                ),

                // ====================================================
                // 6. PROFESSIONAL SKILLS
                // ====================================================

                mentor(
                        "Ravindu Senanayake",
                        "GrowthWorks Lanka",
                        "People & Leadership Partner",
                        "+94711262422",
                        "Professional Skills",

                        "communication,leadership,teamwork,presentation,"
                                + "management,career,professional development,"
                                + "public speaking,collaboration,workplace",

                        "Communication, leadership, teamwork, presentation "
                                + "skills, workplace development, management "
                                + "and career growth",

                        "Professional development specialist who provides "
                                + "guidance on workplace communication, leadership "
                                + "and career development. Can help learners "
                                + "improve presentation skills, teamwork, "
                                + "professional communication, leadership "
                                + "abilities and workplace confidence. "
                                + "This is a demo mentor profile created for "
                                + "JOBSPHERE academic testing."
                )
        );

        /*
         * Save all six demo executives.
         */
        repo.saveAll(data);
    }

    // ============================================================
    // CREATE EXECUTIVE
    // ============================================================

    private Executive mentor(
            String name,
            String company,
            String position,
            String phone,
            String category,
            String tags,
            String expertise,
            String bio
    ) {

        Executive e = new Executive();

        // Basic professional information
        e.name = name;
        e.company = company;
        e.position = position;

        // ========================================================
        // CONTACT INFORMATION
        // ========================================================

        /*
         * Same test number is used for all demo profiles.
         *
         * Local:
         * 0711262422
         *
         * International:
         * +94711262422
         */
        e.phoneNumber = phone;
        e.whatsappNumber = phone;

        // ========================================================
        // PROFILE MATCHING INFORMATION
        // ========================================================

        e.category = category;

        /*
         * These tags are used by JOBSPHERE to identify
         * relevant executives for a user's profile.
         */
        e.profileTags = tags;

        /*
         * Skills and areas that the executive can help with.
         */
        e.expertise = expertise;

        /*
         * Detailed profile description.
         */
        e.bio = bio;

        // ========================================================
        // STATUS
        // ========================================================

        e.active = true;

        // ========================================================
        // PROFILE IMAGE
        // ========================================================

        e.imageUrl =
                "assets/images/executives/"
                        + slug(name)
                        + ".svg";

        return e;
    }

    // ============================================================
    // CREATE IMAGE FILE NAME
    // ============================================================

    private String slug(String n) {

        return n
                .toLowerCase()
                .replace(" ", "-");
    }
}