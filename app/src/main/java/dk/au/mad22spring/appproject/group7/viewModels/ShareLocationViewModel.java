package dk.au.mad22spring.appproject.group7.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;

import dk.au.mad22spring.appproject.group7.Repository;
import dk.au.mad22spring.appproject.group7.models.NotificationModel;

public class ShareLocationViewModel extends AndroidViewModel {

    private Repository repository;

    public ShareLocationViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();

    }

    public void pushNotification(NotificationModel notificationModel, ArrayList<String> friends)
    {
        repository.pushNotification(notificationModel, friends);
    }
}
