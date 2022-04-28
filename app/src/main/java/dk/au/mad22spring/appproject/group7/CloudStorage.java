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

        //Todo remove temp. data
        ArrayList<StudyPlace> temp = new ArrayList<>();
        StudyPlace tempstudy1 = new StudyPlace();
        tempstudy1.setTitle("Nygaard, Kælder");
        tempstudy1.setType(StudyPlaceType.Single);
        tempstudy1.setUserRating(4.0);
        tempstudy1.setStudyPlaceLat(56.17186630246226);
        tempstudy1.setStudyPlaceLong(10.19044975947223);

        StudyPlace tempstudy2 = new StudyPlace();
        tempstudy2.setTitle("Nygaard, 1. sal");
        tempstudy2.setType(StudyPlaceType.Group);
        tempstudy2.setUserRating(9.9);
        tempstudy2.setStudyPlaceLat(56.17270526308221);
        tempstudy2.setStudyPlaceLong(10.190984373182822);

        temp.add(tempstudy1);
        temp.add(tempstudy2);

        mStudyPlaceList.postValue(temp);

        //sendRequest("gs://findmystudyplace.appspot.com");

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
