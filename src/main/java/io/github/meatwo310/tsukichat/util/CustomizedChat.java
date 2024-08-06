package io.github.meatwo310.tsukichat.util;

import io.github.meatwo310.tsukichat.TsukiChat;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class CustomizedChat {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    @Nullable
    public final String message;
    @Nullable
    public final Callable<String> deferredMessage;

    public CustomizedChat() {
        this(null, null);
    }

    public CustomizedChat(@Nullable String message) {
        this(message, null);
    }

    public CustomizedChat(@Nullable String message, @Nullable Callable<String> deferredMessage) {
        this.message = message;
        this.deferredMessage = deferredMessage;
    }

    public void ifMessagePresent(Consumer<String> consumer) {
        if (message == null) return;
        consumer.accept(message);
    }

    public void ifDeferredMessagePresent(Consumer<String> asyncConsumer) {
        if (deferredMessage == null) return;
        executorService.submit(() -> {
            try {
                asyncConsumer.accept(deferredMessage.call());
            } catch (Exception e) {
                TsukiChat.LOGGER.error("Failed to get deferred message: ", e);
            }
        });
    }
}
