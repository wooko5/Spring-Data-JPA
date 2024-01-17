package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.datajpa.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/v1/{id}")
    public String findMemberV1(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).orElse(new Member("John Doe"));
        return member.getUsername();
    }

    @GetMapping("/members/v2/{id}") //도메인 클래스 컨버터 - 실무에서 굳이 추천하지 않음
    public String findMemberV2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

//    @GetMapping("/members")
//    public Page<Member> listV1(@PageableDefault(size = 5, sort = {"id", "username"}) Pageable pageable) { //파라미터에 Pageable를 넣는 순간 스프링부트가 알아서 data binding 처리
//        return memberRepository.findAll(pageable);
//    }

//    @GetMapping("/members") //DTO로 변환함
//    public Page<MemberDto> listV2(@PageableDefault(size = 5, sort = {"id", "username"}) Pageable pageable) {
//        Page<Member> page = memberRepository.findAll(pageable);
//        return page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
//    }

    @GetMapping("/members") //DTO로 변환 시, 즉시 converter로 쓸 수 있게 MemberDto 생성자 수정
    public Page<MemberDto> listV3(@PageableDefault(size = 5, sort = {"id", "username"}) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page.map(member -> new MemberDto(member));
    }

//    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("Dummy Data" + (i + 1), i + 10));
        }
    }
}
