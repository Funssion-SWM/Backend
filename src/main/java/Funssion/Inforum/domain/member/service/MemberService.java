package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.common.exception.BadRequestException;
import Funssion.Inforum.common.exception.ImageIOException;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberInfoDto;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.IsProfileSavedDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.dto.response.ValidatedDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.exception.DuplicateMemberException;
import Funssion.Inforum.domain.member.exception.NotYetImplementException;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/* Spring Security 에서 유저의 정보를 가저오기 위한 로직이 포함. */
@Slf4j
@Service
public class MemberService {
    //생성자로 같은 타입의 클래스(MemberRepository) 다수 조회 후, Map으로 조회
    private final Map<String,MemberRepository> repositoryMap;
    private final MyRepository myRepository;
    private final MemoRepository memoRepository;
    private final AmazonS3 S3client;

    @Value("${aws.s3.profile-dir}")
    private String profileDir;

    public MemberService(Map<String, MemberRepository> repositoryMap,MyRepository myRepository, MemoRepository memoRepository, AmazonS3 S3client) {
        this.repositoryMap = repositoryMap;
        this.myRepository = myRepository;
        this.memoRepository = memoRepository;
        this.S3client = S3client;
    }
    HashMap<LoginType, String> loginTypeMap = new HashMap<>();
    {
        loginTypeMap.put(LoginType.NON_SOCIAL,"nonSocialMemberRepository");
        loginTypeMap.put(LoginType.SOCIAL, "socialMemberRepository");
    }

    @Transactional
    public SaveMemberResponseDto requestMemberRegistration (MemberSaveDto memberSaveDto){
        LoginType loginType = memberSaveDto.getLoginType();
        log.debug("Save Member Email = {}, loginType = {}",memberSaveDto.getUserEmail(), loginType);
        //중복 처리 한번더 검증
        if(!isValidEmail(memberSaveDto.getUserEmail(),loginType).isValid()){
            throw new DuplicateMemberException("이미 가입된 회원 이메일입니다.");
        }
        if(!isValidName(memberSaveDto.getUserName(),loginType).isValid()){
            throw new DuplicateMemberException("이미 가입된 닉네임입니다.");
        }

        switch (loginType) {
            case NON_SOCIAL:
                MemberRepository selectedMemberRepository = repositoryMap.get(loginTypeMap.get(loginType));
                NonSocialMember member = NonSocialMember.createNonSocialMember(memberSaveDto);
                SaveMemberResponseDto savedMember = selectedMemberRepository.save(member);
                return savedMember;
            case SOCIAL: //social 회원가입의 경우 -> 요청 필요
            {
                throw new NotYetImplementException("해당 요청은 아직 구현되지 않았습니다.");
            }
        }
        throw new InvalidParameterException("!~ 수정");
    }

    public ValidatedDto isValidName(String username, LoginType loginType) {
        MemberRepository selectedMemberRepository = getMemberRepository(loginType);
        log.debug("selected repository = {}", selectedMemberRepository);

        boolean isNameAvailable = selectedMemberRepository.findByName(username).isEmpty();
        String message = isNameAvailable ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.";
        return new ValidatedDto(isNameAvailable, message);
    }
    public ValidatedDto isValidEmail(String email, LoginType loginType){
        MemberRepository selectedMemberRepository = getMemberRepository(loginType);
        log.debug("selected repository = {}",selectedMemberRepository);
        boolean isEmailAvailable = selectedMemberRepository.findByEmail(email).isEmpty();
        String message = isEmailAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.";
        return new ValidatedDto(isEmailAvailable,message);
    }

    @Transactional
    public IsProfileSavedDto createMemberProfile(String userId, MemberInfoDto memberInfoDto){
        return memberInfoDto.isEmptyProfileImage() == false
                ? createMemberProfileWithImage(userId, memberInfoDto)
                : createMemberProfileWithoutImage(userId, memberInfoDto);
    }
    @Transactional
    public IsProfileSavedDto updateMemberProfile(String userId,MemberInfoDto memberInfoDto) {
        return memberInfoDto.getImage() != null
                ? updateMemberProfileWithImage(userId, memberInfoDto)
                : updateMemberProfileWithoutImage(userId, memberInfoDto);
    }
    private IsProfileSavedDto createMemberProfileWithoutImage(String userId, MemberInfoDto memberInfoDto) {
        if(!Optional.ofNullable(myRepository.findProfileImageNameById(Long.valueOf(userId))).isPresent()){
            throw new BadRequestException("이미 존재하는 프로필정보를 최초 저장하는 이슈. -> Patch로 전송바람");
        }
        memoRepository.updateAuthorProfile(Long.valueOf(userId), null);
        return myRepository.createProfile(Long.valueOf(userId), generateMemberProfileEntity(memberInfoDto));
    }

