package Funssion.Inforum.domain.mypage.controller;

import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.mypage.dto.MyRecordNumDto;
import Funssion.Inforum.domain.mypage.dto.MyUserInfoDto;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.service.MyService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage/{userId}")
public class MyController {

    private final MyService myService;

    @GetMapping
    public MyUserInfoDto getUserInfo(@PathVariable @Min(1) Long userId) {
        return myService.getUserInfo(userId);
    }

    @GetMapping("/memos")
    public List<MemoListDto> getMyMemos(@PathVariable @Min(1) Long userId) {
        return myService.getMyMemos(userId);
    }

    @GetMapping("/memos/liked")
    public List<MemoListDto> getMyLikedMemos(@PathVariable @Min(1) Long userId) {
        return myService.getMyLikedMemos(userId);
    }

    @GetMapping("/memos/drafts")
    public List<MemoListDto> getMyDraftMemos(@PathVariable @Min(1) Long userId) {return myService.getMyDraftMemos(userId);}

    @GetMapping("/history")
    public List<MyRecordNumDto> getHistory(
            @PathVariable @Min(1) Long userId,
            @RequestParam @Min(1) Integer year,
            @RequestParam @Range(min = 1, max = 12) Integer month) {
        return myService.getHistory(userId, year, month);
    }

}
