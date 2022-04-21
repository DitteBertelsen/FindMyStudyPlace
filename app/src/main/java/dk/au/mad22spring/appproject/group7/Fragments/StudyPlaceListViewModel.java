package dk.au.mad22spring.appproject.group7.Fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import dk.au.mad22spring.appproject.group7.Repository;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class StudyPlaceListViewModel extends ViewModel {
    private LiveData<ArrayList<StudyPlace>> studyPlaces;
    private Repository repository;

    public StudyPlaceListViewModel() {
        repository = Repository.getInstance();
        studyPlaces = repository.getAllStudyPlaces();
    }

    public void updateStudyPlaceRating(int index) {
        //TODO call repo to update rating
    }

    public LiveData<ArrayList<StudyPlace>> getStudyPlaces() {
        return studyPlaces;
    }
}