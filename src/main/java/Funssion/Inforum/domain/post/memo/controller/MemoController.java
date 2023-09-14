package Funssion.Inforum.domain.post.memo.controller;

import Funssion.Inforum.common.constant.memo.MemoOrderType;
import Funssion.Inforum.common.exception.BadRequestException;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.post.memo.service.MemoService;
import Funssion.Inforum.s3.dto.response.ImageDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        log.info("tagList = {}",memoSaveDto.getMemoTags());
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

    @PostMapping("/{id}/image")
    public ImageDto uploadImageInMemo(
            @PathVariable @Min(1) Long id,
            @RequestPart MultipartFile image
    ) {
        return memoService.uploadImageInMemo(id, image);
    }

    @GetMapping("/search")
    public List<MemoListDto> getSearchedMemos(
            @RequestParam @NotBlank String searchString,
            @RequestParam String orderBy,
            @RequestParam Boolean isTag
    ) {
        return memoService.getMemosBy(
                searchString, getOrderBy(orderBy), isTag);
    }

    private static MemoOrderType getOrderBy(String orderBy) {
        try {
            return MemoOrderType.valueOf(orderBy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @GetMapping("/drafts")
    public List<MemoListDto> getDraftMemos() {
        return memoService.getDraftMemos();
    }
}
