package Funssion.Inforum.domain.post.comment.service;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.domain.ReComment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.repository.CommentRepository;
import Funssion.Inforum.domain.post.domain.Post;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import Funssion.Inforum.domain.score.Rank;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentIntegrationTest {
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    MyRepository myRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemoRepository memoRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    AnswerRepository answerRepository;
    static Long saveMemberId;
    static final Long MEMO_ID = 1L;
    static final Long QUESTION_ID = 1L;
    static final Long ANSWER_ID = 1L;

    static final Long COMMENT_ID = 1L;
    static final Long RE_COMMENT_ID = 1L;

    static Memo MEMO_BUILDER;
    static Question QUESTION_BUILDER;
    static Answer ANSWER_BUILDER;

    static final CommentUpdateDto commentUpdateDtoOfMemo = new CommentUpdateDto("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"메모 댓글 수정 내용\", \"type\": \"text\"}]}]}");

    static final CommentSaveDto commentSaveDtoOfQuestion = CommentSaveDto.builder()
            .postId(MEMO_ID)
                .postTypeWithComment(PostType.QUESTION)
                .commentText("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문 댓글 내용\", \"type\": \"text\"}]}]}")
                .build();
    static final CommentUpdateDto commentUpdateDtoOfQuestion = new CommentUpdateDto("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문 댓글 수정 내용\", \"type\": \"text\"}]}]}");

    static final CommentSaveDto commentSaveDtoOfAnswer = CommentSaveDto.builder()
            .postId(MEMO_ID)
                .postTypeWithComment(PostType.ANSWER)
                .commentText("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 댓글 내용\", \"type\": \"text\"}]}]}")
                .build();
    static final CommentUpdateDto commentUpdateDtoOfAnswer = new CommentUpdateDto("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 댓글 내용\", \"type\": \"text\"}]}]}");


    static Memo memo;
    static Question question;
    static Answer answer;

    @BeforeEach
    void makeAllPostType() {

    }
    @BeforeEach
    void init(){
        saveUser("user");
        MEMO_BUILDER = createMemo();
        QUESTION_BUILDER = createQuestion(MEMO_BUILDER.getId());
        ANSWER_BUILDER = createAnswer(QUESTION_BUILDER.getId());

        memo = memoRepository.create(MEMO_BUILDER);
        question = questionRepository.createQuestion(QUESTION_BUILDER);
        answer = answerRepository.createAnswer(ANSWER_BUILDER);
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
    private CommentSaveDto createCommentSaveDto(PostType postType){
        Post post = null;

        switch(postType){
            case MEMO :
                post = memo;
                break;
            case QUESTION:
                post = question;
                break;
            case ANSWER:
                post = answer;
                break;
        }

        return CommentSaveDto.builder()
                .postTypeWithComment(postType)
                .postId(post.getId())
                .commentText("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \""+postType+" 댓글 내용\", \"type\": \"text\"}]}]}")
                .build();
    }
    @Nested
    @DisplayName("댓글 작성")
    class createComment {

        @Test
        @DisplayName("메모 게시글에 대한 댓글을 추가할 경우 단일 메모의 댓글 수 확인 및 대댓글 수 확인")
        void isAddedWhenCreateCommentOfMemo() {

            CommentSaveDto commentSaveDtoIn_MEMO = createCommentSaveDto(PostType.MEMO);

            Comment comment = commentService.createComment(commentSaveDtoIn_MEMO, saveMemberId);
            commentService.createComment(commentSaveDtoIn_MEMO, saveMemberId);

            Memo memoAddedComment = memoRepository.findById(memo.getId());
            assertThat(memoAddedComment.getRepliesCount()).isEqualTo(2L);
            assertThat(commentRepository.getCommentsAtPost(PostType.MEMO,comment.getPostId(),saveMemberId)
                    .get(0).getReCommentsNumber()).isEqualTo(0);

            ReCommentSaveDto reCommentSaveDto = new ReCommentSaveDto(comment.getId(), "대댓글 내용입니다.");
            commentService.createReComment(reCommentSaveDto,saveMemberId);
            assertThat(commentRepository.getCommentsAtPost(PostType.MEMO,comment.getPostId(),saveMemberId)
                    .get(0).getReCommentsNumber()).isEqualTo(1);
            assertThat(commentRepository.getCommentsAtPost(PostType.MEMO,comment.getPostId(),saveMemberId)
                    .get(1).getReCommentsNumber()).isEqualTo(0);

        }

        @Test
        @DisplayName("질문 게시글에 대한 댓글을 추가할 경우 단일 질문의 댓글 수 확인 및 대댓글 수 확인")
        void isAddedWhenCreateCommentOfQuestion() {
            CommentSaveDto commentSaveDtoIn_QUESTION = createCommentSaveDto(PostType.QUESTION);

            Comment comment = commentService.createComment(commentSaveDtoIn_QUESTION, saveMemberId);

            Question questionAddedComment = questionRepository.getOneQuestion(comment.getPostId());
            assertThat(questionAddedComment.getRepliesCount()).isEqualTo(1L);

            ReCommentSaveDto reCommentSaveDto = new ReCommentSaveDto(comment.getId(), "대댓글 내용입니다.");
            commentService.createReComment(reCommentSaveDto,saveMemberId);
            assertThat(commentRepository.getCommentsAtPost(PostType.QUESTION,comment.getPostId(),saveMemberId)
                    .get(0).getReCommentsNumber()).isEqualTo(1);

        }
        @Test
        @DisplayName("답변 게시글에 대한 댓글을 추가할 경우 단일 답변의 댓글 수 확인 및 대댓글 수 확인")
        void isAddedWhenCreateCommentOfAnswer(){
            CommentSaveDto commentSaveDtoIn_ANSWER = createCommentSaveDto(PostType.ANSWER);
            Comment comment = commentService.createComment(commentSaveDtoIn_ANSWER, saveMemberId);

            Answer answerAddedComment = answerRepository.getAnswerById(commentSaveDtoIn_ANSWER.getPostId());
            assertThat(answerAddedComment.getRepliesCount()).isEqualTo(1L);

            ReCommentSaveDto reCommentSaveDto = new ReCommentSaveDto(comment.getId(), "대댓글 내용입니다.");
            commentService.createReComment(reCommentSaveDto,saveMemberId);
            assertThat(commentRepository.getCommentsAtPost(PostType.ANSWER,comment.getPostId(),saveMemberId)
                    .get(0).getReCommentsNumber()).isEqualTo(1);

        }


    }

    @Nested
    @DisplayName("댓글 수정")
    class updateComment{
        @Test
        @DisplayName("메모의 댓글 수정")
        void updateCommentOfMemo(){
            CommentSaveDto commentSaveDtoIn_MEMO = createCommentSaveDto(PostType.MEMO);

            Comment comment = commentService.createComment(commentSaveDtoIn_MEMO, saveMemberId);
            commentService.updateComment(commentUpdateDtoOfMemo, comment.getId());

            List<CommentListDto> commentsAtPost = commentService.getCommentsAtPost(comment.getPostTypeWithComment(), comment.getPostId(), saveMemberId);
            assertThat(commentsAtPost).hasSize(1);
            assertThat(commentsAtPost.get(0).getCommentText()).isNotEqualTo(commentSaveDtoIn_MEMO.getCommentText());
            assertThat(commentsAtPost.get(0).getCommentText()).isEqualTo(commentUpdateDtoOfMemo.getCommentText());
        }

        @Test
        @DisplayName("답변의 댓글 수정")
        void updateCommentOfAnswer(){
            CommentSaveDto commentSaveDtoIn_ANSWER = createCommentSaveDto(PostType.ANSWER);

            Comment comment = commentService.createComment(commentSaveDtoIn_ANSWER, saveMemberId);
            commentService.updateComment(commentUpdateDtoOfAnswer, comment.getId());

            List<CommentListDto> commentsAtPost = commentService.getCommentsAtPost(comment.getPostTypeWithComment(), comment.getPostId(), saveMemberId);
            assertThat(commentsAtPost).hasSize(1);
            assertThat(commentsAtPost.get(0).getCommentText()).isNotEqualTo(commentSaveDtoIn_ANSWER.getCommentText());
            assertThat(commentsAtPost.get(0).getCommentText()).isEqualTo(commentUpdateDtoOfAnswer.getCommentText());
        }


        @Test
        @DisplayName("질문의 댓글 수정")
        void updateCommentOfQuestion(){
            CommentSaveDto commentSaveDtoIn_QUESTION = createCommentSaveDto(PostType.QUESTION);

            Comment comment = commentService.createComment(commentSaveDtoIn_QUESTION, saveMemberId);
            commentService.updateComment(commentUpdateDtoOfQuestion, comment.getId());

            List<CommentListDto> commentsAtPost = commentService.getCommentsAtPost(comment.getPostTypeWithComment(), comment.getPostId(), saveMemberId);
            assertThat(commentsAtPost).hasSize(1);
            assertThat(commentsAtPost.get(0).getCommentText()).isNotEqualTo(commentSaveDtoIn_QUESTION.getCommentText());
            assertThat(commentsAtPost.get(0).getCommentText()).isEqualTo(commentUpdateDtoOfQuestion.getCommentText());
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class deleteComment{
        @Test
        @DisplayName("질문 게시글 댓글 삭제")
        void deleteCommentOfQuestion(){
            CommentSaveDto commentSaveDtoIn_QUESTION = createCommentSaveDto(PostType.QUESTION);

            Comment comment = commentService.createComment(commentSaveDtoIn_QUESTION, saveMemberId);
            List<CommentListDto> commentsAtPostBeforeDelete = commentService.getCommentsAtPost(comment.getPostTypeWithComment(), comment.getPostId(), saveMemberId);
            assertThat(commentsAtPostBeforeDelete).hasSize(1);

            commentService.deleteComment(comment.getId());
            List<CommentListDto> commentsAtPostAfterDelete = commentService.getCommentsAtPost(comment.getPostTypeWithComment(), comment.getPostId(), saveMemberId);
            assertThat(commentsAtPostAfterDelete).hasSize(0);
        }

        @Test
        @DisplayName("메모 게시글의 대댓글이 달리지 않은 댓글 삭제")
        void deleteCommentOfMemo(){
            CommentSaveDto commentSaveDtoIn_MEMO = createCommentSaveDto(PostType.MEMO);

            Comment comment = commentService.createComment(commentSaveDtoIn_MEMO, saveMemberId);
            List<CommentListDto> commentsAtPostBeforeDelete = commentService.getCommentsAtPost(comment.getPostTypeWithComment(), comment.getPostId(), saveMemberId);
            assertThat(commentsAtPostBeforeDelete).hasSize(1);

            commentService.deleteComment(comment.getId());
            List<CommentListDto> commentsAtPostAfterDelete = commentService.getCommentsAtPost(comment.getPostTypeWithComment(), comment.getPostId(), saveMemberId);
            assertThat(commentsAtPostAfterDelete).hasSize(0);
        }
        @Test
        @DisplayName("질문 게시글 댓글의 대댓글이 달렸을 경우 댓글을 삭제할 경우, 완전삭제는 되지 않는다.")
        void deleteCommentWhichHasRecomments(){

            Long recommentAuthorId = makeRecommentUser();
            CommentSaveDto commentSaveDtoIn_QUESTION = createCommentSaveDto(PostType.QUESTION);

            Comment comment = commentService.createComment(commentSaveDtoIn_QUESTION, saveMemberId);
            List<CommentListDto> commentsAtPostBeforeDelete = commentService.getCommentsAtPost(comment.getPostTypeWithComment(), comment.getPostId(), saveMemberId);
            assertThat(commentsAtPostBeforeDelete).hasSize(1);

            ReCommentSaveDto reCommentSaveDto = new ReCommentSaveDto(comment.getId(), "대댓글 내용입니다.");
            commentService.createReComment(reCommentSaveDto,recommentAuthorId);

            commentService.deleteComment(comment.getId());
            List<CommentListDto> commentsAtPostAfterDelete = commentService.getCommentsAtPost(comment.getPostTypeWithComment(), comment.getPostId(), saveMemberId);
            assertThat(commentsAtPostAfterDelete.get(0).getCommentText()).isEqualTo("삭제된 댓글입니다.");
        }
        @Test
        @DisplayName("대댓글이 달리고 댓글이 삭제된 후에, 대댓글이 지워지고 대댓글 수가 0이되면 댓글도 삭제된다.")
        void deleteCommentWhenUserDeleteCommentAndRecommentsDeleted(){
            Long recommentAuthorId = makeRecommentUser();
            CommentSaveDto commentSaveDtoIn_QUESTION = createCommentSaveDto(PostType.QUESTION);

            Comment comment = commentService.createComment(commentSaveDtoIn_QUESTION, saveMemberId);
            List<CommentListDto> commentsAtPostBeforeDelete = commentService.getCommentsAtPost(comment.getPostTypeWithComment(), comment.getPostId(), saveMemberId);
            assertThat(commentsAtPostBeforeDelete).hasSize(1);

            ReCommentSaveDto reCommentSaveDto = new ReCommentSaveDto(comment.getId(), "대댓글 내용입니다.");
            ReComment reComment = commentService.createReComment(reCommentSaveDto, recommentAuthorId);
            commentService.deleteComment(comment.getId());
            List<CommentListDto> commentsAtPostAfterDeleteComment = commentService.getCommentsAtPost(comment.getPostTypeWithComment(), comment.getPostId(), saveMemberId);
            assertThat(commentsAtPostAfterDeleteComment.get(0).getCommentText()).isEqualTo("삭제된 댓글입니다.");

            commentService.deleteReComment(reComment.getId());
            List<CommentListDto> commentsAtPostAfterDeleteRecomment = commentService.getCommentsAtPost(comment.getPostTypeWithComment(), comment.getPostId(), saveMemberId);
            assertThat(commentsAtPostAfterDeleteRecomment).hasSize(0);
        }

        private Long makeRecommentUser() {
            MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                    .userName("tester")
                    .loginType(LoginType.NON_SOCIAL)
                    .userPw("a1234567!")
                    .userEmail("tester@gmail.com")
                    .build();
            MemberProfileEntity memberProfileEntity = MemberProfileEntity.builder()
                    .nickname("tester")
                    .profileImageFilePath("taehoon-image")
                    .introduce("introduce of taehoon")
                    .userTags(List.of("tag1", "tag2"))
                    .build();

            SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(NonSocialMember.createNonSocialMember(memberSaveDto));
            Long recommentAuthorId = saveMemberResponseDto.getId();
            myRepository.createProfile(recommentAuthorId, memberProfileEntity);
            return recommentAuthorId;
        }

        @Test
        @DisplayName("답변 게시글 댓글 삭제")
        void deleteCommentOfAnswer(){
            CommentSaveDto commentSaveDtoIn_ANSWER = createCommentSaveDto(PostType.ANSWER);

            Comment comment = commentService.createComment(commentSaveDtoIn_ANSWER, saveMemberId);
            List<CommentListDto> commentsAtPostBeforeDelete = commentService.getCommentsAtPost(comment.getPostTypeWithComment(), comment.getPostId(), saveMemberId);
            assertThat(commentsAtPostBeforeDelete).hasSize(1);

            commentService.deleteComment(comment.getId());
            List<CommentListDto> commentsAtPostAfterDelete = commentService.getCommentsAtPost(comment.getPostTypeWithComment(), comment.getPostId(), saveMemberId);
            assertThat(commentsAtPostAfterDelete).hasSize(0);

        }
    }





    private static List<String> createTagList(){
        String[] testTagsStringList = {
                "Backend","Java","Spring"
        };
        return new ArrayList<>(Arrays.asList(testTagsStringList));
    }

    private static Answer createAnswer(Long questionId) {
        return Answer.builder()
                .id(ANSWER_ID)
                .authorId(saveMemberId)
                .authorName("답변작성자이름")
                .authorImagePath("답변작성자이미지경로")
                .isSelected(false)
                .likes(0L)
                .repliesCount(0L)
                .rank(Rank.BRONZE_5.toString())
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문입니다.\", \"type\": \"text\"}]}]}")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .questionId(questionId)
                .build();
    }
    private static Question createQuestion(Long memoId){
        return Question.builder()
                .id(QUESTION_ID)
                .authorId(saveMemberId)
                .authorName("작성자이름")
                .authorImagePath("이미지경로")
                .memoId(memoId)
                .answersCount(0L)
                .rank(Rank.BRONZE_5.toString())
                .isSolved(false)
                .likes(0L)
                .title("질문 제목")
                .description("질문 요약 정보")
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문입니다.\", \"type\": \"text\"}]}]}")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .tags(List.of("tag1","tag2"))
                .build();
    }

    private static Memo createMemo(){
        MemoSaveDto form1 = new MemoSaveDto("JPA란?", "JPA일까?","{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"안녕하세요!!\", \"type\": \"text\"}]}]}", "yellow",createTagList(),null,false );

        return Memo.builder()
                .id(MEMO_ID)
                .title(form1.getMemoTitle())
                .text(form1.getMemoText())
                .description(form1.getMemoDescription())
                .color(form1.getMemoColor())
                .authorId(saveMemberId)
                .authorName("Jinu")
                .rank(Rank.BRONZE_5.toString())
                .authorImagePath("http:jinu")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .isTemporary(false)
                .likes(0L)
                .memoTags(List.of("JPA", "Java"))
                .build();
    }
}