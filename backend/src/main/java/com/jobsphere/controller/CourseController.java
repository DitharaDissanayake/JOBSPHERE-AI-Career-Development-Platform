package com.jobsphere.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobsphere.entity.*;
import com.jobsphere.repository.*;
import com.jobsphere.service.TokenService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController extends BaseController {
    private final CourseRepository courses;
    private final CourseQuestionRepository questions;
    private final CourseAttemptRepository attempts;
    private final UserProfileRepository profiles;
    private final CoursePaymentRepository payments;
    private final CourseProgressRepository courseProgress;
    private final CourseCertificateRepository certificates;
    private final ObjectMapper mapper=new ObjectMapper();

    public CourseController(TokenService t, UserRepository u, CourseRepository c, CourseQuestionRepository q,
                            CourseAttemptRepository a, UserProfileRepository p, CoursePaymentRepository payments,
                            CourseProgressRepository courseProgress, CourseCertificateRepository certificates){
        super(t,u); courses=c; questions=q; attempts=a; profiles=p; this.payments=payments;
        this.courseProgress=courseProgress; this.certificates=certificates;
    }

    @GetMapping("/recommended")
    public Map<String,Object> recommended(@RequestHeader("Authorization") String h){
        Long uid=currentId(h);
        UserProfile p=profiles.findByUserId(uid).orElse(null);
        String text=p==null?"":String.join(" ",safe(p.skills),safe(p.careerInterest),safe(p.careerGoal),
                safe(p.degree),safe(p.education),safe(p.experience),safe(p.projects)).toLowerCase();

        List<Course> all=courses.findByActiveTrueOrderByNameAsc();
        List<Course> basic=all.stream().filter(c->!"ADVANCED".equalsIgnoreCase(c.courseLevel)).sorted((a,b)->Integer.compare(score(b.tags,text),score(a.tags,text))).limit(6).toList();
        List<Course> advanced=all.stream().filter(c->"ADVANCED".equalsIgnoreCase(c.courseLevel)).sorted((a,b)->Integer.compare(score(b.tags,text),score(a.tags,text))).limit(6).toList();

        List<Map<String,Object>> basicOut=new ArrayList<>();
        for(Course c:basic) basicOut.add(summaryWithProgress(c,uid,text));
        List<Map<String,Object>> advOut=new ArrayList<>();
        for(Course c:advanced){
            Map<String,Object> m=summaryWithProgress(c,uid,text);
            m.put("price",c.price);
            m.put("durationDays",c.durationDays==null?7:c.durationDays);
            m.put("paid",payments.findTopByUserIdAndCourseIdAndStatusOrderByPaidAtDesc(uid,c.id,"PAID").isPresent());
            advOut.add(m);
        }
        return Map.of("success",true,"profileReady",p!=null,"courses",basicOut,"advancedCourses",advOut);
    }

    @GetMapping("/{id}")
    public Map<String,Object> get(@RequestHeader("Authorization") String h,@PathVariable Long id){
        Long uid=currentId(h);
        Course c=courses.findById(id).filter(x->Boolean.TRUE.equals(x.active))
                .orElseThrow(()->new RuntimeException("Course not found."));
        boolean advanced="ADVANCED".equalsIgnoreCase(c.courseLevel);
        boolean paid=!advanced || payments.findTopByUserIdAndCourseIdAndStatusOrderByPaidAtDesc(uid,id,"PAID").isPresent();

        Map<String,Object> m=courseSummary(c);
        m.put("introduction",c.introduction); m.put("eligibility",c.eligibility); m.put("outcomes",c.outcomes);
        m.put("industryApplication",c.industryApplication); m.put("courseLevel",advanced?"ADVANCED":"BASIC");
        m.put("durationDays",c.durationDays==null?0:c.durationDays); m.put("price",c.price==null?java.math.BigDecimal.ZERO:c.price);
        m.put("paymentRequired",advanced&&!paid); m.put("paid",paid);
        if(advanced&&paid){
            CourseProgress prog=courseProgress.findByUserIdAndCourseId(uid,id).orElse(null);
            m.put("completedDays", prog==null ? List.of() : parseDays(prog.completedDays));
        }
        if(advanced&&!paid){ m.put("modules",List.of()); m.put("questions",List.of()); return m; }

        try{m.put("modules",mapper.readValue(c.modulesJson,List.class));}catch(Exception e){m.put("modules",List.of());}
        List<Map<String,Object>> qs=new ArrayList<>();
        for(CourseQuestion q:questions.findByCourseIdAndActiveTrueOrderByIdAsc(id)){
            Map<String,Object> x=new LinkedHashMap<>();
            x.put("id",q.getId()); x.put("question",q.getQuestion()); x.put("optionA",q.getOptionA());
            x.put("optionB",q.getOptionB()); x.put("optionC",q.getOptionC()); x.put("optionD",q.getOptionD()); qs.add(x);
        }
        m.put("questions",qs);
        return m;
    }

    @PostMapping("/{id}/complete")
    public Map<String,Object> complete(@RequestHeader("Authorization") String h,@PathVariable Long id,@RequestBody Map<String,String> answers){
        Long uid=currentId(h);
        Course c=courses.findById(id).orElseThrow(()->new RuntimeException("Course not found."));
        if("ADVANCED".equalsIgnoreCase(c.courseLevel) &&
                payments.findTopByUserIdAndCourseIdAndStatusOrderByPaidAtDesc(uid,id,"PAID").isEmpty())
            throw new RuntimeException("Please complete the LKR 250 payment before starting this advanced course.");
        List<CourseQuestion> qs=questions.findByCourseIdAndActiveTrueOrderByIdAsc(id);
        if(qs.size()!=10) throw new RuntimeException("This course is not ready: it must contain 10 active questions.");
        int correct=0; Map<String,String> clean=new LinkedHashMap<>();
        for(CourseQuestion q:qs){
            String qid=String.valueOf(q.getId()); String ans=answers.get(qid);
            if(ans!=null)clean.put(qid,ans.toUpperCase());
            if(q.getCorrectOption()!=null&&q.getCorrectOption().equalsIgnoreCase(ans))correct++;
        }
        int score=correct*100/qs.size(); boolean passed=score>=70;
        CourseAttempt a=new CourseAttempt(); a.courseId=id; a.userId=uid; a.score=score; a.correctAnswers=correct;
        a.totalQuestions=qs.size(); a.passed=passed; a.completedAt=LocalDateTime.now();
        try{a.answersJson=mapper.writeValueAsString(clean);}catch(Exception e){a.answersJson="{}";}
        attempts.save(a);
        return Map.of("success",true,"attemptId",a.id,"score",score,"correctAnswers",correct,
                "totalQuestions",qs.size(),"passed",passed,
                "message",passed?"Congratulations! You passed this course. Your certificate is now eligible for administrator issue."
                        :"You scored below 70%. Please complete the course again, review the lessons and try again.");
    }

    @GetMapping("/{id}/progress")
    public Map<String,Object> getProgress(@RequestHeader("Authorization") String h,@PathVariable Long id){
        Long uid=currentId(h); Course c=courses.findById(id).orElseThrow(()->new RuntimeException("Course not found."));
        if(!"ADVANCED".equalsIgnoreCase(c.courseLevel)) return Map.of("courseId",id,"completedDays",List.of(),"totalDays",0);
        if(payments.findTopByUserIdAndCourseIdAndStatusOrderByPaidAtDesc(uid,id,"PAID").isEmpty())
            throw new RuntimeException("Payment is required before tracking advanced course progress.");
        CourseProgress p=courseProgress.findByUserIdAndCourseId(uid,id).orElse(null);
        return Map.of("courseId",id,"completedDays",p==null?List.of():parseDays(p.completedDays),"totalDays",7);
    }

    @PostMapping("/{id}/progress")
    public Map<String,Object> updateProgress(@RequestHeader("Authorization") String h,@PathVariable Long id,@RequestBody Map<String,Integer> body){
        Long uid=currentId(h); Course c=courses.findById(id).orElseThrow(()->new RuntimeException("Course not found."));
        if(!"ADVANCED".equalsIgnoreCase(c.courseLevel)) throw new RuntimeException("Daily progress is only available for advanced courses.");
        if(payments.findTopByUserIdAndCourseIdAndStatusOrderByPaidAtDesc(uid,id,"PAID").isEmpty())
            throw new RuntimeException("Payment is required before tracking advanced course progress.");
        Integer day=body.get("day");
        if(day==null||day<1||day>7) throw new RuntimeException("Select a valid course day from 1 to 7.");
        CourseProgress p=courseProgress.findByUserIdAndCourseId(uid,id).orElseGet(CourseProgress::new);
        p.userId=uid;p.courseId=id;p.updatedAt=LocalDateTime.now();
        Set<Integer> days=new TreeSet<>(parseDays(p.completedDays));days.add(day);
        try{p.completedDays=mapper.writeValueAsString(days);}catch(Exception e){p.completedDays="["+day+"]";}
        courseProgress.save(p);
        return Map.of("success",true,"completedDays",days,"totalDays",7,"allDaysCompleted",days.size()>=7);
    }

    @GetMapping("/my-progress")
    public List<Map<String,Object>> progress(@RequestHeader("Authorization") String h){
        Long uid=currentId(h); List<Map<String,Object>> out=new ArrayList<>();
        for(CourseAttempt a:attempts.findByUserIdOrderByCompletedAtDesc(uid)){
            Map<String,Object> m=new LinkedHashMap<>(); m.put("attemptId",a.id); m.put("courseId",a.courseId);
            courses.findById(a.courseId).ifPresent(c->{m.put("courseName",c.name);m.put("courseLevel",c.courseLevel);});
            m.put("score",a.score);m.put("passed",a.passed);m.put("completedAt",a.completedAt);out.add(m);
        }
        return out;
    }

    @GetMapping("/my-certificates")
    public List<Map<String,Object>> myCertificates(@RequestHeader("Authorization") String h){
        Long uid=currentId(h);
        List<Map<String,Object>> out=new ArrayList<>();

        for(CourseCertificate cert:certificates.findByUserIdOrderByIssuedAtDesc(uid)){
            Map<String,Object> row=new LinkedHashMap<>();
            row.put("id",cert.id);
            row.put("certificateNumber",cert.certificateNumber);
            row.put("fileName",cert.fileName);
            row.put("issuedAt",cert.issuedAt);
            row.put("sentAt",cert.sentAt);

            courses.findById(cert.courseId).ifPresent(c->{
                row.put("courseId",c.id);
                row.put("courseName",c.name);
                row.put("category",c.category);
            });

            attempts.findById(cert.attemptId).ifPresent(a->{
                row.put("score",a.score);
                row.put("completedAt",a.completedAt);
            });

            out.add(row);
        }

        return out;
    }

    @GetMapping("/my-certificates/{id}")
    public ResponseEntity<byte[]> downloadMyCertificate(
            @RequestHeader("Authorization") String h,
            @PathVariable Long id){

        Long uid=currentId(h);

        CourseCertificate certificate=certificates.findById(id)
                .orElseThrow(()->new RuntimeException("Certificate not found."));

        if(!uid.equals(certificate.userId)){
            throw new RuntimeException("You can only access your own certificate.");
        }

        String fileName=certificate.fileName;
        if(fileName==null || fileName.isBlank()){
            fileName="JOBSPHERE-Certificate-"+certificate.id+".pdf";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + fileName.replace("\"", "") + "\"")
                .body(certificate.fileData);
    }

    private Map<String,Object> summaryWithProgress(Course c,Long uid,String text){
        Map<String,Object> m=courseSummary(c);
        m.put("matchScore",Math.min(99,Math.max(60,score(c.tags,text)*10+60)));
        attempts.findTopByUserIdAndCourseIdOrderByCompletedAtDesc(uid,c.id).ifPresent(x->{m.put("latestScore",x.score);m.put("passed",x.passed);m.put("completedAt",x.completedAt);});
        return m;
    }
    private Map<String,Object> courseSummary(Course c){
        Map<String,Object> m=new LinkedHashMap<>();
        m.put("id",c.id);m.put("name",c.name);m.put("category",c.category);m.put("durationMinutes",c.durationMinutes);
        m.put("durationDays",c.durationDays==null?0:c.durationDays);m.put("courseLevel",c.courseLevel==null?"BASIC":c.courseLevel);
        m.put("price",c.price==null?java.math.BigDecimal.ZERO:c.price);m.put("shortDescription",c.shortDescription);return m;
    }
    private int score(String tags,String text){int n=0;for(String t:safe(tags).toLowerCase().split("[, ]+"))if(t.length()>2&&text.contains(t))n++;return n;}
    private List<Integer> parseDays(String json){
        if(json==null||json.isBlank()) return List.of();
        try{
            Integer[] a=mapper.readValue(json,Integer[].class);
            return Arrays.asList(a);
        }catch(Exception e){return List.of();}
    }
    private String safe(String s){return s==null?"":s;}
}
