package net.vaxile.analytics.common.platform;

import java.util.UUID;

public interface LoginEventWrapper {

    UUID getUniqueId();

    String getHostname();
}
