package dk.au.mad22spring.appproject.group7.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import dk.au.mad22spring.appproject.group7.Constants;
import dk.au.mad22spring.appproject.group7.FMSPApplication;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;
import dk.au.mad22spring.appproject.group7.models.StudyPlaceList;

public class CloudStorage {

    private StorageReference storageRef;
    private static CloudStorage instance;

    private MutableLiveData<ArrayList<StudyPlace>> mStudyPlaceList;
    private ArrayList<StudyPlace> aStudyPlaceList = new ArrayList<>();

    //RequestQueue
    private static RequestQueue queue;

    public static CloudStorage getInstance()
    {
        if(instance == null){
            instance = new CloudStorage();
        }
        return instance;
    }

    public CloudStorage() {
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://findmystudyplace.appspot.com");
    }

    public MutableLiveData<ArrayList<StudyPlace>> getStudyPlaceListItems()
    {
        if(mStudyPlaceList == null) {
            mStudyPlaceList = new MutableLiveData<>();
            mStudyPlaceList.setValue(new ArrayList<>());

            //Collect study places from file i Firebase Storage:
            sendRequestUsingVolley("https://firebasestorage.googleapis.com/v0/b/findmystudyplace.appspot.com/o/jsonTestStudyPlacesV2.txt?alt=media&token=103e998d-cfab-40e0-8cb2-2279745419f9");
        }

        return mStudyPlaceList;
    }

    //This mehtod is based on lesson 6, demo
    private void sendRequestUsingVolley(String url) {
        if (queue == null){
            queue = Volley.newRequestQueue(FMSPApplication.getAppContext());
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(Constants.TAG_Rep, "onReponse" + response);
                        parseJson(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Constants.TAG_Rep, "onErrorResponse: faild to collect data using volley");

            }
        });

        queue.add(stringRequest);
    }

    //This method is based on lesson 6, demo:
    private void parseJson(String json) {
        Gson gson = new GsonBuilder().create();
        StudyPlaceList studyPlaceList = gson.fromJson(json, StudyPlaceList.class);
        if (studyPlaceList != null){
            for(StudyPlace studyPlace: studyPlaceList.getStudyPlaces()){
                aStudyPlaceList.add(studyPlace);
            }

        }

        getStudyPlaceListItems().postValue(aStudyPlaceList);
    }
}
