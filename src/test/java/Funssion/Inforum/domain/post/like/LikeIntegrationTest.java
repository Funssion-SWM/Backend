package Funssion.Inforum.domain.post.like;

import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.member.service.MemberService;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.domain.Post;
import Funssion.Inforum.domain.post.like.dto.response.DisLikeResponseDto;
import Funssion.Inforum.domain.post.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.post.like.repository.LikeRepository;
import Funssion.Inforum.domain.post.like.service.LikeService;
import Funssion.Inforum.domain.post.qna.Constant;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import Funssion.Inforum.domain.post.qna.service.AnswerService;
import Funssion.Inforum.domain.post.qna.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
//@Transactional
public class LikeIntegrationTest {
    @Autowired
    LikeService likeService;
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MyRepository myRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    QuestionService questionService;
    @Autowired
    AnswerService answerService;
    @Autowired
    AnswerService answerRepository;
    static final String AUTHORIZED_USER = "999";

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
    @DisplayName("일반 질문 생성 후 다른 유저가 좋아요를 누르고 취소할 경우 반영이 되는지 확인")
    @WithMockUser(username=AUTHORIZED_USER)
    @Transactional
    void cancelLike(){
        Question question = makeQuestion();
        userLike(question);
        assertThat(likeService.getLikeInfo(PostType.QUESTION, question.getId())).isEqualTo(new LikeResponseDto(true, 1L));

        likeService.unlikePost(PostType.QUESTION,question.getId());
        assertThat(likeService.getLikeInfo(PostType.QUESTION, question.getId()).getLikes()).isEqualTo(0L);
    }

    @Test
    @DisplayName("질문(싫어요 할 수 없는 포스트 타입)에 싫어요 요청을 할경우")
    @WithMockUser(username=AUTHORIZED_USER)
    @Transactional
    void 답변_이외에는_싫어요를_할_수_없습니다(){
        Question question = makeQuestion();
        assertThatThrownBy(()-> likeService.dislikePost(PostType.QUESTION,question.getId())).hasMessage("해당 타입의 게시글들은 비추천할 수 없습니다.");
    }

    @Test
    @DisplayName("일반 질문의 답변에 다른 유저가 좋아요를 누르고 취소 없이 싫어요를 누를 경우 에러 확인")
    @WithMockUser(username=AUTHORIZED_USER)
    @Transactional
    void 좋아요_후_취소없이_바로_싫어요_누를경우_에러(){
        Question question = makeQuestion();

        Long answerAuthorId = createAuthorOfAnswer();
        AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                .description("답변 요약")
                .build();

        Answer answerOfQuestion = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);

        userLike(answerOfQuestion);
        assertThat(likeService.getLikeInfo(PostType.ANSWER, answerOfQuestion.getId())).isEqualTo(new LikeResponseDto(true, 1L));

