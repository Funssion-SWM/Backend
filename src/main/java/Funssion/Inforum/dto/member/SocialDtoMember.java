package Funssion.Inforum.dto.member;

public class SocialDtoMember extends Dto_Member {
    private String accessToken;
    //---------------- ACCESS TOKEN을 서비스 로직에서 부여할 것이면 db entity에서 추가해야할듯 -------//
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
