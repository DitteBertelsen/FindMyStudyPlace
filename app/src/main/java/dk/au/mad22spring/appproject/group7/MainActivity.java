package dk.au.mad22spring.appproject.group7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import dk.au.mad22spring.appproject.group7.Fragments.StudyPlaceListFragment;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_MAIN = "Main";

    private Repository repository;
    private LiveData<ArrayList<StudyPlace>> studyPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        repository = Repository.getInstance();

        studyPlaces = repository.getAllStudyPlaces();

        Log.d(TAG_MAIN, "onCreate: " + studyPlaces.getValue().size());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragList, StudyPlaceListFragment.newInstance())
                .commitNow();
    }


}