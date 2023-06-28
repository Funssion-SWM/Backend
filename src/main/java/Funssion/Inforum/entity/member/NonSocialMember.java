package Funssion.Inforum.entity.member;

public class NonSocialMember extends Member {
    private Long user_non_social_id;
    private String user_email;
    private String user_pwd;

    public Long getUser_non_social_id() {
        return user_non_social_id;
    }

    public void setUser_non_social_id(long user_non_social_id) {
        this.user_non_social_id = user_non_social_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_pwd() {
        return user_pwd;
    }

    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd;
    }
}
