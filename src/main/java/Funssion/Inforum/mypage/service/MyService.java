package Funssion.Inforum.mypage.service;

import Funssion.Inforum.memo.dto.MemoListDto;
import Funssion.Inforum.mypage.dto.MyRecordNumDto;
import Funssion.Inforum.mypage.dto.MyUserInfoDto;
import Funssion.Inforum.mypage.repository.MyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MyService {

    private final MyRepository myRepository;

    public MyUserInfoDto getUserInfo(int userId) {
        return myRepository.findUserInfoByUserId(userId).orElseThrow(() -> new NoSuchElementException("user not found"));
    }

    public List<MyRecordNumDto> getHistory(int userId) {
        return myRepository.findRecordNumByUserId(userId);
    }

    public List<MemoListDto> getMyMemos(int userId) {
        return myRepository.findAllByUserId(userId);
    }
}
