package dk.au.mad22spring.appproject.group7;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseConnection {

    FirebaseAuth auth;
    private static FirebaseConnection instance;

    private MutableLiveData<Boolean> isUserCreated;

    public static FirebaseConnection getInstance()
    {
        if(instance == null)
        {
            instance = new FirebaseConnection();
        }
        return instance;
    }


    public  FirebaseConnection() {
        if(auth == null) {
            auth = FirebaseAuth.getInstance();
        }
    }

    public String getCurrentUser() {
        String userEmail = auth.getCurrentUser().getEmail();

        return userEmail;
    }

    public Boolean isSignedIn() {
        if(auth.getCurrentUser() == null)
        {
            return false;
        } else {
            return true;
        }
    }

    public MutableLiveData<Boolean> getUserCreatedResult()
    {
        if(isUserCreated == null)
        {
            isUserCreated = new MutableLiveData<>();
        }

        return isUserCreated;
    }

    //TODO hvordan fixer vi at addOnCompleteListener tager en aktivitet som parameter?
    /*
    public void createNewUser(String email, String password)
    {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Log.d(Constants.TAG_MAIN, "onComplete: Created User");

                        } else {
                            Log.d(Constants.TAG_MAIN, "onComplete: Failed to create user", task.getException());
                        }
                        isUserCreated.postValue(task.isSuccessful());
                    }
                });

    }*/

}
