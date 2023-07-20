package Funssion.Inforum.memo.repository;

import Funssion.Inforum.memo.dto.MemoDto;
import Funssion.Inforum.memo.dto.MemoSaveDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemoRepositoryJdbcTest {

    @Autowired
    private MemoRepository repository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionStatus transactionStatus;

    private MemoSaveDto form1 = new MemoSaveDto("JPA란?", "JPA인가?","JPA다", "yellow");
    private MemoSaveDto form2 = new MemoSaveDto("JDK란?", "JDK인가?","JDK다", "green");
    private MemoSaveDto form3 = new MemoSaveDto("JWT란?", "JWT인가?", "JWT다", "blue");

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
        MemoDto savedMemo = repository.create( 1, "정진우", form1);
        log.info("savedMemo={}",savedMemo);
        assertThat(savedMemo.getMemoText()).isEqualTo(form1.getMemoText());
    }

    @Test
    void updateTest() {
        MemoDto savedMemo = repository.create( 1, "정진우", form1);
        MemoDto updatedMemo = repository.update(savedMemo.getMemoId(), form2);
        log.info("saved={}",savedMemo);
        log.info("updated={}",updatedMemo);
        assertThat(savedMemo.getMemoId()).isEqualTo(updatedMemo.getMemoId());
        assertThat(savedMemo.getUserId()).isEqualTo(updatedMemo.getUserId());
        assertThat(savedMemo.getMemoText()).isNotEqualTo(updatedMemo.getMemoText());
    }

    @Test
    void deleteTest() {
        MemoDto savedMemo = repository.create( 1, "정진우", form1);
        repository.delete(savedMemo.getMemoId());
        assertThatThrownBy(() -> repository.findById(savedMemo.getMemoId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void readTest() {
        ArrayList<MemoDto> savedMemos = new ArrayList<>();
        savedMemos.add(repository.create(1, "정진우", form1));
        savedMemos.add(repository.create( 1, "김태훈", form2));
        savedMemos.add(repository.create( 3, "고동우", form3));
        log.info("savedMemos={}",savedMemos);

        assertThat(repository.findAllWithNewest().get(0).getMemoText()).isEqualTo(form1.getMemoText());
    }
}