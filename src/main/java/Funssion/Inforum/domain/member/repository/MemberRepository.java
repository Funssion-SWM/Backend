package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.domain.member.dto.request.EmployerSaveDto;
import Funssion.Inforum.domain.member.dto.request.PasswordUpdateDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.Member;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.entity.SocialMember;

import java.util.Optional;

// interface 로 정의된 MemberRepository 메서드를 통해, Non-Social 로그인 멤버와, Social 로그인 멤버 두가지 repository를 구현

public interface MemberRepository {
    SaveMemberResponseDto save(NonSocialMember nonSocialMember) ;
    SaveMemberResponseDto save(SocialMember socialMember);
    SaveMemberResponseDto save(EmployerSaveDto employerSaveDto);
    boolean authorizeEmployer(Long tempEmployerId);
    Optional<NonSocialMember> findNonSocialMemberByEmail(String email);
    Optional<SocialMember> findSocialMemberByEmail(String email);
    Optional<Member> findByName(String Name);
    String findNameById(Long id);

    void updateFollowCnt(Long id, Sign sign);
    void updateFollowerCnt(Long id, Sign sign);

    IsSuccessResponseDto saveSocialMemberNickname(String nickname, Long userId);

    String findEmailByNickname(String nickname);

    IsSuccessResponseDto findAndChangePassword(PasswordUpdateDto passwordUpdateDto);

    String findEmailByAuthCode(PasswordUpdateDto passwordUpdateDto);

    void deleteUser(Long userId);

    Long getDailyScore(Long userId);

    String getCompanyName(Long userId);
}
