package dk.au.mad22spring.appproject.group7;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
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
            sendRequestUsingVolley("https://firebasestorage.googleapis.com/v0/b/findmystudyplace.appspot.com/o/jsonTestStudyPlaces.txt?alt=media&token=334062b4-e595-4f48-8438-68416bef56eb&fbclid=IwAR1mTrXuafUx6ZW8ccd2uoxCq7zP3OgOleTo6G3SRv-57ezKBe_J2cN-s5k");

            //sendRequest("jsonTestStudyPlaces.txt");
        }

        /*//Todo remove temp. data
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
        */


        return mStudyPlaceList;
    }

    private void sendRequest(String fileName) {
        StorageReference studyPlaceRef = storageRef.child(fileName);
        studyPlaceRef
                .getStream()
                .addOnCompleteListener(new OnCompleteListener<StreamDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<StreamDownloadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(Constants.TAG_Rep, "onReponse" + task.getResult().toString());
                            parseJson(task.getResult().toString());
                        }
                        else {
                            Log.e(Constants.TAG_Rep, "onErrorResponse: Oh! That did not work..!");
                        }
                    }
                });
    }


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
                Log.e(Constants.TAG_Rep, "onErrorResponse: Oh! That did not work..!");

            }
        });
        queue.add(stringRequest);
    }

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
