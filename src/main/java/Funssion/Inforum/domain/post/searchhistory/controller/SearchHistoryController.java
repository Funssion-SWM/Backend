package Funssion.Inforum.domain.post.searchhistory.controller;

import Funssion.Inforum.domain.post.searchhistory.dto.response.SearchHistoryDto;
import Funssion.Inforum.domain.post.searchhistory.service.SearchHistoryService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchHistoryController {

    private final SearchHistoryService service;

    @GetMapping("/history")
    public List<SearchHistoryDto> getRecentSearchHistoryTop10() {
        return service.getRecentSearchHistoryTop10();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/history")
    public void addSearchHistory(
            @RequestParam String searchString,
            @RequestParam Boolean isTag
    ) {
        service.addSearchHistory(searchString, isTag);
    }

    @DeleteMapping("/history/{id}")
    public void removeSearchHistory(
            @PathVariable @Min(1) Long id
    ) {
        service.removeSearchHistory(id);
    }
}
