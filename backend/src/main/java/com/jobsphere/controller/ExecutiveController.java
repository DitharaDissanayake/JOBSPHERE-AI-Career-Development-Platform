package com.jobsphere.controller;

import com.jobsphere.entity.Executive;
import com.jobsphere.entity.MentorInteraction;
import com.jobsphere.entity.UserProfile;
import com.jobsphere.repository.ExecutiveRepository;
import com.jobsphere.repository.MentorInteractionRepository;
import com.jobsphere.repository.UserProfileRepository;
import com.jobsphere.repository.UserRepository;
import com.jobsphere.service.TokenService;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/executives")
public class ExecutiveController extends BaseController {
    private final ExecutiveRepository executives;
    private final UserProfileRepository profiles;
    private final MentorInteractionRepository interactions;

    public ExecutiveController(TokenService t, UserRepository u, ExecutiveRepository e,
                               UserProfileRepository p, MentorInteractionRepository i) {
        super(t,u); executives=e; profiles=p; interactions=i;
    }

    @GetMapping("/recommended")
    public Map<String,Object> recommended(@RequestHeader("Authorization") String h){
        Long uid=currentId(h);
        UserProfile p=profiles.findByUserId(uid).orElse(null);
        String text=profileText(p);
        List<Executive> all=executives.findByActiveTrueOrderByCompanyAscNameAsc();
        List<Map<String,Object>> out=new ArrayList<>();
        all.sort((a,b)->Integer.compare(match(b,text),match(a,text)));
        for(Executive e:all.stream().limit(6).toList()){
            Map<String,Object> m=new LinkedHashMap<>();
            m.put("id",e.id); m.put("name",e.name); m.put("company",e.company); m.put("position",e.position);
            m.put("phoneNumber",e.phoneNumber); m.put("whatsappNumber",e.whatsappNumber); m.put("imageUrl",e.imageUrl);
            m.put("category",e.category); m.put("expertise",e.expertise); m.put("bio",e.bio);
            m.put("matchScore",Math.min(99,Math.max(60,60+match(e,text)*10)));
            m.put("whatsappUrl",whatsappUrl(e, null));
            out.add(m);
        }
        return Map.of("success",true,"profileReady",p!=null,"executives",out);
    }

    @PostMapping("/{id}/whatsapp")
    public Map<String,Object> whatsapp(@RequestHeader("Authorization") String h,@PathVariable Long id,
                                       @RequestBody(required=false) MessageRequest req){
        Long uid=currentId(h);
        Executive e=executives.findById(id).filter(x->Boolean.TRUE.equals(x.active))
                .orElseThrow(()->new RuntimeException("Career executive not found."));
        MentorInteraction x=new MentorInteraction();
        x.userId=uid; x.executiveId=id; x.action="WHATSAPP_CLICK"; x.createdAt=LocalDateTime.now();
        interactions.save(x);
        String message=req==null?null:req.message();
        return Map.of("success",true,"url",whatsappUrl(e,message),"interactionId",x.id);
    }

    public record MessageRequest(String message){}

    private String whatsappUrl(Executive e,String message){
        String digits=e.whatsappNumber==null?"":e.whatsappNumber.replaceAll("\\D","");
        String defaultMsg="Hello "+e.name+", I am a JOBSPHERE learner interested in your experience at "+e.company+". I would like to ask a few career questions. Is this a convenient time to chat?";
        String msg=(message==null||message.isBlank())?defaultMsg:message;
        return "https://wa.me/"+digits+"?text="+URLEncoder.encode(msg,StandardCharsets.UTF_8);
    }
    private String profileText(UserProfile p){
        if(p==null)return "";
        return String.join(" ",safe(p.skills),safe(p.careerInterest),safe(p.careerGoal),safe(p.degree),
                safe(p.education),safe(p.experience),safe(p.projects)).toLowerCase(Locale.ROOT);
    }
    private int match(Executive e,String text){
        int n=0;
        for(String t:safe(e.profileTags).toLowerCase(Locale.ROOT).split("[, ]+"))
            if(t.length()>2&&text.contains(t))n++;
        if(e.category!=null&&text.contains(e.category.toLowerCase(Locale.ROOT)))n++;
        return n;
    }
    private String safe(String s){return s==null?"":s;}
}
