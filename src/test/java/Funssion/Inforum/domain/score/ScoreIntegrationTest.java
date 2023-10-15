package Funssion.Inforum.domain.score;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.comment.repository.CommentRepository;
import Funssion.Inforum.domain.post.comment.service.CommentService;
import Funssion.Inforum.domain.post.like.repository.LikeRepository;
import Funssion.Inforum.domain.post.like.service.LikeService;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoDto;
import Funssion.Inforum.domain.post.memo.service.MemoService;
import Funssion.Inforum.domain.post.qna.Constant;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.service.AnswerService;
import Funssion.Inforum.domain.post.qna.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ScoreIntegrationTest {
    @Autowired
    ScoreRepository scoreRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ScoreService scoreService;
    @Autowired
    MemoService memoService;
    @Autowired
    QuestionService questionService;
    @Autowired
    AnswerService answerService;
    @Autowired
    MyRepository myRepository;
    @Autowired
    LikeService likeService;
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;
    static Long saveMemberId;
    static Long saveMemberIdForEachTest;

    @BeforeEach
    void saveUser(){
        saveUser("username");
        saveUser2("username2");
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
    private void saveUser2(String name) {
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
        saveMemberIdForEachTest = saveMemberResponseDto.getId();
        myRepository.createProfile(saveMemberIdForEachTest, memberProfileEntity);
    }
    @Nested
    @DisplayName("유저의 Score가 증가하는 경우")
    class addScoreOfUser{
        /**
         *
         *     MAKE_MEMO(50L), 체크 완료
         *     MAKE_QUESTION(30L), 체크 완료
         *     MAKE_ANSWER(20L), 체크 완료
         *     SELECT_ANSWER(20L), 체크 완료
         *     BEST_ANSWER(80L), 체크 완료
         *     MAKE_COMMENT(5L), 체크 완료
         *     LIKE(10L); 체크완료
         */
        @Test
        @DisplayName("유저가 일별 최대 점수를 채우지 않았을 경우(질/답/채택/좋아요/댓글작성 모두 포함")
        void addScoreWhenNotOverDailyLimit(){
            Long scoreOfQuestion = ScoreType.MAKE_QUESTION.getScore();
            Long scoreOfAnswer = ScoreType.MAKE_ANSWER.getScore();
            Long scoreOfBestAnswer = ScoreType.BEST_ANSWER.getScore();
            Long scoreOfSelectingAnswer = ScoreType.SELECT_ANSWER.getScore();
            Long scoreOfLike = ScoreType.LIKE.getScore();
            Long scoreOfComment = ScoreType.MAKE_COMMENT.getScore();

            Question question = makePureQuestion();
            assertThat(scoreRepository.getScore(saveMemberId)).isEqualTo(scoreOfQuestion);
            Answer answerOfQuestion = answerService.createAnswerOfQuestion(createAnswerSaveDto(), question.getId(), saveMemberIdForEachTest);
            assertThat(scoreRepository.getScore(saveMemberIdForEachTest)).isEqualTo(scoreOfAnswer);
            assertThat(scoreRepository.getUserDailyScore(saveMemberIdForEachTest)).isEqualTo(scoreOfAnswer); //답변 채택은 daily score 에 포함되지 않음.

            answerService.selectAnswer(saveMemberId, question.getId(), answerOfQuestion.getId());
            assertThat(scoreRepository.getScore(saveMemberId)).isEqualTo(scoreOfQuestion + scoreOfSelectingAnswer);
            assertThat(scoreRepository.getScore(saveMemberIdForEachTest)).isEqualTo(scoreOfBestAnswer + scoreOfAnswer);
            assertThat(scoreRepository.getUserDailyScore(saveMemberIdForEachTest)).isEqualTo(scoreOfAnswer);//답변 채택은 daily score 에 포함되지 않음.


            setSecurityContextHolderForTestLikeMethod();
            likeService.likePost(PostType.ANSWER,answerOfQuestion.getId());
            assertThat(scoreRepository.getScore(saveMemberIdForEachTest)).isEqualTo(scoreOfBestAnswer + scoreOfAnswer + scoreOfLike);
            assertThat(scoreRepository.getUserDailyScore(saveMemberIdForEachTest)).isEqualTo(scoreOfAnswer); //좋아요 받음도 daily score 에 포함되지 않음

            commentService.createComment(CommentSaveDto.builder()
                    .postId(answerOfQuestion.getId())
                    .postTypeWithComment(PostType.ANSWER)
                    .commentText("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 댓글 내용\", \"type\": \"text\"}]}]}")
                    .build()
            , saveMemberId);

            assertThat(scoreRepository.getScore(saveMemberId)).isEqualTo(scoreOfQuestion + scoreOfSelectingAnswer + scoreOfComment);
        }




        @Test
        @DisplayName("유저가 포스트를 작성하면 일일 최대 점수를 넘어갈때")
        //메모 작성 점수 체크
        void addScoreWhenUserAlreadyInLimit(){
            Long beforeTotalScore = 360L;
            Long dailyScore = 180L;
            setUserScoreForTest(beforeTotalScore, dailyScore);
            createMemo();
            assertThat(scoreRepository.getScore(saveMemberIdForEachTest)).isEqualTo(beforeTotalScore + Score.LIMIT_DAILY_SCORE - dailyScore);
        }

        @Test
        @DisplayName("좋아요를 50개 이상 받을 경우, 점수가 반영되지 않아야 한다")
        void likesOverLimitDoNotApplyScore(){
            MemoDto memoDto = createMemo();//saveMemberIdForTest 유저가 메모작성

            usersLikePost_50(memoDto); //매번 새로운 user설정해서 상황 가정함.
            assertThat(likeRepository.howManyLikesInPost(PostType.MEMO,memoDto.getMemoId())).isEqualTo(50);
            assertThat(scoreRepository.getScore(memoDto.getAuthorId())).isEqualTo(ScoreType.LIKE.getScore() * 50 + ScoreType.MAKE_MEMO.getScore());
            setSecurityContextHolderForTestLikeMethod();
            likeService.likePost(PostType.MEMO,memoDto.getMemoId());
            assertThat(likeRepository.howManyLikesInPost(PostType.MEMO,memoDto.getMemoId())).isEqualTo(51);
            assertThat(scoreRepository.getScore(memoDto.getAuthorId())).isEqualTo(ScoreType.LIKE.getScore() * 50 + ScoreType.MAKE_MEMO.getScore());
        }

        private void setUserScoreForTest(Long beforeTotalScore, Long dailyScore) {
            scoreRepository.updateUserScoreAtDay(saveMemberIdForEachTest, beforeTotalScore, dailyScore);
        }
        private void usersLikePost_50(MemoDto memoDto){
            for(Long i = 11L; i < 61L; i++) {
                setSecurityContextHolderForTestLikeMethodByDifferentUser(i);
                likeService.likePost(PostType.MEMO,memoDto.getMemoId());
            }
        }

        private void setSecurityContextHolderForTestLikeMethodByDifferentUser(Long username) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username.toString(),"12345678"));
        }
    }
    private void setSecurityContextHolderForTestLikeMethod() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(saveMemberId.toString(),"12345678"));
    }


    private Question makePureQuestion() {
        QuestionSaveDto questionSaveDto = QuestionSaveDto.builder().title("테스트 제목 생성")
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문 내용\", \"type\": \"text\"}]}]}")
                .tags(List.of("tag1", "tag2"))
                .build();

        return questionService.createQuestion(questionSaveDto, saveMemberId,Long.valueOf(Constant.NONE_MEMO_QUESTION ));
    }
    private AnswerSaveDto createAnswerSaveDto(){
        return AnswerSaveDto.builder()
                .description("답변의 요약 정보가 들어갑니다.")
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 내용\", \"type\": \"text\"}]}]}")
                .build();
    }

    private MemoDto createMemo() {
        String[] testTagsStringList = {
                "Backend","Java","Spring"
        };
        List<String> testTags = new ArrayList<>(Arrays.asList(testTagsStringList));
        MemoSaveDto form1 = new MemoSaveDto("JPA란?", "JPA일까?","{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"안녕하세요!!\", \"type\": \"text\"}]}]}", "yellow",testTags,false);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(saveMemberIdForEachTest.toString(),"12345678"));
        return memoService.createMemo(form1);
    }


}