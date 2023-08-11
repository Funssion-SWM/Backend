package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.dto.response.ValidatedDto;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.exception.DuplicateMemberException;
import Funssion.Inforum.domain.member.exception.NotYetImplementException;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

/* Spring Security 에서 유저의 정보를 가저오기 위한 로직이 포함. */
@Slf4j
@Service
public class MemberService {
    //생성자로 같은 타입의 클래스(MemberRepository) 다수 조회 후, Map으로 조회
    private final Map<String,MemberRepository> repositoryMap;
    private final MyRepository myRepository;

    public MemberService(Map<String, MemberRepository> repositoryMap,MyRepository myRepository) {
        this.repositoryMap = repositoryMap;
        this.myRepository = myRepository;
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
                myRepository.createHistory(savedMember.getId());
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

<<<<<<< Updated upstream
=======
    public IsProfileSavedDto createOrUpdateMemberProfile(String userId,MemberInfoDto memberInfoDto) {
        try {
            if (memberInfoDto.getImage() != null) {
                return createOrUpdateMemberProfileWithImage(userId, memberInfoDto);
            } else {
                return createOrUpdateMemberProfileWithoutImage(userId, memberInfoDto);
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Transactional
    private IsProfileSavedDto createOrUpdateMemberProfileWithoutImage(String userId, MemberInfoDto memberInfoDto) {
        Optional<String> imageName = Optional.ofNullable(myRepository.findProfileImageNameById(Long.valueOf(userId)));
        if (imageName.isPresent()) {
            deleteImageFromS3(imageName.get());
        }
        return myRepository.updateProfile(Long.valueOf(userId), generateMemberProfileEntity(memberInfoDto));
    }

    private IsProfileSavedDto createOrUpdateMemberProfileWithImage(String userId, MemberInfoDto memberInfoDto) {
        MultipartFile memberProfileImage = memberInfoDto.getImage();
        String imageName = generateImageNameOfS3(memberInfoDto, userId);
        try {
            Optional<String> priorImageName = Optional.ofNullable(myRepository.findProfileImageNameById(Long.valueOf(userId)));
            if (priorImageName.isPresent()) {
                deleteImageFromS3(priorImageName.get());
            }
            uploadImageToS3(memberProfileImage, imageName);
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
>>>>>>> Stashed changes

    private MemberRepository getMemberRepository(LoginType loginType) {
        MemberRepository selectedMemberRepository = repositoryMap.get(loginTypeMap.get(loginType));
        return selectedMemberRepository;
    }
}
