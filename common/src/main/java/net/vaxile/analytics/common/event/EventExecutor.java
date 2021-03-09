package net.vaxile.analytics.common.event;

import lombok.RequiredArgsConstructor;
import net.vaxile.analytics.common.platform.LoginEventWrapper;
import net.vaxile.analytics.common.storage.DataStorage;
import net.vaxile.analytics.common.util.AsyncHelper;
import net.vaxile.analytics.common.util.StringUtil;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

@RequiredArgsConstructor
public final class EventExecutor {
    private final @NonNull DataStorage storage;

    public void execute(@NonNull LoginEventWrapper wrapper) {
        String hostname = StringUtil.parse(wrapper.getHostname());
        UUID uniqueId = wrapper.getUniqueId();

        AsyncHelper.executor().submit(
                () -> storage.insertJoin(hostname, uniqueId)
        );
    }
}
