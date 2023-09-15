package Funssion.Inforum.domain.member.service;

class MemberServiceTest {
//    @Mock
//    MyRepository myRepository;
//    @Mock
//    MemoRepository memoRepository;
//    @Mock
//    S3Repository s3Repository;
//    @Mock
//    MemberRepositoryImpl nonSocialMemberRepository;
//    @Mock
//    SocialMemberRepository socialMemberRepository;
//
//    HashMap<LoginType, String> loginTypeMap = new HashMap<>();
//    {
//        loginTypeMap.put(LoginType.NON_SOCIAL,"nonSocialMemberRepository");
//        loginTypeMap.put(LoginType.SOCIAL, "socialMemberRepository");
//    }
//    Map<String, MemberRepository> repositoryMap = new HashMap<>();
//    repositoryMap.put("nonSocialMemberRepository", nonSocialMemberRepository);
//    repositoryMap.put("socialMemberRepository", socialMemberRepository);
//
//    @InjectMocks
//    MemberService memberService;
//    @Nested
//    @DisplayName("등록된 회원 정보 찾기 - nonSocial")
//    class findUserInfo{
//        MemberRepository memberRepository = repositoryMap.get(loginTypeMap.get(LoginType.NON_SOCIAL));
//        NonSocialMember nonSocialMember = NonSocialMember.createNonSocialMember(new MemberSaveDto(
//                "username test",
//                LoginType.NON_SOCIAL,
//                "test@gmail.com",
//                "a1234567!"
//        ));
//        SaveMemberResponseDto saveMemberResponseDto = SaveMemberResponseDto.builder()
//                .email(nonSocialMember.getUserEmail())
//                .id(1L)
//                .loginType(LoginType.NON_SOCIAL)
//                .createdDate(LocalDateTime.now())
//                .name(nonSocialMember.getUserName())
//                .build();
//        @Test
//        @DisplayName("등록한 닉네임으로 이메일 찾기")
//        void findEmailByUsername(){
//            when(memberRepository.findEmailByNickname(nonSocialMember.getUserName())).thenReturn(saveMemberResponseDto.getEmail());
//            Assertions.assertThat(memberService.findEmailByNickname(nonSocialMember.getUserName()))
//                    .isEqualTo(saveMemberResponseDto.getEmail());
//
//
//
//        }
//    }
    /*
     * <requestMemberRegistration>
     * 1.중복아닌거 가정하고 / NonSocial 로그인 타입 요청시 / 저장 객체 반환
     * 2.중복이면 ? / .. / throw duplication
     * 3.중복아니고, social 이면 / .. / throw
     *
     */

}