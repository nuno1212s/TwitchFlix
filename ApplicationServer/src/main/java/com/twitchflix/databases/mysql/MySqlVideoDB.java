package com.twitchflix.databases.mysql;

import com.twitchflix.databases.VideoDatabase;
import com.twitchflix.videohandler.Video;
import com.twitchflix.videohandler.VideoBuilder;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MySqlVideoDB extends MySQLDB implements VideoDatabase {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS VIDEOS(VIDEOID BINARY(16) NOT NULL PRIMARY KEY," +
            " UPLOADER BINARY(16) NOT NULL, TITLE varchar(255) NOT NULL, DESCRIPTION TEXT NOT NULL," +
            " UPLOADDATE TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, LIKES BIGINT NOT NULL DEFAULT 0," +
            " VIEWS BIGINT NOT NULL DEFAULT 0, LIVE BOOLEAN NOT NULL DEFAULT FALSE," +
            " LINK varchar(2048), THUMBNAILLINK varchar(2048)" +
            ", UNIQUE(VIDEOID));";

    public MySqlVideoDB() {
        super();

        createTable();
    }


    @Override
    public Video getVideoByID(UUID videoID) {

        try (Connection c = getConnection();
             PreparedStatement s = c.prepareStatement("SELECT HEX(UPLOADER) AS UPLOADER, TITLE, DESCRIPTION, " +
                     "UPLOADDATE, LIKES, VIEWS, LIVE, LINK, THUMBNAILLINK FROM VIDEOS WHERE VIDEOID = UNHEX(?)")) {

            s.setString(1, videoID.toString());

            ResultSet resultSet = s.executeQuery();

            if (resultSet.next()) {

                return new VideoBuilder()
                        .setVideoID(videoID)
                        .fromResultSet(resultSet)
                        .createVideo();

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Video> getVideosByID(HashSet<UUID> videoID) {
        return getAllVideos().stream()
                .filter(video -> videoID.contains(video.getVideoID()))
                .collect(Collectors.toList());

    }

    @Override
    public List<Video> getAllVideos() {

        try (Connection c = getConnection();
             PreparedStatement s = c.prepareStatement("SELECT HEX(VIDEOID) AS VIDEOID, HEX(UPLOADER) AS UPLOADER, TITLE, DESCRIPTION, " +
                     "UPLOADDATE, LIKES, VIEWS, LIVE, LINK, THUMBNAILLINK FROM VIDEOS")) {

            ResultSet resultSet = s.executeQuery();

            List<Video> totalVideos = new ArrayList<>();

            while (resultSet.next()) {

                totalVideos.add(new VideoBuilder()
                        .setVideoID(UUID.fromString(resultSet.getString("VIDEOID")))
                        .fromResultSet(resultSet).createVideo());

            }

            return totalVideos;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Video> getVideosSortedByUploadDate() {
        List<Video> allVideos = getAllVideos();

        allVideos.sort(Comparator.comparingLong(Video::getUploadDate));

        return allVideos;
    }

    @Override
    public void incrementVideoViews(UUID videoID) {

        try (Connection c = getConnection();
             PreparedStatement s = c.prepareStatement("UPDATE VIDEOS SET VIEWS=VIEWS + 1 WHERE VIDEOID = UNHEX(?)")) {

            s.setString(1, videoID.toString().replace("-", ""));

            s.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void registerVideoStream(Video video) {

        try (Connection c = getConnection();
             PreparedStatement s = c.prepareStatement("INSERT INTO VIDEOS(VIDEOID, UPLOADER, TITLE, DESCRIPTION, LINK, THUMBNAILLINK) " +
                     "values(UNHEX(?), UNHEX(?), ?, ?, ?, ?)")) {

            s.setString(1, video.getVideoID().toString().replace("-", ""));
            s.setString(2, video.getUploader().toString().replace("-", ""));
            s.setString(3, video.getTitle());
            s.setString(4, video.getDescription());
            s.setString(5, video.getLink());
            s.setString(6, video.getThumbnailLink());

            s.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateVideo(Video video) {

        try (Connection c = getConnection();
            PreparedStatement s = c.prepareStatement("UPDATE VIDEOS SET LIVE=?, LINK=?" +
                    " WHERE VIDEOID=UNHEX(?)")) {

            s.setBoolean(1, video.isLive());
            s.setString(2, video.getLink());

            s.setString(3, video.getVideoID().toString().replace("-", ""));

            s.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void createTable() {

        try (Connection c = getConnection();
             Statement s = c.createStatement()) {

            s.execute(CREATE_TABLE);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
