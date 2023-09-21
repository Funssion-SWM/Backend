package Funssion.Inforum.domain.mypage.service;

import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MyServiceTest {

    @Mock MyRepository myRepository;
    @Mock MemoRepository memoRepository;
    @InjectMocks MyService myService;

    @Test
    void getUserInfo() {
    }

    @Test
    void getHistory() {
    }

    @Test
    void getMyMemos() {
    }

    @Test
    void getMyLikedMemos() {
    }

    @Test
    void getMyDraftMemos() {
    }
}