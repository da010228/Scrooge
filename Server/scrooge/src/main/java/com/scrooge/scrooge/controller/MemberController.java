package com.scrooge.scrooge.controller;

import com.scrooge.scrooge.dto.LoginRequestDto;
import com.scrooge.scrooge.dto.member.MemberDto;
import com.scrooge.scrooge.dto.SignUpRequestDto;
import com.scrooge.scrooge.config.jwt.JwtTokenProvider;
import com.scrooge.scrooge.repository.member.MemberRepository;
import com.scrooge.scrooge.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Member", description = "Member API")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Operation(summary = "회원가입 API", description = "회원가입 POST")
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        memberService.signUp(signUpRequestDto);
        return ResponseEntity.ok("회원가입 완료");
    }

    @Operation(summary = "로그인 API", description = "로그인 POST")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto) {
        String token = memberService.login(loginRequestDto);
        return ResponseEntity.ok(token);
    }
    
    // 유저 토큰을 받아서 해당 유저 정보를 반환
    @Operation(summary = "멤버정보 API", description = "멤버정보 GET")
    @GetMapping("/info")
    public ResponseEntity<MemberDto> getUserInfo(@RequestHeader("Authorization") String tokenHeader) {
       String token = extractToken(tokenHeader);

       if (!jwtTokenProvider.validateToken(token)) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
       }

       String email = jwtTokenProvider.extractEmail(token);
       Optional<MemberDto> memberDto = memberService.getInfo(email);
       return memberDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer")) {
            return header.substring(7);
        }
        return null;
    }
    
    // 주간 목표 설정
    @Operation(summary = "주간 목표를 설정하는 API")
    @PutMapping("/weekly-goal")
    public ResponseEntity<?> updateWeeklyGoal(@RequestBody MemberDto memberDto) {
        MemberDto memberDto1 = memberService.updateWeeklyGoal(memberDto);
        return ResponseEntity.ok(memberDto1);
    }
}
