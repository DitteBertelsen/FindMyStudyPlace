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
    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public Repository() {
        cloudStorage = CloudStorage.getInstance();
        firebaseConnection = FirebaseConnection.getInstance();
    }

//SIGNING IN:

    //Returns true if a user is signed in:
    public LiveData<Boolean> isSignedIn() {
        return firebaseConnection.isSignedIn();
    }

    //Validates login-information through firebase-connection
    public void SignIn(String email, String password, Activity activity) {
        firebaseConnection.SignIn(email, password, activity);
    }

    //Gets the current user through firebase-connection
    public String getCurrentUser() {
        return firebaseConnection.getCurrentUser();
    }

    //When the user pushes 'Log out' button:
    public void LogOut() {
        firebaseConnection.LogOut();
    }

//USER CREATION:

    //Returns true if a new user i successfully created
    public LiveData<Boolean> getUserCreatedResult() {
        return firebaseConnection.getUserCreatedResult();
    }

    //Calls Firebase Connection and tries to create a new user
    public void createNewUser(String email, String password, Activity activity) {
        firebaseConnection.createNewUser(email, password, activity);
    }

//STUDY PLACES:

    //Gets all study places from realtime-database
    public LiveData<List<StudyPlace>> getAllStudyPlaces() {
        return firebaseConnection.getStudyPlacesRealTimeDb();
    }

    //Check if there has been made changes to the study places in the storage file:
    public void CheckForNewStudyplaces(LifecycleOwner lifecycleOwner) {
        cloudStorage.getStudyPlaceListItems().observe(lifecycleOwner, new Observer<ArrayList<StudyPlace>>() {
            @Override
            public void onChanged(ArrayList<StudyPlace> studyPlaces) {
                storageList = studyPlaces;

                //Only start listening on getStudyPlacesRealTimeDb() when the storage list has returned:
                firebaseConnection.getStudyPlacesRealTimeDb().observe(lifecycleOwner, new Observer<List<StudyPlace>>() {
                    @Override
                    public void onChanged(List<StudyPlace> studyPlaces) {
                        List<StudyPlace> realtimeList = new ArrayList<>();

                        //Transfer the study places retrieved from db to realtime list:
                        if (studyPlaces.size() != 0) {
                            realtimeList = studyPlaces;
                        }
                        compareStudyplaces(realtimeList);
                    }
                });
            }
        });
    }

    //This method is called to check if there are changes in the study places in the storage:
    public void compareStudyplaces(List<StudyPlace> realtimeList) {
        //This controls if any changes has been made:
        Boolean realTimeListHasChanges = false;

        //Check that both async calls has returned
        if (storageList.size() != 0) {
            //In case a new study place has been added to the storage list:
            for (StudyPlace studyplace : storageList) {
                //Add the study place if it exists in the storageList but not in db list:
                if (realtimeList.contains(studyplace.getId())) {
                    realtimeList.add(studyplace);
                    realTimeListHasChanges = true;
                }
            }

            //Creates a temporary object to collect all items to delete:
            ArrayList<StudyPlace> itemsToDelete = new ArrayList<>();

            //In case a study place has been removed from the storage list:
            for (StudyPlace studyplace : realtimeList) {
                //Remove the study place if it exists in the db list but not in storageList:
                if (storageList.contains(studyplace.getId())) {
                    itemsToDelete.add(studyplace);
                }
            }

            //Deleting the items from realtimeList:
            for (StudyPlace studyplace : itemsToDelete) {
                realtimeList.remove(studyplace);
                realTimeListHasChanges = true;
            }

            //Update all attributes:
            for (StudyPlace storageStudyplace : storageList) {
                //Find the matching object of storageStudyPlace i realtimeList:
                StudyPlace realTimeStudyPlace = new StudyPlace();

                //This bool is used to control when to save to db:
                Boolean studyplaceAttributesHaveChanged = false;
                int indexInRealtimeList = -1;

                for (StudyPlace studyPlace : realtimeList) {
                    if (storageStudyplace.getId() == studyPlace.getId()) {
                        realTimeStudyPlace = studyPlace;
                        indexInRealtimeList = realtimeList.indexOf(studyPlace);

                        if (realTimeStudyPlace.getTitle() != storageStudyplace.getTitle()) {
                            realTimeStudyPlace.setTitle(storageStudyplace.getTitle());
                            realTimeListHasChanges = true;
                            studyplaceAttributesHaveChanged = true;
                        }

                        if (realTimeStudyPlace.getStudyPlaceLat() != storageStudyplace.getStudyPlaceLat()) {
                            realTimeStudyPlace.setStudyPlaceLat(storageStudyplace.getStudyPlaceLat());
                            realTimeListHasChanges = true;
                            studyplaceAttributesHaveChanged = true;
                        }
                        if (realTimeStudyPlace.getStudyPlaceLong() != storageStudyplace.getStudyPlaceLong()) {
                            realTimeStudyPlace.setStudyPlaceLong(storageStudyplace.getStudyPlaceLong());
                            realTimeListHasChanges = true;
                            studyplaceAttributesHaveChanged = true;
                        }
                        if (realTimeStudyPlace.getImage() != storageStudyplace.getImage()) {
                            realTimeStudyPlace.setImage(storageStudyplace.getImage());
                            realTimeListHasChanges = true;
                            studyplaceAttributesHaveChanged = true;
                        }

                        if (realTimeStudyPlace.getType() != storageStudyplace.getType()) {
                            realTimeStudyPlace.setType(storageStudyplace.getType());
                            realTimeListHasChanges = true;
                            studyplaceAttributesHaveChanged = true;
                        }
                        if (realTimeStudyPlace.getProperties() != storageStudyplace.getProperties()) {
                            realTimeStudyPlace.setProperties(storageStudyplace.getProperties());
                            realTimeListHasChanges = true;
                            studyplaceAttributesHaveChanged = true;
                        }
                    }
                }
                if (studyplaceAttributesHaveChanged) {
                    realtimeList.remove(indexInRealtimeList);
                    realtimeList.add(realTimeStudyPlace);
                }
            }

            //Only save to db if there has been made any changes:
            if (realTimeListHasChanges) {
                //Save the updated list in db:
                firebaseConnection.saveStudyPlaceList(realtimeList);
            }
        }
    }

    //This method will invoke all observers:
    public void invokeGetStudyplaces() {
        firebaseConnection.invokeGetStudyplaces();
    }

    //When the user rating is changed:
    public void onUserRatingChanged(StudyPlace studyPlace, double newRating) {
        firebaseConnection.onStudyPlaceRatingChanged(studyPlace, newRating);
    }

//NOTIFICATIONS:

    //Gets notification from realtime database when a new notification
    // has been pushed for the given user.
    public LiveData<NotificationModel> getNotifications() {
        return firebaseConnection.getNotifications();
    }

    //When the user has pushed the 'Share Location' button:
    public void pushNotification(NotificationModel notificationModel, ArrayList<String> friends) {
        firebaseConnection.pushNotification(notificationModel, friends);
    }

    //Delete notficifations in db when it is created:
    public void deleteNotification() {
        firebaseConnection.deleteNotification();
    }

    public LiveData<Boolean> getNotificationPushResult() {
        return firebaseConnection.getNotificationPushResult();
    }
}
