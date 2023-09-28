package Funssion.Inforum.domain.mypage.repository;

import Funssion.Inforum.domain.mypage.domain.History;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static Funssion.Inforum.common.constant.PostType.*;
import static Funssion.Inforum.common.constant.Sign.MINUS;
import static Funssion.Inforum.common.constant.Sign.PLUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        assertThatThrownBy(() -> repository.updateHistory(TEST_USERID + 1, MEMO, PLUS, curDate.toLocalDate()))
                .isInstanceOf(HistoryNotFoundException.class);

        assertThatThrownBy(() -> repository.updateHistory(TEST_USERID, QUESTION, MINUS, curDate.toLocalDate()))
                .isInstanceOf(IllegalStateException.class);
    }
}