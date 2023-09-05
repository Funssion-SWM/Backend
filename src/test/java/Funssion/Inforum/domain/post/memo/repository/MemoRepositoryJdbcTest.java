package Funssion.Inforum.domain.post.memo.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.constant.memo.MemoOrderType;
import Funssion.Inforum.domain.like.domain.Like;
import Funssion.Inforum.domain.like.repository.LikeRepository;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.exception.MemoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
class MemoRepositoryJdbcTest {

    @Autowired
    MemoRepository repository;
    @Autowired
    LikeRepository likeRepository;

    MemoSaveDto form1 = new MemoSaveDto("JPA란?", "JPA일까?","{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"안녕하세요!!\", \"type\": \"text\"}]}]}", "yellow", false);
    MemoSaveDto form2 = new MemoSaveDto("JDK란?", "JDK일까?","{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"Hello!\", \"type\": \"text\"}]}]}", "green", false);
    MemoSaveDto form3 = new MemoSaveDto("JWT란?", "JWT일까?","{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\"}]}", "blue", false);
    Memo memo1 = Memo.builder()
            .title(form1.getMemoTitle())
            .text(form1.getMemoText())
            .description(form1.getMemoDescription())
            .color(form1.getMemoColor())
            .authorId(9999L)
            .authorName("Jinu")
            .authorImagePath("http:jinu")
            .createdDate(Date.valueOf(LocalDate.now()))
            .updatedDate(Date.valueOf(LocalDate.now()))
            .isTemporary(false)
            .likes(0)
            .build();
    Memo memo2 = Memo.builder()
            .title(form2.getMemoTitle())
            .text(form2.getMemoText())
            .description(form2.getMemoDescription())
            .color(form2.getMemoColor())
            .authorId(9999L)
            .authorName("Jinu")
            .authorImagePath("http:jinu")
            .createdDate(Date.valueOf(LocalDate.now()))
            .updatedDate(Date.valueOf(LocalDate.now()))
            .isTemporary(false)
            .likes(1)
            .build();
    Memo memo3 = Memo.builder()
            .title(form3.getMemoTitle())
            .text(form3.getMemoText())
            .description(form3.getMemoDescription())
            .color(form3.getMemoColor())
            .authorId(10000L)
            .authorName("Jinu2")
            .authorImagePath("http:jinu2")
            .createdDate(Date.valueOf(LocalDate.now()))
            .updatedDate(Date.valueOf(LocalDate.now()))
            .isTemporary(false)
            .likes(9999)
            .build();
    Memo memo4 = Memo.builder()
            .title(form3.getMemoTitle())
            .text(form3.getMemoText())
            .description(form3.getMemoDescription())
            .color(form3.getMemoColor())
            .authorId(10000L)
            .authorName("Jinu2")
            .authorImagePath("http:jinu2")
            .createdDate(Date.valueOf(LocalDate.now()))
            .updatedDate(Date.valueOf(LocalDate.now()))
            .isTemporary(true)
            .likes(9999)
            .build();

    @Nested
    @DisplayName("메모 생성")
    class CreateMemo {
        Memo createdMemo;
        @Test
        @DisplayName("메모 생성하기")
        void createTest() {
            createdMemo = repository.create(memo1);
            Memo savedMemo = repository.findById(createdMemo.getId());

            assertThat(createdMemo).isEqualTo(savedMemo);
        }
    }


    @Nested
    @DisplayName("메모 수정")
    class UpdateMemo {
        Memo createdMemo;
        Memo createdMemo2;
        Memo createdMemo3;
        @BeforeEach
        void setUp() {
            createdMemo = repository.create(memo1);
            createdMemo2 = repository.create(memo2);
            createdMemo3 = repository.create(memo3);

            likeRepository.create(new Like(9999L, PostType.MEMO, createdMemo2.getId()));
            likeRepository.create(new Like(9999L, PostType.MEMO, createdMemo3.getId()));
        }

        @Test
        @DisplayName("메모 내용 수정")
        void updateContentTest() {
            Memo updatedMemo = repository.updateContentInMemo(form2, createdMemo.getId());

            Memo savedMemo = repository.findById(createdMemo.getId());

            assertThat(createdMemo).isNotEqualTo(savedMemo);
            assertThat(updatedMemo).isEqualTo(savedMemo);

            assertThatThrownBy(() -> repository.updateContentInMemo(form3, 0L))
                    .isInstanceOf(MemoNotFoundException.class);
        }

