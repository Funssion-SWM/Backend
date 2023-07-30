package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.entity.Member;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

// interface 로 정의된 MemberRepository 메서드를 통해, Non-Social 로그인 멤버와, Social 로그인 멤버 두가지 repository를 구현

public interface MemberRepository<T extends Member> {
    Long save(T member) throws NoSuchAlgorithmException; //Dto_Member가 아니라 상속받은 NonSocialDtoMember를 참조해야함.....
    //jdbc template 이용 예정이므로 jdbc template insert 방법을 익힐것. https://www.springcloud.io/post/2022-06/jdbctemplate-id/#gsc.tab=0

    Optional<T> findByEmail(String Email);
    Optional<T> findByName(String Name);

}
