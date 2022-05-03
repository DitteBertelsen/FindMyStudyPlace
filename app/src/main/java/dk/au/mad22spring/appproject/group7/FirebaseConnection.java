package dk.au.mad22spring.appproject.group7;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class FirebaseConnection {

    private FirebaseAuth auth;
    private static FirebaseConnection instance;
    private FirebaseDatabase database;

    private MutableLiveData<Boolean> isUserCreated;
    private MutableLiveData<Boolean> isUserSignedIn;

    //Upset database
    private ArrayList<StudyPlace> studyPlaces;
    //private List<StudyPlace> studyPlaces;
    private MutableLiveData<List<StudyPlace>> mStudyPlaces;


    public static FirebaseConnection getInstance()
    {
        if(instance == null)
        {
            instance = new FirebaseConnection();
        }
        return instance;
    }


    public FirebaseConnection() {
        if(auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }

        studyPlaces = new ArrayList<>();
    }

    public String getCurrentUser() {
        String userEmail = auth.getCurrentUser().getEmail();

        if (userEmail != "")
            setupFirebaseListener();

        return userEmail;
    }

    public void SignIn(String email, String password, Activity activity){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(Constants.TAG_MAIN, "onComplete: User signed in");
                    isUserSignedIn.postValue(true);
                }
                else {
                    Log.d(Constants.TAG_MAIN, "onComplete: Failed to sign user in");
                    isUserSignedIn.postValue(false);
                }
            }
        });
    }

    public MutableLiveData<Boolean> isSignedIn() {
        if (isUserSignedIn == null){
            isUserSignedIn = new MutableLiveData<>();
        }
        return isUserSignedIn;
    }

    public MutableLiveData<Boolean> getUserCreatedResult()
    {
        if(isUserCreated == null)
        {
            isUserCreated = new MutableLiveData<>();
        }

        return isUserCreated;
    }

    public void createNewUser(String email, String password, Activity activity)
    {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(Constants.TAG_MAIN, "onComplete: Created User");
                            isUserCreated.postValue(true);
                        } else {
                            Log.d(Constants.TAG_MAIN, "onComplete: Failed to create user", task.getException());
                            isUserCreated.postValue(false);
                        }

                    }
                });

    }

    //MutableLiveData for activities to observe for updates in realtime database
    public MutableLiveData<List<StudyPlace>> getStudyPlacesRealTimeDb() {
        if(mStudyPlaces == null) {
            mStudyPlaces = new MutableLiveData<>(new ArrayList());
        }

        return mStudyPlaces;
    }

    //Method created based on the Demo2 from lesson 10: WorldMan
    //sets up a Firebase listener for the list of StudyPlaces
    private void setupFirebaseListener() {
        //TODO get the userId from the repository instead
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference refDB = database.getReference("users/" + userId + "/studyPlaces");

        //Listener listening for changes in the database
        refDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Uses the iterator to go through results
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                while(snapshots.iterator().hasNext()) {
                    studyPlaces.add(snapshots.iterator().next().getValue(StudyPlace.class));
                }
                if(studyPlaces.size()>0) {
                    getStudyPlacesRealTimeDb().postValue(studyPlaces);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("DATA", "onCancelled: Failed to read values", error.toException());
            }
        });

    }

    //Method created based on the Demo2 from lesson 10: WorldMan
    public void onStudyPlaceRatingChanged(StudyPlace studyPlace, double newRating){
        studyPlace.setUserRating(newRating);
        try {
            String userId = auth.getCurrentUser().getUid();

            DatabaseReference refPlaces = database.getReference("users/" + userId + "/studyPlaces");   //reference to list of study places
            String key = refPlaces.push().getKey(); //push adds new element in list - save key for easy update of objects

            refPlaces.child(key).setValue(studyPlace);

        } catch (Exception ex) {
            Log.e("DATA", "onStudyPlaceRatingChanged: Error updating user rating", ex);
        }
    }

    public void saveStudyPlaceList(List<StudyPlace> studyPlaceList) {
        //TODO test save
        try {
            String userId = auth.getCurrentUser().getUid();
            DatabaseReference studyRef = database.getReference("users");

            for (StudyPlace studyPlace: studyPlaceList) {
                //String key = studyRef.push().getKey();
                studyRef.child(userId).child("studyplaces").child(""+studyPlace.getId()).setValue(studyPlace);
                Log.e("DATA", "saveStudyPlaceList: study place added:" + studyPlace.getTitle());
            }

        } catch (Exception ex) {
            Log.e("DATA", "saveStudyPlaceList: Error saving user study places", ex);
        }
    }

    public void LogOut(){
        auth.signOut();
    }
}
