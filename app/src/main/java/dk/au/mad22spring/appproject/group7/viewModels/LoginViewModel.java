package dk.au.mad22spring.appproject.group7.viewModels;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import dk.au.mad22spring.appproject.group7.Repository;

public class LoginViewModel extends AndroidViewModel {

    private Repository repository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
    }

    public String getCurrentUser() {
        return repository.getCurrentUser();
    }

    public LiveData<Boolean> isSignedIn() {
        return repository.isSignedIn();
    }
    public void SignIn(String email, String password, Activity activity){
        repository.SignIn(email,password,activity);
    }

    //Returns true if a new user i successfully created
    public LiveData<Boolean> getUserCreatedResult() {
        return repository.getUserCreatedResult();
    }

    //Calls all the way to Firebase Connection and tries to create a new user
    public void createNewUser(String email, String password, Activity activity) {
        repository.createNewUser(email, password, activity);
    }

    public void CheckForNewStudyplaces(LifecycleOwner lifecycleOwner) {
        repository.CheckForNewStudyplaces(lifecycleOwner);
    }
}
