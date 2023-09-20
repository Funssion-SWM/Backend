package Funssion.Inforum.domain.tag.service;

import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class TagService {
    private final TagRepository tagRepository;

    public List<String> getDefaultTags() {
        return tagRepository.getDefaultTags();
    }

    public List<String> getUserTags(Long userId, Integer tagCnt) {
        if (Objects.isNull(tagCnt))
            return tagRepository.findAllOrderByCountDesc(userId);
        return tagRepository.findAllOrderByCountDesc(userId, tagCnt);
    }
}
