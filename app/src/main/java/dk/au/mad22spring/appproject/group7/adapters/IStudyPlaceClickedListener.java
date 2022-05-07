package dk.au.mad22spring.appproject.group7.adapters;

import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public interface IStudyPlaceClickedListener {
    void onItemClicked(int index);
    void onUserRatingChanged(StudyPlace studyPlace, double newRating);
}
