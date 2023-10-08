package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    DataSource dataSource;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepositoryImpl memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.save(
                NonSocialMember.createNonSocialMember(
                        new MemberSaveDto("test", LoginType.NON_SOCIAL, "test12@gmail.com", "a1234567!"))
                );
    }
    @Test
    @DisplayName("존재하는 회원 이름으로 아이디 앞 세글자만 보이는 이메일 반환")
    void findEmailBlurredByNickname(){
        Assertions.assertThat(memberRepository.findEmailByNickname("test")).isEqualTo("test12@gmail.com");
    }


//    @Test
//    @DisplayName("논소셜 계정 회원가입 성공")
//    void joinWithNonSocialLoginType() throws NoSuchAlgorithmException {
//        Long joinResult = memberService.requestMemberRegistration(joinNonSocialMember);
//        assertEquals(1L, joinResult);
//        verify(memberRepository, times(1)).save(any());
//        verify(myRepository, times(1)).createHistory(1L);
//    }

//    @Test
//    @DisplayName("논소셜 계정 회원가입 실패")
//    void joinWithNonSocialLoginTypeFail() throws NoSuchAlgorithmException {
//        when(memberRepository.save(any())).thenReturn(1L);
//
//        memberService.join(joinNonSocialMemberFail);
//
//        IllegalStateException e =assertThrows(IllegalStateException.class, ()->{
//            memberService.join(joinNonSocialMemberFail);
//        });
//        Assertions.assertThat(e.getMessage()).isEqualTo("이미 가입된 회원 이메일입니다.");
//    }

//    @Test
//    @DisplayName("중복 이메일 회원가입 체크")
//    void duplicationEmail() throws NoSuchAlgorithmException {
//        // NON_SOCIAL 로그인 타입에 대한 모의 동작 설정
//        when(memberRepository.save(any())).thenReturn(1L);
//
//        Long joinResult = memberService.join(joinNonSocialMember);
//        assertEquals(1L, joinResult);
//        verify(memberRepository, times(1)).save(any());
//        verify(myRepository, times(1)).createHistory(1L);
//    }


    private MemberSaveDto joinNonSocialMember = MemberSaveDto.builder()
            .userName("nickNameN")
            .loginType(LoginType.NON_SOCIAL)
            .userEmail("testN@gmail.com")
            .userPw("password")
            .build();
    private MemberSaveDto joinNonSocialMemberFail = MemberSaveDto.builder()
            .userName("nickNameN_1")
            .loginType(LoginType.NON_SOCIAL)
            .userEmail("testN@gmail.com")
            .userPw("password")
            .build();
    private MemberSaveDto joinSocialMember = MemberSaveDto.builder()
            .userName("nickNameS")
            .loginType(LoginType.SOCIAL)
            .userEmail("testS@gmail.com")
            .build();
}