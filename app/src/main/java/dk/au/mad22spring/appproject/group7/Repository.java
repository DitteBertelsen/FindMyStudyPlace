package dk.au.mad22spring.appproject.group7;

import android.app.Activity;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import dk.au.mad22spring.appproject.group7.database.CloudStorage;
import dk.au.mad22spring.appproject.group7.database.FirebaseConnection;
import dk.au.mad22spring.appproject.group7.models.NotificationModel;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class Repository {

    //Instance for Singleton pattern
    private static Repository instance;

    //Firebase classes
    private CloudStorage cloudStorage;
    private FirebaseConnection firebaseConnection;

    //private List<StudyPlace> realtimeList;
    private ArrayList<StudyPlace> storageList;

    //Singleton patten
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
                        List<StudyPlace> realtimeList = new ArrayList<>();
                        if (studyPlaces.size() == 0 ) {
                            realtimeList = storageList;

                        }
                        if (storageList.size() != 0) {
                            realtimeList = studyPlaces;
                            compareStudyplaces(realtimeList);
                        }
                    }
                });
            }
        });
    }

    //Todo delete?
    //This method is called when an user does not have any study places in db (an user is created):
    /*private void initializeStudyplaces() {
        //if null - hent study places from firestorage:
        //realtimeList = cloudStorage.getStudyPlaceListItems().getValue();

        //gem i real time db for log in user
        firebaseConnection.saveStudyPlaceList(realtimeList);
    }

     */

    //This method is called to check if there are changes in the study places in the storage:
    public void compareStudyplaces(List<StudyPlace> realtimeList) {
        //Check that both async calls has returned
        if (storageList.size() != 0) {
            //In case a new study place has been added to the storage list:
            for (StudyPlace studyplace: storageList) {
                //Add the study place if it exists in the storageList but not in db list:
                if (!realtimeList.contains(studyplace.getId())) {
                    realtimeList.add(studyplace);
                }
            }

            //Creates a temporary object to collect all items to delete:
            ArrayList<StudyPlace> itemsToDelete = new ArrayList<>();

            //In case a study place has been removed from the storage list:
            for (StudyPlace studyplace: realtimeList) {
                //Remove the study place if it exists in the db list but not in storageList:
                if (storageList.contains(studyplace.getId()))
                {
                    itemsToDelete.add(studyplace);
                }
            }

            //Deleting the items from realtimeList:
            for (StudyPlace studyplace: itemsToDelete) {
                realtimeList.remove(studyplace);
            }

            //Update all attributes:
            for (StudyPlace storageStudyplace: storageList) {
                //Find the matching object of storageStudyPlace i realtimeList:
                StudyPlace realTimeStudyPlace = new StudyPlace();
                Boolean studyplaceHasChanged = false;
                int indexInRealtimeList = -1;

                for (StudyPlace studyPlace: realtimeList) {
                    if (storageStudyplace.getId() == studyPlace.getId()) {
                        realTimeStudyPlace = studyPlace;
                        indexInRealtimeList = realtimeList.indexOf(studyPlace);

                        realTimeStudyPlace.setTitle(storageStudyplace.getTitle());
                        realTimeStudyPlace.setStudyPlaceLat(storageStudyplace.getStudyPlaceLat());
                        realTimeStudyPlace.setStudyPlaceLong(storageStudyplace.getStudyPlaceLong());
                        realTimeStudyPlace.setImage(storageStudyplace.getImage());
                        realTimeStudyPlace.setType(storageStudyplace.getType());
                        realTimeStudyPlace.setProperties(storageStudyplace.getProperties());

                        studyplaceHasChanged = true;
                    }
                }
                if (studyplaceHasChanged) {
                    realtimeList.remove(indexInRealtimeList);
                    realtimeList.add(realTimeStudyPlace);
                }
            }

            //Save the updated list in db:
            firebaseConnection.saveStudyPlaceList(realtimeList);
        }
    }

    //When the user rating is changed:
    public void onUserRatingChanged(StudyPlace studyPlace, double newRating) {
        firebaseConnection.onStudyPlaceRatingChanged(studyPlace, newRating);
    }

    //Gets all study places from realtime-database
    public LiveData<List<StudyPlace>> getAllStudyPlaces()
    {
        return firebaseConnection.getStudyPlacesRealTimeDb();
    }

    //Gets notification from realtime database when a new notification
    // has been pushed for the given user.
    public MutableLiveData<NotificationModel> getNotifications()
    {
        return firebaseConnection.getNotifications();
    }

    //When the user has pushed the 'Share Location' button:
    public void pushNotification(NotificationModel notificationModel, ArrayList<String> friends) {
        firebaseConnection.pushNotification(notificationModel, friends);
    }

    //Gets the current user through firebase-connection
    public String getCurrentUser() {
        return firebaseConnection.getCurrentUser();
    }

    //Returns true if a user is signed in:
    public MutableLiveData<Boolean> isSignedIn() {
        return firebaseConnection.isSignedIn();
    }

    //Validates login-information through firebase-connection
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

    //When the user pushes 'Log out' button:
    public void LogOut(){
        firebaseConnection.LogOut();
    }

    public void poke() {
        firebaseConnection.pokeStudyPlaces();
    }
}
