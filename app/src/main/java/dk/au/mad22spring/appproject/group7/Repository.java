package dk.au.mad22spring.appproject.group7;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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


    public MutableLiveData<ArrayList<StudyPlace>> getAllStudyPlaces()
    {
        return cloudStorage.getStudyPlaceListItems();
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

    public void LogOut(){
        firebaseConnection.LogOut();
    }

}