        @Test
        @DisplayName("메모 좋아요 수정")
        void updateLikesTest() {
            Memo likesUpdatedMemo = repository.updateLikesInMemo(createdMemo.updateLikes(Sign.PLUS), createdMemo.getId());

            assertThat(likesUpdatedMemo.getLikes()).isEqualTo(createdMemo.getLikes());
        }

        @Test
        @DisplayName("메모 작성자 프로필 수정")
        void updateAuthorProfileTest() {
            repository.updateAuthorProfile(createdMemo.getAuthorId(), "TEST URL");

            Memo updatedMemo = repository.findById(createdMemo.getId());

            assertThat(updatedMemo.getAuthorImagePath()).isEqualTo("TEST URL");

//         null input test
            repository.updateAuthorProfile(createdMemo.getAuthorId(), null);
        }

    }

    @Nested
    @DisplayName("메모 삭제")
    class DeleteMemo {
        Memo createdMemo;
        @Test
        @DisplayName("메모 삭제하기")
        void deleteTest() {
            createdMemo = repository.create(memo1);
            repository.delete(createdMemo.getId());

            assertThatThrownBy(() -> repository.delete(createdMemo.getId()))
                    .isInstanceOf(MemoNotFoundException.class);
            assertThatThrownBy(() -> repository.findById(createdMemo.getId()))
                    .isInstanceOf(MemoNotFoundException.class);
        }


    }

    @Nested
    @DisplayName("메모 조회")
    class ReadMemo {
        Memo createdMemo;
        Memo createdMemo2;
        Memo createdMemo3;
        Memo createdMemo4;
        @BeforeEach
        void setUp() {
            createdMemo = repository.create(memo1);
            createdMemo2 = repository.create(memo2);
            createdMemo3 = repository.create(memo3);
            createdMemo4 = repository.create(memo4);

            likeRepository.create(new Like(9999L, PostType.MEMO, createdMemo3.getId()));
            likeRepository.create(new Like(9999L, PostType.MEMO, createdMemo2.getId()));
        }
        @Test
        @DisplayName("좋아요 순 날짜별 메모 불러오기")
        void findAllByDaysOrderByLikesTest() {
            List<Memo> memoListCreatedAtToday = repository.findAllByDaysOrderByLikes(1L);

            assertThat(memoListCreatedAtToday.size()).isEqualTo(3);
            assertThat(memoListCreatedAtToday.get(0)).isEqualTo(createdMemo3);
        }

        @Test
        @DisplayName("최신 순 메모 불러오기")
        void findAllOrderByIdTest() {
            List<Memo> memoList = repository.findAllOrderById();

            assertThat(memoList.get(0)).isEqualTo(createdMemo3);
        }

        @Test
        @DisplayName("최신 순 특정 유저 메모 불러오기")
        void findAllByUserIdOrderByIdTest() {
            List<Memo> memoList = repository.findAllByUserIdOrderById(9999L);

            assertThat(memoList.size()).isEqualTo(2);
            assertThat(memoList.get(0)).isEqualTo(createdMemo2);
        }

        @Test
        @DisplayName("최신 순 좋아요한 메모 불러오기")
        void findAllLikedMemosByUserIdTest() {
            List<Memo> likedMemoList = repository.findAllLikedMemosByUserId(9999L);

            assertThat(likedMemoList.size()).isEqualTo(2);
            assertThat(likedMemoList.get(0)).isEqualTo(createdMemo3);
        }

        @Test
        @DisplayName("최신 순 임시 메모 글 불러오기")
        void findAllDraftMemosByUserIdTest() {
            List<Memo> draftMemoList = repository.findAllDraftMemosByUserId(10000L);

            assertThat(draftMemoList.size()).isEqualTo(1);
            assertThat(draftMemoList.get(0)).isEqualTo(createdMemo4);
        }

        @Test
        @DisplayName("메모 검색")
        void findAllBySearchQueryTest() {
            List<String> searchStringList = new ArrayList<>();
            searchStringList.add("%JPA란?%"); // memo1
            searchStringList.add("%JDK란?%"); // memo2

            List<Memo> foundMemoList = repository.findAllBySearchQuery(searchStringList, MemoOrderType.NEW);

            assertThat(foundMemoList.size()).isEqualTo(2);

            Memo memo1 = foundMemoList.get(1);
            Memo memo2 = foundMemoList.get(0);

            assertThat(memo1).isEqualTo(createdMemo);
            assertThat(memo2).isEqualTo(createdMemo2);
        }

    }
}