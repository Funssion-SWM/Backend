package Funssion.Inforum.domain.post.series.controller;

import Funssion.Inforum.common.constant.DateType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.utils.CustomListUtils;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.series.dto.request.SeriesRequestDto;
import Funssion.Inforum.domain.post.series.dto.response.SeriesCreateResponseDto;
import Funssion.Inforum.domain.post.series.dto.response.SeriesListDto;
import Funssion.Inforum.domain.post.series.dto.response.SeriesResponseDto;
import Funssion.Inforum.domain.post.series.service.SeriesService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/series")
public class SeriesController {

    private final SeriesService seriesService;

    @GetMapping
    public List<SeriesListDto> getSeriesList(
            @RequestParam(required = false) @Min(1) Long authorId,
            @RequestParam(required = false) String searchString,
            @RequestParam(required = false, defaultValue = "MONTH") DateType period,
            @RequestParam(required = false, defaultValue = "HOT") OrderType orderBy,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Long pageNum,
            @RequestParam(required = false, defaultValue = "12") @Min(1) Long resultCntPerPage
    ) {
        if (Objects.nonNull(searchString) && searchString.isEmpty()) return Collections.emptyList();
        return seriesService.getSeries(authorId, searchString, period, orderBy, pageNum, resultCntPerPage);
    }

    @PostMapping
    public SeriesCreateResponseDto createSeries(
            @RequestPart @NotEmpty(message = "시리즈 제목을 입력해주세요.") String title,
            @RequestPart @NotEmpty(message = "시리즈 설명을 입력해주세요.") String description,
            @RequestPart @NotEmpty(message = "시리즈에 들어갈 메모를 선택해주세요") String memoIdList,
            @RequestPart(required = false) MultipartFile thumbnailImage
    ) {
        Long authorId = SecurityContextUtils.getAuthorizedUserId();
        SeriesRequestDto seriesRequestDto = new SeriesRequestDto(title, description, CustomListUtils.toLongList(memoIdList));
        return seriesService.create(seriesRequestDto, thumbnailImage, authorId);
    }

    @GetMapping("/{seriesId}")
    public SeriesResponseDto getSingleSeries(@PathVariable @Min(1) Long seriesId) {
        return seriesService.getSeries(seriesId);
    }

    @PostMapping("/{seriesId}")
    public SeriesResponseDto updateSeries(
            @RequestPart @NotEmpty(message = "시리즈 제목을 입력해주세요.") String title,
            @RequestPart @NotEmpty(message = "시리즈 설명을 입력해주세요.") String description,
            @RequestPart @NotEmpty(message = "시리즈에 들어갈 메모를 선택해주세요") String memoIdList,
            @RequestPart Boolean isEmpty,
            @RequestPart(required = false) MultipartFile thumbnailImage,
            @PathVariable Long seriesId
    ) {
        Long authorId = SecurityContextUtils.getAuthorizedUserId();
        SeriesRequestDto seriesRequestDto = new SeriesRequestDto(title, description, CustomListUtils.toLongList(memoIdList));
        return seriesService.update(seriesId, seriesRequestDto, thumbnailImage, authorId, isEmpty);
    }

    @DeleteMapping("/{seriesId}")
    public void deleteSeries(@PathVariable Long seriesId) {
        Long authorId = SecurityContextUtils.getAuthorizedUserId();
        seriesService.delete(seriesId, authorId);
    }
}


