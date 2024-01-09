package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/v1/{id}")
    public String findMemberV1(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).orElse(new Member("John Doe"));
        return member.getUsername();
    }

    @GetMapping("/members/v2/{id}") //도메인 클래스 컨버터 - 실무에서 굳이 추천하지 않음
    public String findMemberV2(@PathVariable("id") Member member){
        return member.getUsername();
    }

    @PostConstruct
    public void init(){
        memberRepository.save(new Member("Jaeuk Oh"));
    }
}
