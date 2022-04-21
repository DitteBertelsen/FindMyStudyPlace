package dk.au.mad22spring.appproject.group7.models;

import java.util.List;

import dk.au.mad22spring.appproject.group7.StudyPlaceType;

public class StudyPlace {

    private int id;

    private String Title;

    //TODO add location coordinates

    //TODO Find ud af hvordan vi får hentet billder og hvor de skal ligge
    //private int picture

    private StudyPlaceType Type;

    private List<String> Properties;

    private Double UserRating;


}
