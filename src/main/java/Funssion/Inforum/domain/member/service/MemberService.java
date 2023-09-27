package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.domain.member.dto.request.MemberInfoDto;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.request.NicknameRequestDto;
import Funssion.Inforum.domain.member.dto.response.EmailDto;
import Funssion.Inforum.domain.member.dto.response.IsProfileSavedDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.dto.response.ValidatedDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.exception.DuplicateMemberException;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.comment.repository.CommentRepository;
import Funssion.Inforum.domain.member.dto.request.PasswordUpdateDto;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.s3.S3Repository;
import Funssion.Inforum.s3.S3Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/* Spring Security 에서 유저의 정보를 가저오기 위한 로직이 포함. */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MyRepository myRepository;
    private final MemoRepository memoRepository;
    private final CommentRepository commentRepository;
    private final S3Repository s3Repository;

    @Value("${aws.s3.profile-dir}")
    private String profileDir;

    @Transactional
    public SaveMemberResponseDto requestMemberRegistration (MemberSaveDto memberSaveDto){
        //중복 처리 한번더 검증
        if(!isValidEmail(memberSaveDto.getUserEmail()).isValid()){
            throw new DuplicateMemberException("이미 가입된 회원 이메일입니다.");
        }
        if(!isValidName(memberSaveDto.getUserName()).isValid()){
            throw new DuplicateMemberException("이미 가입된 닉네임입니다.");
        }

        NonSocialMember member = NonSocialMember.createNonSocialMember(memberSaveDto);
        SaveMemberResponseDto savedMember = memberRepository.save(member);
        return savedMember;
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

        boolean isEmailAvailable = memberRepository.findNonSocialMemberByEmail(email).isEmpty();
        String message = isEmailAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.";
        return new ValidatedDto(isEmailAvailable,message);
    }
    public ValidatedDto isRegisteredEmail(String email){

        boolean isEmailRegistered = memberRepository.findNonSocialMemberByEmail(email).isPresent();
        String message = isEmailRegistered ? "해당 이메일로 코드를 전송하겠습니다." : "해당 이메일로 가입된 회원 정보가 없습니다.";
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
        String imageName = S3Utils.generateImageNameOfS3(memberInfoDto, userId);

        checkAlreadyExists(userId);

        String uploadedImageURL = s3Repository.upload(memberProfileImage, profileDir, imageName);

        MemberProfileEntity memberProfileEntity = MemberProfileEntity.generateWithProfileImage(memberInfoDto, uploadedImageURL);

        return createProfile(userId, memberProfileEntity);

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
        String imageName = S3Utils.generateImageNameOfS3(memberInfoDto, userId);

        Optional<String> priorImageName = Optional.ofNullable(myRepository.findProfileImageNameById(userId));
        priorImageName.ifPresent(s -> s3Repository.delete(profileDir, s));

        String uploadedImageURL = s3Repository.upload(memberProfileImage, profileDir, imageName);
        MemberProfileEntity memberProfileEntity = MemberProfileEntity.generateWithProfileImage(memberInfoDto, uploadedImageURL);

        return updateProfile(userId, memberProfileEntity);
    }

    private IsProfileSavedDto updateProfile(Long userId, MemberProfileEntity memberProfileEntity) {
        memoRepository.updateAuthorProfile(userId, memberProfileEntity.getProfileImageFilePath());
        commentRepository.updateProfileImageOfComment(userId, memberProfileEntity.getProfileImageFilePath());
        commentRepository.updateProfileImageOfReComment(userId,memberProfileEntity.getProfileImageFilePath() );
        return myRepository.updateProfile(userId, memberProfileEntity);
    }

    public MemberProfileEntity getMemberProfile(Long userId){
        return myRepository.findProfileByUserId(userId);
    }

    public IsSuccessResponseDto findAndChangePassword(PasswordUpdateDto passwordUpdateDto) {
        memberRepository.findEmailByAuthCode(passwordUpdateDto.getCode());
        return memberRepository.findAndChangePassword(passwordUpdateDto);
    }
}
