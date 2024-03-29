package dk.au.mad22spring.appproject.group7.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import dk.au.mad22spring.appproject.group7.StudyPlaceType;

public class StudyPlace {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("Title")
    @Expose
    private String Title;

    @SerializedName("Image")
    @Expose
    private String Image;

    @SerializedName("StudyPlaceLat")
    @Expose
    private double StudyPlaceLat;

    @SerializedName("StudyPlaceLong")
    @Expose
    private double StudyPlaceLong;

    @SerializedName("Type")
    @Expose
    private StudyPlaceType Type;

    @SerializedName("Properties")
    @Expose
    private List<String> Properties = null;

    @SerializedName("UserRating")
    @Expose
    private Double UserRating;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public StudyPlaceType getType() {
        return Type;
    }

    public void setType(StudyPlaceType type) {
        Type = type;
    }

    public List<String> getProperties() {
        return Properties;
    }

    public void setProperties(List<String> properties) {
        Properties = properties;
    }

    public Double getUserRating() {
        return UserRating;
    }

    public void setUserRating(Double userRating) {
        UserRating = userRating;
    }

    public double getStudyPlaceLat() {
        return StudyPlaceLat;
    }

    public void setStudyPlaceLat(double studyPlaceLat) {
        StudyPlaceLat = studyPlaceLat;
    }

    public double getStudyPlaceLong() {
        return StudyPlaceLong;
    }

    public void setStudyPlaceLong(double studyPlaceLong) {
        StudyPlaceLong = studyPlaceLong;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
