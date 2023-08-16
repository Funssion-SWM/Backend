package Funssion.Inforum.domain.member.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;


public class CustomUserDetails implements UserDetails, OAuth2User, Serializable {

    private static final long serialVersionUID = 174726374856727L;

    private String id;	// DB에서 PK 값
    private String loginId;		// 로그인용 ID 값
    private String password;	// 비밀번호
    private String email;	//이메일
    private boolean emailVerified;	//이메일 인증 여부
    private boolean locked;	//계정 잠김 여부
    private String nickname;	//닉네임
    private Collection<GrantedAuthority> authorities;	//권한 목록

    private SocialMember socialMember;
    private Map<String, Object> attributes;

    //Social Login 용
    public CustomUserDetails(SocialMember socialMember, Map<String, Object> attributes) {
        //PrincipalOauth2UserService 참고
        this.socialMember = socialMember;
        this.attributes = attributes;
    }

    //Non Social Login 용
    public  CustomUserDetails(Long authId, String userEmail, String userPw, boolean emailVerified,boolean locked) {
        this.id = String.valueOf(authId);
        this.email = userEmail;
        this.password = userPw;
        this.emailVerified = emailVerified;
        this.locked = !locked;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * 해당 유저의 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * 비밀번호
     */
    @Override
    public String getPassword() {
        return password;
    }


    /**
     * PK값
     */
    @Override
    public String getUsername() {
        return id;
    }

    /**
     * 계정 만료 여부
     * true : 만료 안됨
     * false : 만료
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠김 여부
     * true : 잠기지 않음
     * false : 잠김
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return locked;
    }

    /**
     * 비밀번호 만료 여부
     * true : 만료 안됨
     * false : 만료
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    /**
     * 사용자 활성화 여부
     * ture : 활성화
     * false : 비활성화
     * @return
     */
    @Override
    public boolean isEnabled() {
        //이메일이 인증되어 있고 계정이 잠겨있지 않으면 true
        //상식과 조금 벗어나서, Customizing 하였음
        return (emailVerified && locked);

    }

    @Override
    public String getName() {
        String sub = attributes.get("sub").toString();
        return sub;
    }
}