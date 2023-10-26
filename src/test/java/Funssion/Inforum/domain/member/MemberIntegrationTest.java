package Funssion.Inforum.domain.member;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.Role;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.EmailRequestDto;
import Funssion.Inforum.domain.member.dto.request.EmployerSaveDto;
import Funssion.Inforum.domain.member.dto.request.PasswordUpdateDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.member.repository.AuthCodeRepository;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.member.service.AuthService;
import Funssion.Inforum.domain.member.service.MemberService;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.domain.ReComment;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;
import Funssion.Inforum.domain.post.comment.repository.CommentRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import Funssion.Inforum.domain.score.Rank;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@Transactional
@AutoConfigureMockMvc
public class MemberIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    AuthCodeRepository authCodeRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    MyRepository myRepository;
    @Autowired
    AuthService authService;

    @Autowired
    MemoRepository memoRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    CommentRepository commentRepository;

    Long savedNonsocialMemberId;
    Long savedSocialMemberId;
    Long savedEmployerId;
    String savedNonsocialMemberEmail = "nonsocial@gmail.com";
    String savedSocialMemberEmail = "social@gmail.com";
    String savedMemberName = "jinu";
    String savedMemberImagePath = "https://image";
    SaveMemberResponseDto savedNonsocialMember;
    SaveMemberResponseDto savedSocialMember;
    SaveMemberResponseDto savedEmployer;
    Memo savedMemo;
    Question savedQuestion;
    Answer savedAnswer;
    Comment savedComment;
    ReCommentListDto savedReComment;

    NonSocialMember saveMember;

    @BeforeEach
    void init() {
        saveMember = NonSocialMember.builder()
                .userPw("1234")
                .userEmail(savedNonsocialMemberEmail)
                .loginType(LoginType.NON_SOCIAL)
                .authId(1L)
                .userName(savedMemberName)
                .introduce("hi")
                .createdDate(LocalDateTime.now())
                .tags("Java")
                .imagePath(savedMemberImagePath)
                .build();
        savedNonsocialMember = memberRepository.save(saveMember);

        savedNonsocialMemberId = savedNonsocialMember.getId();

        savedSocialMember = memberRepository.save(SocialMember.builder()
                .userEmail(savedSocialMemberEmail)
                .loginType(LoginType.SOCIAL)
                .userName(savedMemberName)
                .introduce("hi")
                .createdDate(LocalDateTime.now())
                .tags("Java")
                .imagePath(savedMemberImagePath)
                .build());

        savedSocialMemberId = savedSocialMember.getId();

        EmployerSaveDto employerSaveDto = EmployerSaveDto.builder()
                .userEmail("employer@gmail.com")
                .userPw("a1234567!")
                .userName("employer")
                .loginType(LoginType.NON_SOCIAL)
                .companyName("inflearn")
                .build();
        savedEmployer = memberRepository.save(employerSaveDto);
        savedEmployerId = savedEmployer.getId();
        memberRepository.authorizeEmployer(savedEmployerId);


        savedMemo = memoRepository.create(Memo.builder()
                .title("JPA")
                .text("{\"content\" : \"JPA is JPA\"}")
                .description("JPA is ...")
                .color("yellow")
                .authorId(savedNonsocialMemberId)
                .authorName(savedMemberName)
                .rank(Rank.BRONZE_5.toString())
                .authorImagePath(savedMemberImagePath)
                .createdDate(LocalDateTime.now().minusDays(1))
                .likes(0L)
                .isTemporary(false)
                .memoTags(List.of("Java", "JPA"))
                .build());

        savedQuestion = questionRepository.createQuestion(Question.builder()
                .authorId(savedNonsocialMemberId)
                .authorName(savedMemberName)
                .authorImagePath(savedMemberImagePath)
                .title("JPA")
                .text("{\"content\" : \"JPA is what?\"}")
                .rank(Rank.BRONZE_5.toString())
                .tags(List.of("JAVA"))
                .memoId(savedMemo.getId())
                .description("JPA ...")
                .build());

        savedAnswer = answerRepository.createAnswer(Answer.builder()
                .questionId(savedQuestion.getId())
                .authorId(savedNonsocialMemberId)
                .authorName(savedMemberName)
                .rank(Rank.BRONZE_5.toString())
                .authorImagePath(savedMemberImagePath)
                .text("{\"content\" : \"JPA is good.\"}")
                .build());

        savedComment = commentRepository.createComment(Comment.builder()
                .authorId(savedNonsocialMemberId)
                .authorName(savedMemberName)
                .authorImagePath(savedMemberImagePath)
                .postTypeWithComment(PostType.MEMO)
                .rank(Rank.BRONZE_5.toString())
                .postId(savedMemo.getId())
                .commentText("wow")
                .build());

        commentRepository.createReComment(ReComment.builder()
                .authorId(savedNonsocialMemberId)
                .authorName(savedMemberName)
                .authorImagePath(savedMemberImagePath)
                .rank(Rank.BRONZE_5.toString())
                .parentCommentId(savedComment.getId())
                .commentText("wow")
                .createdDate(LocalDateTime.now())
                .build());

        savedReComment = commentRepository.getReCommentsAtComment(savedComment.getId(), savedNonsocialMemberId).get(0);
    }

    @Nested
    @DisplayName("회원가입")
    class SignUp{
        @Test
        @DisplayName("채용자 회원가입")
        void employerSignUp() throws Exception {
            ObjectMapper objectMapper = new ObjectMapper();
            EmployerSaveDto employerSaveJSONDto = EmployerSaveDto.builder()
                    .userEmail("employer_join@gmail.com")
                    .userPw("a1234567!")
                    .userName("test")
                    .loginType(LoginType.NON_SOCIAL)
                    .companyName("inflearn")
                    .build();
            String employerSaveStringDto = objectMapper.writeValueAsString(employerSaveJSONDto);
            mvc.perform(post("/users/employer")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(employerSaveStringDto));
            assertThat(memberRepository.findNonSocialMemberByEmail(employerSaveJSONDto.getUserEmail())
                    .get().getRole()).isEqualTo(Role.TEMP_EMPLOYER.toString());
        }
    }

    @Nested
    @DisplayName("로그인")
    class signIn{
        @Test
        @DisplayName("인증받은 채용자가 로그인 되었는지 확인")
        void employerLoginSucceed() throws Exception {
            EmployerSaveDto employerSaveJSONDto = EmployerSaveDto.builder()
                    .userEmail("employer_auth@gmail.com")
                    .userPw("a1234567!")
                    .userName("employer")
                    .loginType(LoginType.NON_SOCIAL)
                    .companyName("inflearn")
                    .build();
            SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(employerSaveJSONDto);
            memberRepository.authorizeEmployer(saveMemberResponseDto.getId());

            mvc.perform(post("/users/login")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .param("username",employerSaveJSONDto.getUserEmail())
                            .param("password",employerSaveJSONDto.getUserPw()))
                    .andExpect(status().isOk());

        }
        @Test
        @DisplayName("인증받은 채용자의 role 확인")
        void authorizedEmployerRoleCheck() throws Exception {
            EmployerSaveDto employerSaveJSONDto = EmployerSaveDto.builder()
                    .userEmail("employer12@gmail.com")
                    .userPw("a1234567!")
                    .userName("employer")
                    .loginType(LoginType.NON_SOCIAL)
                    .companyName("inflearn")
                    .build();
            SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(employerSaveJSONDto);
            memberRepository.authorizeEmployer(saveMemberResponseDto.getId());

            mvc.perform(post("/users/login")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .param("username",employerSaveJSONDto.getUserEmail())
                            .param("password",employerSaveJSONDto.getUserPw()))
                    .andExpect(status().isOk());
            UserDetails userDetails = authService.loadUserByUsername(employerSaveJSONDto.getUserEmail());
            assertThat(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                    .contains(Role.EMPLOYER.getRoles().split(",")[0]).hasSize(2);


        }

        @Test
        @DisplayName("인증받은 employer 유저의 경우 해당 유저만 접근 가능한 페이지인지 확인")
        void testAccessSucceedByEmployer() throws Exception {
            UserDetails userDetails = authService.loadUserByUsername(savedEmployer.getEmail());
            User principal = new User(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            mvc.perform(get("/users/employer"))
                    .andExpect(status().isOk());
        }
        @Test
        @DisplayName("인증받지 않은 employer 유저의 경우 employer 페이지에 접속 불가능한지 확인")
        void testAccessFailByEmployer() throws Exception {
            EmployerSaveDto employerSaveJSONDto = EmployerSaveDto.builder()
                    .userEmail("employernotauth@gmail.com")
                    .userPw("a1234567!")
                    .userName("employer")
                    .loginType(LoginType.NON_SOCIAL)
                    .companyName("inflearn")
                    .build();
            SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(employerSaveJSONDto);
            assertThat(saveMemberResponseDto.getRole().equals("TEMP_EMPLOYER"));
            UserDetails userDetails = authService.loadUserByUsername(saveMemberResponseDto.getEmail());
            User principal = new User(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            mvc.perform(get("/users/employer"))
                    .andExpect(status().isForbidden());
        }
    }
    @Test
    @DisplayName("이메일로 비밀번호를 찾을 때, 구글로그인으로 등록된 계정인 경우 해당 사실을 알려주어야 함")
    void findPasswordWhenGoogleLoginMustNotify() throws Exception {
        EmailRequestDto emailRequestDto = new EmailRequestDto(savedSocialMember.getEmail());
        ObjectMapper objectMapper = new ObjectMapper();
        String emailRequest = objectMapper.writeValueAsString(emailRequestDto);

        MvcResult result = mvc.perform(post("/users/authenticate-email/find")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailRequest))
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = JsonPath.read(contentAsString, "$.message");
        Object success = JsonPath.read(contentAsString, "$.isSuccess");
        assertThat(success).isEqualTo(false);
        assertThat(message).isEqualTo("구글 로그인으로 등록된 계정입니다.");
    }

    @Test
    @DisplayName("이메일로 비밀번호를 찾을 때, 등록된 이메일이 아니면 해당 사실을 알려주어야 함.")
    void findPasswordWhenRequestIsNotInDB() throws Exception {
        EmailRequestDto emailRequestDto = new EmailRequestDto("aaaaaaa@gmail.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String emailRequest = objectMapper.writeValueAsString(emailRequestDto);

        MvcResult result = mvc.perform(post("/users/authenticate-email/find")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailRequest))
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = JsonPath.read(contentAsString, "$.message");
        assertThat(message).isEqualTo("해당 이메일로 가입된 회원 정보가 없습니다.");
        Object success = JsonPath.read(contentAsString, "$.isSuccess");
        assertThat(success).isEqualTo(false);
    }

    @Nested
    @DisplayName("회원 정보 수정")
    class editMember{

        @Test
        @DisplayName("비밀번호를 수정할 때, 인증 코드가 중복 되어도(1/10^6확률), email과 함께 검증하기 때문에, 보안적인 이슈가 없습니다")
        void authenticationSuccess() throws Exception {
            NonSocialMember saveAnotherNonSocialMember = NonSocialMember.builder()
                    .userPw("a1234561!")
                    .userEmail("changed@gmail.com")
                    .loginType(LoginType.NON_SOCIAL)
                    .authId(2L)
                    .userName("tester")
                    .introduce("hi")
                    .createdDate(LocalDateTime.now())
                    .tags("Java")
                    .imagePath("testerimagepath")
                    .build();
            SaveMemberResponseDto saveAnotherUser = memberRepository.save(saveAnotherNonSocialMember);

            String code = "A12345";
            authCodeRepository.insertEmailCodeForVerification(LocalDateTime.now().plusMinutes(5),saveAnotherNonSocialMember.getUserEmail(),code);
            authCodeRepository.insertEmailCodeForVerification(LocalDateTime.now().plusMinutes(5),saveMember.getUserEmail(),code);

            PasswordUpdateDto passwordUpdateDto1 = PasswordUpdateDto.builder()
                    .email(saveAnotherNonSocialMember.getUserEmail())
                    .userPw("change123")
                    .code(code)
                    .build();

            PasswordUpdateDto passwordUpdateDto2 = PasswordUpdateDto.builder()
                    .email(saveMember.getUserEmail())
                    .userPw("change321")
                    .code(code)
                    .build();

            memberService.findAndChangePassword(passwordUpdateDto1);
            memberService.findAndChangePassword(passwordUpdateDto2);

            mvc.perform(post("/users/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("username",saveAnotherNonSocialMember.getUserEmail())
                    .param("password",passwordUpdateDto1.getUserPw()))
                    .andExpect(status().isOk());
            mvc.perform(post("/users/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("username",saveAnotherNonSocialMember.getUserEmail())
                    .param("password",saveMember.getUserPw()))
                    .andExpect(status().isUnauthorized());
        }

    }

    @Nested
    @DisplayName("회원 탈퇴하기")
    class withdrawMember {

        @Test
        @DisplayName("로그인 없이 회원 탈퇴 시도")
        void withdrawWithOutLogin() throws Exception {

            mvc.perform(post("/users/withdraw")
                            .with(user(SecurityContextUtils.ANONYMOUS_USER_NAME)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("자체 회원가입 유저 정상 회원 탍퇴")
        void success() throws Exception {

            mvc.perform(post("/users/withdraw")
                            .with(user(savedNonsocialMemberId.toString())))
                    .andExpect(status().isOk());

            String deletedUserName = memberRepository.findNameById(savedNonsocialMemberId);
            MemberProfileEntity deletedUserProfile = myRepository.findProfileByUserId(savedNonsocialMemberId);
            Memo memoByDeletedUser = memoRepository.findById(savedMemo.getId());
            Question questionByDeletedUser = questionRepository.getOneQuestion(savedQuestion.getId());
            Answer answerByDeletedUser = answerRepository.getAnswerById(savedAnswer.getId());
            CommentListDto commentByDeletedUser = commentRepository.getCommentsAtPost(PostType.MEMO, savedMemo.getId(), deletedUserProfile.getUserId()).get(0);
            ReCommentListDto reCommentByDeletedUser = commentRepository.getReCommentsAtComment(commentByDeletedUser.getId(), deletedUserProfile.getUserId()).get(0);

            assertThat(deletedUserName).hasSize(15);

            assertThat(deletedUserProfile.getNickname()).isEqualTo(deletedUserName);
            assertThat(deletedUserProfile.getProfileImageFilePath()).isNull();
            assertThat(deletedUserProfile.getIntroduce()).isEqualTo("탈퇴한 유저입니다.");

            assertThat(memberRepository.findByName(deletedUserName)).isNotPresent();
            assertThat(memberRepository.findNonSocialMemberByEmail(savedNonsocialMemberEmail)).isNotPresent();
            assertThatThrownBy(() -> memberRepository.findEmailByNickname(deletedUserName))
                    .isInstanceOf(NotFoundException.class);

            assertThat(memoByDeletedUser.getAuthorImagePath()).isNull();
            assertThat(memoByDeletedUser.getAuthorName()).isEqualTo(deletedUserName);

            assertThat(questionByDeletedUser.getAuthorImagePath()).isNull();
            assertThat(questionByDeletedUser.getAuthorName()).isEqualTo(deletedUserName);

            assertThat(answerByDeletedUser.getAuthorImagePath()).isNull();
            assertThat(answerByDeletedUser.getAuthorName()).isEqualTo(deletedUserName);

            assertThat(commentByDeletedUser.getAuthorImagePath()).isNull();
            assertThat(commentByDeletedUser.getAuthorName()).isEqualTo(deletedUserName);

            assertThat(reCommentByDeletedUser.getAuthorImagePath()).isNull();
            assertThat(reCommentByDeletedUser.getAuthorName()).isEqualTo(deletedUserName);
        }
        
        @Test
        @DisplayName("구글 로그인 회원가입 회원 정상 탈퇴") 
        void successWithGoogleAuth() throws Exception {

            mvc.perform(post("/users/withdraw")
                            .with(user(savedSocialMemberId.toString())))
                    .andExpect(status().isOk());

            String deletedUserName = memberRepository.findNameById(savedSocialMemberId);
            MemberProfileEntity deletedUserProfile = myRepository.findProfileByUserId(savedSocialMemberId);

            assertThat(deletedUserName).hasSize(15);

            assertThat(deletedUserProfile.getNickname()).isEqualTo(deletedUserName);
            assertThat(deletedUserProfile.getProfileImageFilePath()).isNull();
            assertThat(deletedUserProfile.getIntroduce()).isEqualTo("탈퇴한 유저입니다.");

            assertThat(memberRepository.findByName(deletedUserName)).isNotPresent();
            assertThat(memberRepository.findSocialMemberByEmail(savedSocialMemberEmail)).isNotPresent();
            assertThatThrownBy(() -> memberRepository.findEmailByNickname(deletedUserName))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
