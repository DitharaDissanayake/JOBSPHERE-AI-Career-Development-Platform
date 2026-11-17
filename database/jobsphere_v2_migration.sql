USE jobsphere;
CREATE TABLE IF NOT EXISTS password_reset_tokens (id BIGINT PRIMARY KEY AUTO_INCREMENT,user_id BIGINT NOT NULL,code_hash VARCHAR(255) NOT NULL,expires_at DATETIME NOT NULL,used BOOLEAN NOT NULL DEFAULT FALSE,attempts INT NOT NULL DEFAULT 0,created_at DATETIME NOT NULL,CONSTRAINT fk_reset_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS interview_questions (id BIGINT PRIMARY KEY AUTO_INCREMENT,category VARCHAR(120) NOT NULL,position VARCHAR(160) NOT NULL,question TEXT NOT NULL,ideal_answer TEXT NOT NULL,explanation TEXT,tips TEXT,active BOOLEAN NOT NULL DEFAULT TRUE) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS interview_sessions (id BIGINT PRIMARY KEY AUTO_INCREMENT,user_id BIGINT NOT NULL,category VARCHAR(120),position VARCHAR(160),source_type VARCHAR(30) NOT NULL,session_json LONGTEXT NOT NULL,created_at DATETIME NOT NULL,CONSTRAINT fk_interview_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS media_recommendations (id BIGINT PRIMARY KEY AUTO_INCREMENT,user_id BIGINT NOT NULL,media_type VARCHAR(20) NOT NULL,title VARCHAR(255) NOT NULL,url VARCHAR(500) NOT NULL,embed_url VARCHAR(500),description TEXT,source VARCHAR(160),category VARCHAR(120),position VARCHAR(160),created_at DATETIME NOT NULL,CONSTRAINT fk_media_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE) ENGINE=InnoDB;
INSERT INTO interview_questions(category,position,question,ideal_answer,explanation,tips) VALUES
('Software Engineering','Software Engineer','Tell me about yourself.','Give a 60–90 second summary of your education, relevant projects, technical strengths and the role you want next.','Tests communication and relevance.','Use present → evidence → future. Avoid personal life details.'),
('Software Engineering','Software Engineer','Describe a software project you are proud of.','Explain the problem, your responsibility, technologies, an important challenge and measurable result.','Tests ownership and technical depth.','Use STAR and be specific about your contribution.'),
('Software Engineering','Software Engineer','How do you debug a production issue?','Reproduce safely, inspect logs/metrics, isolate the failing component, form hypotheses, test the smallest change, validate and document the root cause.','Tests systematic troubleshooting.','Mention monitoring, rollback and prevention.'),
('Software Engineering','Software Engineer','Explain REST APIs.','REST APIs expose resources through HTTP methods such as GET, POST, PUT/PATCH and DELETE, using clear URLs, status codes and representations such as JSON.','Tests web fundamentals.','Give one practical example.'),
('Software Engineering','Software Engineer','What is database normalization?','It organizes relational data to reduce redundancy and update anomalies by separating related facts into appropriate tables and relationships.','Tests database fundamentals.','Mention 1NF, 2NF and 3NF briefly.'),
('Software Engineering','Software Engineer','How do you handle a code review comment you disagree with?','Understand the concern, discuss the trade-off with evidence, propose alternatives and follow the team decision while keeping the discussion respectful.','Tests collaboration.','Never make it personal.'),
('Software Engineering','Software Engineer','What is your approach to learning a new technology?','Start with official documentation, build a small proof of concept, apply it to a real task, test edge cases and record lessons learned.','Tests adaptability.','Connect the method to a real example.'),
('Data','Data Analyst','How would you approach a new analytics problem?','Clarify the business question, identify data sources, clean and validate data, explore patterns, analyze, visualize and communicate actionable findings.','Tests analytical thinking.','Always start with the decision the analysis supports.'),
('Data','Data Analyst','How do you handle missing data?','Understand why values are missing, quantify the impact, then choose deletion, imputation or an explicit missing category based on context.','Tests data quality judgement.','Explain why your choice is appropriate.'),
('Business','Business Analyst','How do you gather requirements?','Identify stakeholders, understand the business goal, conduct interviews/workshops, document functional and non-functional requirements and validate them with stakeholders.','Tests requirements practice.','Use examples such as user stories or acceptance criteria.'),
('Design','UI/UX Designer','How do you validate a design?','Combine user research, prototypes, usability testing and analytics to validate whether users can complete important tasks efficiently.','Tests user-centred design.','Talk about evidence, not personal taste.'),
('Cyber Security','Cyber Security Analyst','How would you respond to a suspicious login alert?','Validate the alert, check identity and device context, inspect logs, contain the account/device if necessary, investigate scope, recover safely and document lessons learned.','Tests incident response thinking.','Mention escalation and evidence preservation.'),
('DevOps','DevOps Engineer','What is CI/CD?','Continuous integration frequently validates code changes through automated build and test pipelines; continuous delivery/deployment automates release to environments with appropriate controls.','Tests delivery fundamentals.','Connect it to quality and fast feedback.');


-- ============================================================
-- JOBSPHERE REAL-WORLD LEARNING COURSES + USER SETTINGS
-- ============================================================
USE jobsphere;

CREATE TABLE IF NOT EXISTS courses (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(180) NOT NULL UNIQUE, category VARCHAR(120) NOT NULL,
 duration_minutes INT NOT NULL DEFAULT 30, short_description VARCHAR(500), introduction TEXT, eligibility TEXT,
 outcomes TEXT, modules_json LONGTEXT, industry_application TEXT, tags TEXT, active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS course_questions (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, course_id BIGINT NOT NULL, question TEXT NOT NULL,
 option_a VARCHAR(500) NOT NULL, option_b VARCHAR(500) NOT NULL, option_c VARCHAR(500) NOT NULL, option_d VARCHAR(500) NOT NULL,
 correct_option CHAR(1) NOT NULL, explanation TEXT, active BOOLEAN NOT NULL DEFAULT TRUE,
 CONSTRAINT fk_course_question_course FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS course_attempts (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, course_id BIGINT NOT NULL, user_id BIGINT NOT NULL, score INT NOT NULL,
 correct_answers INT NOT NULL, total_questions INT NOT NULL, passed BOOLEAN NOT NULL, answers_json LONGTEXT,
 completed_at DATETIME NOT NULL, certificate_sent BOOLEAN NOT NULL DEFAULT FALSE,
 CONSTRAINT fk_attempt_course FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE CASCADE,
 CONSTRAINT fk_attempt_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_settings (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NOT NULL UNIQUE, email_notifications BOOLEAN NOT NULL DEFAULT TRUE,
 course_reminders BOOLEAN NOT NULL DEFAULT TRUE, career_recommendations BOOLEAN NOT NULL DEFAULT TRUE,
 profile_visibility BOOLEAN NOT NULL DEFAULT TRUE, theme VARCHAR(20) NOT NULL DEFAULT 'system',
 language VARCHAR(20) NOT NULL DEFAULT 'English', updated_at DATETIME NOT NULL,
 CONSTRAINT fk_settings_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS course_certificates (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, course_id BIGINT NOT NULL, user_id BIGINT NOT NULL, attempt_id BIGINT NOT NULL UNIQUE,
 certificate_number VARCHAR(120) NOT NULL UNIQUE, file_name VARCHAR(255) NOT NULL, content_type VARCHAR(100) NOT NULL,
 file_data LONGBLOB NOT NULL, sent_at DATETIME NULL, issued_at DATETIME NOT NULL,
 CONSTRAINT fk_cert_course FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE CASCADE,
 CONSTRAINT fk_cert_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
 CONSTRAINT fk_cert_attempt FOREIGN KEY(attempt_id) REFERENCES course_attempts(id) ON DELETE CASCADE
) ENGINE=InnoDB;


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
