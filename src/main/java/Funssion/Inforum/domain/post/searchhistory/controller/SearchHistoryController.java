package Funssion.Inforum.domain.post.searchhistory.controller;

import Funssion.Inforum.domain.post.searchhistory.dto.response.SearchHistoryDto;
import Funssion.Inforum.domain.post.searchhistory.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchHistoryController {

    private final SearchHistoryService service;

    @GetMapping("/history")
    public List<SearchHistoryDto> getRecentSearchHistoryTop10() {
        return service.getRecentSearchHistoryTop10();
    }
}
