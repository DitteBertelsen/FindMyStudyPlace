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
    //private MutableLiveData<List<StudyPlace>> shownStudyPlaces;
    private List<StudyPlace> allStudyPlaces;
    private Repository repository;
    private StudyPlaceType studyPlaceType = StudyPlaceType.Group;
    private MediatorLiveData<List<StudyPlace>> shownStudyPlace;

    public StudyPlaceListViewModel() {
        repository = Repository.getInstance();
        //shownStudyPlaces = new MutableLiveData<>(new ArrayList<>());
//        studyPlaces.setValue(new ArrayList<>());
    }

    public void updateStudyPlaceRating(int index) {
        //TODO call repo to update rating
    }

    public LiveData<List<StudyPlace>> getStudyPlaces(LifecycleOwner lifecycleOwner) {
        /*repository.getAllStudyPlaces().observe(lifecycleOwner, studyPlaceList -> {
            shownStudyPlaces.postValue(studyPlaceList);
        });*/

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

       repository.poke();
/*
        ArrayList<StudyPlace> singles = new ArrayList<>();

        for (StudyPlace studyplace: shownStudyPlaces.getValue()) {

            if (studyplace.getType() == StudyPlaceType.Single)
            singles.add(studyplace);
        }

        shownStudyPlaces.postValue(singles);
*/
    }

     public void addGroupPlaces() {
        studyPlaceType = StudyPlaceType.Group;
        repository.poke();
    }

    public void onUserRatingChanged(StudyPlace studyPlace, double newRating) {
        repository.onUserRatingChanged(studyPlace, newRating);
    }

    public void LogOut() {
        repository.LogOut();
    }
}