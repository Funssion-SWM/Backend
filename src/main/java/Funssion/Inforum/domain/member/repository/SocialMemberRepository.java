package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.entity.SocialMember;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SocialMemberRepository implements MemberRepository<SocialMember> {
    @Override
    public SocialMember save(SocialMember member) {
        return null;
    }

    @Override
    public Optional<SocialMember> findByEmail(String Email) {
        return Optional.empty();
    }

    @Override
    public Optional<SocialMember> findByName(String Name) {
        return Optional.empty();
    }
    /* 설정 필요 */
}
