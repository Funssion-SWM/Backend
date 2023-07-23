package Funssion.Inforum.domain.memo.controller;

import Funssion.Inforum.domain.memo.dto.MemoDto;
import Funssion.Inforum.domain.memo.dto.MemoListDto;
import Funssion.Inforum.domain.memo.dto.MemoSaveDto;
import Funssion.Inforum.domain.memo.service.MemoService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/memos")
public class MemoController {

    private final MemoService memoService;

    @GetMapping
    public List<MemoListDto> getMemoList(
            @RequestParam(required = false, defaultValue = "DAY") String period,
            @RequestParam(required = false, defaultValue = "NEW") String orderBy) {
        List<MemoListDto> memos = memoService.getMemosInMainPage(period, orderBy);
        return memos;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public MemoDto addMemo(@Validated @RequestBody MemoSaveDto memoSaveDto) {
        log.debug("동우 들어옴");
        return memoService.createMemo(memoSaveDto);
    }

    @GetMapping("/{id}")
    public MemoDto getMemoDetails(@PathVariable int id) {
        return memoService.getMemoBy(id);
    }

    @PostMapping("/{id}")
    public MemoDto modifyMemo(@PathVariable int id, @Validated @RequestBody MemoSaveDto memoSaveDto) {
        return memoService.updateMemo(id, memoSaveDto);
    }

    @DeleteMapping("/{id}")
    public void removeMemo(@PathVariable int id) {
        memoService.deleteMemo(id);
    }

}