        assertThatThrownBy(() -> userDisLike(answerOfQuestion)).hasMessage("You Cannot Both Dislike and Like Post");
    }

    @Test
    @DisplayName("일반 질문 생성 후 다른 유저가 싫어요를 누르고 취소 없이 좋아요를 누를 경우 에러 확인")
    @WithMockUser(username=AUTHORIZED_USER)
    @Transactional
    void 싫어요_후_취소없이_바로_좋아요_누를경우_에러(){
        Question question = makeQuestion();

        Long answerAuthorId = createAuthorOfAnswer();
        AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                .description("답변 요약")
                .build();

        Answer answerOfQuestion = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);

        userDisLike(answerOfQuestion);
        assertThat(likeService.getDisLikeInfo(PostType.ANSWER, answerOfQuestion.getId())).isEqualTo(new DisLikeResponseDto(true, 1L));

        assertThatThrownBy(() -> userLike(answerOfQuestion)).hasMessage("You Cannot Both Dislike and Like Post");
    }


    @Test
    @DisplayName("답변에 좋아요 누르고, 내가 좋아요 눌렀는지 확인")
    @WithMockUser(username=AUTHORIZED_USER)
    @Transactional
    void 내가_답변_좋아요를_눌렀는지_확인(){
        Question question = makeQuestion();

        Long answerAuthorId = createAuthorOfAnswer();
        AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                .description("답변 요약")
                .build();

        Answer answerOfQuestion = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);

        userLike(answerOfQuestion);
        assertThat(answerRepository.getAnswersOfQuestion(SecurityContextUtils.getUserId(),answerOfQuestion.getQuestionId())).hasSize(1);
        assertThat(answerRepository.getAnswersOfQuestion(SecurityContextUtils.getUserId(),answerOfQuestion.getQuestionId()).get(0).getLikes()).isEqualTo(1L);
        assertThat(answerRepository.getAnswersOfQuestion(SecurityContextUtils.getUserId(),answerOfQuestion.getQuestionId()).get(0).isLike()).isEqualTo(true);
    }

    @Test
    @DisplayName("답변에 싫어요 누르고, 내가 싫어요 눌렀는지 확인")
    @WithMockUser(username=AUTHORIZED_USER)
    @Transactional
    void 내가_답변_싫어요를_눌렀는지_확인(){
        Question question = makeQuestion();

        Long answerAuthorId = createAuthorOfAnswer();
        AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                .description("답변 요약")
                .build();

        Answer answerOfQuestion = answerService.createAnswerOfQuestion(answerSaveDto, question.getId(), answerAuthorId);

        userDisLike(answerOfQuestion);
        assertThat(answerRepository.getAnswersOfQuestion(SecurityContextUtils.getUserId(),answerOfQuestion.getQuestionId())).hasSize(1);
        assertThat(answerRepository.getAnswersOfQuestion(SecurityContextUtils.getUserId(),answerOfQuestion.getQuestionId()).get(0).getLikes()).isEqualTo(0L);
        assertThat(answerRepository.getAnswersOfQuestion(SecurityContextUtils.getUserId(),answerOfQuestion.getQuestionId()).get(0).getDislikes()).isEqualTo(1L);
        assertThat(answerRepository.getAnswersOfQuestion(SecurityContextUtils.getUserId(),answerOfQuestion.getQuestionId()).get(0).isLike()).isEqualTo(false);
        assertThat(answerRepository.getAnswersOfQuestion(SecurityContextUtils.getUserId(),answerOfQuestion.getQuestionId()).get(0).isDisLike()).isEqualTo(true);
    }

    @Test
    @DisplayName("질문에 좋아요 누르고, 내가 좋아요 눌렀는지 확인 및 취소 ")
    @WithMockUser(username=AUTHORIZED_USER)
    @Transactional
    void 내가_질문에_좋아요_눌렀는지_확인_및_취소(){
        Question question = makeQuestion();

        userLike(question);

        assertThat(questionRepository.getQuestions(SecurityContextUtils.getUserId(), OrderType.NEW).get(0).getLikes()).isEqualTo(1);
        assertThat(questionRepository.getQuestions(1234L, OrderType.NEW).get(0).getLikes()).isEqualTo(1);
        assertThat(questionRepository.getQuestions(SecurityContextUtils.getUserId(), OrderType.NEW).get(0).isLike()).isEqualTo(true);
        assertThat(questionRepository.getQuestions(1234L, OrderType.NEW).get(0).isLike()).isEqualTo(false);

        likeService.unlikePost(PostType.QUESTION,question.getId());
        assertThat(questionRepository.getQuestions(SecurityContextUtils.getUserId(), OrderType.NEW).get(0).getLikes()).isEqualTo(0);
        assertThat(questionRepository.getQuestions(SecurityContextUtils.getUserId(), OrderType.NEW).get(0).isLike()).isEqualTo(false);

    }

    private void userLike(Post post) {
        PostType postType = switch (post.getClass().getSimpleName()) {
            case "Question" -> PostType.QUESTION;
            case "Answer" -> PostType.ANSWER;
            case "Memo" -> PostType.MEMO;
            default -> null;
        };
        likeService.likePost(postType,post.getId());
    }
    private void userDisLike(Post post) {
        PostType postType = switch (post.getClass().getSimpleName()) {
            case "Question" -> PostType.QUESTION;
            case "Answer" -> PostType.ANSWER;
            case "Memo" -> PostType.MEMO;
            default -> null;
        };
        likeService.dislikePost(postType,post.getId());
    }

    private Question makeQuestion() {
        QuestionSaveDto questionSaveDto = QuestionSaveDto.builder().title("테스트 제목 생성")
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문 내용\", \"type\": \"text\"}]}]}")
                .tags(List.of("tag1", "tag2"))
                .build();

        return questionService.createQuestion(questionSaveDto, saveMemberId,Long.valueOf(Constant.NONE_MEMO_QUESTION ));
    }

    private Long createAuthorOfAnswer(){
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
        Long answerAuthorId = saveMemberResponseDto.getId();
        myRepository.createProfile(answerAuthorId, memberProfileEntity);

        return answerAuthorId;
    }
}
