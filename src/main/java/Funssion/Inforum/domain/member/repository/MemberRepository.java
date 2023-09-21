package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.Member;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.post.memo.dto.request.PasswordUpdateDto;

import java.util.Optional;

// interface 로 정의된 MemberRepository 메서드를 통해, Non-Social 로그인 멤버와, Social 로그인 멤버 두가지 repository를 구현

public interface MemberRepository {
    SaveMemberResponseDto save(NonSocialMember nonSocialMember) ;
    SaveMemberResponseDto save(SocialMember socialMember);
    Optional<NonSocialMember> findNonSocialMemberByEmail(String email);
    Optional<SocialMember> findSocialMemberByEmail(String email);
    Optional<Member> findByName(String Name);

    IsSuccessResponseDto saveSocialMemberNickname(String nickname, Long userId);

    String findEmailByNickname(String nickname);

    IsSuccessResponseDto findAndChangePassword(PasswordUpdateDto passwordUpdateDto, String email);

    String findEmailByAuthCode(String usersTemporaryCode);
}
