package dk.au.mad22spring.appproject.group7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import dk.au.mad22spring.appproject.group7.Fragments.StudyPlaceListFragment;
import dk.au.mad22spring.appproject.group7.Fragments.StudyPlaceListViewModel;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class OverviewActivity extends AppCompatActivity {

    //Setup UI
    Button btnLogOut;

    private LiveData<ArrayList<StudyPlace>> studyPlaces;
    private StudyPlaceListViewModel studyPlaceListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);


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

    }
}