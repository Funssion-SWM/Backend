package Funssion.Inforum.domain.series.repository;

import Funssion.Inforum.domain.series.domain.Series;
import Funssion.Inforum.domain.series.dto.request.SeriesRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface SeriesRepository {

    Long create(Series series);
    Optional<Series> findById(Long id);
    void update(Long id, SeriesRequestDto seriesRequestDto, String newThumbnailImagePath);
    void delete(Long seriesId);
}
