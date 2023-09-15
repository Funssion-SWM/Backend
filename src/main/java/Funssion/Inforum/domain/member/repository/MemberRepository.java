package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.Member;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.entity.SocialMember;

import java.util.Optional;

// interface 로 정의된 MemberRepository 메서드를 통해, Non-Social 로그인 멤버와, Social 로그인 멤버 두가지 repository를 구현

public interface MemberRepository {
    SaveMemberResponseDto save(NonSocialMember nonSocialMember) ; //Dto_Member가 아니라 상속받은 NonSocialDtoMember를 참조해야함.....
    //jdbc template 이용 예정이므로 jdbc template insert 방법을 익힐것. https://www.springcloud.io/post/2022-06/jdbctemplate-id/#gsc.tab=0
    SaveMemberResponseDto save(SocialMember socialMember);
    Optional<NonSocialMember> findNonSocialMemberByEmail(String email);
    Optional<SocialMember> findSocialMemberByEmail(String email);
    Optional<Member> findByName(String Name);

    IsSuccessResponseDto saveSocialMemberNickname(String nickname, Long userId);

    String findEmailByNickname(String nickname);
}
