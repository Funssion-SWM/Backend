package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.s3.S3Repository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    MyRepository myRepository;
    @Mock
    MemoRepository memoRepository;
    @Mock
    S3Repository s3Repository;
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    MemberService memberService;
    @Nested
    @DisplayName("등록된 회원 정보 찾기 - nonSocial")
    class findUserInfo{
        NonSocialMember nonSocialMember = NonSocialMember.createNonSocialMember(new MemberSaveDto(
                "username_test",
                LoginType.NON_SOCIAL,
                "test@gmail.com",
                "a1234567!"
        ));
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
            when(memberRepository.findEmailByNickname(nonSocialMember.getUserName())).thenReturn(saveMemberResponseDto.getEmail());
            Assertions.assertThat(memberService.findEmailByNickname(nonSocialMember.getUserName())
                            .getEmail())
                    .isEqualTo(memberService.blur(saveMemberResponseDto.getEmail()));

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