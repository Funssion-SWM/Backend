package Funssion.Inforum.common.tag.controller;

import Funssion.Inforum.common.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/tags")
public class TagController {
    private final TagService tagService;
    @GetMapping
    public List<String> getTags(){
        return tagService.getAllTags();
    }
}
