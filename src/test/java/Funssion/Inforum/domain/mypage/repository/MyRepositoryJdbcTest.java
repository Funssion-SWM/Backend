package Funssion.Inforum.domain.mypage.repository;

import Funssion.Inforum.domain.mypage.domain.History;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static Funssion.Inforum.common.constant.PostType.*;
import static Funssion.Inforum.common.constant.Sign.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
class MyRepositoryJdbcTest {

    @Autowired
    private MyRepository repository;

    private static final Long TEST_USERID = 9999L;
    private static final LocalDateTime curDate = LocalDateTime.now();

    @Test
    @DisplayName("히스토리 생성")
    void createHistoryTest() {
        repository.createHistory(TEST_USERID, QUESTION);

        History created = repository.findMonthlyHistoryByUserId(TEST_USERID, LocalDate.now().getYear(), LocalDate.now().getMonthValue()).get(0);

        assertThat(created.getQuestionCnt()).isEqualTo(1);
        assertThat(created.getBlogCnt()).isEqualTo(0);
        assertThat(created.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("히스토리 수정")
    void updateHistoryTest() {
        repository.createHistory(TEST_USERID, QUESTION);

        repository.updateHistory(TEST_USERID, BLOG, PLUS, curDate.toLocalDate());
        repository.updateHistory(TEST_USERID, QUESTION, MINUS, curDate.toLocalDate());

        History updated = repository.findMonthlyHistoryByUserId(TEST_USERID, LocalDate.now().getYear(), LocalDate.now().getMonthValue()).get(0);

        assertThat(updated.getBlogCnt()).isEqualTo(1);
        assertThat(updated.getQuestionCnt()).isEqualTo(0);

        assertThatThrownBy(() -> repository.updateHistory(TEST_USERID, QUESTION, MINUS, curDate.toLocalDate()))
                .isInstanceOf(HistoryNotFoundException.class);
    }
}