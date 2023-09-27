package Funssion.Inforum.domain.member.entity;

import Funssion.Inforum.domain.member.constant.LoginType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

// 상속을 이용하여 Social 로그인 Member, Non Social 로그인 Member 분리
@Getter
@SuperBuilder
public class Member {
    private final Long userId;

    private final String userName;

    private final LoginType loginType;

    private final String userEmail;

    private final LocalDateTime createdDate;

    private String imagePath;

    private String introduce;

    private String tags;

    private Long followCnt;

    private Long followerCnt;
}
