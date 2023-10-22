package Funssion.Inforum.domain.post.qna.service;

import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.domain.History;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.like.repository.LikeRepository;
import Funssion.Inforum.domain.post.like.service.LikeService;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.Constant;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.dto.response.QuestionDto;
import Funssion.Inforum.domain.post.qna.exception.AnswerNotFoundException;
import Funssion.Inforum.domain.post.qna.exception.DuplicateSelectedAnswerException;
import Funssion.Inforum.domain.post.qna.exception.QuestionNotFoundException;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import Funssion.Inforum.domain.score.Rank;
import Funssion.Inforum.domain.score.repository.ScoreRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static Funssion.Inforum.domain.post.qna.Constant.DEFAULT_PAGE_NUM;
import static Funssion.Inforum.domain.post.qna.Constant.DEFAULT_RESULT_SIZE_PER_PAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
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
    @Autowired
    MemoRepository memoRepository;
    @Autowired
    LikeService likeService;
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    ScoreRepository scoreRepository;

    static final String AUTHORIZED_USER = "999";

    static Long saveMemberId;

    static QuestionSaveDto firstQuestionSaveDto;
    static QuestionSaveDto secondQuestionSaveDto;
    static QuestionSaveDto thirdQuestionSaveDto;


    private static QuestionSaveDto makeQuestionDto(){
       return QuestionSaveDto.builder().title("메모와 연관된 질문 제목 생성")
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문 내용\", \"type\": \"text\"}]}]}")
                .tags(List.of("tag1", "tag2"))
                .description("질문 내용 요약")
                .build();
    }
    @BeforeEach
    void init() {
        saveUser("user");
    }

    private void saveUser(String name) {
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .userName(name)
                .loginType(LoginType.NON_SOCIAL)
                .userPw("a1234567!")
                .userEmail(name+"@gmail.com")
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
        Question question = makePureQuestion();

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
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                .description("답변 요약")
                .build();

        SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(NonSocialMember.createNonSocialMember(memberSaveDto));
        Long answerAuthorId = saveMemberResponseDto.getId();
        myRepository.createProfile(answerAuthorId, memberProfileEntity);

        answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);

        List<Question> questions = questionRepository.getQuestions(saveMemberId, OrderType.NEW,DEFAULT_PAGE_NUM,DEFAULT_RESULT_SIZE_PER_PAGE);
        assertThat(questions.get(0).getAnswersCount()).isEqualTo(1);

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
        assertThat(historyOfAnswerOwner.getAnswerCnt()).isEqualTo(1L);
        assertThat(historyOfAnswerOwner.getMemoCnt()).isEqualTo(0L);

    }

    @Test
    @DisplayName("답변 삭제 후 질문 리스트에 삭제된 답변의 갯수가 반영되는지 확인")
    @Transactional
    void checkAnswersCountWhenDelete(){
        Question question = makePureQuestion();

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
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                .description("답변 요약")
                .build();

        SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(NonSocialMember.createNonSocialMember(memberSaveDto));
        Long answerAuthorId = saveMemberResponseDto.getId();
        myRepository.createProfile(answerAuthorId, memberProfileEntity);

        answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);
        Answer beDeletedAnswer = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);

        List<Question> questionsBeforeDelete = questionRepository.getQuestions(saveMemberId, OrderType.NEW,DEFAULT_PAGE_NUM,DEFAULT_RESULT_SIZE_PER_PAGE);
        assertThat(questionsBeforeDelete.get(0).getAnswersCount()).isEqualTo(2);

        answerService.deleteAnswer(beDeletedAnswer.getId(),beDeletedAnswer.getAuthorId());

        List<Question> questionsAfterDelete = questionRepository.getQuestions(saveMemberId, OrderType.NEW,DEFAULT_PAGE_NUM,DEFAULT_RESULT_SIZE_PER_PAGE);
        assertThat(questionsAfterDelete.get(0).getAnswersCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("일반 질문 생성")
    @Transactional
    void createQuestion(){
        Question question = makePureQuestion();
        assertThat(question.getTitle()).isEqualTo("테스트 제목 생성");

        LocalDateTime appliedDateTime = question.getCreatedDate();
        List<History> monthlyHistoryByUserId = myRepository.findMonthlyHistoryByUserId(saveMemberId, appliedDateTime.getYear(), appliedDateTime.getMonthValue());
        assertThat(monthlyHistoryByUserId).hasSize(1);

        History history = monthlyHistoryByUserId.get(0);
        assertThat(history.getUserId()).isEqualTo(saveMemberId);
        assertThat(history.getQuestionCnt()).isEqualTo(1L);
        assertThat(history.getMemoCnt()).isEqualTo(0L);

    }

    @Test
    @DisplayName("자신이 질문한 것들만 가져오기")
    @Transactional
    void getMyQuestion(){
        makePureQuestion();
        makePureQuestion();
        makeQuestionOfOtherAuthor();

        List<Question> myQuestions = questionRepository.getMyQuestions(saveMemberId, OrderType.NEW, DEFAULT_PAGE_NUM, DEFAULT_RESULT_SIZE_PER_PAGE);
        assertThat(myQuestions).hasSize(2);
    }

    @Test
    @DisplayName("자신이 좋아요한 질문만 가져오기")
    @Transactional
    void getMyLikedQuestion(){
        Question question1 = makePureQuestion();
        Question question2 = makePureQuestion();
        Question question3 = makePureQuestion();
        Long likeUserId = createUniqueAuthor();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(likeUserId.toString(),"12345678"));

        likeService.likePost(PostType.QUESTION, question1.getId());
        likeService.likePost(PostType.QUESTION, question2.getId());

        List<Question> myLikedQuestions = questionRepository.getMyLikedQuestions(likeUserId, DEFAULT_PAGE_NUM, DEFAULT_RESULT_SIZE_PER_PAGE);
        assertThat(myLikedQuestions).hasSize(2);
    }

    @Test
    @DisplayName("자신이 답변한 질문만 가져오기")
    @Transactional
    void getQuestionsOfMyAnswer(){
        Question question1 = makePureQuestion();
        Question question2 = makePureQuestion();
        Question question3 = makePureQuestion();
        Long answerAuthorId = createUniqueAuthor();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(answerAuthorId.toString(),"12345678"));
        AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                .description("답변 요약")
                .build();
        answerService.createAnswerOfQuestion(answerSaveDto,question2.getId(),answerAuthorId);
        answerService.createAnswerOfQuestion(answerSaveDto,question3.getId(),answerAuthorId);

        List<Question> questionsOfMyAnswer = questionRepository.getQuestionsOfMyAnswer(answerAuthorId, DEFAULT_PAGE_NUM, DEFAULT_RESULT_SIZE_PER_PAGE);
        assertThat(questionsOfMyAnswer).hasSize(2);
    }

    @Test
    @DisplayName("자신이 좋아요한 답변이 존재하는 질문들 가져오기")
    @Transactional
    void getQuestionsOfMyLikedAnswer(){
        Question question1 = makePureQuestion();
        Question question2 = makePureQuestion();
        Question question3 = makePureQuestion();


        Long answerAuthorId = createUniqueAuthor();
        makeAnswerOfQuestion(answerAuthorId,List.of(question1,question2));
        List<Answer> answersOfQuestion1 = answerService.getAnswersOfQuestion(saveMemberId, question1.getId());
        List<Answer> answersOfQuestion2 = answerService.getAnswersOfQuestion(saveMemberId, question2.getId());

        Long likeUserId = createUniqueAuthor();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(likeUserId.toString(),"12345678"));
        likeService.likePost(PostType.ANSWER, answersOfQuestion1.get(0).getId());
        likeService.likePost(PostType.ANSWER, answersOfQuestion2.get(0).getId());

        List<Question> questionsOfMyLikedAnswer = questionRepository.getQuestionsOfMyLikedAnswer(likeUserId, DEFAULT_PAGE_NUM, DEFAULT_RESULT_SIZE_PER_PAGE);
        assertThat(questionsOfMyLikedAnswer).hasSize(2);
    }

    private void makeQuestionOfOtherAuthor() {
        Long questionAuthorId = createUniqueAuthor();
        QuestionSaveDto questionSaveDto = QuestionSaveDto.builder().title("테스트 제목 생성")
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문 내용\", \"type\": \"text\"}]}]}")
                .tags(List.of("tag1", "tag2"))
                .build();
        questionService.createQuestion(questionSaveDto, questionAuthorId, Long.valueOf(Constant.NONE_MEMO_QUESTION));
    }


    private Question makePureQuestion() {
        QuestionSaveDto questionSaveDto = QuestionSaveDto.builder().title("테스트 제목 생성")
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문 내용\", \"type\": \"text\"}]}]}")
                .tags(List.of("tag1", "tag2"))
                .build();

        return questionService.createQuestion(questionSaveDto, saveMemberId,Long.valueOf(Constant.NONE_MEMO_QUESTION ));
    }
    @Test
    @DisplayName("특정 메모에 관한 질문 생성")
    @Transactional
    void createQuestionInMemo() {
        Memo memo = createMemo();

        QuestionSaveDto questionSaveDto = makeQuestionDto();

        Question question = questionService.createQuestion(questionSaveDto, saveMemberId, memo.getId());
        assertThat(question.getTitle()).isEqualTo("메모와 연관된 질문 제목 생성");
        assertThat(memoRepository.findById(question.getMemoId()).getQuestionCount()).isEqualTo(1);
    }
    @Test
    @DisplayName("특정 메모를 태그한 질문 열람하기")
    @Transactional
    void getQuestionsOfMemo(){
        Memo memo = createMemo();

        QuestionSaveDto questionSaveDto1 = makeQuestionDto();
        QuestionSaveDto questionSaveDto2 = makeQuestionDto();

        questionService.createQuestion(questionSaveDto1, saveMemberId, memo.getId());
        questionService.createQuestion(questionSaveDto2, saveMemberId, Long.valueOf(Constant.NONE_MEMO_QUESTION));

        List<Question> questionsOfMemo = questionRepository.getQuestionsOfMemo(saveMemberId, memo.getId());
        assertThat(questionsOfMemo).hasSize(1);
        assertThat(questionsOfMemo.get(0).getTitle()).isEqualTo("메모와 연관된 질문 제목 생성");
    }

    @Test
    @DisplayName("특정 질문을 id로 열람하기")
    @WithMockUser(username=AUTHORIZED_USER)
    @Transactional
    void getOneQuestion(){
        QuestionSaveDto questionSaveDto = makeQuestionDto();
        Question question = questionService.createQuestion(questionSaveDto, saveMemberId, Long.valueOf(Constant.NONE_MEMO_QUESTION));
        QuestionDto oneQuestion = questionService.getOneQuestion(SecurityContextUtils.getUserId(), question.getId());
        assertThat(oneQuestion.isMine()).isEqualTo(false);
    }

