package net.vaxile.analytics.common.storage;

import java.util.UUID;

public interface DataStorage {

    boolean init();

    void close();

    void insertJoin(String hostname, UUID uniqueId);

    int getUnique(String hostname);
    int getTotal(String hostname);
}
