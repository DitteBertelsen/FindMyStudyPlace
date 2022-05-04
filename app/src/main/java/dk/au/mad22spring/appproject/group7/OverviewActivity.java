package dk.au.mad22spring.appproject.group7;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dk.au.mad22spring.appproject.group7.Adaptors.StudyPlaceListViewAdaptor;
import dk.au.mad22spring.appproject.group7.Fragments.StudyPlaceListFragment;
import dk.au.mad22spring.appproject.group7.Fragments.StudyPlaceListViewModel;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class OverviewActivity extends AppCompatActivity {

    //Setup UI
    Button btnLogOut;
    Button btnMap;
    Button btnList;
    Button btnShareLocation;
    Switch swtSingle;


    private String userName;

    private LiveData<ArrayList<StudyPlace>> studyPlaces;
    private StudyPlaceListViewModel studyPlaceListViewModel;
    private StudyPlaceListViewModel viewModel;

    private ActivityResultLauncher<Intent> shareLocationLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Intent intent = getIntent();
        userName = intent.getStringExtra(Constants.USER_NAME);

        viewModel = new ViewModelProvider(this).get(StudyPlaceListViewModel.class);

        //Apply default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragList, StudyPlaceListFragment.newInstance())
                .commitNow();

        shareLocationLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK){
                Toast.makeText(this, R.string.txtSharedLocation, Toast.LENGTH_SHORT).show();
            }
        });

        setUpUI();

        //viewModel.CheckForNewStudyplaces(this);
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

                // Open fragment
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragList,mapFragment)
                        .commit();
            }
        });

        btnList = findViewById(R.id.btnList);
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                StudyPlaceListViewModel viewModel = new ViewModelProvider(OverviewActivity.this).get(StudyPlaceListViewModel.class);

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

        Toast.makeText(this, R.string.txtWelcome + userName, Toast.LENGTH_LONG);
    }
}