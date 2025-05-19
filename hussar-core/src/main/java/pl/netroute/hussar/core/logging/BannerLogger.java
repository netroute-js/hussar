package pl.netroute.hussar.core.logging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import pl.netroute.hussar.core.api.InternalUseOnly;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BannerLogger {
    private static final String BANNER_FILE_NAME = "banner.txt";

    public static void logBanner() {
        try(var bannerStream = getBannerInputStream()) {
            var bannerText = IOUtils.toString(bannerStream, StandardCharsets.UTF_8);

            log.info(bannerText);
        } catch (IOException ex) {
        }
    }

    private static InputStream getBannerInputStream() {
        return BannerLogger.class.getClassLoader().getResourceAsStream(BANNER_FILE_NAME);
    }

}
