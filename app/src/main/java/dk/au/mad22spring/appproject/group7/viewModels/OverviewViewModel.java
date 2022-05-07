package dk.au.mad22spring.appproject.group7.viewModels;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import dk.au.mad22spring.appproject.group7.Repository;
import dk.au.mad22spring.appproject.group7.StudyPlaceType;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class OverviewViewModel extends ViewModel {
    private Repository repository;
    private StudyPlaceType studyPlaceType = StudyPlaceType.Group;
    private MediatorLiveData<List<StudyPlace>> shownStudyPlace;

    public OverviewViewModel() {
        repository = Repository.getInstance();
    }

    public LiveData<List<StudyPlace>> getStudyPlaces() {
        shownStudyPlace = new MediatorLiveData<>();

        //This MediatorLiveData is used to add the single/group filter on UI:
        //ref: https://developer.android.com/reference/androidx/lifecycle/MediatorLiveData
        shownStudyPlace.addSource(repository.getAllStudyPlaces(), studyPlaceList -> {
            ArrayList<StudyPlace> singles = new ArrayList<>();

            //If the switch is set to true, aka. single, then remove all group study places:
            if (studyPlaceType == StudyPlaceType.Single) {
                for (StudyPlace studyplace : studyPlaceList) {
                    if (studyplace.getType() == studyPlaceType)
                        singles.add(studyplace);
                }

                shownStudyPlace.postValue(singles);

            }

            //If the switch is set to false, aka. all types, them post all study places:
            else {
                shownStudyPlace.postValue(studyPlaceList);
            }
        });

        return shownStudyPlace;
    }

    public void removeGroupPlaces(){
        studyPlaceType = StudyPlaceType.Single;

        //invoke getAllStudyPlaces list to update UI to only show single study places:
        repository.invokeGetStudyplaces();
    }

     public void addGroupPlaces() {
        studyPlaceType = StudyPlaceType.Group;

         //invoke getAllStudyPlaces list to update UI to show all study places:
        repository.invokeGetStudyplaces();
    }

    public void onUserRatingChanged(StudyPlace studyPlace, double newRating) {
        repository.onUserRatingChanged(studyPlace, newRating);
    }

    public LiveData<Boolean> getIsStudyPlacesLoaded() {
        return repository.getIsStudyPlacesLoaded();
    }

    public void LogOut() {
        repository.LogOut();
    }
}