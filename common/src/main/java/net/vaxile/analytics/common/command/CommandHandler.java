package net.vaxile.analytics.common.command;

import lombok.RequiredArgsConstructor;
import net.vaxile.analytics.common.platform.CommandSender;
import net.vaxile.analytics.common.storage.DataStorage;
import net.vaxile.analytics.common.util.AsyncHelper;
import net.vaxile.analytics.common.util.JoinCount;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.CompletableFuture;

import static java.lang.String.format;

@RequiredArgsConstructor
public final class CommandHandler {
    private static final String PERMISSION = "analytics.use";
    private static final String NO_PERMISSION = "&cYou do not have permission to execute this command!";
    private static final String USAGE = "/sda <subdomain>";
    private static final String OUTPUT = "Stats for %s\nUnique: %s\nTotal: %s";

    private final @NonNull DataStorage storage;

    public void handle(@NonNull CommandSender sender, @NonNull String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(NO_PERMISSION);
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(USAGE);
            return;
        }

        String hostname = args[0];
        CompletableFuture<JoinCount> future = CompletableFuture.supplyAsync(
                () -> new JoinCount(storage.getUnique(hostname), storage.getTotal(hostname)),
                AsyncHelper.executor()
        );

        future.thenAccept(
                (result) -> sender.sendMessage(format(OUTPUT, hostname, result.getUnique(), result.getTotal()))
        );
    }
}
