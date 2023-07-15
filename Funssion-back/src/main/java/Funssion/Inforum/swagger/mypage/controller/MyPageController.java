package Funssion.Inforum.swagger.mypage.controller;

import Funssion.Inforum.swagger.ErrorResponse;
import Funssion.Inforum.swagger.mypage.response.ContentList;
import Funssion.Inforum.swagger.mypage.response.History;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Tag(name = "MyPage", description = "마이페이지 API, 토큰 정보 필요")
public class MyPageController{
    @Operation(summary = "내가 쓴 글 조회",description = "자기가 작성한 모든글을 볼 수 있게 함 (헤더 access token 필요)", tags = {"MyPage"})
    @GetMapping("/list/{category}/users?id={userId}")
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 카테고리 리스트 조회 성공", content = @Content(schema=@Schema(implementation= ContentList.class),mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 유저 고유 ID정보", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증받지 않은 사용자 정보", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "권한없는 사용자 정보", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "유저 정보 없음", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
    })
    public String getMyCategoryList(@Parameter(description="카테고리 명", required = true, example="memo, story, qna",in = ParameterIn.PATH) String category,@Parameter(description="유저 고유 ID", required = true,in = ParameterIn.QUERY) Integer userId){
        return "ok";
    }

    @Operation(summary = "히스토리 기능(잔디심기)",description = "자기가 채운 잔디를 보게함 (비공개라면 토큰필요)", tags = {"MyPage"})
    @GetMapping("/history/users?id={userId}")
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "잔디 조회 성공", content = @Content(schema=@Schema(implementation= History.class),mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 유저 고유 ID정보", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증받지 않은 사용자 정보", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "권한없는 사용자 정보", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "유저 정보 없음", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
    })
    public String getMyHistory(@Parameter(description="유저 고유 ID", required = true,in = ParameterIn.QUERY) Integer userId){
        return "ok";
    }

}