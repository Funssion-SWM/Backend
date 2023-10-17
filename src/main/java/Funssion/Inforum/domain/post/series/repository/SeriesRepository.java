package Funssion.Inforum.domain.post.series.repository;

import Funssion.Inforum.common.constant.DateType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.post.series.domain.Series;
import Funssion.Inforum.domain.post.series.dto.request.SeriesRequestDto;

import java.util.List;
import java.util.Optional;

public interface SeriesRepository {

    Long create(Series series);
    Optional<Series> findById(Long id);
    List<Series> findAllBy(DateType period, OrderType orderBy, Long pageNum, Long resultCntPerPage);
    List<Series> findAllBy(Long authorId, Long pageNum, Long resultCntPerPage);
    List<Series> findAllBy(List<String> searhStringList ,DateType period, OrderType orderBy, Long pageNum, Long resultCntPerPage);
    List<Series> findLikedBy(Long userId, Long pageNum, Long resultCntPerPage);
    void update(Long id, SeriesRequestDto seriesRequestDto, String newThumbnailImagePath);
    void updateLikes(Long seriesId, Sign sign);
    void delete(Long seriesId);
}
