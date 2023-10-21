package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.request.PasswordUpdateDto;
import Funssion.Inforum.domain.member.dto.response.GenCodeResponse;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.member.repository.AuthCodeRepository;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class MemberIntegrationTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;


    // 인증 메일 관련 주입
    @Autowired
    MailService mailService;

    @MockBean
    JavaMailSender mailSender;

    @Autowired
    AuthCodeRepository authCodeRepository;

    NonSocialMember nonSocialMember = NonSocialMember.createNonSocialMember(new MemberSaveDto(
            "userstㄷ12",
            LoginType.NON_SOCIAL,
            "ownest11211ㄷ1@gmail.com",
            "a1234567!"
    ));
    @Nested
    @DisplayName("등록된 회원 정보 찾기 - nonSocial")
    class findUserInfo{
        SaveMemberResponseDto saveMemberResponseDto = SaveMemberResponseDto.builder()
                .email(nonSocialMember.getUserEmail())
                .id(1L)
                .loginType(LoginType.NON_SOCIAL)
                .createdDate(LocalDateTime.now())
                .name(nonSocialMember.getUserName())
                .build();
        @Test
        @DisplayName("등록한 닉네임으로 이메일 찾기")
        void findEmailByUsername(){
            memberRepository.save(nonSocialMember);
            assertThat(memberService.findEmailByNickname(nonSocialMember.getUserName())
                            .getEmail())
                    .isEqualTo(memberService.blur(saveMemberResponseDto.getEmail()));

        }
    }

    @Nested
    @DisplayName("로그인")
    class login{
        @Test
        @DisplayName("일반 회원가입한 유저가 같은 이메일로 구글로그인하면 실패해야한다")
        void failToOauthLoginWhenSameEmailWithNonSocialRegistration(){

            SaveMemberResponseDto savedNonsocialMember = memberRepository.save(NonSocialMember.builder()
                    .userPw("1234")
                    .userEmail("test@gmail.com")
                    .loginType(LoginType.NON_SOCIAL)
                    .authId(1L)
                    .userName("test")
                    .introduce("hi")
                    .createdDate(LocalDateTime.now())
                    .tags("Java")
                    .imagePath("path")
                    .build());

            Long savedNonsocialMemberId = savedNonsocialMember.getId();

            // OAuth로그인이므로, OAuthService로직만 검증
            Optional<SocialMember> socialMember = memberRepository.findSocialMemberByEmail(savedNonsocialMember.getEmail());
            assertThat(socialMember.isEmpty()).isEqualTo(true);
        }
    }

    @Nested
    @DisplayName("회원정보 수정")
    class updateUser{

        @BeforeEach
        void saveUser(){
            memberRepository.save(nonSocialMember);
        }
        @Test
        @DisplayName("비밀번호 수정")
        void updatePassword(){
            MimeMessage mimeMessage = mock(MimeMessage.class);
            // 때때로 메소드 내에서 mocking할 줄 알자.
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            doNothing().when(mailSender).send(any(MimeMessage.class));
            GenCodeResponse genCodeResponse = mailService.sendEmailLink(nonSocialMember.getUserEmail());

            PasswordUpdateDto updatePasswordDto = PasswordUpdateDto.builder()
                    .email(nonSocialMember.getUserEmail()).code(genCodeResponse.getCode()).userPw("a7654321!")
                    .build();
            IsSuccessResponseDto isSuccessResponseDto = memberRepository.findAndChangePassword(updatePasswordDto);
            assertThat(isSuccessResponseDto.getMessage()).isEqualTo("비밀번호가 수정되었습니다.");

        }
    }
    /*
     * <requestMemberRegistration>
     * 1.중복아닌거 가정하고 / NonSocial 로그인 타입 요청시 / 저장 객체 반환
     * 2.중복이면 ? / .. / throw duplication
     * 3.중복아니고, social 이면 / .. / throw
     *
     */

}