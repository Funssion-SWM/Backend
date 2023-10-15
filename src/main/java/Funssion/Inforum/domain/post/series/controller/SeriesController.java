package Funssion.Inforum.domain.post.series.controller;

import Funssion.Inforum.common.constant.DateType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.series.dto.request.SeriesRequestDto;
import Funssion.Inforum.domain.post.series.dto.response.SeriesCreateResponseDto;
import Funssion.Inforum.domain.post.series.dto.response.SeriesListDto;
import Funssion.Inforum.domain.post.series.dto.response.SeriesResponseDto;
import Funssion.Inforum.domain.post.series.service.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/series")
public class SeriesController {

    private final SeriesService seriesService;

    @GetMapping
    public List<SeriesListDto> getSeriesList(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String searchString,
            @RequestParam(required = false, defaultValue = "MONTH") DateType period,
            @RequestParam(required = false, defaultValue = "HOT") OrderType orderBy,
            @RequestParam(required = false, defaultValue = "0") Long pageNum,
            @RequestParam(required = false, defaultValue = "12") Long resultCntPerPage
    ) {
        return seriesService.getSeries(authorId, searchString, period, orderBy, pageNum, resultCntPerPage);
    }

    @PostMapping
    public SeriesCreateResponseDto createSeries(
            @RequestPart @Validated SeriesRequestDto seriesRequestDto,
            @RequestPart MultipartFile thumbnailImage
    ) {
        Long authorId = SecurityContextUtils.getAuthorizedUserId();
        return seriesService.create(seriesRequestDto, thumbnailImage, authorId);
    }

    @GetMapping("/{seriesId}")
    public SeriesResponseDto getSingleSeries(@PathVariable Long seriesId) {
        return seriesService.getSeries(seriesId);
    }

    @PostMapping("/{seriesId}")
    public SeriesResponseDto updateSeries(
            @RequestPart @Validated SeriesRequestDto seriesRequestDto,
            @RequestPart MultipartFile thumbnailImage,
            @PathVariable Long seriesId
    ) {
        Long authorId = SecurityContextUtils.getAuthorizedUserId();
        return seriesService.update(seriesId, seriesRequestDto, thumbnailImage, authorId);
    }

    @DeleteMapping("/{seriesId}")
    public void deleteSeries(@PathVariable Long seriesId) {
        Long authorId = SecurityContextUtils.getAuthorizedUserId();
        seriesService.delete(seriesId, authorId);
    }
}


