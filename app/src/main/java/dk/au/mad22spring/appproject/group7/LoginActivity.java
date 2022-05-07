package dk.au.mad22spring.appproject.group7;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

    private EditText edtEmailAddress, edtPassword;
    private Button btnCreateNewUser, btnLogIn;
    private ProgressBar prgbLogin;
    private ActivityResultLauncher<Intent> signInLauncher, overviewLauncher;
    private Boolean isUserCreated = false;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        //Setup ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

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

        //Listens on isUserCreated
        loginViewModel.getUserCreatedResult().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isUserCreated = aBoolean;
                prgbLogin.setVisibility(View.GONE);

                if (isUserCreated) {
                    Toast.makeText(FMSPApplication.getAppContext(), R.string.userCreated, Toast.LENGTH_SHORT).show();
                    SignIn();
                } else {
                    Toast.makeText(FMSPApplication.getAppContext(), R.string.errorUserCreatation, Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginViewModel.isSignedIn().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSignedIn) {
                prgbLogin.setVisibility(View.GONE);
                if (isSignedIn){
                    goToOverview();
                }
                else {
                    Toast.makeText(FMSPApplication.getAppContext(), R.string.errorUserSignIn, Toast.LENGTH_SHORT);
                    edtEmailAddress.setError(getString(R.string.errorUserSignIn));
                    edtPassword.setError(getString(R.string.errorUserSignIn));
                }
            }
        });

        setupUI();
    }

    private void setupUI() {
        addLogoToActionBar();

        edtEmailAddress = findViewById(R.id.edtEmailAddress);
        edtPassword = findViewById(R.id.edtPassword);

        //This widget is inspired by: https://www.tutorialspoint.com/android/android_loading_spinner.htm
        prgbLogin = findViewById(R.id.prgbLogin);
        prgbLogin.setVisibility(View.GONE);

        btnLogIn = findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prgbLogin.setVisibility(View.VISIBLE);
                edtEmailAddress.setError(null);
                edtPassword.setError(null);
                closeKeyboard();

                SignIn();
            }
        });

        btnCreateNewUser = findViewById(R.id.btnCreateNewUser);
        btnCreateNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtEmailAddress.setError(null);
                edtPassword.setError(null);
                prgbLogin.setVisibility(View.VISIBLE);
                closeKeyboard();

                createNewUser();
            }
        });
    }

    private void createNewUser() {
        String email = edtEmailAddress.getText().toString();
        String password = edtPassword.getText().toString();

        //Validating email and password:
        if(email == null || email.length()<1 || !email.contains("@")) {
            edtEmailAddress.setError(getString(R.string.errorInvalidEmail));
            prgbLogin.setVisibility(View.GONE);
        }
        if (password == null || password.length()< 8)
        {
            edtPassword.setError(getString(R.string.errorInvalidPw));
            prgbLogin.setVisibility(View.GONE);
        }
        else {
            loginViewModel.createNewUser(email, password, this);
        }
    }

    private void SignIn() {
        String email = edtEmailAddress.getText().toString();
        String password = edtPassword.getText().toString();

        //Validating email and password:
        if(email == null || email.length()<1 || !email.contains("@")) {
            edtEmailAddress.setError(getString(R.string.errorInvalidEmail));
            prgbLogin.setVisibility(View.GONE);
        }
        if (password == null || password.length()< 8)
        {
            edtPassword.setError(getString(R.string.errorInvalidPw));
            prgbLogin.setVisibility(View.GONE);
        }
        else {
            loginViewModel.SignIn(email,password,this);
        }
    }

    private void goToOverview() {
        Intent intent = new Intent(this, OverviewActivity.class);
        overviewLauncher.launch(intent);
    }

    //This method is based on: https://www.geeksforgeeks.org/how-to-programmatically-hide-android-soft-keyboard/
    private void closeKeyboard() {
        View view = this.getCurrentFocus();

        if (view != null) {
            InputMethodManager manager
                    = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }

    //This method is based on https://stackoverflow.com/questions/43389066/how-to-set-an-icon-in-title-bar-including-with-name-of-the-app
    private void addLogoToActionBar() {
        //Set up actionBar:
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.aulogo_hvid);
    }
}