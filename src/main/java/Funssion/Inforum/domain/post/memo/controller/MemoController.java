package Funssion.Inforum.domain.post.memo.controller;

import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.post.memo.service.MemoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/memos")
public class MemoController {

    private final MemoService memoService;

    @GetMapping
    public List<MemoListDto> getMemos(
            @RequestParam(required = false, defaultValue = "MONTH") String period,
            @RequestParam(required = false, defaultValue = "HOT") String orderBy) {
        log.info("pr = {}, or = {}", period, orderBy);
        return memoService.getMemosForMainPage(period, orderBy);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public MemoDto addMemo(@Validated @RequestBody MemoSaveDto memoSaveDto) {
        return memoService.createMemo(memoSaveDto);
    }

    @GetMapping("/{id}")
    public MemoDto getMemoDetails(@PathVariable @Min(1) Long id) {
        return memoService.getMemoBy(id);
    }

    @PostMapping("/{id}")
    public MemoDto modifyMemo(@PathVariable @Min(1) Long id, @Validated @RequestBody MemoSaveDto memoSaveDto) {
        return memoService.updateMemo(id, memoSaveDto);
    }

    @DeleteMapping("/{id}")
    public void removeMemo(@PathVariable @Min(1) Long id) {
        memoService.deleteMemo(id);
    }

    @GetMapping("/search")
    public List<MemoListDto> getSearchedMemos(
            @RequestParam(name = "q") @NotBlank String searchString,
            @RequestParam(required = false, defaultValue = "HOT") String orderBy
    ) {
        return memoService.getMemosBy(searchString, orderBy);
    }

    @GetMapping("/drafts")
    public List<MemoListDto> getDraftMemos() {
        return memoService.getDraftMemos();
    }

//    @GetMapping("/comments")
//    public List<CommentListDto> getMemoComments(
//
//    )

}
