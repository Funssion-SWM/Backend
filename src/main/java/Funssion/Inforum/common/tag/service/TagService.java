package Funssion.Inforum.common.tag.service;

import Funssion.Inforum.common.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class TagService {
    private final TagRepository tagRepository;

    public List<String> getAllTags() {
        return tagRepository.getDefaultTags();
    }

}
