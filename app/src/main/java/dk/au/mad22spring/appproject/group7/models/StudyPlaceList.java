package dk.au.mad22spring.appproject.group7.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StudyPlaceList {
    @SerializedName("studyPlaces")
    @Expose
    private List<StudyPlace> studyPlaces = null;

    public List<StudyPlace> getStudyPlaces() {return studyPlaces;}

}
