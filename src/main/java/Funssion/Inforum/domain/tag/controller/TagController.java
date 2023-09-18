package Funssion.Inforum.domain.tag.controller;

import Funssion.Inforum.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/tags")
public class TagController {
    private final TagService tagService;
    @GetMapping
    public List<String> getDefaultTags(){
        return tagService.getDefaultTags();
    }

    @GetMapping("/{userId}")
    public List<String> getUserTags(
            @PathVariable Long userId,
            @RequestParam(required = false) @Nullable Integer tagCnt
    ) {
        return tagService.getUserTags(userId, tagCnt);
    }
}
