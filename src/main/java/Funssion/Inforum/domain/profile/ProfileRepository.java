package Funssion.Inforum.domain.profile;

import Funssion.Inforum.domain.member.entity.MemberProfileEntity;

public interface ProfileRepository {
    void updateProfile(Long userId, MemberProfileEntity memberProfile);
    void updateAuthorImagePathInPost(Long userId, String newImageURL);
}
