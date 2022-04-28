package dk.au.mad22spring.appproject.group7;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import dk.au.mad22spring.appproject.group7.viewModels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmailAddress, edtPassword;
    Button btnCreateNewUser, btnLogIn;

    ActivityResultLauncher<Intent> signInLauncher, overviewLauncher;

    private Boolean isUserCreated = false;

    private Repository repository;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        repository = Repository.getInstance();


        setupUI();

        //Setup ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        //Listens on isUserCreated
        loginViewModel.getUserCreatedResult().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isUserCreated = aBoolean;
            }
        });


        signInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->  {
            if (result.getResultCode() == Activity.RESULT_OK) {
                String uEmail = loginViewModel.getCurrentUser();
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
        loginViewModel.createNewUser(email, password,this);

        if (isUserCreated) {
            Toast.makeText(FMSPApplication.getAppContext(), R.string.userCreated, Toast.LENGTH_SHORT).show();
            SignIn();
        } else {
            Toast.makeText(FMSPApplication.getAppContext(), R.string.errorUserCreatation, Toast.LENGTH_SHORT).show();
        }

    }

    private void SignIn() {
        if(loginViewModel.isSignedIn()) {
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

        String strEmail = loginViewModel.getCurrentUser();
        String[] arrayEmail = strEmail.split("@",2);
        intent.putExtra(Constants.USER_NAME, arrayEmail[0]);

        overviewLauncher.launch(intent);

    }


}