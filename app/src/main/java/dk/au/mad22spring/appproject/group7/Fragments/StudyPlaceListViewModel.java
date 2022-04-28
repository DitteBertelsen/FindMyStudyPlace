package dk.au.mad22spring.appproject.group7.Fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import dk.au.mad22spring.appproject.group7.Repository;
import dk.au.mad22spring.appproject.group7.StudyPlaceType;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class StudyPlaceListViewModel extends ViewModel {
    private MutableLiveData<ArrayList<StudyPlace>> studyPlaces;
    private Repository repository;

    public StudyPlaceListViewModel() {
        repository = Repository.getInstance();
        studyPlaces = repository.getAllStudyPlaces();
    }

    public void updateStudyPlaceRating(int index) {
        //TODO call repo to update rating
    }

    public MutableLiveData<ArrayList<StudyPlace>> getStudyPlaces() {
        return studyPlaces;
    }

    public void removeGroupPlaces(){
        ArrayList<StudyPlace> singles = new ArrayList<>();

        for (StudyPlace studyplace: studyPlaces.getValue()) {

            if (studyplace.getType() == StudyPlaceType.Single)
            singles.add(studyplace);
        }

        studyPlaces.postValue(singles);
    }

    public void addGroupPlaces() {
        studyPlaces = repository.getAllStudyPlaces();
    }
}