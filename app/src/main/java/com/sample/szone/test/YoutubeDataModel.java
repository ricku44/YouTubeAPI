package com.sample.szone.test;

import android.os.Parcel;
import android.os.Parcelable;

public class YoutubeDataModel implements Parcelable {
    private String title = "";
    private String description = "";
    private String publishedAt = "";
    private String thumbnail = "";
    private String channel="";
    private String video_id = "";

    String getVideo_id() {
        return video_id;
    }

    String getChannel() {
        return channel;
    }

    void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    String getPublishedAt() {
        return publishedAt;
    }


    void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    String getThumbnail() {
        return thumbnail;
    }

    void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(publishedAt);
        dest.writeString(thumbnail);
        dest.writeString(video_id);
    }

    YoutubeDataModel() {
        super();
    }


    private YoutubeDataModel(Parcel in) {
        this();
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.publishedAt = in.readString();
        this.thumbnail = in.readString();
        this.video_id = in.readString();

    }

    public static final Creator<YoutubeDataModel> CREATOR = new Creator<YoutubeDataModel>() {
        @Override
        public YoutubeDataModel createFromParcel(Parcel in) {
            return new YoutubeDataModel(in);
        }

        @Override
        public YoutubeDataModel[] newArray(int size) {
            return new YoutubeDataModel[size];
        }
    };
}
