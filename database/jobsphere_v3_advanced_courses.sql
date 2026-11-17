USE jobsphere;

-- Advanced course metadata. Run once on an existing JOBSPHERE database.
ALTER TABLE courses ADD COLUMN course_level VARCHAR(20) NULL;
ALTER TABLE courses ADD COLUMN duration_days INT NULL;
ALTER TABLE courses ADD COLUMN price DECIMAL(10,2) NULL;

UPDATE courses SET course_level='BASIC' WHERE course_level IS NULL;
UPDATE courses SET duration_days=0 WHERE duration_days IS NULL;
UPDATE courses SET price=0.00 WHERE price IS NULL;

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
