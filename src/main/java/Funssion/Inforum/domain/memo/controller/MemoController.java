package Funssion.Inforum.domain.memo.controller;

import Funssion.Inforum.domain.memo.dto.MemoDto;
import Funssion.Inforum.domain.memo.dto.MemoListDto;
import Funssion.Inforum.domain.memo.dto.MemoSaveDto;
import Funssion.Inforum.domain.memo.service.MemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/memos")
public class MemoController {

    private final MemoService memoService;

    @GetMapping
    public ArrayList<MemoListDto> memoList(
            @RequestParam(required = false, defaultValue = "day") String period,
            @RequestParam(required = false, defaultValue = "new") String orderBy) {
        ArrayList<MemoListDto> memos = memoService.getMemosInMainPage(period, orderBy);
        log.debug("memos={}",memos);
        return memos;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public MemoDto memoAdd(@Validated @RequestBody MemoSaveDto memoSaveDto) {
        return memoService.createMemo(memoSaveDto);
    }

    @GetMapping("/{id}")
    public MemoDto memoDetail(@PathVariable int id) {
        return memoService.getMemoBy(id);
    }

    @PostMapping("/{id}")
    public MemoDto memoModify(@PathVariable int id, @Validated @RequestBody MemoSaveDto memoSaveDto) {
        return memoService.updateMemo(id, memoSaveDto);
    }

    @DeleteMapping("/{id}")
    public void memoRemove(@PathVariable int id) {
        memoService.deleteMemo(id);
    }

}
