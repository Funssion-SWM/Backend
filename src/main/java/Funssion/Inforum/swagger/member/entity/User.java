package Funssion.Inforum.swagger.member.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class User {
    private Integer user_id;
    private String user_name;
    private Integer login_type;
    private LocalDate created_date;

    public User(Integer userId, String userName, Integer loginType, LocalDate createdDate) {
        user_id = userId;
        user_name = userName;
        login_type = loginType;
        created_date = createdDate;
    }
}
