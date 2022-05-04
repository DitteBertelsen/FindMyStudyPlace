package dk.au.mad22spring.appproject.group7.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationModel {
    private String Comment;
    private String FriendName;
    private String Building;
    private double FriendLocationLat;
    private double FriendLocationLong;

    public String getFriendName() {
        return FriendName;
    }

    public void setFriendName(String friendName) {
        FriendName = friendName;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public double getFriendLocationLat() {
        return FriendLocationLat;
    }

    public void setFriendLocationLat(double friendLocationLat) {
        FriendLocationLat = friendLocationLat;
    }

    public double getFriendLocationLong() {
        return FriendLocationLong;
    }

    public void setFriendLocationLong(double friendLocationLong) {
        FriendLocationLong = friendLocationLong;
    }

    public String getBuilding() {
        return Building;
    }

    public void setBuilding(String building) {
        Building = building;
    }
}
