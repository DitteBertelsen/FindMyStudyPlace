package dk.au.mad22spring.appproject.group7;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.au.mad22spring.appproject.group7.models.StudyPlace;
import dk.au.mad22spring.appproject.group7.models.StudyPlaceList;

public class Repository {

    private static Repository instance;

    private CloudStorage cloudStorage;
    private FirebaseConnection firebaseConnection;

    private List<StudyPlace> realtimeList;
    private ArrayList<StudyPlace> storageList;

    public static Repository getInstance()
    {
        if(instance == null){
            instance = new Repository();
        }
        return instance;
    }

    public Repository() {
        cloudStorage = CloudStorage.getInstance();
        firebaseConnection = FirebaseConnection.getInstance();
    }

    //Todo how to syncronize??!!
    public void CheckForNewStudyplaces(LifecycleOwner lifecycleOwner){
        cloudStorage.getStudyPlaceListItems().observe(lifecycleOwner, new Observer<ArrayList<StudyPlace>>() {
            @Override
            public void onChanged(ArrayList<StudyPlace> studyPlaces) {
                storageList = studyPlaces;

                firebaseConnection.getStudyPlacesRealTimeDb().observe(lifecycleOwner, new Observer<List<StudyPlace>>() {
                    @Override
                    public void onChanged(List<StudyPlace> studyPlaces) {
                        if (studyPlaces.size() == 0 ) {
                            realtimeList = storageList;

                        }
                        if (storageList.size() != 0) {
                            realtimeList = studyPlaces;
                            compareStudyplaces();
                        }
                    }
                });
            }
        });



    }

    //This method is called when an user does not have any study places in db (an user is created):
    /*private void initializeStudyplaces() {
        //if null - hent study places from firestorage:
        //realtimeList = cloudStorage.getStudyPlaceListItems().getValue();

        //gem i real time db for log in user
        firebaseConnection.saveStudyPlaceList(realtimeList);
    }

     */

    //This method is called to check if there are changes in the study places in the storage:
    public void compareStudyplaces() {
        //Check that both async calls has returned
        if (storageList.size() != 0 && realtimeList.size() != 0) {

            //Tjek om der findes nogen locations i storage listen som ikke findes i real time listen
            //--> tilføj dem til real time listen
            //In case a new study place has been added to the storage list:
            for (StudyPlace studyplace: storageList) {
                if (!realtimeList.contains(studyplace.getId())) {
                    realtimeList.add(studyplace);
                }
            }

            //Tjek om der findes nogen locations i real time listen som ikke findes i storage listen
            //--> fjern dem fram real time listen
            //In case a study place has been removed from the storage list:
            for (StudyPlace studyplace: realtimeList) {
                if (!storageList.contains(studyplace.getId()))
                {
                    realtimeList.remove(studyplace);
                }
            }

            //For hver location, tjek om der er ændringer i attributterne
            //--> opdater værdien i real time listen til værdien i storage listen
            //Update all attributes:
            for (StudyPlace storageStudyplace: storageList) {
                StudyPlace realTimeStudyPlace = realtimeList.get(realtimeList.indexOf(storageStudyplace.getId()));

                realTimeStudyPlace.setTitle(storageStudyplace.getTitle());
                realTimeStudyPlace.setStudyPlaceLat(storageStudyplace.getStudyPlaceLat());
                realTimeStudyPlace.setStudyPlaceLong(storageStudyplace.getStudyPlaceLong());
                //Todo update IMage
                realTimeStudyPlace.setType(storageStudyplace.getType());
                realTimeStudyPlace.setProperties(storageStudyplace.getProperties());
            }

        }

        //gem listen i real time db for log in user
        firebaseConnection.saveStudyPlaceList(storageList);
    }

    public void onUserRatingChanged(StudyPlace studyPlace, double newRating) {
        firebaseConnection.onStudyPlaceRatingChanged(studyPlace, newRating);
    }

    public MutableLiveData<List<StudyPlace>> getAllStudyPlaces()
    {
        return firebaseConnection.getStudyPlacesRealTimeDb();
    }

    public String getCurrentUser() {
        return firebaseConnection.getCurrentUser();
    }

    public MutableLiveData<Boolean> isSignedIn() {
        return firebaseConnection.isSignedIn();
    }

    public void SignIn(String email, String password, Activity activity){
        firebaseConnection.SignIn(email,password,activity);
    }

    //Returns true if a new user i successfully created
    public MutableLiveData<Boolean> getUserCreatedResult() {
        return firebaseConnection.getUserCreatedResult();
    }

    //Calls Firebase Connection and tries to create a new user
    public void createNewUser(String email, String password, Activity activity) {
        firebaseConnection.createNewUser(email, password, activity);
    }

    public LiveData<List<StudyPlace>> getStudyPlacesRealTimeDb() {
        return firebaseConnection.getStudyPlacesRealTimeDb();
    }

    public void onStudyPlaceRatingChanged(StudyPlace studyPlace, double newRating) {
        firebaseConnection.onStudyPlaceRatingChanged(studyPlace, newRating);
    }

    public void LogOut(){
        firebaseConnection.LogOut();
    }

}
