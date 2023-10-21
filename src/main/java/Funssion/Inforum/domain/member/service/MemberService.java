package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.follow.repository.FollowRepository;
import Funssion.Inforum.domain.member.dto.request.MemberInfoDto;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.request.NicknameRequestDto;
import Funssion.Inforum.domain.member.dto.request.PasswordUpdateDto;
import Funssion.Inforum.domain.member.dto.response.*;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.exception.DuplicateMemberException;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.comment.repository.CommentRepository;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import Funssion.Inforum.domain.profile.ProfileRepository;
import Funssion.Inforum.jwt.TokenProvider;
import Funssion.Inforum.s3.S3Repository;
import Funssion.Inforum.s3.S3Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/* Spring Security 에서 유저의 정보를 가저오기 위한 로직이 포함. */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final TokenProvider tokenProvider;
    @Value("${jwt.domain}") private String domain;

    private final MemberRepository memberRepository;
    private final MyRepository myRepository;
    private final MemoRepository memoRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;
    private final S3Repository s3Repository;
    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;

    @Value("${aws.s3.profile-dir}")
    private String profileDir;

    @Transactional
    public SaveMemberResponseDto requestMemberRegistration (MemberSaveDto memberSaveDto, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //중복 처리 한번더 검증
        if(!isValidEmail(memberSaveDto.getUserEmail()).isValid()){
            throw new DuplicateMemberException("이미 가입된 회원 이메일입니다.");
        }
        if(!isValidName(memberSaveDto.getUserName()).isValid()){
            throw new DuplicateMemberException("이미 가입된 닉네임입니다.");
        }

        NonSocialMember member = NonSocialMember.createNonSocialMember(memberSaveDto);
        SaveMemberResponseDto savedMember = memberRepository.save(member);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(savedMember.getId(), memberSaveDto.getUserPw());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        makeLoginForSavedUser(request, response, authentication);
        return savedMember;
    }

    private void makeLoginForSavedUser(HttpServletRequest request, HttpServletResponse response, UsernamePasswordAuthenticationToken authentication) {
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        resolveResponseCookieByOrigin(request, response, accessToken, refreshToken);
    }

    private void resolveResponseCookieByOrigin(HttpServletRequest request, HttpServletResponse response, String accessToken, String refreshToken){
        if(request.getServerName().equals("localhost") || request.getServerName().equals("dev.inforum.me")){
            addCookie(accessToken, refreshToken, response,false);
        }
        else{
            addCookie(accessToken, refreshToken, response,true);
        }
    }
    private void addCookie(String accessToken, String refreshToken, HttpServletResponse response,boolean isHttpOnly) {
        String accessCookieString = makeAccessCookieString(accessToken, isHttpOnly);
        String refreshCookieString = makeRefreshCookieString(refreshToken, isHttpOnly);
        response.setHeader("Set-Cookie", accessCookieString);
        response.addHeader("Set-Cookie", refreshCookieString);
    }

    private String makeAccessCookieString(String token,boolean isHttpOnly) {
        if(isHttpOnly){
            return "accessToken=" + token + "; Path=/; Domain=" + domain + "; Max-Age=3600; SameSite=Lax; HttpOnly; Secure";
        }else{
            return "accessToken=" + token + "; Path=/; Domain=" + domain + "; Max-Age=3600;";
        }
    }

    private String makeRefreshCookieString(String token,boolean isHttpOnly) {
        if(isHttpOnly){
            return "refreshToken=" + token + "; Path=/; Domain=" + domain + "; Max-Age=864000; SameSite=Lax; HttpOnly; Secure";
        }else{
            return "refreshToken=" + token + "; Path=/; Domain=" + domain + "; Max-Age=864000;";
        }
    }
    @Transactional
    public IsSuccessResponseDto requestNicknameRegistration(NicknameRequestDto nicknameRequestDto,Long userId){
        ValidatedDto isValidName = isValidName(nicknameRequestDto.getNickname());
        if (isValidName.isValid()){
            return memberRepository.saveSocialMemberNickname(nicknameRequestDto.getNickname(), userId);
        }
        else{
            return new IsSuccessResponseDto(false,"닉네임 저장에 실패하였습니다.");
        }
    }
    public ValidatedDto isValidName(String username) {
        boolean isNameAvailable = memberRepository.findByName(username).isEmpty();
        String message = isNameAvailable ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.";
        return new ValidatedDto(isNameAvailable, message);
    }
    public ValidatedDto isValidEmail(String email){

        boolean isEmailAvailable = memberRepository.findNonSocialMemberByEmail(email).isEmpty() && memberRepository.findSocialMemberByEmail(email).isEmpty();
        String message = isEmailAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.";
        return new ValidatedDto(isEmailAvailable,message);
    }
    public ValidatedDto isRegisteredEmail(String email){
        if(memberRepository.findSocialMemberByEmail(email).isPresent()){
            return new ValidatedDto(false,"구글 로그인으로 등록된 계정입니다.");
        }

        boolean isEmailRegistered = memberRepository.findNonSocialMemberByEmail(email).isPresent();
        String message = isEmailRegistered ? "해당 이메일로 코드를 전송하였습니다." : "해당 이메일로 가입된 회원 정보가 없습니다.";
        return new ValidatedDto(isEmailRegistered,message);
    }

    @Transactional
    public IsProfileSavedDto createMemberProfile(Long userId, MemberInfoDto memberInfoDto){
        return !memberInfoDto.isEmptyProfileImage()
                ? createMemberProfileWithImage(userId, memberInfoDto)
                : createMemberProfileWithoutImage(userId, memberInfoDto);
    }
    @Transactional
    public IsProfileSavedDto updateMemberProfile(Long userId,MemberInfoDto memberInfoDto) {
        return memberInfoDto.getImage() != null
                ? updateMemberProfileWithImage(userId, memberInfoDto)
                : updateMemberProfileWithoutImage(userId, memberInfoDto);
    }

    public EmailDto findEmailByNickname(String nickname){
        String emailFoundByNickname = memberRepository.findEmailByNickname(nickname);
        return new EmailDto(blur(emailFoundByNickname),"해당 닉네임으로 등록된 이메일 정보입니다.");
    }
    public String blur(String email){
        int startOfDomainIndex = email.indexOf("@");
        char blurChar = '*';
        char[] charEmailArray = email.toCharArray();
        for(int i =3; i<startOfDomainIndex; i++){
            charEmailArray[i] = blurChar;
        }
        return new String(charEmailArray);
    }
    private IsProfileSavedDto createMemberProfileWithoutImage(Long userId, MemberInfoDto memberInfoDto) {

        checkAlreadyExists(userId);

        MemberProfileEntity memberProfileEntity = MemberProfileEntity.generateWithNoProfileImage(memberInfoDto);

        return createProfile(userId, memberProfileEntity);
    }

    private IsProfileSavedDto createMemberProfileWithImage(Long userId, MemberInfoDto memberInfoDto) {
        MultipartFile memberProfileImage = memberInfoDto.getImage();
        checkImageSize(memberProfileImage);

        String imageName = S3Utils.generateImageNameOfS3(memberInfoDto, userId);

        checkAlreadyExists(userId);

        String uploadedImageURL = s3Repository.upload(memberProfileImage, profileDir, imageName);

        MemberProfileEntity memberProfileEntity = MemberProfileEntity.generateWithProfileImage(memberInfoDto, uploadedImageURL);

        return createProfile(userId, memberProfileEntity);

    }

    private void checkImageSize(MultipartFile image){
        if (image.getSize() > S3Utils.MAX_PROFILE_IMAGE_SIZE){
            throw new BadRequestException("프로필 이미지 사이즈는 2MB 이하로 등록 가능합니다.");
        }
    }
    private void checkAlreadyExists(Long userId) {
        if(!Optional.ofNullable(myRepository.findProfileImageNameById(userId)).isEmpty()){
            throw new BadRequestException("이미 존재하는 프로필정보를 최초 저장하는 이슈. -> Patch로 전송바람");
        }
    }

    private IsProfileSavedDto createProfile(Long userId, MemberProfileEntity memberProfileEntity) {
        memoRepository.updateAuthorProfile(userId, memberProfileEntity.getProfileImageFilePath());
        return myRepository.createProfile(userId, memberProfileEntity);
    }

    private IsProfileSavedDto updateMemberProfileWithoutImage(Long userId, MemberInfoDto memberInfoDto) {
        Optional<String> priorImageName = Optional.ofNullable(myRepository.findProfileImageNameById(userId));
        MemberProfileEntity memberProfileEntity;

        // 이전 프로필 이미지가 존재하고, 프로필을 지워달라는 요청이 오면 이전 프로필 이미지 지우기
        if (priorImageName.isPresent() && memberInfoDto.isEmptyProfileImage()) {
            s3Repository.delete(profileDir, priorImageName.get());
            memberProfileEntity = MemberProfileEntity.generateWithNoProfileImage(memberInfoDto);
        }
        else if(priorImageName.isPresent()){
            memberProfileEntity = MemberProfileEntity.generateKeepingImagePath(memberInfoDto,priorImageName.get());
        } else {
            memberProfileEntity = MemberProfileEntity.generateWithNoProfileImage(memberInfoDto);
        }

        return updateProfile(userId, memberProfileEntity);
    }

    private IsProfileSavedDto updateMemberProfileWithImage(Long userId, MemberInfoDto memberInfoDto) {
        MultipartFile memberProfileImage = memberInfoDto.getImage();
        checkImageSize(memberProfileImage);

        String imageName = S3Utils.generateImageNameOfS3(memberInfoDto, userId);

        Optional<String> priorImageName = Optional.ofNullable(myRepository.findProfileImageNameById(userId));
        priorImageName.ifPresent(s -> s3Repository.delete(profileDir, s));

        String uploadedImageURL = s3Repository.upload(memberProfileImage, profileDir, imageName);
        MemberProfileEntity memberProfileEntity = MemberProfileEntity.generateWithProfileImage(memberInfoDto, uploadedImageURL);

        return updateProfile(userId, memberProfileEntity);
    }

    private IsProfileSavedDto updateProfile(Long userId, MemberProfileEntity memberProfileEntity) {
        profileRepository.updateAuthorImagePathInPost(userId, memberProfileEntity.getProfileImageFilePath());
        return myRepository.updateProfile(userId, memberProfileEntity);
    }

    @Transactional
    public void withdrawUser(Long userId) {
        String anonymousUserName = UUID.randomUUID().toString().substring(0, 15);
        profileRepository.updateProfile(userId, new MemberProfileEntity(null, anonymousUserName, "탈퇴한 유저입니다."));
        memberRepository.deleteUser(userId);
    }


    @Transactional(readOnly = true)
    public MemberProfileDto getMemberProfile(Long userId){
        Long requestUserId = SecurityContextUtils.getUserId();
        MemberProfileDto memberProfileDto = MemberProfileDto.valueOf(
                myRepository.findProfileByUserId(userId));

        followRepository.findByUserIdAndFollowedUserId(requestUserId, userId)
                .ifPresent((follow) -> memberProfileDto.setIsFollowed(Boolean.TRUE));

        return memberProfileDto;
    }

    public IsSuccessResponseDto findAndChangePassword(PasswordUpdateDto passwordUpdateDto) {
        memberRepository.findEmailByAuthCode(passwordUpdateDto.getCode());
        return memberRepository.findAndChangePassword(passwordUpdateDto);
    }
}
