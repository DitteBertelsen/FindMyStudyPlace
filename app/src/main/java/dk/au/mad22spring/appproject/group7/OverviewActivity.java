package dk.au.mad22spring.appproject.group7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import dk.au.mad22spring.appproject.group7.Fragments.StudyPlaceListFragment;
import dk.au.mad22spring.appproject.group7.Fragments.StudyPlaceListViewModel;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class OverviewActivity extends AppCompatActivity {

    //Setup UI
    Button btnLogOut;
    Button btnMap;

    private String userName;

    private LiveData<ArrayList<StudyPlace>> studyPlaces;
    private StudyPlaceListViewModel studyPlaceListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Intent intent = getIntent();
        userName = intent.getStringExtra(Constants.USER_NAME);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragList, StudyPlaceListFragment.newInstance())
                .commitNow();

        btnLogOut = findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        btnMap=findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize fragment ref: https://www.geeksforgeeks.org/how-to-implement-google-map-inside-fragment-in-android/
                Fragment mapFragment=new MapFragment();

                // Open fragment
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragList,mapFragment)
                        .commit();
            }
        });

        Toast.makeText(this, R.string.txtWelcome + userName, Toast.LENGTH_LONG);
    }
}