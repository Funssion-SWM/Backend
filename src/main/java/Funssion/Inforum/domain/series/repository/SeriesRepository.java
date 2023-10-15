package Funssion.Inforum.domain.series.repository;

import Funssion.Inforum.common.constant.DateType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.domain.series.domain.Series;
import Funssion.Inforum.domain.series.dto.request.SeriesRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface SeriesRepository {

    Long create(Series series);
    Optional<Series> findById(Long id);
    List<Series> findAllBy(DateType period, OrderType orderBy, Long pageNum, Long resultCntPerPage);
    List<Series> findAllBy(Long authorId, DateType period, OrderType orderBy, Long pageNum, Long resultCntPerPage);
    List<Series> findAllBy(List<String> searhStringList ,DateType period, OrderType orderBy, Long pageNum, Long resultCntPerPage);
    void update(Long id, SeriesRequestDto seriesRequestDto, String newThumbnailImagePath);
    void delete(Long seriesId);
}
