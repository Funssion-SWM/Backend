package Funssion.Inforum.domain.post.qna.service;

import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.domain.History;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.exception.QuestionNotFoundException;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class QuestionIntegrationTest {
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    MyRepository myRepository;
    @Autowired
    QuestionService questionService;
    @Autowired
    MemberRepository memberRepository;

    static Long saveMemberId;

    static QuestionSaveDto firstQuestionSaveDto;
    static QuestionSaveDto secondQuestionSaveDto;
    static QuestionSaveDto thirdQuestionSaveDto;

    @BeforeEach
    void init() {
        saveUser();
    }


    private void saveUser() {
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .userName("taehoon")
                .loginType(LoginType.NON_SOCIAL)
                .userPw("a1234567!")
                .userEmail("test@gmail.com")
                .build();
        MemberProfileEntity memberProfileEntity = MemberProfileEntity.builder()
                .nickname("taehoon")
                .profileImageFilePath("taehoon-image")
                .introduce("introduce of taehoon")
                .userTags(List.of("tag1", "tag2"))
                .build();

        SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(NonSocialMember.createNonSocialMember(memberSaveDto));
        saveMemberId = saveMemberResponseDto.getId();
        myRepository.createProfile(saveMemberId, memberProfileEntity);
    }



    @Test
    @DisplayName("질문 생성")
    @Transactional
    void createQuestion(){
        QuestionSaveDto questionSaveDto = QuestionSaveDto.builder().title("테스트 제목 생성")
                .text("질문 내용")
                .tags(List.of("tag1", "tag2"))
                .build();

        Question question = questionService.createQuestion(questionSaveDto, saveMemberId);
        assertThat(question.getTitle()).isEqualTo("테스트 제목 생성");

        LocalDateTime appliedDateTime = question.getCreatedDate();
        List<History> monthlyHistoryByUserId = myRepository.findMonthlyHistoryByUserId(saveMemberId, appliedDateTime.getYear(), appliedDateTime.getMonthValue());
        assertThat(monthlyHistoryByUserId).hasSize(1);

        History history = monthlyHistoryByUserId.get(0);
        assertThat(history.getUserId()).isEqualTo(saveMemberId);
        assertThat(history.getQuestionCnt()).isEqualTo(1L);
        assertThat(history.getMemoCnt()).isEqualTo(0L);

    }
    @Nested
    @DisplayName("파라미터에 알맞는 정렬된 메모 리스트 가져오기")
    class getOrderedQuestions{
        private void saveQuestions() {
            firstQuestionSaveDto = QuestionSaveDto.builder().title("첫번째 질문")
                    .text("질문 내용")
                    .tags(List.of("tag1", "tag2"))
                    .build();

            secondQuestionSaveDto = QuestionSaveDto.builder().title("두번째 질문")
                    .text("질문 내용")
                    .tags(List.of("tag1", "tag2"))
                    .build();

            thirdQuestionSaveDto = QuestionSaveDto.builder().title("세번째 질문")
                    .text("질문 내용")
                    .tags(List.of("tag1", "tag2"))
                    .build();
            //@Transactional 처리했음. 순서 보장
            questionService.createQuestion(firstQuestionSaveDto,saveMemberId);
            questionService.createQuestion(secondQuestionSaveDto,saveMemberId);
            questionService.createQuestion(thirdQuestionSaveDto,saveMemberId);
        }
        @Test
        @DisplayName("최신순으로 정렬")
        void getLatest(){
            saveQuestions();
            List<Question> latestQuestionList = questionService.getQuestions(OrderType.NEW);
            assertThat(latestQuestionList).hasSize(3);
            List<String> questionTitleList = latestQuestionList.stream().map(question -> {
                return question.getTitle();
            }).collect(Collectors.toList());
            assertThat(questionTitleList).containsExactly(firstQuestionSaveDto.getTitle(),secondQuestionSaveDto.getTitle(),thirdQuestionSaveDto.getTitle());
        }
        @Test
        @DisplayName("인기순으로 정렬")
        void getHottest(){
            saveQuestions();
            List<Question> hottestQuestionList = questionService.getQuestions(OrderType.HOT);
            assertThat(hottestQuestionList).hasSize(3);
        }
    }

    @Nested
    @DisplayName("질문 수정")
    class updateQuestion{
        QuestionSaveDto saveQuestionDto = QuestionSaveDto.builder().title("저장된 질문")
                .text("저장된 질문 내용")
                .tags(List.of("tag1", "tag2"))
                .build();

        QuestionSaveDto updateQuestionDto = QuestionSaveDto.builder().title("수정된 질문")
                .text("수정된 질문 내용")
                .tags(List.of("tag3", "tag4"))
                .build();
        @Test
        @DisplayName("수정할 질문이 존재하지 않을때 예외처리")
        void ifQuestionDeleted(){
            Long EMPTY_QUESTION_ID = 9999L;
            assertThatThrownBy(()->questionService.updateQuestion(updateQuestionDto,EMPTY_QUESTION_ID,saveMemberId)).isExactlyInstanceOf(QuestionNotFoundException.class);
        }
        @Test
        @DisplayName("등록된 질문이 수정되었는지 확인")
        void IsQuestionUpdated() {
            //생성 확인
            Question savedQuestion = questionService.createQuestion(saveQuestionDto, saveMemberId);
            List<History> monthlyHistoryByUserIdBeforeUpdate = myRepository.findMonthlyHistoryByUserId(saveMemberId, savedQuestion.getCreatedDate().getYear(), savedQuestion.getCreatedDate().getMonthValue());
            assertThat(monthlyHistoryByUserIdBeforeUpdate.get(0).getQuestionCnt()).isEqualTo(1L);

            Question updatedQuestion = questionService.updateQuestion(updateQuestionDto, savedQuestion.getId(), saveMemberId);
            assertThat(updatedQuestion.getTitle()).isEqualTo(updateQuestionDto.getTitle());
            assertThat(updatedQuestion.getTags()).isEqualTo(updateQuestionDto.getTags());

            LocalDateTime appliedDateTime = savedQuestion.getCreatedDate();
            List<History> monthlyHistoryByUserId = myRepository.findMonthlyHistoryByUserId(saveMemberId, appliedDateTime.getYear(), appliedDateTime.getMonthValue());
            assertThat(monthlyHistoryByUserId).hasSize(1);

            History history = monthlyHistoryByUserId.get(0);
            assertThat(history.getUserId()).isEqualTo(saveMemberId);
            assertThat(history.getQuestionCnt()).isEqualTo(1L);
            assertThat(history.getMemoCnt()).isEqualTo(0L);

            //MyRepository 수정했을때 날짜 바뀌는지 확인 위해 변수로 바꿔야할 필요 있음
        }
    }
}