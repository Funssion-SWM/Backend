package Funssion.Inforum.domain.mypage.controller;

import Funssion.Inforum.domain.memo.dto.MemoListDto;
import Funssion.Inforum.domain.mypage.dto.MyRecordNumDto;
import Funssion.Inforum.domain.mypage.dto.MyUserInfoDto;
import Funssion.Inforum.domain.mypage.service.MyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage/{userId}")
public class MyController {

    private final MyService myService;

    @GetMapping
    public MyUserInfoDto getUserInfo(@PathVariable int userId) {
        return myService.getUserInfo(userId);
    }

    @GetMapping("/memos")
    public List<MemoListDto> getMyMemos(@PathVariable int userId) {
        return myService.getMyMemos(userId);
    }

    @GetMapping("/history")
    public List<MyRecordNumDto> getHistory(@PathVariable int userId) {
        return myService.getHistory(userId);
    }
}