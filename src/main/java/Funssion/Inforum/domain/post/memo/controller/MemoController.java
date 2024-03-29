package Funssion.Inforum.domain.post.memo.controller;

import Funssion.Inforum.common.constant.DateType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoIDListDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.post.memo.service.MemoService;
import Funssion.Inforum.s3.dto.response.ImageDto;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static Funssion.Inforum.common.utils.SecurityContextUtils.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/memos")
public class MemoController {

    private final MemoService memoService;
    @GetMapping
    public List<MemoListDto> getMemos(
            @RequestParam(required = false, defaultValue = "MONTH") DateType period,
            @RequestParam(required = false, defaultValue = "HOT") OrderType orderBy,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Long pageNum,
            @RequestParam(required = false, defaultValue = "12") @Min(1) Long resultCntPerPage
    ) {
        return memoService.getMemosForMainPage(period, orderBy, pageNum, resultCntPerPage);
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

    @GetMapping("/{id}/recommendations")
    public List<MemoListDto> getMemoRecommendationsByTags(
            @PathVariable @Min(1) Long id
    ) {
        return memoService.getMemoRecommendations(id);
    }

    @PostMapping("/{id}/image")
    public ImageDto uploadImageInMemo(
            @PathVariable @Min(1) Long id,
            @RequestPart MultipartFile image
    ) {
        return memoService.uploadImageInMemo(id, image);
    }

    @GetMapping("/search")
    public List<MemoListDto> getSearchedMemos(
            @RequestParam(required = false) String searchString,
            @RequestParam(required = false, defaultValue =  ANONYMOUS_USER_ID_STRING) @Min(0) Long userId,
            @RequestParam OrderType orderBy,
            @RequestParam Boolean isTag,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Long pageNum,
            @RequestParam(required = false, defaultValue = "12") @Min(1) Long resultCntPerPage
    ) {
        if (userId.equals(ANONYMOUS_USER_ID) && (Objects.isNull(searchString) || searchString.isBlank())) {
            return new ArrayList<>();
        }

        return memoService.searchMemosBy(searchString, userId, orderBy, isTag, pageNum, resultCntPerPage);
    }

    @GetMapping("/drafts")
    public List<MemoListDto> getDraftMemos() {
        return memoService.getDraftMemos();
    }

    @GetMapping("/ids")
    public List<MemoIDListDto> getMemoIds() {
        return memoService.getMemoIds();
    }
}
