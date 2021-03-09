package net.vaxile.analytics.common.platform;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface CommandSender {

    boolean hasPermission(@NonNull String permission);

    void sendMessage(@NonNull String message);
}
