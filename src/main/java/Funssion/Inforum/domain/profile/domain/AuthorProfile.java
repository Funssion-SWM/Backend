package Funssion.Inforum.domain.profile.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class AuthorProfile {
    private final Long id;
    private final String name;
    private final String profileImagePath;
}
