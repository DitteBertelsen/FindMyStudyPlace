package dk.au.mad22spring.appproject.group7.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import dk.au.mad22spring.appproject.group7.Repository;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class MapFragmentViewmodel extends AndroidViewModel {
    private LiveData<List<StudyPlace>> studyPlaces;
    private Repository repository;

    public MapFragmentViewmodel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
        studyPlaces = repository.getAllStudyPlaces();
    }

    public LiveData<List<StudyPlace>> getStudyPlaces() {
        return studyPlaces;
    }

}
