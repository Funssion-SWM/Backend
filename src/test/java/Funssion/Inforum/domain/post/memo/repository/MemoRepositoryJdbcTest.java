package Funssion.Inforum.domain.post.memo.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.constant.memo.MemoOrderType;
import Funssion.Inforum.domain.post.like.domain.Like;
import Funssion.Inforum.domain.post.like.repository.LikeRepository;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.exception.MemoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Funssion.Inforum.common.constant.memo.MemoOrderType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
@Transactional
class MemoRepositoryJdbcTest {

    @Autowired
    MemoRepository repository;
    @Autowired
    LikeRepository likeRepository;
    String[] testTagsStringList = {
        "Backend","Java","Spring"
    };
    List<String> testTags = new ArrayList<>(Arrays.asList(testTagsStringList));
    MemoSaveDto form1 = new MemoSaveDto("JPA란?", "JPA일까?","{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"안녕하세요!!\", \"type\": \"text\"}]}]}", "yellow",testTags,false);
    MemoSaveDto form2 = new MemoSaveDto("JDK란?", "JDK일까?","{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"Hello!\", \"type\": \"text\"}]}]}", "green", testTags,false);
    MemoSaveDto form3 = new MemoSaveDto("JWT란?", "JWT일까?","{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\"}]}", "blue",testTags, false);
    Memo memo1 = Memo.builder()
            .title(form1.getMemoTitle())
            .text(form1.getMemoText())
            .description(form1.getMemoDescription())
            .color(form1.getMemoColor())
            .authorId(9999L)
            .authorName("Jinu")
            .authorImagePath("http:jinu")
            .memoTags(form1.getMemoTags())
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .isTemporary(false)
            .likes(0L)
            .memoTags(List.of("JPA", "Java"))
            .build();
    Memo memo2 = Memo.builder()
            .title(form2.getMemoTitle())
            .text(form2.getMemoText())
            .description(form2.getMemoDescription())
            .color(form2.getMemoColor())
            .authorId(9999L)
            .authorName("Jinu")
            .authorImagePath("http:jinu")
            .memoTags(form2.getMemoTags())
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .isTemporary(false)
            .likes(1L)
            .memoTags(List.of("JDK", "Java"))
            .build();
    Memo memo3 = Memo.builder()
            .title(form3.getMemoTitle())
            .text(form3.getMemoText())
            .description(form3.getMemoDescription())
            .color(form3.getMemoColor())
            .authorId(10000L)
            .authorName("Jinu2")
            .authorImagePath("http:jinu2")
            .memoTags(form3.getMemoTags())
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .isTemporary(false)
            .likes(9999L)
            .memoTags(List.of("JWT", "JAVA"))
            .build();
    Memo memo4 = Memo.builder()
            .title(form3.getMemoTitle())
            .text(form3.getMemoText())
            .description(form3.getMemoDescription())
            .color(form3.getMemoColor())
            .authorId(10000L)
            .authorName("Jinu2")
            .authorImagePath("http:jinu2")
            .memoTags(form3.getMemoTags())
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .isTemporary(true)
            .likes(9999L)
            .memoTags(List.of("JWT", "Java"))
            .build();

    @Nested
    @DisplayName("메모 생성")
    class CreateMemo {
        Memo createdMemo;
        @Test
        void createTest() {
            createdMemo = repository.create(memo1);
            System.out.println("memo1 = " + memo1);
            Memo savedMemo = repository.findById(createdMemo.getId());
            System.out.println("savedMemo = " + savedMemo);
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
        @DisplayName("메모 텍스트로 검색")
        void findAllBySearchQueryTest() {
            List<String> searchStringList = new ArrayList<>();
            searchStringList.add("%JPA란?%"); // memo1
            searchStringList.add("%JDK란?%"); // memo2

            List<Memo> foundMemoList = repository.findAllBySearchQuery(searchStringList, NEW);

            assertThat(foundMemoList.size()).isEqualTo(2);

            Memo memo1 = foundMemoList.get(1);
            Memo memo2 = foundMemoList.get(0);

            assertThat(memo1).isEqualTo(createdMemo);
            assertThat(memo2).isEqualTo(createdMemo2);
        }

        @Test
        @DisplayName("메모 태그로 검색")
        void findAllByTag() {
            List<Memo> foundByJavaTag = repository.findAllByTag("Java", NEW);

            assertThat(foundByJavaTag).contains(createdMemo, createdMemo2, createdMemo3);

            List<Memo> foundByLowerCaseJavaTag = repository.findAllByTag("java", NEW);
            List<Memo> foundByUpperCaseJavaTag = repository.findAllByTag("JAVA", NEW);

            assertThat(foundByJavaTag).isEqualTo(foundByLowerCaseJavaTag);
            assertThat(foundByJavaTag).isEqualTo(foundByUpperCaseJavaTag);

            List<Memo> foundByJWTTag = repository.findAllByTag("JWT", NEW);

            assertThat(foundByJWTTag).contains(createdMemo3);
        }
    }
}