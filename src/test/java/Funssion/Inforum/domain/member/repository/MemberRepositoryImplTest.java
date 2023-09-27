package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryImplTest {

    @Autowired
    MemberRepository memberRepository;
    NonSocialMember nonSocialMember = NonSocialMember.builder()
            .userPw("1234")
            .userEmail("nonsocial@gmail.com")
            .loginType(LoginType.NON_SOCIAL)
            .authId(1L)
            .userName("jinu")
            .introduce("hi")
            .createdDate(LocalDateTime.now())
            .tags("Java")
            .imagePath("https://image")
            .build();

    SaveMemberResponseDto saved;
    @BeforeEach
    void init() {
         saved = memberRepository.save(nonSocialMember);
    }


    @Test
    void updateFollowCnt() {
        memberRepository.updateFollowCnt(saved.getId(), Sign.PLUS);
        NonSocialMember updatedFollowCntPLUS = memberRepository.findNonSocialMemberByEmail(saved.getEmail()).get();

        assertThat(updatedFollowCntPLUS.getFollowCnt()).isEqualTo(1L);

        memberRepository.updateFollowCnt(saved.getId(), Sign.MINUS);
        NonSocialMember updatedFollowCntMINUS = memberRepository.findNonSocialMemberByEmail(saved.getEmail()).get();

        assertThat(updatedFollowCntMINUS.getFollowCnt()).isEqualTo(0L);

        assertThatThrownBy(() -> memberRepository.updateFollowCnt(saved.getId(), Sign.MINUS))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void updateFollowerCnt() {
        memberRepository.updateFollowerCnt(saved.getId(), Sign.PLUS);
        NonSocialMember updatedFollowerCntPLUS = memberRepository.findNonSocialMemberByEmail(saved.getEmail()).get();

        assertThat(updatedFollowerCntPLUS.getFollowCnt()).isEqualTo(1L);

        memberRepository.updateFollowerCnt(saved.getId(), Sign.MINUS);
        NonSocialMember updatedFollowerCntMINUS = memberRepository.findNonSocialMemberByEmail(saved.getEmail()).get();

        assertThat(updatedFollowerCntMINUS.getFollowCnt()).isEqualTo(0L);

        assertThatThrownBy(() -> memberRepository.updateFollowerCnt(saved.getId(), Sign.MINUS))
                .isInstanceOf(BadRequestException.class);
    }
}