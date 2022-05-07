package dk.au.mad22spring.appproject.group7.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import dk.au.mad22spring.appproject.group7.FMSPApplication;
import dk.au.mad22spring.appproject.group7.fragments.MapFragment;
import dk.au.mad22spring.appproject.group7.fragments.StudyPlaceListFragment;
import dk.au.mad22spring.appproject.group7.services.NotificationService;
import dk.au.mad22spring.appproject.group7.R;
import dk.au.mad22spring.appproject.group7.viewModels.StudyPlaceListViewModel;

public class OverviewActivity extends AppCompatActivity {

    //UI widgets:
    private Button btnLogOut;
    private Button btnMap;
    private Button btnList;
    private Button btnShareLocation;
    private Switch swtSingle;
    private ProgressBar prgbOverview;

    private StudyPlaceListViewModel viewModel;
    private ActivityResultLauncher<Intent> shareLocationLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        viewModel = new ViewModelProvider(this).get(StudyPlaceListViewModel.class);

        //Apply default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragList, StudyPlaceListFragment.newInstance())
                .commitNow();

        shareLocationLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result){
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(FMSPApplication.getAppContext(), R.string.txtSharedLocation, Toast.LENGTH_SHORT).show();
                }
            }
        });

        setUpUI();

        //Start foreground service to listen on new notifications:
        startForegroundService();

        viewModel.CheckForNewStudyplaces(this);

        viewModel.getIsStudyPlacesLoaded().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean result) {
                prgbOverview.setVisibility(View.GONE);

                if (!result) {
                    Toast.makeText(FMSPApplication.getAppContext(), R.string.errorLoadingStudyPlaces, Toast.LENGTH_SHORT);
                }
            }
        });
    }

    //Start listening on incomming notifications:
    private void startForegroundService() {
        Intent fgIntent = new Intent(this, NotificationService.class);
        startService(fgIntent);
    }

    private void setUpUI() {
        btnLogOut = findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.LogOut();
                setResult(RESULT_OK);
                finish();
            }
        });

        btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize fragment
                Fragment mapFragment=new MapFragment();

                // Open Map fragment
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragList,mapFragment)
                        .commit();
            }
        });

        btnList = findViewById(R.id.btnList);
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Change fragment to List fragment:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragList, StudyPlaceListFragment.newInstance())
                        .commitNow();
            }
        });

        btnShareLocation = findViewById(R.id.btnOverShareLocation);
        btnShareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FMSPApplication.getAppContext(), ShareLocationActivity.class);
                shareLocationLauncher.launch(i);
            }
        });

        swtSingle = findViewById(R.id.swtSingle);

        //This method is inspired by: https://www.youtube.com/watch?v=EH0ixNRoS8g
        swtSingle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    //remove all group places
                    viewModel.removeGroupPlaces();
                }
                else {
                    //add all group places
                    viewModel.addGroupPlaces();
                }
            }
        });

        prgbOverview = findViewById(R.id.prgbOverview);
        prgbOverview.setVisibility(View.VISIBLE);
    }
}