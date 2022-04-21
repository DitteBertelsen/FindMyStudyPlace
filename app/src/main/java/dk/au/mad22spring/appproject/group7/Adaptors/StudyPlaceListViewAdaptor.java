package dk.au.mad22spring.appproject.group7.Adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dk.au.mad22spring.appproject.group7.R;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class StudyPlaceListViewAdaptor extends RecyclerView.Adapter<StudyPlaceListViewAdaptor.StudyPlaceItemViewHolder>{

    public interface IStudyPlaceClickedListener{
        void onItemClicked(int index);
    }

    private ArrayList<StudyPlace> studyplaces;
    private IStudyPlaceClickedListener studyPlaceClickListener;

    public StudyPlaceListViewAdaptor(IStudyPlaceClickedListener listner){
        studyPlaceClickListener = listner;
    }

    public void updateStudyPlaces(ArrayList<StudyPlace> itemLists){
        studyplaces = itemLists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudyPlaceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.study_place_list_fragment, parent, false);
        StudyPlaceItemViewHolder vh = new StudyPlaceItemViewHolder(v, studyPlaceClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull StudyPlaceItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if(studyplaces == null){
            return 0;
        } else {
            return studyplaces.size();
        }
    }


    public class StudyPlaceItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtType;
        TextView txtLocation;
        TextView txtRating;

        IStudyPlaceClickedListener list;

        public StudyPlaceItemViewHolder(@NonNull View itemView, IStudyPlaceClickedListener listener) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgstudyplaceimage);
            txtType = itemView.findViewById(R.id.txtType);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtRating = itemView.findViewById(R.id.txtRating);
            list = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            list.onItemClicked(getAdapterPosition());
        }
    }
}
