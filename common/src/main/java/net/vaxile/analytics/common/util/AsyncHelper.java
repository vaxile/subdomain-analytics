package net.vaxile.analytics.common.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@UtilityClass
public class AsyncHelper {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static ExecutorService executor() {
        return executorService;
    }
}
