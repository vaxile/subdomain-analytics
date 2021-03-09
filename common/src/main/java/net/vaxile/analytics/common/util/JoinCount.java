package net.vaxile.analytics.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class JoinCount {
    private final int unique, total;
}
