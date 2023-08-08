package Funssion.Inforum.domain.mypage.controller;

import Funssion.Inforum.domain.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.mypage.dto.MyRecordNumDto;
import Funssion.Inforum.domain.mypage.dto.MyUserInfoDto;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.service.MyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage/{userId}")
public class MyController {

    private final MyService myService;

    @GetMapping
    public MyUserInfoDto getUserInfo(@PathVariable Long userId) {
        return myService.getUserInfo(userId);
    }

    @GetMapping("/memos")
    public List<MemoListDto> getMyMemos(@PathVariable Long userId) {
        return myService.getMyMemos(userId);
    }

    @GetMapping("/history")
    public ResponseEntity<List<MyRecordNumDto>> getHistory(@PathVariable Long userId,@RequestParam Integer year, @RequestParam Integer month) {
        try {
            return new ResponseEntity<>(myService.getHistory(userId, year, month), HttpStatus.OK);
        } catch (HistoryNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
