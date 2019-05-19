package com.twitchflix.databases.mysql;

import com.twitchflix.databases.VideoDatabase;
import com.twitchflix.videohandler.Video;
import com.twitchflix.videohandler.VideoBuilder;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MySqlVideoDB extends MySQLDB implements VideoDatabase {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS VIDEOS(VIDEOID BINARY(16) NOT NULL PRIMARY KEY," +
            " UPLOADER BINARY(16) NOT NULL PRIMARY KEY, TITLE varchar(255) NOT NULL, DESCRIPTION SMALLTEXT NOT NULL DEFAULT ''," +
            " UPLOADDATE BIGINT, LIKES BIGINTEGER, VIEWS BIGINTEGER, LIVE BOOLEAN, LINK varchar(2048), THUMBNAILLINK varchar(2048));";

    public MySqlVideoDB() {
        super();

        createTable();
    }


    @Override
    public Video getVideoByID(UUID videoID) {

        try (Connection c = getConnection();
             PreparedStatement s = c.prepareStatement("SELECT BIN_TO_UUID(UPLOADER), TITLE, DESCRIPTION, " +
                     "UPLOADDATE, LIKES, VIEWS, LIVE, LINK, THUMBNAILLINK FROM VIDEOS WHERE VIDEO_ID = UUID_FROM_BIN(?)")) {

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
             PreparedStatement s = c.prepareStatement("SELECT BIN_TO_UUID(VIDEOID), BIN_TO_UUID(UPLOADER), TITLE, DESCRIPTION, " +
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
             PreparedStatement s = c.prepareStatement("UPDATE VIDEOS SET VIEWS=VIEWS + 1 WHERE VIDEOID = UUID_TO_BIN(?)")) {

            s.setString(1, videoID.toString());

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
