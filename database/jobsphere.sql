CREATE DATABASE IF NOT EXISTS jobsphere CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE jobsphere;
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS resume_analyses, resumes, career_analyses, dashboard_insights, certificates, feedback, contact_messages, user_profiles, jobs, users;
SET FOREIGN_KEY_CHECKS=1;
CREATE TABLE users (id BIGINT PRIMARY KEY AUTO_INCREMENT, full_name VARCHAR(120) NOT NULL, email VARCHAR(160) NOT NULL UNIQUE, password_hash VARCHAR(255) NOT NULL, role ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER', active BOOLEAN NOT NULL DEFAULT TRUE, created_at DATETIME NOT NULL) ENGINE=InnoDB;
CREATE TABLE user_profiles (id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NOT NULL UNIQUE, phone VARCHAR(20), location VARCHAR(160), university VARCHAR(200), degree VARCHAR(200), graduation_year INT, employment_status VARCHAR(100), headline VARCHAR(200), education TEXT, skills TEXT, soft_skills TEXT, experience TEXT, projects TEXT, certifications TEXT, career_interest TEXT, career_goal TEXT, portfolio_url VARCHAR(255), linkedin_url VARCHAR(255), github_url VARCHAR(255), updated_at DATETIME NOT NULL, CONSTRAINT fk_profile_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE) ENGINE=InnoDB;
CREATE TABLE certificates (id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NOT NULL, file_name VARCHAR(255) NOT NULL, content_type VARCHAR(100) NOT NULL, file_size BIGINT NOT NULL, file_data LONGBLOB NOT NULL, uploaded_at DATETIME NOT NULL, CONSTRAINT fk_certificate_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE) ENGINE=InnoDB;
CREATE TABLE dashboard_insights (id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NOT NULL, profile_snapshot LONGTEXT NOT NULL, insight_json LONGTEXT NOT NULL, created_at DATETIME NOT NULL, CONSTRAINT fk_dashboard_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE) ENGINE=InnoDB;
CREATE TABLE career_analyses (id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NOT NULL, profile_snapshot LONGTEXT NOT NULL, analysis_json LONGTEXT NOT NULL, created_at DATETIME NOT NULL, CONSTRAINT fk_career_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE) ENGINE=InnoDB;
CREATE TABLE resumes (id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NOT NULL, file_name VARCHAR(255) NOT NULL, content_type VARCHAR(100) NOT NULL, file_size BIGINT NOT NULL, file_data LONGBLOB NOT NULL, extracted_text LONGTEXT, uploaded_at DATETIME NOT NULL, CONSTRAINT fk_resume_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE) ENGINE=InnoDB;
CREATE TABLE resume_analyses (id BIGINT PRIMARY KEY AUTO_INCREMENT, resume_id BIGINT NOT NULL, user_id BIGINT NOT NULL, analysis_json LONGTEXT NOT NULL, created_at DATETIME NOT NULL, CONSTRAINT fk_resume_analysis_resume FOREIGN KEY(resume_id) REFERENCES resumes(id) ON DELETE CASCADE, CONSTRAINT fk_resume_analysis_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE) ENGINE=InnoDB;
CREATE TABLE jobs (id BIGINT PRIMARY KEY AUTO_INCREMENT, title VARCHAR(200) NOT NULL, company VARCHAR(200) NOT NULL, location VARCHAR(160) NOT NULL, skills TEXT NOT NULL, employment_type VARCHAR(80), description TEXT) ENGINE=InnoDB;
CREATE TABLE contact_messages (id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(120) NOT NULL, email VARCHAR(160) NOT NULL, subject VARCHAR(255), message TEXT NOT NULL, is_read BOOLEAN NOT NULL DEFAULT FALSE, created_at DATETIME NOT NULL) ENGINE=InnoDB;
CREATE TABLE feedback (id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NULL, rating TINYINT NOT NULL, comment VARCHAR(1000), created_at DATETIME NOT NULL, CONSTRAINT chk_feedback_rating CHECK(rating BETWEEN 1 AND 5), CONSTRAINT fk_feedback_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE SET NULL) ENGINE=InnoDB;
INSERT INTO jobs(title,company,location,skills,employment_type,description) VALUES
('Junior Software Engineer','99X','Colombo, Sri Lanka','Java, Spring Boot, SQL, Git, REST APIs','Full-time','Software development role focused on backend and web application engineering.'),
('Associate Software Engineer','WSO2','Colombo, Sri Lanka','Java, Python, APIs, SQL, Linux, Git','Full-time','Engineering role for building scalable software and API products.'),
('Software QA Engineer','IFS','Colombo, Sri Lanka','Manual Testing, Automation, SQL, Java, Selenium, Agile','Full-time','Quality assurance role covering functional and automated testing.'),
('Data Analyst','MillenniumIT ESP','Colombo, Sri Lanka','SQL, Excel, Python, Power BI, Statistics','Full-time','Analyze business data and communicate insights to stakeholders.'),
('Business Analyst','Virtusa','Colombo, Sri Lanka','Requirements Analysis, SQL, UML, Communication, Agile','Full-time','Bridge business requirements and technology delivery teams.'),
('UI/UX Designer','Creative Software','Colombo, Sri Lanka','Figma, UX Research, Prototyping, HTML, CSS','Full-time','Design user-centred digital experiences and prototypes.'),
('Cyber Security Analyst','hSenid','Colombo, Sri Lanka','Networking, Linux, Security, SIEM, Risk Analysis','Full-time','Monitor security events and support cyber risk management.'),
('Cloud / DevOps Engineer','Sysco LABS','Colombo, Sri Lanka','AWS, Docker, Linux, CI/CD, Git, Kubernetes','Full-time','Automate delivery and operate reliable cloud infrastructure.'),
('AI / Machine Learning Engineer','Dialog Axiata','Colombo, Sri Lanka','Python, Machine Learning, SQL, Statistics, APIs','Full-time','Develop and evaluate intelligent data-driven solutions.');
-- Admin accounts are seeded automatically by Spring Boot. Default password: Admin@123


-- JOBSPHERE v2 feature tables are created automatically by Hibernate update. For manual setup run database/jobsphere_v2_migration.sql after this file.


-- ============================================================
-- JOBSPHERE AI CHAT HISTORY
-- Conversations and messages are isolated by authenticated user.
-- ============================================================
CREATE TABLE IF NOT EXISTS chat_conversations (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 user_id BIGINT NOT NULL,
 user_email VARCHAR(160) NOT NULL,
 title VARCHAR(180) NOT NULL DEFAULT 'New conversation',
 created_at DATETIME NOT NULL,
 updated_at DATETIME NOT NULL,
 CONSTRAINT fk_chat_conversation_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
 INDEX idx_chat_conversation_user_updated(user_id,updated_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS chat_messages (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 conversation_id BIGINT NOT NULL,
 user_id BIGINT NOT NULL,
 role ENUM('USER','ASSISTANT') NOT NULL,
 content LONGTEXT NOT NULL,
 created_at DATETIME NOT NULL,
 CONSTRAINT fk_chat_message_conversation FOREIGN KEY(conversation_id) REFERENCES chat_conversations(id) ON DELETE CASCADE,
 CONSTRAINT fk_chat_message_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
 INDEX idx_chat_message_conversation_created(conversation_id,created_at)
) ENGINE=InnoDB;


-- JOBSPHERE ADVANCED COURSES / PAYMENTS / MENTORS
CREATE TABLE IF NOT EXISTS course_payments (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 user_id BIGINT NOT NULL,
 course_id BIGINT NOT NULL,
 order_id VARCHAR(100) NOT NULL UNIQUE,
 amount DECIMAL(10,2) NOT NULL,
 currency VARCHAR(3) NOT NULL DEFAULT 'LKR',
 status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
 gateway VARCHAR(30) NOT NULL DEFAULT 'DEMO',
 payment_id VARCHAR(120),
 payment_method VARCHAR(40),
 card_brand VARCHAR(30),
 card_last4 VARCHAR(4),
 invoice_file_name VARCHAR(255),
 invoice_data LONGBLOB,
 email_sent BOOLEAN NOT NULL DEFAULT FALSE,
 created_at DATETIME NOT NULL,
 paid_at DATETIME NULL,
 CONSTRAINT fk_course_payment_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
 CONSTRAINT fk_course_payment_course FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE CASCADE,
 INDEX idx_course_payment_user(user_id),
 INDEX idx_course_payment_order(order_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS course_progress (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 user_id BIGINT NOT NULL,
 course_id BIGINT NOT NULL,
 completed_days TEXT,
 updated_at DATETIME NOT NULL,
 CONSTRAINT uk_course_progress_user_course UNIQUE(user_id,course_id),
 CONSTRAINT fk_course_progress_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
 CONSTRAINT fk_course_progress_course FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS career_executives (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 name VARCHAR(160) NOT NULL,
 company VARCHAR(160) NOT NULL,
 position VARCHAR(160) NOT NULL,
 phone_number VARCHAR(30) NOT NULL,
 whatsapp_number VARCHAR(30) NOT NULL,
 image_url VARCHAR(255),
 category VARCHAR(120),
 expertise TEXT,
 profile_tags TEXT,
 bio TEXT,
 active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS mentor_interactions (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 user_id BIGINT NOT NULL,
 executive_id BIGINT NOT NULL,
 action VARCHAR(40) NOT NULL,
 created_at DATETIME NOT NULL,
 CONSTRAINT fk_mentor_interaction_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
 CONSTRAINT fk_mentor_interaction_exec FOREIGN KEY(executive_id) REFERENCES career_executives(id) ON DELETE CASCADE,
 INDEX idx_mentor_interaction_user(user_id),
 INDEX idx_mentor_interaction_executive(executive_id)
) ENGINE=InnoDB;
