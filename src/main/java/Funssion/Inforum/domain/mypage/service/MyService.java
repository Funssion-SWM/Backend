package Funssion.Inforum.domain.mypage.service;

import Funssion.Inforum.domain.memo.dto.MemoListDto;
import Funssion.Inforum.domain.mypage.dto.MyRecordNumDto;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.mypage.dto.MyUserInfoDto;
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
        List<MyRecordNumDto> records = myRepository.findRecordNumByUserId(userId);
        if(records.isEmpty()) throw new NoSuchElementException("user not found");
        return records;
    }

    public List<MemoListDto> getMyMemos(int userId) {
        List<MemoListDto> memos = myRepository.findAllByUserId(userId);
        if(memos.isEmpty()) throw new NoSuchElementException("user not found");
        return memos;
    }
}
