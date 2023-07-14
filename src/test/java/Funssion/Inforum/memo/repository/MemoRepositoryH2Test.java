package Funssion.Inforum.memo.repository;

import Funssion.Inforum.memo.entity.Memo;
import Funssion.Inforum.memo.form.MemoSaveForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import javax.sql.DataSource;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemoRepositoryH2Test {

    @Autowired
    private MemoRepository repository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionStatus transactionStatus;

    private MemoSaveForm form1 = new MemoSaveForm("JPA란?", "JPA다", "yellow");
    private MemoSaveForm form2 = new MemoSaveForm("JDK란?", "JDK다", "green");
    private MemoSaveForm form3 = new MemoSaveForm("JWT란?", "JWT다", "blue");

    @BeforeEach
    void beforeEach() {
        transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    @AfterEach
    void afterEach() {
        transactionManager.rollback(transactionStatus);
    }

    @Test
    void createTest() {
        Memo savedMemo = repository.create( 1, "정진우", form1);
        log.info("savedMemo={}",savedMemo);
        assertThat(savedMemo.getMemoText()).isEqualTo(form1.getMemoText());
    }

    @Test
    void updateTest() {
        Memo savedMemo = repository.create( 1, "정진우", form1);
        Memo updatedMemo = repository.update(savedMemo.getMemoId(), form2);
        log.info("saved={}",savedMemo);
        log.info("updated={}",updatedMemo);
        assertThat(savedMemo.getMemoId()).isEqualTo(updatedMemo.getMemoId());
        assertThat(savedMemo.getUserId()).isEqualTo(updatedMemo.getUserId());
        assertThat(savedMemo.getMemoText()).isNotEqualTo(updatedMemo.getMemoText());
    }

    @Test
    void deleteTest() {
        Memo savedMemo = repository.create( 1, "정진우", form1);
        repository.delete(savedMemo.getMemoId());
        assertThatThrownBy(() -> repository.findById(savedMemo.getMemoId()))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void readTest() {
        ArrayList<Memo> savedMemos = new ArrayList<>();
        savedMemos.add(repository.create( 1, "정진우", form1));
        savedMemos.add(repository.create( 1, "정진우", form2));
        savedMemos.add(repository.create( 1, "정진우", form3));
        log.info("savedMemos={}",savedMemos);

        assertThat(repository.findAllByUserId(1)).isEqualTo(savedMemos);

        savedMemos.add(repository.create( 2, "김태훈", form3));

        assertThat(repository.findAllByUserId(1)).isNotEqualTo(savedMemos);
        assertThat(repository.findAllByPeriod(1)).isEqualTo(savedMemos);
    }
}