//    @Test
//    @DisplayName("특정 질문을 id로 열람할 때, 자기가 작성한 질문인지 확인하기")
//    @WithMockUser(username=AUTHORIZED_USER)
//    @Transactional
//    void checkMyQuestion(){
//        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
//                .userName(AUTHORIZED_USER)
//                .loginType(LoginType.NON_SOCIAL)
//                .userPw("a1234567!")
//                .userEmail(AUTHORIZED_USER+"@gmail.com")
//                .build();
//        MemberProfileEntity memberProfileEntity = MemberProfileEntity.builder()
//                .nickname(AUTHORIZED_USER)
//                .profileImageFilePath("taehoon-image")
//                .introduce("introduce of taehoon")
//                .userTags(List.of("tag1", "tag2"))
//                .build();
//
//        SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(NonSocialMember.createNonSocialMember(memberSaveDto));
//        Long memberId = saveMemberResponseDto.getId();
//        myRepository.createProfile(memberId, memberProfileEntity);
//        QuestionSaveDto questionSaveDto = makeQuestionDto();
//        Question question = questionService.createQuestion(questionSaveDto, memberId, Long.valueOf(Constant.NONE_MEMO_QUESTION));
//        //memberId 모킹하였음. (@WithMockUser랑 맞추기위해)
//        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(AUTHORIZED_USER,"a123456"));
//
//        System.out.println("userid " + SecurityContextUtils.getUserId());
//        System.out.println("userid " + question.getAuthorId());
//        QuestionDto oneQuestion = questionService.getOneQuestion(question.getId());
//        assertThat(oneQuestion.isMine()).isEqualTo(true);
//    }

    private Memo createMemo() {
        String[] testTagsStringList = {
                "Backend","Java","Spring"
        };
        List<String> testTags = new ArrayList<>(Arrays.asList(testTagsStringList));
        MemoSaveDto form1 = new MemoSaveDto("JPA란?", "JPA일까?","{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"안녕하세요!!\", \"type\": \"text\"}]}]}", "yellow",testTags);
        Memo memo1 = Memo.builder()
                .title(form1.getMemoTitle())
                .text(form1.getMemoText())
                .description(form1.getMemoDescription())
                .color(form1.getMemoColor())
                .authorId(9999L)
                .authorName("Jinu")
                .authorImagePath("http:jinu")
                .rank(Rank.BRONZE_5.toString())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .isTemporary(false)
                .likes(0L)
                .memoTags(List.of("JPA", "Java"))
                .build();
        return memoRepository.create(memo1);
    }

    private AnswerSaveDto createAnswerSaveDto(){
        return AnswerSaveDto.builder()
                .description("답변의 요약 정보가 들어갑니다.")
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                .build();
    }

    @Nested
    @DisplayName("파라미터에 알맞는 정렬된 메모 리스트 가져오기")
    class getOrderedQuestions{
        private void saveQuestions(){
            firstQuestionSaveDto = QuestionSaveDto.builder().title("첫번째 질문")
                    .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문 내용\", \"type\": \"text\"}]}]}")
                    .tags(List.of("tag1", "tag2"))
                    .description("질문 내용 요약")
                    .build();

            secondQuestionSaveDto = QuestionSaveDto.builder().title("두번째 질문")
                    .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문 내용\", \"type\": \"text\"}]}]}")
                    .tags(List.of("tag1", "tag2"))
                    .description("질문 내용 요약")
                    .build();

            thirdQuestionSaveDto = QuestionSaveDto.builder().title("세번째 질문")
                    .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문 내용\", \"type\": \"text\"}]}]}")
                    .tags(List.of("tag1", "tag2"))
                    .description("질문 내용 요약")
                    .build();
            questionService.createQuestion(firstQuestionSaveDto, saveMemberId, Long.valueOf(Constant.NONE_MEMO_QUESTION));
            questionService.createQuestion(secondQuestionSaveDto, saveMemberId, Long.valueOf(Constant.NONE_MEMO_QUESTION));
            questionService.createQuestion(thirdQuestionSaveDto, saveMemberId, Long.valueOf(Constant.NONE_MEMO_QUESTION));
        }
