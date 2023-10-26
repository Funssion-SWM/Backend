package Funssion.Inforum.jwt;

import Funssion.Inforum.common.exception.badrequest.BadTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    private Key key;
    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInMilliseconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInMilliseconds) {
        this.secret = secret;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds * 1000;
    }

    // 빈이 생성되고 주입을 받은 후에 secret값을 Base64 Decode해서 key 변수에 할당하기 위해
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Authentication authentication) {
        // 토큰의 expire 시간을 설정
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            String authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            log.info("[Login] User Id = {}, authority = {}", authentication.getName(), authorities);
            return Jwts.builder()
                    .setSubject(authentication.getName()) // user_id가 반환됨
                    .claim(AUTHORITIES_KEY, authorities) // 정보 저장
                    .signWith(key, SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘과 , signature 에 들어갈 secret값 세팅
                    .setExpiration(validity) // set Expire Time 해당 옵션 안넣으면 expire안함
                    .compact();
        }
        else{
            //OAuth의 경우 SimpleGrantedAuthority가 아닌 OAuth2UserAuthority 같음.
            String authorities = authentication.getAuthorities().stream()
                    .map(OAuth2UserAuthority->OAuth2UserAuthority.getAuthority())
                    .collect(Collectors.joining(","));
            log.info("[Login] User Id = {}, authority = {}", authentication.getName(), authorities);

            return Jwts.builder()
                    .setSubject(authentication.getName()) // user_id가 반환됨
                    .claim(AUTHORITIES_KEY, authorities) // 정보 저장
                    .signWith(key, SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘과 , signature 에 들어갈 secret값 세팅
                    .setExpiration(validity) // set Expire Time 해당 옵션 안넣으면 expire안함
                    .compact();
        }

    }
    public String createRefreshToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 토큰의 expire 시간을 설정
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenValidityInMilliseconds);
        return Jwts.builder()
                .setSubject(authentication.getName()) // user_id가 반환됨
                .claim(AUTHORITIES_KEY, authorities) // 정보 저장
                .signWith(key, SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘과 , signature 에 들어갈 secret값 세팅
                .setExpiration(validity) // set Expire Time 해당 옵션 안넣으면 expire안함
                .compact();
    }

    // 토큰으로 클레임을 만들고 이를 이용해 유저 객체를 만들어서 최종적으로 authentication 객체를 리턴
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        List<GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());


        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
    // 토큰의 유효성 검증을 수행
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new BadTokenException("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e ){
            return false;
        } catch (UnsupportedJwtException e) {
            throw new BadTokenException("지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            throw new BadTokenException("JWT 토큰이 잘못되었습니다.", e);
        }
    }

}
