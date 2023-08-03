package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.domain.member.dto.CodeCheckDto;
import Funssion.Inforum.domain.member.dto.SuccessEmailSendDto;
import Funssion.Inforum.domain.member.repository.AuthCodeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class MailService {
    private final JavaMailSender mailSender;
    private final AuthCodeRepository authCodeRepository;


    @Value("${naver.id}")
    String adminId;
    @Value("${naver.email}")
    String  adminEmail;

    @Transactional
    public SuccessEmailSendDto sendEmailCode(String beVerifiedEmail){
        try {
            String generatedCode = makeRandomString();
            authCodeRepository.invalidateExistedEmailCode(beVerifiedEmail);
            authCodeRepository.insertEmailCodeForVerification(beVerifiedEmail, generatedCode);
            sendEmail(beVerifiedEmail,generatedCode);
        }catch(DataAccessException e){
            return new SuccessEmailSendDto(false);
        }
        return new SuccessEmailSendDto(true);
    }
    public boolean isAuthorizedEmail(CodeCheckDto requestCodeDto){
        return authCodeRepository.checkRequestCode(requestCodeDto);
    }
    private String makeRandomString() {
        Random r = new Random();
        String randomNumberString = "";
        for(int i = 0; i < 6; i++) {
            randomNumberString += Integer.toString(r.nextInt(10));
        }
        return randomNumberString;
    }


    //mail을 어디서 보내는지, 어디로 보내는지 , 인증 번호를 html 형식으로 어떻게 보내는지 작성합니다.
    private String sendEmail(String receiverEmail,String code) {
        String title = "회원 가입 인증 이메일 입니다."; // 이메일 제목
        String content =
                " 반갑습니다!" + "<br>" + "Inforum 회원가입을 위한 인증번호입니다." +    //html 형식으로 작성 !
                        "<br><br>" +
                        "인증 번호는 " + code + "입니다." +
                        "<br>" +
                        "인증번호를 인증번호 칸에 입력해주세요"; //이메일 내용 삽입
        mailSendFromAdminToUser(adminEmail, receiverEmail, title, content);
        return code;
    }
    //이메일을 전송합니다.
    private void mailSendFromAdminToUser(String senderEmail, String receiverEmail, String title, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();//JavaMailSender 객체를 사용하여 MimeMessage 객체를 생성
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true,"utf-8");//이메일 메시지와 관련된 설정을 수행합니다.
            // true를 전달하여 multipart 형식의 메시지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
            helper.setFrom(senderEmail);//이메일의 발신자 주소 설정
            helper.setTo(receiverEmail);//이메일의 수신자 주소 설정
            helper.setSubject(title);//이메일의 제목을 설정
            helper.setText(content,true);//이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {//이메일 서버에 연결할 수 없거나, 잘못된 이메일 주소를 사용하거나, 인증 오류가 발생하는 등 오류
            // 이러한 경우 MessagingException이 발생
            e.printStackTrace();
        }
    }
    @Scheduled(cron = "0 03 00 * * ?")
    public void scheduledForRemovingExpirationEmailCode(){
        authCodeRepository.removeExpiredEmailCode();
    }
}