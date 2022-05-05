package dk.au.mad22spring.appproject.group7.Fragments;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import dk.au.mad22spring.appproject.group7.Repository;
import dk.au.mad22spring.appproject.group7.StudyPlaceType;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class StudyPlaceListViewModel extends ViewModel {
    private Repository repository;
    private StudyPlaceType studyPlaceType = StudyPlaceType.Group;
    private MediatorLiveData<List<StudyPlace>> shownStudyPlace;

    public StudyPlaceListViewModel() {
        repository = Repository.getInstance();
    }

    public LiveData<List<StudyPlace>> getStudyPlaces(LifecycleOwner lifecycleOwner) {
        shownStudyPlace = new MediatorLiveData<>();
        shownStudyPlace.addSource(repository.getAllStudyPlaces(), studyPlaceList -> {
            ArrayList<StudyPlace> singles = new ArrayList<>();

            if (studyPlaceType == StudyPlaceType.Single) {
                for (StudyPlace studyplace : studyPlaceList) {

                    if (studyplace.getType() == studyPlaceType)
                        singles.add(studyplace);
                }
                shownStudyPlace.postValue(singles);
            } else {
                shownStudyPlace.postValue(studyPlaceList);
            }
        });
        return shownStudyPlace;
    }

    public void CheckForNewStudyplaces(LifecycleOwner lifecycleOwner) {
        repository.CheckForNewStudyplaces(lifecycleOwner);
    }

    public void removeGroupPlaces(){
        studyPlaceType = StudyPlaceType.Single;
        repository.invokeGetStudyplaces();
    }

     public void addGroupPlaces() {
        studyPlaceType = StudyPlaceType.Group;
        repository.invokeGetStudyplaces();
    }

    public void onUserRatingChanged(StudyPlace studyPlace, double newRating) {
        repository.onUserRatingChanged(studyPlace, newRating);
    }

    public void LogOut() {
        repository.LogOut();
    }
}