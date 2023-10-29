package Funssion.Inforum.domain.profile;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.profile.domain.AuthorProfile;
import Funssion.Inforum.domain.profile.dto.response.UserProfileForEmployer;

import java.util.List;

public interface ProfileRepository {
    void updateProfile(Long userId, MemberProfileEntity memberProfile);
    void updateAuthorImagePathInPost(Long userId, String newImageURL);
    AuthorProfile findAuthorProfile(PostType postType, Long postId);
    Long findAuthorId(PostType postType, Long postId);
    List<UserProfileForEmployer> findUserProfilesForEmployer(String developmentArea, String techStack);
}
