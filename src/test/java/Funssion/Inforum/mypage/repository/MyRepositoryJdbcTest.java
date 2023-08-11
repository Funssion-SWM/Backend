package Funssion.Inforum.mypage.repository;

import Funssion.Inforum.domain.mypage.domain.History;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Date;
import java.time.LocalDate;
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

    @Test
    void createTest() {
        repository.createHistory(TEST_USERID, QNA);

        List<History> histories = repository.findMonthlyHistoryByUserId(TEST_USERID, LocalDate.now().getYear(), LocalDate.now().getMonthValue());

        assertThat(histories.size()).isEqualTo(1);
        assertThat(histories.get(0).getQnaCnt()).isEqualTo(1);
        assertThat(histories.get(0).getBlogCnt()).isEqualTo(0);
        assertThat(histories.get(0).getDate()).isEqualTo(Date.valueOf(LocalDate.now()));
    }

    @Test
    void updateTest() {
        repository.createHistory(TEST_USERID, QNA);

        repository.updateHistory(TEST_USERID, BLOG, PLUS);
        repository.updateHistory(TEST_USERID, QNA, MINUS);

        History history = repository.findMonthlyHistoryByUserId(TEST_USERID, LocalDate.now().getYear(), LocalDate.now().getMonthValue()).get(0);

        assertThat(history.getBlogCnt()).isEqualTo(1);
        assertThat(history.getQnaCnt()).isEqualTo(0);

        assertThatThrownBy(() -> repository.updateHistory(TEST_USERID, QNA, MINUS))
                .isInstanceOf(HistoryNotFoundException.class);
    }
}