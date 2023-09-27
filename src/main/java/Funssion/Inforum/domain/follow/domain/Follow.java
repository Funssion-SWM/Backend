package Funssion.Inforum.domain.follow.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Builder
@EqualsAndHashCode(exclude = {"id", "created"})
public class Follow {
    private final Long id;
    private final Long userId;
    private final Long followedUserId;
    private final LocalDateTime created;
}
