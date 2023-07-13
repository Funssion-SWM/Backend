package Funssion.Inforum.swagger.member.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Auth {
    private Integer auth_id;
    private Integer user_id;
    private String user_email;
    private String user_pw;

    public Auth(Integer authId, Integer userId, String userEmail, String userPw) {
        auth_id = authId;
        user_id = userId;
        user_email = userEmail;
        user_pw = userPw;
    }
}
