package net.vaxile.analytics.common;

import lombok.extern.slf4j.Slf4j;
import net.vaxile.analytics.common.command.CommandHandler;
import net.vaxile.analytics.common.event.EventExecutor;
import net.vaxile.analytics.common.storage.DataStorage;
import net.vaxile.analytics.common.storage.impl.H2Storage;

import java.io.File;

@Slf4j
public abstract class AnalyticsPlugin {
    protected DataStorage dataStorage;
    protected CommandHandler commandHandler;
    protected EventExecutor eventExecutor;

    protected abstract void registerCommand();
    protected abstract void registerEvent();
    public abstract File getDataFolder();

    public final void enable() {
        log.info("Initializing plugin...");

        dataStorage = new H2Storage(this);
        if (!dataStorage.init()) {
            log.error("Failed to set up data storage.");
            return;
        }

        commandHandler = new CommandHandler(dataStorage);
        eventExecutor = new EventExecutor(dataStorage);

        registerCommand();
        registerEvent();

        log.info("Plugin successfully enabled.");
    }

    public final void disable() {
        dataStorage.close();
        log.info("Plugin disabled.");
    }
}
