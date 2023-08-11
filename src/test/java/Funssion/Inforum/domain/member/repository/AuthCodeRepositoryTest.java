package Funssion.Inforum.domain.member.repository;

class AuthCodeRepositoryTest {
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