
        package com.jobsphere.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "course_questions")
public class CourseQuestion {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(name = "course_id", nullable = false)
 private Long courseId;

 @Column(name = "question", nullable = false, columnDefinition = "TEXT")
 private String question;

 @Column(name = "option_a", nullable = false, columnDefinition = "TEXT")
 private String optionA;

 @Column(name = "option_b", nullable = false, columnDefinition = "TEXT")
 private String optionB;

 @Column(name = "option_c", nullable = false, columnDefinition = "TEXT")
 private String optionC;

 @Column(name = "option_d", nullable = false, columnDefinition = "TEXT")
 private String optionD;

 @Column(name = "correct_option", nullable = false, length = 1)
 private String correctOption;

 @Column(name = "explanation", columnDefinition = "TEXT")
 private String explanation;

 @Column(name = "active", nullable = false)
 private Boolean active = true;

 public CourseQuestion() {
 }

 public Long getId() {
  return id;
 }

 public void setId(Long id) {
  this.id = id;
 }

 public Long getCourseId() {
  return courseId;
 }

 public void setCourseId(Long courseId) {
  this.courseId = courseId;
 }

 public String getQuestion() {
  return question;
 }

 public void setQuestion(String question) {
  this.question = question;
 }

 public String getOptionA() {
  return optionA;
 }

 public void setOptionA(String optionA) {
  this.optionA = optionA;
 }

 public String getOptionB() {
  return optionB;
 }

 public void setOptionB(String optionB) {
  this.optionB = optionB;
 }

 public String getOptionC() {
  return optionC;
 }

 public void setOptionC(String optionC) {
  this.optionC = optionC;
 }

 public String getOptionD() {
  return optionD;
 }

 public void setOptionD(String optionD) {
  this.optionD = optionD;
 }

 public String getCorrectOption() {
  return correctOption;
 }

 public void setCorrectOption(String correctOption) {
  this.correctOption = correctOption;
 }

 public String getExplanation() {
  return explanation;
 }

 public void setExplanation(String explanation) {
  this.explanation = explanation;
 }

 public Boolean getActive() {
  return active;
 }

 public void setActive(Boolean active) {
  this.active = active;
 }
}

