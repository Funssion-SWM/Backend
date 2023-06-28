package Funssion.Inforum.model.member;
//DTO 정의
public class Dto_Member {
    private String user_name;
    private String user_email; //DTO이므로 소셜로그인 이메일, 개인 인증 이메일 둘다 user_email에 속함

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
}
