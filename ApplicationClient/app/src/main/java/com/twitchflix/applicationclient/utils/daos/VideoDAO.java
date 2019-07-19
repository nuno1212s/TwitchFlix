package com.twitchflix.applicationclient.utils.daos;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class VideoDAO implements Parcelable {

    private UUID videoID;

    private String title, description;

    private Bitmap thumbnail;

    private UserDAO uploader;

    private VideoDAO(UUID videoID, UserDAO uploader, String title,
                     String description, Bitmap thumbnail) {
        this.videoID = videoID;
        this.uploader = uploader;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    protected VideoDAO(Parcel in) {
        this.videoID = UUID.fromString(in.readString());
        title = in.readString();
        description = in.readString();
        thumbnail = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<VideoDAO> CREATOR = new Creator<VideoDAO>() {
        @Override
        public VideoDAO createFromParcel(Parcel in) {
            return new VideoDAO(in);
        }

        @Override
        public VideoDAO[] newArray(int size) {
            return new VideoDAO[size];
        }
    };

    public static VideoDAO fromData(UUID videoID, UserDAO uploader, String title,
                                    String description, Bitmap thumbnail) {
        return new VideoDAO(videoID, uploader, title, description, thumbnail);
    }

    public UUID getVideoID() {
        return videoID;
    }

    public UserDAO getUploader() {
        return uploader;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUploaderName() {
        return getUploader().getFullName();
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public Bitmap getChannelThumbnail() {
        return this.getUploader().getChannelThumbnail();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.videoID.toString());
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeParcelable(thumbnail, flags);
    }
}