    private IsProfileSavedDto createMemberProfileWithImage(String userId, MemberInfoDto memberInfoDto) {
        MultipartFile memberProfileImage = memberInfoDto.getImage();
        String imageName = generateImageNameOfS3(memberInfoDto, userId);
        try {
            if(Optional.ofNullable(myRepository.findProfileImageNameById(Long.valueOf(userId))).isPresent()){
                throw new BadRequestException("이미 존재하는 프로필정보를 최초 저장하는 이슈. -> Patch로 전송바람");
            }
            uploadImageToS3(memberProfileImage, imageName);
            memoRepository.updateAuthorProfile(Long.valueOf(userId), getImagePath(imageName));
            return myRepository.createProfile(Long.valueOf(userId), generateMemberProfileEntity(memberInfoDto, imageName));
        } catch (IOException e) {
            throw new ImageIOException("프로필 이미지 IO Exception 발생", e);
        }
    }

    private String getImagePath(String imageName) {
        return "https://store.inforum.me/" + S3client.getUrl(profileDir, imageName).getPath();
    }


    private IsProfileSavedDto updateMemberProfileWithoutImage(String userId, MemberInfoDto memberInfoDto) {
        Optional<String> priorImageName = Optional.ofNullable(myRepository.findProfileImageNameById(Long.valueOf(userId)));
        if (priorImageName.isPresent() && memberInfoDto.isEmptyProfileImage()) {
            deleteImageFromS3(priorImageName.get());
            return myRepository.updateProfile(Long.valueOf(userId), generateMemberProfileEntityWithNoProfileImage(memberInfoDto));
        }
        else if(priorImageName.isPresent()){
            return myRepository.updateProfile(Long.valueOf(userId), generateMemberProfileEntityKeepingImagePath(memberInfoDto,priorImageName.get()));
        }
        memoRepository.updateAuthorProfile(Long.valueOf(userId), null);
        return myRepository.updateProfile(Long.valueOf(userId), generateMemberProfileEntity(memberInfoDto));
    }

    private IsProfileSavedDto updateMemberProfileWithImage(String userId, MemberInfoDto memberInfoDto) {
        MultipartFile memberProfileImage = memberInfoDto.getImage();
        String imageName = generateImageNameOfS3(memberInfoDto, userId);
        try {
            Optional<String> priorImageName = Optional.ofNullable(myRepository.findProfileImageNameById(Long.valueOf(userId)));
            if (priorImageName.isPresent()) {
                deleteImageFromS3(priorImageName.get());
            }
            uploadImageToS3(memberProfileImage, imageName);
            memoRepository.updateAuthorProfile(Long.valueOf(userId), getImagePath(imageName));
            return myRepository.updateProfile(Long.valueOf(userId), generateMemberProfileEntity(memberInfoDto, imageName));
        } catch (IOException e) {
            throw new ImageIOException("프로필 이미지 IO Exception 발생", e);
        }
    }

    private void uploadImageToS3(MultipartFile memberProfileImage, String imageName) throws IOException {
        S3client.putObject(profileDir, imageName, memberProfileImage.getInputStream(), getObjectMetaData(memberProfileImage));
    }

    private void deleteImageFromS3(String imageName){
        String imageNameInS3 = parseImageNameOfS3(imageName);
        S3client.deleteObject(profileDir,imageNameInS3);
    }
    public MemberProfileEntity getMemberProfile(String userId){
        return myRepository.findProfileByUserId(Long.valueOf(userId));
    }

    private MemberRepository getMemberRepository(LoginType loginType) {
        MemberRepository selectedMemberRepository = repositoryMap.get(loginTypeMap.get(loginType));
        return selectedMemberRepository;
    }

    private String parseImageNameOfS3(String imagePathS3){
        int startIndexOfParsing = imagePathS3.lastIndexOf("/");
        return imagePathS3.substring(startIndexOfParsing+1);
    }
    private String generateImageNameOfS3(MemberInfoDto memberInfoDto,String userId) {
        if(memberInfoDto.getImage().isEmpty()) return "";
        String fileName = UUID.randomUUID()+ "-" + userId;
        return fileName;
    }

    private MemberProfileEntity generateMemberProfileEntity(MemberInfoDto memberInfoDto,String imageName){
        return MemberProfileEntity.builder()
                .profileImageFilePath(getImagePath(imageName))
                .tags(memberInfoDto.getTags())
                .nickname(memberInfoDto.getNickname())
                .introduce(memberInfoDto.getIntroduce())
                .build();
    }

    private MemberProfileEntity generateMemberProfileEntityKeepingImagePath(MemberInfoDto memberInfoDto,String imagePath){
        return MemberProfileEntity.builder()
                .profileImageFilePath(imagePath)
                .tags(memberInfoDto.getTags())
                .nickname(memberInfoDto.getNickname())
                .introduce(memberInfoDto.getIntroduce())
                .build();
    }
    private MemberProfileEntity generateMemberProfileEntity(MemberInfoDto memberInfoDto){
        return MemberProfileEntity.builder()
                .tags(memberInfoDto.getTags())
                .nickname(memberInfoDto.getNickname())
                .introduce(memberInfoDto.getIntroduce())
                .build();
    }


    private MemberProfileEntity generateMemberProfileEntityWithNoProfileImage(MemberInfoDto memberInfoDto){
        return MemberProfileEntity.builder()
                .profileImageFilePath(null)
                .tags(memberInfoDto.getTags())
                .nickname(memberInfoDto.getNickname())
                .introduce(memberInfoDto.getIntroduce())
                .build();
    }
    private ObjectMetadata getObjectMetaData(MultipartFile file){
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        return objectMetadata;
    }
}
