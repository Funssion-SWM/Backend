package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.dto.request.CodeCheckDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AuthCodeRepositoryTest {
    @Autowired
    DataSource dataSource;
    @Autowired
    AuthCodeRepository authCodeRepository;

    String beVerifiedEmail = "test@gmail.com";
    String authCode1 = "123456";
    String authCode2 = "654321";

    CodeCheckDto codeCheckDto = new CodeCheckDto(beVerifiedEmail,authCode1);
    @Test
    @DisplayName("이메일 인증 코드 만료시키는 메소드 호출시 만료 되는지 확인")
    void expireAuthEmailCode(){
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(5); //유효시간 5분
        authCodeRepository.insertEmailCodeForVerification(dueDate, beVerifiedEmail,authCode1);
        boolean isTrueBeforeExpiration = authCodeRepository.checkRequestCode(codeCheckDto);
        assertThat(isTrueBeforeExpiration).isEqualTo(true);

        authCodeRepository.invalidateExistedEmailCode(beVerifiedEmail);
        boolean isFalseAfterExpiration = authCodeRepository.checkRequestCode(codeCheckDto);
        assertThat(isFalseAfterExpiration).isEqualTo(false);
    }

    @Test
    @DisplayName("만료 시간이 지난 경우 인증이 안되어야 함")
    void doNotAuthorizeAfterDueTime(){
        LocalDateTime overDueTime = LocalDateTime.now().minusMinutes(6);
        authCodeRepository.insertEmailCodeForVerification(overDueTime,codeCheckDto.getEmail(),codeCheckDto.getCode());
        boolean isAuthorized = authCodeRepository.checkRequestCode(codeCheckDto);
        assertThat(isAuthorized).isEqualTo(false);
    }

    @Test
    @DisplayName("expire된 row 삭제여부 확인")
    void deleteExpiredCode(){
        LocalDateTime overDueTime = LocalDateTime.now().minusMinutes(6);
        authCodeRepository.insertEmailCodeForVerification(overDueTime,codeCheckDto.getEmail(),codeCheckDto.getCode());
        authCodeRepository.removeExpiredEmailCode();
        boolean isAuthorized = authCodeRepository.checkRequestCode(codeCheckDto);
        assertThat(isAuthorized).isEqualTo(false);
    }
    /*
     * <invalidateExistedEmail> -> 반환값이 필요할듯
     * 1. DB에 이미 존재하는 이메일인증이 존재할 때/ 같은 이메일로 인증이 올경우 / 원래 존재하는 row를 만료시킴 (expiration 필드값)
     * 2. DB에 이메일 인증정보 없으면 / 이메일 인증 요청이 왔을 때 / 아무일도 일어나지 않음

     * <insertEmailCodeForVerification>
     * 1. DB에 중복된 이메일 인증정보가 없다고 가정, 이메일과 인증코드 정보를 파라미터로 받으면 / 데이터를 삽입하고 / ..암것도안함?
     * 2. ..

     * <checkRequestCode>
     * 유효기간 내에 존재하는 이메일 인증 코드가 db에 존재하면 / 인증 요청을 받았고, DB내용과 일치할 때 / 성공 반환
     * 유효기간 내에 존재하지 않은 이메일인증 코드가 DB에 존재하면 / 인증 요청을 받았고, DB내용과 일치해도 / 실패 반환
     * 유효기간 내에 존재하는 이메일 인증 코드가 db에 존재하면 / 인증 요청을 받았고, DB내용과 일치하지 않으면/ 실패 반환

     * <removeExpiredEmailCode>
     * 유효기간이 지난 이메일 인증 코드가 DB에 존재하면 / 요청 들어오면 /해당 row 삭제
     
     * */
}