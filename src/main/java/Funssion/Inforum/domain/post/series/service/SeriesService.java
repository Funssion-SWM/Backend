package Funssion.Inforum.domain.post.series.service;

import Funssion.Inforum.common.constant.DateType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.series.domain.Series;
import Funssion.Inforum.domain.post.series.dto.MemoMetaInfoInSeries;
import Funssion.Inforum.domain.post.series.dto.request.SeriesRequestDto;
import Funssion.Inforum.domain.post.series.dto.response.SeriesCreateResponseDto;
import Funssion.Inforum.domain.post.series.dto.response.SeriesListDto;
import Funssion.Inforum.domain.post.series.dto.response.SeriesResponseDto;
import Funssion.Inforum.domain.post.series.repository.SeriesRepository;
import Funssion.Inforum.s3.S3Repository;
import Funssion.Inforum.s3.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static Funssion.Inforum.common.utils.CustomStringUtils.getSearchStringList;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final MyRepository myRepository;
    private final S3Repository s3Repository;
    private final MemoRepository memoRepository;
    @Value("${aws.s3.series-dir}")
    private String SERIES_DIR;

    @Transactional
    public SeriesCreateResponseDto create(
            SeriesRequestDto seriesRequestDto,
            MultipartFile thumbnailImage,
            Long authorId
    ) {
        String uploadedImagePath = uploadThumbnailImage(thumbnailImage, authorId);

        Long seriesId = createSeriesAndGetSeriesId(seriesRequestDto, authorId, uploadedImagePath);

        memoRepository.updateSeriesIdAndTitle(seriesId, seriesRequestDto.getTitle(), authorId, seriesRequestDto.getMemoIdList());

        return new SeriesCreateResponseDto(seriesId);
    }

    private String uploadThumbnailImage(MultipartFile thumbnailImage, Long authorId) {
        if (Objects.isNull(thumbnailImage)) return null;

        String imageName = S3Utils.generateImageNameOfS3(authorId);
        return s3Repository.upload(thumbnailImage, SERIES_DIR, imageName);
    }

    private Long createSeriesAndGetSeriesId(SeriesRequestDto seriesRequestDto, Long authorId, String uploadedImagePath) {
        MemberProfileEntity authorProfile = myRepository.findProfileByUserId(authorId);

        Long seriesId = seriesRepository.create(
                Series.builder()
                        .title(seriesRequestDto.getTitle())
                        .description(seriesRequestDto.getDescription())
                        .thumbnailImagePath(uploadedImagePath)
                        .rank(authorProfile.getRank())
                        .authorId(authorProfile.getUserId())
                        .authorName(authorProfile.getNickname())
                        .authorImagePath(authorProfile.getProfileImageFilePath())
                        .build()
        );
        return seriesId;
    }

    public List<SeriesListDto> getSeries(String searchString, DateType period, OrderType orderBy, Long pageNum, Long resultCntPerPage) {
        List<Series> resultList = getSeriesList(searchString, period, orderBy, pageNum, resultCntPerPage);

        return resultList.stream()
                .map(series -> {
                    SeriesListDto seriesListDto = SeriesListDto.valueOf(series);
                    seriesListDto.setTopThreeColors(memoRepository.findTop3ColorsBySeriesId(series.getId()));
                    return seriesListDto;
                }).toList();
    }

    private List<Series> getSeriesList(String searchString, DateType period, OrderType orderBy, Long pageNum, Long resultCntPerPage) {
        List<Series> resultList;
        if (Objects.isNull(searchString)) {
            resultList = seriesRepository.findAllBy(period, orderBy, pageNum, resultCntPerPage);
        } else {
            resultList = seriesRepository.findAllBy(getSearchStringList(searchString), period, orderBy, pageNum, resultCntPerPage);
        }
        return resultList;
    }

    @Transactional(readOnly = true)
    public SeriesResponseDto getSeries(Long seriesId) {
        return getSeriesResponse(seriesId);
    }

    @Transactional
    public SeriesResponseDto update(
            Long seriesId,
            SeriesRequestDto seriesRequestDto,
            MultipartFile thumbnailImage,
            Long authorId,
            Boolean isEmpty
    ) {
        deleteSeriesInfoInOtherStorage(seriesId, authorId, thumbnailImage, isEmpty);
        updateSeries(seriesId, seriesRequestDto, thumbnailImage, authorId, isEmpty);
        memoRepository.updateSeriesIdAndTitle(seriesId, seriesRequestDto.getTitle(), authorId, seriesRequestDto.getMemoIdList());

        return getSeriesResponse(seriesId);
    }

    private void updateSeries(Long seriesId, SeriesRequestDto seriesRequestDto, MultipartFile thumbnailImage, Long authorId, Boolean isEmpty) {
        if (!isEmpty && Objects.isNull(thumbnailImage)) {
            seriesRepository.update(seriesId, seriesRequestDto);
        } else {
            String uploadedImagePath = getUploadedImagePath(thumbnailImage, authorId, isEmpty);
            seriesRepository.update(seriesId, seriesRequestDto, uploadedImagePath);
        }
    }

    private String getUploadedImagePath(MultipartFile thumbnailImage, Long authorId, Boolean isEmpty) {
        if (isEmpty) return null;
        return uploadThumbnailImage(thumbnailImage, authorId);
    }

    private SeriesResponseDto getSeriesResponse(Long seriesId) {
        SeriesResponseDto response = SeriesResponseDto.valueOf(getValidatedSeries(seriesId));
        List<MemoMetaInfoInSeries> memoMetaInfoList =
                memoRepository.findAllBySeriesId(seriesId).stream()
                        .map((memo -> new MemoMetaInfoInSeries(memo.getId(), memo.getTitle(), memo.getColor())))
                        .toList();
        response.setMemoMetaInfo(memoMetaInfoList);
        return response;
    }

    @Transactional
    public void delete(Long seriesId, Long authorId) {
        deleteSeriesInfoInOtherStorage(seriesId, authorId, null, Boolean.TRUE);

        seriesRepository.delete(seriesId);
    }

    private void deleteSeriesInfoInOtherStorage(Long seriesId, Long authorId, MultipartFile thumbnailImage, Boolean isEmpty) {
        Series willBeDeletedSeries = getValidatedSeries(seriesId);

        if (!willBeDeletedSeries.getAuthorId().equals(authorId)) {
            throw new UnAuthorizedException("다른 유저의 게시물을 수정 또는 삭제할 수 없습니다.");
        }

        memoRepository.updateSeriesIdsToZero(willBeDeletedSeries.getId(), authorId);

        if (Objects.isNull(thumbnailImage) && !isEmpty) return;

        if (Objects.nonNull(willBeDeletedSeries.getThumbnailImagePath())) {
            s3Repository.delete(SERIES_DIR, willBeDeletedSeries.getThumbnailImagePath());
        }
    }

    private Series getValidatedSeries(Long seriesId) {
        return seriesRepository.findById(seriesId).orElseThrow(() -> new NotFoundException("존재하지 않는 시리즈입니다."));
    }
}
