package net.vaxile.analytics.common.util;

import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.regex.Pattern;

@UtilityClass
public class StringUtil {
    private final Pattern PATTERN = Pattern.compile(":");

    public @NonNull String parse(@NonNull String value) {
        return PATTERN.split(value)[0];
    }
}
