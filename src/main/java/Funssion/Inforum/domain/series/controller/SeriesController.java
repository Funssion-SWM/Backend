package Funssion.Inforum.domain.series.controller;

import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.series.dto.request.SeriesRequestDto;
import Funssion.Inforum.domain.series.dto.response.SeriesCreateResponseDto;
import Funssion.Inforum.domain.series.dto.response.SeriesResponseDto;
import Funssion.Inforum.domain.series.service.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Validated
@RequestMapping("/series")
public class SeriesController {

    private final SeriesService seriesService;

    @PostMapping
    public SeriesCreateResponseDto createSeries(
            @RequestPart @Validated SeriesRequestDto seriesRequestDto,
            @RequestPart MultipartFile thumbnailImage
    ) {
        Long authorId = SecurityContextUtils.getAuthorizedUserId();
        return seriesService.create(seriesRequestDto, thumbnailImage, authorId);
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


