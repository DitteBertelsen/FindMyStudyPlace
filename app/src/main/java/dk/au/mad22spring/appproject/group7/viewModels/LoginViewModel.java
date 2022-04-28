package dk.au.mad22spring.appproject.group7.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

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

    public Boolean isSignedIn() {
        return repository.isSignedIn();
    }

}
