package Funssion.Inforum.domain.follow.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Builder
public class Follow {
    private final Long id;
    private final Long userId;
    private final Long followId;
    private final LocalDateTime created;
}
