package Funssion.Inforum.domain.post.memo.service;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.etc.ArrayToListException;
import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.follow.repository.FollowRepository;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.notification.repository.NotificationRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import Funssion.Inforum.domain.score.ScoreRepository;
import Funssion.Inforum.domain.score.ScoreService;
import Funssion.Inforum.domain.tag.repository.TagRepository;
import Funssion.Inforum.s3.S3Repository;
import Funssion.Inforum.s3.S3Utils;
import Funssion.Inforum.s3.dto.response.ImageDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static Funssion.Inforum.common.constant.CRUDType.READ;
import static Funssion.Inforum.common.constant.DateType.*;
import static Funssion.Inforum.common.constant.OrderType.HOT;
import static Funssion.Inforum.common.constant.OrderType.NEW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class MemoServiceTest {

    @Mock MemoRepository memoRepository;
    @Mock TagRepository tagRepository;
    @Mock MyRepository myRepository;
    @Mock S3Repository s3Repository;
    @Mock
    ScoreRepository scoreRepository;
    @Mock
    ScoreService scoreService;
    @Mock FollowRepository followRepository;
    @Mock NotificationRepository notificationRepository;
    @InjectMocks MemoService memoService;

    MockedStatic<SecurityContextUtils> mockSecurityContextUtils;
    MockedStatic<AuthUtils> mockAuthUtils;

    static Memo memo1;
    static Memo memo2;
    static Memo memo3;
    static Memo memo4;
    static Memo memo5;
    static MemoListDto memoListDto1;
    static MemoListDto memoListDto2;
    static MemoListDto memoListDto3;
    static MemoListDto memoListDto4;
    static MemoListDto memoListDto5;
    static MemoDto memoDto1;
    static MemoDto memoDto2;
    static MemoDto memoDto3;
    static MemoDto memoDto4;
    static MemoDto memoDto5;
    static Long userID1 = 1L;
    static Long userID2 = 2L;
    static Long memoID1 = 1L;
    static Long memoID2 = 2L;
    static Long memoID3 = 3L;
    static Long memoID4 = 4L;
    static Long memoID5 = 5L;

    static Long DEFAULT_MEMO_CNT = 20L;
    static Long DEFAULT_PAGE_NUM = 0L;

    @BeforeAll
    static void beforeAll() {
        memo1 = Memo.builder()
                .id(memoID1)
                .title("JPA")
                .text("JPA is JPA")
                .description("JPA is ...")
                .color("yellow")
                .authorId(userID1)
                .authorName("jinu")
                .authorImagePath("jinu-image")
                .createdDate(LocalDateTime.now().minusDays(1))
                .likes(0L)
                .isTemporary(false)
                .memoTags(List.of("Java", "JPA"))
                .build();
        memo2 = Memo.builder()
                .id(memoID2)
                .title("JDK")
                .text("JDK is JDK")
                .description("JDK is ...")
                .color("blue")
                .authorId(userID1)
                .authorName("jinu")
                .authorImagePath("jinu-image")
                .createdDate(LocalDateTime.now())
                .likes(10000L)
                .isTemporary(false)
                .memoTags(List.of("Java", "Spring", "JDK"))
                .build();
        memo3 = Memo.builder()
                .id(memoID3)
                .title("JWT")
                .text("JWT is JWT")
                .description("JWT is ...")
                .color("yellow")
                .authorId(userID2)
                .authorName("jinu2")
                .authorImagePath("jinu2-image")
                .createdDate(LocalDateTime.now())
                .likes(9999L)
                .isTemporary(false)
                .memoTags(List.of("Java", "Spring-Security", "JWT"))
                .build();
        memo4 = Memo.builder()
                .id(memoID4)
                .title("JSP")
                .text("JSP is JSP")
                .description("JSP is ...")
                .color("black")
                .authorId(userID2)
                .authorName("jinu2")
                .authorImagePath("jinu2-image")
                .createdDate(LocalDateTime.now())
                .likes(0L)
                .isTemporary(true)
                .memoTags(List.of("JSP"))
                .isCreated(Boolean.FALSE)
                .build();
        memo5 = Memo.builder()
                .id(memoID5)
                .title("Junit")
                .text("Junit is Junit")
                .description("Junit is ...")
                .color("yellow")
                .authorId(userID2)
                .authorName("jinu2")
                .authorImagePath("jinu2-image")
                .createdDate(LocalDateTime.now())
                .likes(0L)
                .isTemporary(true)
                .memoTags(List.of("Junit"))
                .isCreated(Boolean.TRUE)
                .build();

        memoListDto1 = new MemoListDto(memo1);
        memoListDto2 = new MemoListDto(memo2);
        memoListDto3 = new MemoListDto(memo3);
        memoListDto4 = new MemoListDto(memo4);
        memoListDto5 = new MemoListDto(memo5);

        memoDto1 = new MemoDto(memo1);
        memoDto2 = new MemoDto(memo2);
        memoDto3 = new MemoDto(memo3);
        memoDto4 = new MemoDto(memo4);
        memoDto5 = new MemoDto(memo5);
    }

    @BeforeEach
    void beforeEach() {
        mockSecurityContextUtils = mockStatic(SecurityContextUtils.class);
        mockAuthUtils = mockStatic(AuthUtils.class);
    }

    @AfterEach
    void afterEach() {
        mockSecurityContextUtils.close();
        mockAuthUtils.close();
    }


    @Nested()
    @DisplayName("메모 조회하기")
    class MemoRead {

        @Test
        @DisplayName("메인 페이지 메모 조회")
        void getMemosForMainPage() {
            given(memoRepository.findAllOrderById(DEFAULT_PAGE_NUM, DEFAULT_MEMO_CNT))
                    .willReturn(List.of(memo3, memo2, memo1));
            given(memoRepository.findAllByDaysOrderByLikes(ArgumentMatchers.any(), eq(DEFAULT_PAGE_NUM), eq(DEFAULT_MEMO_CNT)))
                    .willReturn(List.of(memo2, memo3, memo1));
            given(memoRepository.findAllByDaysOrderByLikes(ArgumentMatchers.eq(DAY), eq(DEFAULT_PAGE_NUM), eq(DEFAULT_MEMO_CNT)))
                    .willReturn(List.of(memo2, memo3));

            List<MemoListDto> memosForMainPageOrderByDays = memoService.getMemosForMainPage(WEEK, NEW, DEFAULT_PAGE_NUM, DEFAULT_MEMO_CNT);
            List<MemoListDto> memosForMainPageOrderByLikesByMonth = memoService.getMemosForMainPage(MONTH, HOT, DEFAULT_PAGE_NUM, DEFAULT_MEMO_CNT);
            List<MemoListDto> memosForMainPageOrderByLikesByDay = memoService.getMemosForMainPage(DAY, HOT, DEFAULT_PAGE_NUM, DEFAULT_MEMO_CNT);


            assertThat(memosForMainPageOrderByDays).containsExactly(memoListDto3, memoListDto2, memoListDto1);
            assertThat(memosForMainPageOrderByLikesByMonth).containsExactly(memoListDto2, memoListDto3, memoListDto1);
            assertThat(memosForMainPageOrderByLikesByDay).containsExactly(memoListDto2, memoListDto3);
        }


        @Nested
        @DisplayName("임시 메모 조회")
        class getDraftMemos {

            @Test
            @DisplayName("비 로그인 유저 케이스")
            void getDraftsWithOutLogin() {

                given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                        .willThrow(UnAuthorizedException.class);
                given(AuthUtils.getUserId(READ))
                        .willReturn(SecurityContextUtils.ANONYMOUS_USER_ID);

                given(memoRepository.findAllDraftMemosByUserId(SecurityContextUtils.ANONYMOUS_USER_ID))
                        .willReturn(List.of());

                List<MemoListDto> draftMemos = memoService.getDraftMemos();

                assertThat(draftMemos).isEmpty();
            }

            @Test
            @DisplayName("로그인 유저 케이스")
            void getDraftsWithLogin() {

                given(AuthUtils.getUserId(any()))
                        .willReturn(userID1);

                given(memoRepository.findAllDraftMemosByUserId(AdditionalMatchers.not(eq(SecurityContextUtils.ANONYMOUS_USER_ID))))
                        .willReturn(List.of(memo4));

                List<MemoListDto> draftMemos = memoService.getDraftMemos();

                assertThat(draftMemos).containsExactly(memoListDto4);

            }
        }

        @Nested
        @DisplayName("메모 ID로 메모 조회")
        class getMemo {
            @Test
            @DisplayName("로그인한 유저 케이스")
            void getMemoWithLogin() {

                given(SecurityContextUtils.getUserId()).willReturn(userID1);


                given(memoRepository.findById(eq(memoID1)))
                        .willReturn(memo1);
                given(memoRepository.findById(eq(memoID3)))
                        .willReturn(memo3);

                MemoDto found1 = memoService.getMemoBy(memoID1);
                MemoDto found3 = memoService.getMemoBy(memoID3);

                assertThat(found1).isEqualTo(memoDto1);
                assertThat(found1.getIsMine()).isTrue();

                assertThat(found3).isEqualTo(memoDto3);
                assertThat(found3.getIsMine()).isFalse();

            }

            @Test
            @DisplayName("비로그인 유저 케이스")
            void getMemoWithOutLogin() {

                given(SecurityContextUtils.getUserId())
                        .willReturn(SecurityContextUtils.ANONYMOUS_USER_ID);

                given(memoRepository.findById(eq(memoID1)))
                        .willReturn(memo1);

                MemoDto memo = memoService.getMemoBy(memoID1);

                assertThat(memo).isEqualTo(memoDto1);
                assertThat(memo.getIsMine()).isFalse();

            }
        }

        @Nested
        @DisplayName("메모 검색")
        class searchMemo {

            @Test
            @DisplayName("유저 ID, 태그로 메모 검색")
            void searchMemosByTagAndUserID() {
                given(memoRepository.findAllByTag(any(), AdditionalMatchers.not(eq(SecurityContextUtils.ANONYMOUS_USER_ID)), any()))
                        .willReturn(List.of(memo2, memo1));

                List<MemoListDto> searchMemos = memoService.searchMemosBy("Java", userID1, HOT, true);

                assertThat(searchMemos).containsExactly(memoListDto2, memoListDto1);
            }

            @Test
            @DisplayName("태그로 메모 검색")
            void searchMemosByTag() {
                given(memoRepository.findAllByTag(any(), any()))
                        .willReturn(List.of(memo3, memo2));

                List<MemoListDto> searchMemos = memoService.searchMemosBy("Java", SecurityContextUtils.ANONYMOUS_USER_ID, NEW, true);

                assertThat(searchMemos).containsExactly(memoListDto3, memoListDto2);
            }

            @Test
            @DisplayName("텍스트로 메모 검색")
            void searchMemosBySearchString() {
                given(memoRepository.findAllBySearchQuery(any(), any(), any()))
                        .willReturn(List.of(memo3, memo2, memo1));

                List<MemoListDto> searchMemos = memoService.searchMemosBy("Java", SecurityContextUtils.ANONYMOUS_USER_ID, HOT, false);

                assertThat(searchMemos).containsExactly(memoListDto3, memoListDto2, memoListDto1);
            }
        }
    }

    @Nested
    @DisplayName("메모 생성")
    class createMemo {

        MemoSaveDto memoSaveDto = MemoSaveDto.valueOf(memo1);

        MemoSaveDto tempMemoSaveDto = MemoSaveDto.valueOf(memo4);

        @Test
        @DisplayName("로그인 하지 않은 케이스")
        void createMemoWithoutLogin() {
            given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                    .willThrow(UnAuthorizedException.class);

            assertThatThrownBy(() -> memoService.createMemo(memoSaveDto))
                    .isInstanceOf(UnAuthorizedException.class);

        }

        @Test
        @DisplayName("로그인 후 오늘의 첫 일반 메모 저장")
        void createMemoWithLogin() {

            given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                    .willReturn(userID1);
            given(myRepository.findProfileByUserId(userID1))
                    .willReturn(new MemberProfileEntity());
            given(memoRepository.create(any()))
                    .willReturn(memo1);
            given(tagRepository.saveTags(any(), any()))
                    .willReturn(new IsSuccessResponseDto(true, "save success"));
            given(followRepository.findFollowedUserIdByUserId(any()))
                    .willReturn(Collections.emptyList());
            willThrow(HistoryNotFoundException.class)
                    .given(myRepository)
                    .updateHistory(any(), any(), any(), any());

            MemoDto memo = memoService.createMemo(memoSaveDto);

            assertThat(memoSaveDto).isEqualTo(MemoSaveDto.valueOf(memo));
        }

        @Test
        @DisplayName("로그인 후 오늘의 첫번째가 아닌 임시 메모 저장")
        void createTempMemoWithLogin() {
            given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                    .willReturn(userID1);
            given(myRepository.findProfileByUserId(userID1))
                    .willReturn(new MemberProfileEntity());
            given(memoRepository.create(any()))
                    .willReturn(memo4);
            given(tagRepository.saveTags(any(), any()))
                    .willReturn(new IsSuccessResponseDto(true, "save success"));

            MemoDto memo = memoService.createMemo(tempMemoSaveDto);

            assertThat(tempMemoSaveDto).isEqualTo(MemoSaveDto.valueOf(memo));
        }
    }

    @Nested
    @DisplayName("메모 수정하기")
    class updateMemo {

        MemoSaveDto memoSaveDto = MemoSaveDto.valueOf(memo2);
        MemoSaveDto tempMemoSaveDto = MemoSaveDto.valueOf(memo4);

        @Test
        @DisplayName("로그인 하지 않은 케이스")
        void updateMemoWithoutLogin() {
            given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                    .willThrow(UnAuthorizedException.class);

            assertThatThrownBy(() -> memoService.updateMemo(memoID2, memoSaveDto))
                    .isInstanceOf(UnAuthorizedException.class);
        }

        @Test
        @DisplayName("로그인 후 다른 사람의 메모를 수정하려하는 케이스")
        void updateMemoAnotherAuthorWrites() {
            given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                    .willReturn(userID2);
            given(memoRepository.findById(memoID2))
                    .willReturn(memo2);

            assertThatThrownBy(() -> memoService.updateMemo(memoID2, memoSaveDto))
                    .isInstanceOf(UnAuthorizedException.class);
        }

        @Nested
        @DisplayName("실제 글로 등록")
        class updateMemoToRealMemo {

            @Test
            @DisplayName("로그인 후 이미 등록된 글을 정상적으로 재등록하는 케이스")
            void updateMemoWithLogin() {
                given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                        .willReturn(userID1);
                given(memoRepository.findById(memoID2))
                        .willReturn(memo2);
                given(memoRepository.updateContentInMemo(memoSaveDto, memoID2))
                        .willReturn(memo2);

                MemoDto updated = memoService.updateMemo(memoID2, memoSaveDto);

                assertThat(memoSaveDto).isEqualTo(MemoSaveDto.valueOf(updated));
            }

            @Test
            @DisplayName("임시 글을 최초 등록하는 케이스")
            void updateTempMemoToRealMemoFirstTime() {
                given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                        .willReturn(userID2);
                given(memoRepository.findById(memoID4))
                        .willReturn(memo4);
                given(memoRepository.updateContentInMemo(memoSaveDto, memoID4, Boolean.TRUE))
                        .willReturn(memo2);
                given(followRepository.findFollowedUserIdByUserId(any()))
                        .willReturn(Collections.emptyList());

                MemoDto updated = memoService.updateMemo(memoID4, memoSaveDto);

                assertThat(memoSaveDto).isEqualTo(MemoSaveDto.valueOf(updated));
            }

            @Test
            @DisplayName("로그인 후 등록했다가 임시저장된 글을 다시 등록하는 케이스")
            void updateTempMemoToRealMemo() {
                given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                        .willReturn(userID2);
                given(memoRepository.findById(memoID5))
                        .willReturn(memo5);
                given(memoRepository.updateContentInMemo(memoSaveDto, memoID5))
                        .willReturn(memo2);
                given(followRepository.findFollowedUserIdByUserId(any()))
                        .willReturn(Collections.emptyList());

                MemoDto updated = memoService.updateMemo(memoID5, memoSaveDto);

                assertThat(memoSaveDto).isEqualTo(MemoSaveDto.valueOf(updated));
            }

        }


        @Nested
        @DisplayName("임시 글로 수정")
        class updateTempMemoToRealMemoWithLogin {
            @Test
            @DisplayName("등록된 글을 수정 중 임시 저장하는 케이스")
            void updateTempMemoToRealMemoWithLogin() {
                given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                        .willReturn(userID1);
                given(memoRepository.findById(memoID2))
                        .willReturn(memo2);
                given(memoRepository.updateContentInMemo(tempMemoSaveDto, memoID2))
                        .willReturn(memo4);

                MemoDto updated = memoService.updateMemo(memoID2, tempMemoSaveDto);

                assertThat(tempMemoSaveDto).isEqualTo(MemoSaveDto.valueOf(updated));
            }

            @Test
            @DisplayName("임시 저장된 글을 다시 임시 저장하는 케이스")
            void updateTempMemoToTempMemo() {
                given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                        .willReturn(userID2);
                given(memoRepository.findById(memoID5))
                        .willReturn(memo5);
                given(memoRepository.updateContentInMemo(tempMemoSaveDto, memoID5))
                        .willReturn(memo4);

                MemoDto updated = memoService.updateMemo(memoID5, tempMemoSaveDto);

                assertThat(tempMemoSaveDto).isEqualTo(MemoSaveDto.valueOf(updated));
            }

        }

        @Test
        @DisplayName("로그인 후 등록된 글을 임시 글로 수정하다가 Array 변환에서 예외가 발생하는 케이스")
        void updateMemoToTempMemoWithLoginEx() throws SQLException {
            given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                    .willReturn(userID2);
            given(memoRepository.findById(memoID4))
                    .willReturn(memo4);
            given(tagRepository.updateTags(eq(memoID4), any()))
                    .willThrow(SQLException.class);
            given(followRepository.findFollowedUserIdByUserId(any()))
                    .willReturn(Collections.emptyList());

            assertThatThrownBy(() -> memoService.updateMemo(memoID4, memoSaveDto))
                    .isInstanceOf(ArrayToListException.class);
        }
    }

    @Nested
    @DisplayName("S3 이미지 업로드")
    class uploadImageInMemo {

        MockedStatic<S3Utils> utils;
        byte[] dummyContent = {0};
        @BeforeEach
        void beforeEach() {
            utils = mockStatic(S3Utils.class);
        }

        @AfterEach
        void afterEach() {
            utils.close();
        }

        @Test
        @DisplayName("로그인 하지 않은 케이스")
        void uploadImageInMemoWithLogin() {
            given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                    .willThrow(UnAuthorizedException.class);

            assertThatThrownBy(() -> memoService.uploadImageInMemo(memoID1, new MockMultipartFile("image", dummyContent)))
                    .isInstanceOf(UnAuthorizedException.class);
        }

        @Test
        @DisplayName("성공 케이스")
        void uploadImageInMemo() {
            given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                    .willReturn(userID1);
            given(S3Utils.generateImageNameOfS3(userID1))
                    .willReturn("imageName");
            given(s3Repository.createFolder(any(), any()))
                    .willReturn("bucketName");
            given(s3Repository.upload(any(), any(), any()))
                    .willReturn("upload url");

            ImageDto image = memoService.uploadImageInMemo(memoID1, new MockMultipartFile("image", dummyContent));

            assertThat(image.getImageName()).isEqualTo("imageName");
            assertThat(image.getImagePath()).isEqualTo("upload url");

        }
    }

    @Nested
    @DisplayName("메모 삭제하기")
    class deleteMemo {

        @Test
        @DisplayName("로그인 하지 않은 케이스")
        void deleteMemoWithoutLogin() {
            given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                    .willThrow(UnAuthorizedException.class);

            assertThatThrownBy(() -> memoService.deleteMemo(memoID1))
                    .isInstanceOf(UnAuthorizedException.class);
        }

        @Test
        @DisplayName("로그인 후 다른 사람의 메모를 삭제하려는 케이스")
        void deleteMemoAnotherAuthorWrites() {
            given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                    .willReturn(userID2);
            given(memoRepository.findById(memoID1))
                    .willReturn(memo1);

            assertThatThrownBy(() -> memoService.deleteMemo(memoID1))
                    .isInstanceOf(UnAuthorizedException.class);
        }

        @Test
        @DisplayName("로그인 후 일반 메모 삭제 Array 예외 케이스")
        void deleteMemoWithLoginArrayEx() throws SQLException {
            given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                    .willReturn(userID1);
            given(memoRepository.findById(memoID1))
                    .willReturn(memo1);
            given(tagRepository.deleteTags(memoID1))
                    .willThrow(SQLException.class);

            assertThatThrownBy(() -> memoService.deleteMemo(memoID1))
                    .isInstanceOf(ArrayToListException.class);
        }

        @Test
        @DisplayName("로그인 후 임시 메모 삭제 성공 케이스")
        void deleteMemoWithLogin() {
            given(AuthUtils.getUserId(AdditionalMatchers.not(eq(READ))))
                    .willReturn(userID2);
            given(memoRepository.findById(memoID4))
                    .willReturn(memo4);

            memoService.deleteMemo(memoID4);
        }
    }
}
