package dev.jaronline.cuttingdelight.common.platform;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.ServiceLoader;

public final class Services {
    private Services() {}

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final PlatformHelper PLATFORM = load(PlatformHelper.class);

    public static <T> T load(Class<T> serviceClass) {
        T loadedService = ServiceLoader.load(serviceClass)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + serviceClass.getName()));
        LOGGER.debug("Loaded {} for service {}", loadedService, serviceClass);
        return loadedService;
    }
}
