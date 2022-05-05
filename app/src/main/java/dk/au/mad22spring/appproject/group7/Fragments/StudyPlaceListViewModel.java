package dk.au.mad22spring.appproject.group7.Fragments;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import dk.au.mad22spring.appproject.group7.Repository;
import dk.au.mad22spring.appproject.group7.StudyPlaceType;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class StudyPlaceListViewModel extends ViewModel {
    private MutableLiveData<List<StudyPlace>> studyPlaces;
    private List<StudyPlace> allStudyPlaces;
    private Repository repository;

    public StudyPlaceListViewModel() {
        repository = Repository.getInstance();
        studyPlaces = new MutableLiveData<>();
        studyPlaces.setValue(new ArrayList<>());
    }

    public void updateStudyPlaceRating(int index) {
        //TODO call repo to update rating
    }

    public MutableLiveData<List<StudyPlace>> getStudyPlaces() {
        studyPlaces = repository.getAllStudyPlaces();
        return studyPlaces;
    }

    public void CheckForNewStudyplaces(LifecycleOwner lifecycleOwner) {
        repository.CheckForNewStudyplaces(lifecycleOwner);
    }

    public void removeGroupPlaces(){
        ArrayList<StudyPlace> singles = new ArrayList<>();

        for (StudyPlace studyplace: getStudyPlaces().getValue()) {

            if (studyplace.getType() == StudyPlaceType.Single)
            singles.add(studyplace);
        }

        studyPlaces.postValue(singles);
    }

    public void addGroupPlaces() {
        studyPlaces.postValue(getStudyPlaces().getValue());
    }

    public void onUserRatingChanged(StudyPlace studyPlace, double newRating) {
        repository.onUserRatingChanged(studyPlace, newRating);
    }

    public void LogOut() {
        repository.LogOut();
    }
}