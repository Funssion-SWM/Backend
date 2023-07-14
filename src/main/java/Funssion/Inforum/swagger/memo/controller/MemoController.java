package Funssion.Inforum.swagger.memo.controller;

import Funssion.Inforum.swagger.memo.entity.MemoEntity;
import Funssion.Inforum.swagger.memo.request.MemoCreateDataForm;
import Funssion.Inforum.swagger.memo.response.MemoListDataForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/memos")
@Tag(name = "memo", description = "memo API")
public class MemoController {
    @Operation(summary = "Get memo list",description = "get memo list in main page and my page", tags = {"memo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "bad request, query is not defined", content = @Content),
            @ApiResponse(responseCode = "404", description = "user not found", content = @Content),
    })
    @GetMapping
    public ArrayList<MemoListDataForm> memos(@Parameter(description = "criteria in hot memos, period={day, week, month, year}, defult is day") @RequestParam(required = false) String period,
                                        @Parameter(description = "criteria in sorting, orderBy={hot, new}, default is hot") @RequestParam(required = false) String orderBy,
                                        @Parameter(description = "get one user's memo list") @RequestParam(required = false) String userId) {
        ArrayList<MemoListDataForm> memos = new ArrayList<>();
        memos.add(new MemoListDataForm(1,"JDK란?", "JDK이다", "green", LocalDate.now(), "1", "정진우"));
        memos.add(new MemoListDataForm(2,"JPA란?", "JPA이다", "black", LocalDate.now(), "2", "김태훈"));
        return memos;
    }

    @Operation(summary = "Get memo", tags = {"memo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Memo not found", content = @Content),
    })
    @GetMapping("{id}")
    public MemoEntity memo(@Parameter(description = "memo id") @PathVariable String id) {
        return new MemoEntity(1,  "JDK란?", "JDK이다.", "green", 1, "정진우", LocalDate.now(), LocalDate.now());
    }

    @Operation(summary = "Create memo", tags = {"memo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successful created", content = @Content(mediaType = "application/json")),
    })
    @PostMapping(consumes = "application/json")
    public MemoEntity creteMemo(@Parameter(description = "create form") @RequestBody MemoCreateDataForm createDataForm) {
        return new MemoEntity(1,  "JDK란?", "JDK이다.", "green",1, "정진우", LocalDate.now(), LocalDate.now());
    }

    @Operation(summary = "Update memo", tags = {"memo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful updated", content = @Content),
            @ApiResponse(responseCode = "404", description = "memo not found", content = @Content),
    })
    @PostMapping(value = "{id}",consumes = "application/json")
    public MemoEntity updateMemo(@Parameter(description = "create form") @RequestBody MemoCreateDataForm createDataForm,
                           @Parameter(description = "memo id") @PathVariable int id) {
        return new MemoEntity(1,  "JDK란?", "JDK이다.", "green",1, "정진우", LocalDate.now(), LocalDate.now());
    }

    @Operation(summary = "Delete memo", tags = {"memo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "memo not found", content = @Content),
    })
    @DeleteMapping(value = "{id}")
    public void deleteMemo(@Parameter(description = "memo id") @PathVariable int id) {}
}
