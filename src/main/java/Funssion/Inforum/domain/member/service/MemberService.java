package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.common.exception.BadRequestException;
import Funssion.Inforum.common.exception.ImageIOException;
import Funssion.Inforum.s3.S3Repository;
import Funssion.Inforum.s3.S3Utils;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberInfoDto;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.request.NicknameRequestDto;
import Funssion.Inforum.domain.member.dto.response.IsProfileSavedDto;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
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
import lombok.RequiredArgsConstructor;
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

/* Spring Security 에서 유저의 정보를 가저오기 위한 로직이 포함. */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    //생성자로 같은 타입의 클래스(MemberRepository) 다수 조회 후, Map으로 조회
    private final Map<String,MemberRepository> repositoryMap;
    private final MyRepository myRepository;
    private final MemoRepository memoRepository;
    private final S3Repository s3Repository;

    @Value("${aws.s3.profile-dir}")
    private String profileDir;

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
            case SOCIAL: //social 회원가입의 경우 -> 요청 필요 -
                // ---------- 의미 없어짐 ----------- //
            {
                throw new NotYetImplementException("해당 요청은 아직 구현되지 않았습니다.");
            }
        }
        throw new InvalidParameterException("회원가입 로직중 잘못된 파라미터가 전달되었습니다.");
    }

    public IsSuccessResponseDto requestNicknameRegistration(NicknameRequestDto nicknameRequestDto,Long userId){
        MemberRepository memberRepository = getMemberRepository(LoginType.SOCIAL);
        ValidatedDto isValidName = isValidName(nicknameRequestDto.getNickname(), LoginType.SOCIAL);
        if (isValidName.isValid()){
            return memberRepository.saveSocialMemberNickname(nicknameRequestDto.getNickname(), userId);
        }
        else{
            return new IsSuccessResponseDto(false,"닉네임 저장에 실패하였습니다.");
        }
    }
    public ValidatedDto isValidName(String username, LoginType loginType) {
        MemberRepository selectedMemberRepository = getMemberRepository(loginType);
        log.debug("selected repository = {}", selectedMemberRepository);

        boolean isNameAvailable = selectedMemberRepository.findByName(username).isEmpty();
        String message = isNameAvailable ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.";
        return new ValidatedDto(isNameAvailable, message);
    }
    /* NonSocial만 해도 생관 없음 수정해야하는 부분  -> repository 분기가 의미가 없어짐 */
    public ValidatedDto isValidEmail(String email, LoginType loginType){
        MemberRepository selectedMemberRepository = getMemberRepository(loginType);
        log.debug("selected repository = {}",selectedMemberRepository);

        boolean isEmailAvailable = selectedMemberRepository.findByEmail(email).isEmpty();
        String message = isEmailAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.";
        return new ValidatedDto(isEmailAvailable,message);
    }

    @Transactional
    public IsProfileSavedDto createMemberProfile(Long userId, MemberInfoDto memberInfoDto){
        return memberInfoDto.isEmptyProfileImage() == false
                ? createMemberProfileWithImage(userId, memberInfoDto)
                : createMemberProfileWithoutImage(userId, memberInfoDto);
    }
    @Transactional
    public IsProfileSavedDto updateMemberProfile(Long userId,MemberInfoDto memberInfoDto) {
        return memberInfoDto.getImage() != null
                ? updateMemberProfileWithImage(userId, memberInfoDto)
                : updateMemberProfileWithoutImage(userId, memberInfoDto);
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
        return myRepository.updateProfile(userId, memberProfileEntity);
    }

    public MemberProfileEntity getMemberProfile(Long userId){
        return myRepository.findProfileByUserId(userId);
    }

    private MemberRepository getMemberRepository(LoginType loginType) {
        MemberRepository selectedMemberRepository = repositoryMap.get(loginTypeMap.get(loginType));
        return selectedMemberRepository;
    }

}