//        @Test
//        @DisplayName("최신순으로 정렬")
//        void getLatest(){
//            saveQuestions();
//            List<Question> latestQuestionList = questionService.getQuestions(saveMemberId,OrderType.NEW);
//            assertThat(latestQuestionList).hasSize(3);
//            List<String> questionTitleList = latestQuestionList.stream().map(question -> {
//                return question.getTitle();
//            }).collect(Collectors.toList());
//            assertThat(questionTitleList).containsExactly(firstQuestionSaveDto.getTitle(),secondQuestionSaveDto.getTitle(),thirdQuestionSaveDto.getTitle());
//        }
        @Test
        @DisplayName("인기순으로 정렬")
        void getHottest(){
            saveQuestions();
            List<Question> hottestQuestionList = questionService.getQuestions(saveMemberId, OrderType.HOT,DEFAULT_PAGE_NUM,DEFAULT_RESULT_SIZE_PER_PAGE);
            assertThat(hottestQuestionList).hasSize(3);
        }
    }

    @Nested
    @DisplayName("답변 가져오기")
    class getAnswerList{
        @Test
        @DisplayName("질문과 연관된 답변 리스트 가져오기")
        void getAnswerListOfQuestion(){
            Question question1 = makePureQuestion();
            Question question2 = makePureQuestion();

            Long answerAuthorId = createUniqueAuthor();

            makeAnswerOfQuestion(answerAuthorId,List.of(question1,question2));

            List<Answer> answersOfQuestion = answerService.getAnswersOfQuestion(saveMemberId, question1.getId());
            assertThat(answersOfQuestion).hasSize(3);

            List<History> monthlyHistoryAnswerAuthorId = myRepository.findMonthlyHistoryByUserId(answerAuthorId, question1.getCreatedDate().getYear(), question1.getCreatedDate().getMonthValue());
            assertThat(monthlyHistoryAnswerAuthorId .get(0).getAnswerCnt()).isEqualTo(4L);

        }
        @Test
        @DisplayName("질문과 연관된 답변 리스트를 채택순, 좋아요-싫어요 순으로 가져오기")
        void getOrderedAnswerListOfQuestion(){
            Question question = makePureQuestion();

            Long answerAuthorId = createUniqueAuthor();
            AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                    .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                    .description("답변 요약")
                    .build();

            Answer answerOfQuestion1 = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);
            Answer answerOfQuestion2 = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);
            Answer answerOfQuestion3 = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);
            Answer answerOfQuestion4 = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(saveMemberId.toString(),"12345678"));

            likeService.likePost(PostType.ANSWER,answerOfQuestion4.getId());
            answerService.selectAnswer(saveMemberId,answerOfQuestion3.getQuestionId(),answerOfQuestion3.getId());
            likeService.dislikePost(PostType.ANSWER,answerOfQuestion2.getId());

            List<Answer> answersOfQuestion = answerService.getAnswersOfQuestion(saveMemberId, question.getId());
            assertThat(answersOfQuestion).hasSize(4);
            assertThat(answersOfQuestion.stream().map(answer->answer.getId()).collect(Collectors.toList())).containsExactly(answerOfQuestion3.getId(),answerOfQuestion4.getId(),answerOfQuestion1.getId(),answerOfQuestion2.getId());
        }


        @Test
        @DisplayName("고유 id로 답변 하나만 가져오기")
        void getAnswerOfQuestion(){
            Question question = makePureQuestion();
            Long answerAuthorId = createUniqueAuthor();

            AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                    .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                    .description("답변 요약")
                    .build();

            Answer answerOfQuestion = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);
            Answer answer = answerService.getAnswerBy(answerOfQuestion.getId());
            assertThat(answerOfQuestion.getText()).isEqualTo(answer.getText());
        }


    }
    private void makeAnswerOfQuestion(Long authorId, List<Question> questions) {
        answerService.createAnswerOfQuestion(createAnswerSaveDto(),questions.get(0).getId(),authorId);
        answerService.createAnswerOfQuestion(createAnswerSaveDto(),questions.get(0).getId(),authorId);
        answerService.createAnswerOfQuestion(createAnswerSaveDto(),questions.get(0).getId(),authorId);
        answerService.createAnswerOfQuestion(createAnswerSaveDto(),questions.get(1).getId(),authorId);
    }

    @Nested
    @DisplayName("질문 수정")
    class updateQuestion{
        QuestionSaveDto saveQuestionDto = QuestionSaveDto.builder().title("저장된 질문")
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"저장된 질문 내용\", \"type\": \"text\"}]}]}")
                .tags(List.of("tag1", "tag2"))
                .description("질문 내용 요약")
                .build();

        QuestionSaveDto updateQuestionDto = QuestionSaveDto.builder().title("수정된 질문")
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"수정된 질문 내용\", \"type\": \"text\"}]}]}")
                .tags(List.of("tag3", "tag4"))
                .description("질문 내용 요약")
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
            Question savedQuestion = questionService.createQuestion(saveQuestionDto, saveMemberId, Long.valueOf(Constant.NONE_MEMO_QUESTION));
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
    @Nested
    @DisplayName("답변 작성")
    class createAnswer{
        @Test
        @DisplayName("자신이 작성한 질문글에 답변을 작성할 경우")
        void authorOfQuestionCreateAnswer(){
            Question question = makePureQuestion();
            assertThatThrownBy(()->answerService.createAnswerOfQuestion(createAnswerSaveDto(), question.getId(), saveMemberId)).hasMessage("자신이 작성한 질문 글에 답변을 달 수 없습니다.");
        }
    }
    @Nested
    @DisplayName("답변 수정")
    class updateAnswer{
        @Test
        @DisplayName("답변 수정 확인")
        void updateAnswer(){
            Question question = makePureQuestion();

            Long answerAuthorId = createUniqueAuthor();

            AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                    .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                    .description("답변 요약")
                    .build();
            Answer answerOfQuestion = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);

            Answer updatedAnswer = answerService.updateAnswer(createAnswerSaveDto(), answerOfQuestion.getId());
            assertThat(updatedAnswer.getUpdatedDate()).isNotEqualTo(answerOfQuestion.getCreatedDate());
        }
    }
    @Nested
    @DisplayName("답변 삭제")
    class deleteAnswer{

        @Test
        @DisplayName("자신이 작성한 답변을 삭제")
        void deleteAnswer(){
            Question question = makePureQuestion();

            Long answerAuthorId = createUniqueAuthor();

            AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                    .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                    .description("답변 요약")
                    .build();

            Answer answerOfQuestion = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);

            LocalDateTime createdTime = answerOfQuestion.getCreatedDate();
            List<History> beforeDeleteHistory = myRepository.findMonthlyHistoryByUserId(answerAuthorId, createdTime.getYear(), createdTime.getMonthValue());
            assertThat(beforeDeleteHistory.get(0).getAnswerCnt()).isEqualTo(1L);

            answerService.deleteAnswer(answerOfQuestion.getId(),answerAuthorId);
            assertThatThrownBy(()->answerService.getAnswerBy(answerOfQuestion.getId())).isExactlyInstanceOf(AnswerNotFoundException.class);

            List<History> afterDeleteHistory = myRepository.findMonthlyHistoryByUserId(answerAuthorId, createdTime.getYear(), createdTime.getMonthValue());
            assertThat(afterDeleteHistory.get(0).getAnswerCnt()).isEqualTo(0L);

        }
    }

    @Nested
    @DisplayName("답변 채택")
    class selectAnswer{
        @Test
        @DisplayName("답변 채택 확인 및 질문에도 채택된 질문인지 확인")
        void selectAnswer(){
            Question question = makePureQuestion();

            Long answerAuthorId = createUniqueAuthor();

            AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                    .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                    .description("답변 요약")
                    .build();

            Answer answerOfQuestion = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);

            Answer answer = answerService.selectAnswer(saveMemberId, question.getId(), answerOfQuestion.getId());

            assertThat(answer.isSelected()).isEqualTo(true);

            QuestionDto questionAfterSolved = questionService.getOneQuestion(saveMemberId, question.getId());

            assertThat(questionAfterSolved.isSolved()).isEqualTo(true);

            QuestionDto questionAfterSolvedByOther = questionService.getOneQuestion(9999L, question.getId());

            assertThat(questionAfterSolvedByOther.isSolved()).isEqualTo(true);

        }
        @Test
        @DisplayName("답변 채택했었음에도 또 답변 체크하는 경우")
        void selectDuplicateAnswer(){
            Question question = makePureQuestion();

            Long answerAuthorId = createUniqueAuthor();

            AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                    .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                    .description("답변 요약")
                    .build();

            Answer answerOfQuestion1 = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);
            Answer answerOfQuestion2 = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);

            answerService.selectAnswer(saveMemberId, question.getId(), answerOfQuestion1.getId());
            assertThatThrownBy(()->answerService.selectAnswer(saveMemberId, question.getId(), answerOfQuestion2.getId())).isExactlyInstanceOf(DuplicateSelectedAnswerException.class);
        }
        @Test
        @DisplayName("질문 작성자가 아님에도 답변 체택하는 경우")
        void AuthorOfAnswerSelectOwnAnswer(){
            Question question = makePureQuestion();

            Long answerAuthorId = createUniqueAuthor();

            AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                    .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                    .description("답변 요약")
                    .build();

            Answer answerOfQuestion = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);

            assertThatThrownBy(()->answerService.selectAnswer(answerAuthorId, question.getId(), answerOfQuestion.getId())).isExactlyInstanceOf(UnAuthorizedException.class);
        }
    }
    @Nested
    @DisplayName("질문 삭제")
    class deleteQuestion{
        @Test
        @DisplayName("메모와 연관된 질문을 삭제할 경우 질문 수가 메모에 반영")
        void deleteQuestionThenApplyQuestionCountOfMemo(){
            Memo memo = createMemo();

            QuestionSaveDto questionSaveDto = makeQuestionDto();

            Question question = questionService.createQuestion(questionSaveDto, saveMemberId, memo.getId());
            Question question2 = questionService.createQuestion(questionSaveDto, saveMemberId, memo.getId());

            assertThat(memoRepository.findById(question.getMemoId()).getQuestionCount()).isEqualTo(2);
            questionService.deleteQuestion(question.getId(),saveMemberId);
            assertThat(memoRepository.findById(question.getMemoId()).getQuestionCount()).isEqualTo(1);
        }
        @Test
        @DisplayName("답변이 달린 질문을 삭제할 경우 오류 발생")
        void deleteQuestionWhenItHasAnswer(){
            QuestionSaveDto questionSaveDto = makeQuestionDto();

            Question question = questionService.createQuestion(questionSaveDto, saveMemberId, Long.valueOf(Constant.NONE_MEMO_QUESTION));

            Long answerAuthorId = createUniqueAuthor();

            AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                    .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                    .description("답변 요약")
                    .build();

            answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);

            assertThatThrownBy(()->questionService.deleteQuestion(question.getId(),saveMemberId)).hasMessage("답변이 달린 질문은 삭제할 수 없습니다.");
        }

    }
    private Long createUniqueAuthor(){
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


        SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(NonSocialMember.createNonSocialMember(memberSaveDto));
        Long authorId = saveMemberResponseDto.getId();
        myRepository.createProfile(authorId, memberProfileEntity);

        return authorId;
    }

}