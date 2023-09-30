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
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.exception.QuestionNotFoundException;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
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
class QnAIntegrationTest {
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    MyRepository myRepository;
    @Autowired
    AnswerService answerService;
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
        saveUser("user");
    }


    private void saveUser(String name) {
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .userName(name)
                .loginType(LoginType.NON_SOCIAL)
                .userPw("a1234567!")
                .userEmail("test@gmail.com")
                .build();
        MemberProfileEntity memberProfileEntity = MemberProfileEntity.builder()
                .nickname(name)
                .profileImageFilePath("taehoon-image")
                .introduce("introduce of taehoon")
                .userTags(List.of("tag1", "tag2"))
                .build();

        SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(NonSocialMember.createNonSocialMember(memberSaveDto));
        saveMemberId = saveMemberResponseDto.getId();
        myRepository.createProfile(saveMemberId, memberProfileEntity);
    }
    @Test
    @DisplayName("질문과 연관된 답변 생성")
    @Transactional
    void createAnswer(){
        Question question = makeQuestion();

        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .userName("answer_user")
                .loginType(LoginType.NON_SOCIAL)
                .userPw("a1234567!")
                .userEmail("test@gmail.com")
                .build();
        MemberProfileEntity memberProfileEntity = MemberProfileEntity.builder()
                .nickname("answer_user")
                .profileImageFilePath("taehoon-image")
                .introduce("introduce of taehoon")
                .userTags(List.of("tag1", "tag2"))
                .build();

        AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                .text("답변 텍스트")
                .description("답변 요약")
                .build();

        SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(NonSocialMember.createNonSocialMember(memberSaveDto));
        Long answerAuthorId = saveMemberResponseDto.getId();
        myRepository.createProfile(answerAuthorId, memberProfileEntity);

        Answer answerOfQuestion = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);
        assertThat(answerOfQuestion.getDescription()).isEqualTo("답변 요약");

        LocalDateTime appliedDateTime = question.getCreatedDate();
        List<History> monthlyHistoryOfQuestionAuthorId = myRepository.findMonthlyHistoryByUserId(saveMemberId, appliedDateTime.getYear(), appliedDateTime.getMonthValue());
        List<History> monthlyHistoryOfAnswerAuthorId = myRepository.findMonthlyHistoryByUserId(answerAuthorId, appliedDateTime.getYear(), appliedDateTime.getMonthValue());

        assertThat(monthlyHistoryOfQuestionAuthorId).hasSize(1);
        assertThat(monthlyHistoryOfAnswerAuthorId).hasSize(1);

        History historyOfQuestionOwner = monthlyHistoryOfQuestionAuthorId.get(0);
        History historyOfAnswerOwner = monthlyHistoryOfAnswerAuthorId.get(0);
        assertThat(historyOfQuestionOwner.getUserId()).isEqualTo(saveMemberId);
        assertThat(historyOfAnswerOwner.getUserId()).isEqualTo(answerAuthorId);
        assertThat(historyOfQuestionOwner.getQuestionCnt()).isEqualTo(1L);
        assertThat(historyOfQuestionOwner.getMemoCnt()).isEqualTo(0L);
        assertThat(historyOfAnswerOwner.getQuestionCnt()).isEqualTo(1L);
        assertThat(historyOfAnswerOwner.getMemoCnt()).isEqualTo(0L);

    }



    @Test
    @DisplayName("질문 생성")
    @Transactional
    void createQuestion(){
        Question question = makeQuestion();
        assertThat(question.getTitle()).isEqualTo("테스트 제목 생성");

        LocalDateTime appliedDateTime = question.getCreatedDate();
        List<History> monthlyHistoryByUserId = myRepository.findMonthlyHistoryByUserId(saveMemberId, appliedDateTime.getYear(), appliedDateTime.getMonthValue());
        assertThat(monthlyHistoryByUserId).hasSize(1);

        History history = monthlyHistoryByUserId.get(0);
        assertThat(history.getUserId()).isEqualTo(saveMemberId);
        assertThat(history.getQuestionCnt()).isEqualTo(1L);
        assertThat(history.getMemoCnt()).isEqualTo(0L);

    }

    private Question makeQuestion() {
        QuestionSaveDto questionSaveDto = QuestionSaveDto.builder().title("테스트 제목 생성")
                .text("질문 내용")
                .tags(List.of("tag1", "tag2"))
                .build();

        return questionService.createQuestion(questionSaveDto, saveMemberId);
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