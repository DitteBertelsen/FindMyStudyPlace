package dk.au.mad22spring.appproject.group7.Fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import dk.au.mad22spring.appproject.group7.Adaptors.StudyPlaceListViewAdaptor;
import dk.au.mad22spring.appproject.group7.R;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

//This class is based on Tracker demo
public class StudyPlaceListFragment extends Fragment implements StudyPlaceListViewAdaptor.IStudyPlaceClickedListener {

    //Define objects for recycler view
    private StudyPlaceListViewAdaptor adapter;
    private RecyclerView rcvStudyPlaceView;
    private RecyclerView.LayoutManager layoutMan;

    //Define other objects
    private ArrayList<StudyPlace> studyPlaces;
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
        viewModel.getStudyPlaces().observe(this.getViewLifecycleOwner(), new Observer<ArrayList<StudyPlace>>() {
            @Override
            public void onChanged(ArrayList<StudyPlace> items) {
                studyPlaces = items;
                adapter.updateStudyPlaces(items);
            }
        });
    }

    @Override
    public void onItemClicked(int index) {
        viewModel.updateStudyPlaceRating(index);
    }

}