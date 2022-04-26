package dk.au.mad22spring.appproject.group7;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.au.mad22spring.appproject.group7.Fragments.StudyPlaceListViewModel;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class MainActivity extends AppCompatActivity {

    EditText edtEmailAddress, edtPassword;
    Button btnCreateNewUser, btnLogIn;

    ActivityResultLauncher<Intent> signInLauncher, overviewLauncher;
    FirebaseAuth auth;
    private Repository repository;
    private LiveData<ArrayList<StudyPlace>> studyPlaces;
    private StudyPlaceListViewModel studyPlaceListViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        repository = Repository.getInstance();

        if(auth == null) {
            auth = FirebaseAuth.getInstance();
        }

        setupUI();

        signInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->  {
            if (result.getResultCode() == Activity.RESULT_OK) {
                String uEmail = auth.getCurrentUser().getEmail();
                Toast.makeText(this, uEmail + R.string.txtloggedIn, Toast.LENGTH_SHORT).show();
                SignIn();
            }
        });

        overviewLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK) {
                    Toast.makeText(FMSPApplication.getAppContext(), R.string.txtloggedOut, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setupUI() {
        edtEmailAddress = findViewById(R.id.edtEmailAddress);
        edtPassword = findViewById(R.id.edtPassword);

        btnLogIn = findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });

        btnCreateNewUser = findViewById(R.id.btnCreateNewUser);
        btnCreateNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewUser();
            }
        });


    }

    private void createNewUser() {
        String email = edtEmailAddress.getText().toString();
        String password = edtPassword.getText().toString();

        if(email == null || email.length()<1 || !email.contains("@")) {
            edtEmailAddress.setError("" + R.string.errorInvalidEmail);
            if (password == null || password.length()< 8)
            {
                edtPassword.setError("" + R.string.errorInvalidPw);
            }
        }
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Log.d(Constants.TAG_MAIN, "onComplete: Created User");
                            Toast.makeText(FMSPApplication.getAppContext(), R.string.userCreated, Toast.LENGTH_SHORT).show();
                            SignIn();
                        } else {
                            Log.d(Constants.TAG_MAIN, "onComplete: Failed to create user", task.getException());
                            Toast.makeText(FMSPApplication.getAppContext(), R.string.errorUserCreatation, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void SignIn() {
        if(auth.getCurrentUser() != null) {
            goToOverview();
        }
        else {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build());

            signInLauncher.launch(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build());

        }
    }

    private void goToOverview() {
        Intent intent = new Intent(this, OverviewActivity.class);

        String strEmail = auth.getCurrentUser().getEmail();
        String[] arrayEmail = strEmail.split("@",2);
        intent.putExtra(Constants.USER_NAME, arrayEmail[0]);

        overviewLauncher.launch(intent);

    }


}