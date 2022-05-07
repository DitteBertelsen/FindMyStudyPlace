package dk.au.mad22spring.appproject.group7.fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import dk.au.mad22spring.appproject.group7.adapters.IStudyPlaceClickedListener;
import dk.au.mad22spring.appproject.group7.adapters.StudyPlaceListViewAdaptor;
import dk.au.mad22spring.appproject.group7.R;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;
import dk.au.mad22spring.appproject.group7.viewModels.StudyPlaceListViewModel;

//This class is based on Tracker demo
public class StudyPlaceListFragment extends Fragment implements IStudyPlaceClickedListener {

    //Define objects for recycler view
    private StudyPlaceListViewAdaptor adapter;
    private RecyclerView rcvStudyPlaceView;
    private RecyclerView.LayoutManager layoutMan;

    //Define other objects
    private List<StudyPlace> studyPlaces;
    private StudyPlaceListViewModel viewModel;

    public static StudyPlaceListFragment newInstance() {
        return new StudyPlaceListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.study_place_list_fragment, container, false);

        rcvStudyPlaceView = v.findViewById(R.id.rcvListView);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new StudyPlaceListViewAdaptor(this);
        layoutMan = new LinearLayoutManager(getContext());

        rcvStudyPlaceView.setAdapter(adapter);
        rcvStudyPlaceView.setLayoutManager(layoutMan);

        viewModel = new ViewModelProvider(getActivity()).get(StudyPlaceListViewModel.class);

        viewModel.getStudyPlaces().observe(this.getViewLifecycleOwner(), new Observer<List<StudyPlace>>() {
            @Override
            public void onChanged(List<StudyPlace> items) {
                studyPlaces = items;
                adapter.updateStudyPlaces(items);
            }
        });
    }

    @Override
    public void onItemClicked(int index) {
        //start detail activity (not implemented in this project)
    }

    @Override
    public void onUserRatingChanged(StudyPlace studyPlace, double newRating) {
        viewModel.onUserRatingChanged(studyPlace, newRating);
    }

}