package dk.au.mad22spring.appproject.group7;

import android.util.Log;

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
        }

        sendRequest("gs://findmystudyplace.appspot.com");

        return mStudyPlaceList;
    }


    private void sendRequest(String url) {
        if (queue == null){
            queue = Volley.newRequestQueue(FMSPApplication.getAppContext());
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(Constants.TAG_Rep, "onReponse" + response);
                        parseSearchJson(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Constants.TAG_Rep, "onErrorResponse: Oh! That did not work..!");

            }
        });
        queue.add(stringRequest);
    }

    private void parseSearchJson(String json) {
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
