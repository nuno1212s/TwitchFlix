package com.twitchflix.applicationclient.utils;

import android.annotation.SuppressLint;
import com.twitchflix.applicationclient.ServerConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class VideoLinkCreator {

    private static final String SECRET = "xTefrgg9xFhXv6w0eEzcHt8LGj6ZYOPXbPi5nsiX";

    private static final String LIVE_VIDEO_LINK = "https://" + ServerConnection.getStreamDomain() + "/video/hls/%s/%d/%s.m3u8";

    private static final String VOD_VIDEO_LINK = "https://" + ServerConnection.getStreamDomain() + "/recordings/%s.mp4";

    private static final String IMAGE_LINK = "https://" + ServerConnection.getDomainName() + "/images/%s.jpg";

    private static final String STREAM_LINK = "rtmp://" +  ServerConnection.getStreamDomain() + "/show/%s";

    private static final long REQUEST_DURATION = TimeUnit.MINUTES.toMillis(30);

    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("DefaultLocale")
    public static String createVideoURL(String videoID, boolean isLive) {

        if (isLive) {

            long expireTime = System.currentTimeMillis() + REQUEST_DURATION;

            return String.format(LIVE_VIDEO_LINK, generateSecureHash(expireTime, ""), expireTime, videoID);

        } else {

            return String.format(VOD_VIDEO_LINK, videoID);

        }

    }

    public static String createStreamURL(String videoID) {
        return String.format(STREAM_LINK, videoID);
    }

    public static String createThumbnailLink(String videoID) {
        return String.format(IMAGE_LINK, videoID);
    }

    private static String generateSecureHash(long expireDate, String client_ip) {

        digest.reset();

        digest.update(Byte.parseByte(expireDate + " " + client_ip + " " + SECRET));

        final byte[] result = digest.digest();

        String s = android.util.Base64.encodeToString(result, 0);

        s = s.replace("=", "").replace("+", "-").replace( "\\", "_");

        return s;
    }
}
