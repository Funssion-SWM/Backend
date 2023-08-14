package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.service.MemberService;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonSocialMemberRepositoryTest {
    HashMap<LoginType, String> loginTypeMap = new HashMap<>();
    {
        loginTypeMap.put(LoginType.NON_SOCIAL,"nonSocialMemberRepository");
        loginTypeMap.put(LoginType.SOCIAL, "socialMemberRepository");
    }
    @Mock
    Map<String,MemberRepository> repositoryMap;
    @Mock
    MemberRepository nonSocialMemberRepository;

    @Mock
    MemberRepository socialMemberRepository;
    @Mock
    MyRepository myRepository;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        when(repositoryMap.get("nonSocialMemberRepository")).thenReturn(nonSocialMemberRepository);
//        when(repositoryMap.get("socialMemberRepository")).thenReturn(socialMemberRepository);
    }

//    @Test
//    @DisplayName("논소셜 계정 회원가입 성공")
//    void joinWithNonSocialLoginType() throws NoSuchAlgorithmException {
//        Long joinResult = memberService.requestMemberRegistration(joinNonSocialMember);
//        assertEquals(1L, joinResult);
//        verify(nonSocialMemberRepository, times(1)).save(any());
//        verify(myRepository, times(1)).createHistory(1L);
//    }

//    @Test
//    @DisplayName("논소셜 계정 회원가입 실패")
//    void joinWithNonSocialLoginTypeFail() throws NoSuchAlgorithmException {
//        when(nonSocialMemberRepository.save(any())).thenReturn(1L);
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
//        when(nonSocialMemberRepository.save(any())).thenReturn(1L);
//
//        Long joinResult = memberService.join(joinNonSocialMember);
//        assertEquals(1L, joinResult);
//        verify(nonSocialMemberRepository, times(1)).save(any());
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