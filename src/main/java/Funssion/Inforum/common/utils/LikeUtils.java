package Funssion.Inforum.common.utils;

import Funssion.Inforum.domain.like.domain.Like;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class LikeUtils {

    public static boolean isLikeMatched(List<Like> likeList, Long memoId) {
        for (Like like : likeList) {
            log.info("like = {}, memoId = {}", like, memoId);
        }
        return likeList.stream()
                .filter(like -> like.getPostId().equals(memoId))
                .findAny()
                .isPresent();
    }
}
