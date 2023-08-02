package com.scrooge.scrooge.controller.challenge;

import com.scrooge.scrooge.config.jwt.JwtTokenProvider;
import com.scrooge.scrooge.dto.challengeDto.ChallengeReqDto;
import com.scrooge.scrooge.dto.challengeDto.ChallengeRespDto;
import com.scrooge.scrooge.service.challenge.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name="Challenge", description = "챌린지 API")
@RestController
@RequestMapping("/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    private final JwtTokenProvider jwtTokenProvider;

    // 챌린지를 생성하는 API
    @Operation(summary = "챌린지 생성")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ChallengeReqDto> createChallenge(ChallengeReqDto challengeReqDto, @RequestParam List<MultipartFile> images) {
        challengeService.createChallenge(challengeReqDto, images);

        return ResponseEntity.ok(challengeReqDto);
    }

    // 전체 챌린지를 조회하는 API
    @Operation(summary = "전체 챌린지를 조회하는 API")
    @GetMapping
    public ResponseEntity<List<ChallengeRespDto>> getAllChallenges() {
        List<ChallengeRespDto> challengeDtos = challengeService.getAllChallenges();
        return ResponseEntity.ok(challengeDtos);
    }

    // 카테고리 별 챌린지를 조회하는 API
    @Operation(summary = "카테고리 별 챌린지를 조회하는 API", description = "categoryId => 1: 식비, 2: 교통비, 3: 쇼핑, 4: 기타")
    @GetMapping("category/{categoryId}")
    public ResponseEntity<List<ChallengeRespDto>> getChallengesbyCategory(@PathVariable("categoryId") Integer categoryId) {
        List<ChallengeRespDto> challengeDtos = challengeService.getChallengesbyCategory(categoryId);
        return ResponseEntity.ok(challengeDtos);
    }

    // 마이 챌린지를 조회하는 API
    @Operation(summary = "마이 챌린지를 조회하는 API", description = "statusId => 1: 시작 전, 2: 진행 중, 3: 종료")
    @GetMapping("my-challenge/{memberId}/{statusId}")
    public ResponseEntity<List<ChallengeRespDto>> getMyChallenges(@PathVariable("memberId")Long memberId, @PathVariable("statusId")Integer statusId) {
        List<ChallengeRespDto> myChallengeDtos = challengeService.getMyChallenges(memberId, statusId);
        return ResponseEntity.ok(myChallengeDtos);
    }

    // 챌린지 상세 조회 API
    @Operation(summary = "챌린지를 상세 조회하는 API")
    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeRespDto> getChallenge(@PathVariable("challengeId") Long challengeId) throws IllegalAccessException {
        ChallengeRespDto challengeDto = challengeService.getChallenge(challengeId);
        return ResponseEntity.ok(challengeDto);
    }

    // 사용자가 챌린지를 참여하는 API
    @Operation(summary = "사용자가 챌린지에 참여하는 API")
    @PostMapping("/{challengeId}/participate")
    public ResponseEntity<String> participateInChallenge(@RequestHeader("Authorization") String tokenHeader, @PathVariable("challengeId") Long challengeId) {
        String token = extractToken(tokenHeader);

        if(!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        challengeService.participateInChallenge(challengeId, jwtTokenProvider.extractMemberId(token));
        return ResponseEntity.ok("챌린지 참여 완료");
    }

    private String extractToken(String tokenHeader) {
        if(tokenHeader != null && tokenHeader.startsWith("Bearer")) {
            return tokenHeader.substring(7);
        }
        return null;
    }


}
