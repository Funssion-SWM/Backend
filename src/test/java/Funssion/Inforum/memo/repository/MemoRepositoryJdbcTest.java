package Funssion.Inforum.memo.repository;

import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.exception.MemoNotFoundException;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
class MemoRepositoryJdbcTest {

    @Autowired
    private MemoRepository repository;

    private MemoSaveDto form1 = new MemoSaveDto("JPA란?", "JPA일까?","{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"안녕하세요!!\", \"type\": \"text\"}]}]}", "yellow");
    private MemoSaveDto form2 = new MemoSaveDto("JDK란?", "JDK일까?","{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"Hello!\", \"type\": \"text\"}]}]}", "green");
    private MemoSaveDto form3 = new MemoSaveDto("JWT란?", "JWT일까?","{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\"}]}", "blue");
    private Memo memo1 = new Memo(form1);
    private Memo memo2 = new Memo(form2);
    private Memo memo3 = new Memo(form3);

    private Memo createdMemo;

    @BeforeEach
    void before() {
        createdMemo = repository.create(memo1);
    }


    @Test
    void createTest() {
        Memo savedMemo = repository.findById(createdMemo.getId());

        assertThat(createdMemo).isEqualTo(savedMemo);
    }

    @Test
    void updateContentTest() {
        Memo updatedMemo = repository.updateContentInMemo(form2, createdMemo.getId());

        Memo savedMemo = repository.findById(createdMemo.getId());

        assertThat(createdMemo).isNotEqualTo(savedMemo);
        assertThat(updatedMemo).isEqualTo(savedMemo);

        assertThatThrownBy(() -> repository.updateContentInMemo(form3, 0L))
                .isInstanceOf(MemoNotFoundException.class);
    }

    @Test
    void updateLikesTest() {
        Memo likesUpdatedMemo = repository.updateLikesInMemo(createdMemo.updateLikes(Sign.PLUS), createdMemo.getId());

        assertThat(likesUpdatedMemo.getLikes()).isEqualTo(createdMemo.getLikes());
    }

    @Test
    void updateAuthorProfileTest() {
        repository.updateAuthorProfile(createdMemo.getAuthorId(), "TEST URL");

        Memo updatedMemo = repository.findById(createdMemo.getId());

        assertThat(updatedMemo.getAuthorImagePath()).isEqualTo("TEST URL");

//         null input test
        repository.updateAuthorProfile(createdMemo.getAuthorId(), null);
    }

    @Test
    void deleteTest() {
        repository.delete(createdMemo.getId());

        assertThatThrownBy(() -> repository.delete(createdMemo.getId()))
                .isInstanceOf(MemoNotFoundException.class);
        assertThatThrownBy(() -> repository.findById(createdMemo.getId()))
                .isInstanceOf(MemoNotFoundException.class);
    }

    @Test
    void readTest() {
        Memo createdMemo2 = repository.create(memo2);
        Memo createdMemo3 = repository.create(memo3);

        assertThat(repository.findById(createdMemo2.getId())).isEqualTo(createdMemo2);

        assertThat(repository.findAllByUserIdOrderById(memo1.getAuthorId()).get(0)).isEqualTo(createdMemo3);

        assertThat(repository.findAllOrderById().get(0)).isEqualTo(createdMemo3);
    }
}