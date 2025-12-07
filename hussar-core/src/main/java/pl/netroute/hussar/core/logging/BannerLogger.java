package pl.netroute.hussar.core.logging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.helper.StringHelper;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BannerLogger {
    private static final String BANNER_FILE_NAME = "hussar-banner.txt";

    public static void logBanner() {
        try(var bannerStream = getBannerInputStream()) {
            var bannerText = StringHelper.toText(bannerStream);

            log.info(bannerText);
        } catch (IOException ex) {
            log.warn("Could not log Hussar banner", ex);
        }
    }

    private static InputStream getBannerInputStream() {
        return BannerLogger.class.getClassLoader().getResourceAsStream(BANNER_FILE_NAME);
    }

}
