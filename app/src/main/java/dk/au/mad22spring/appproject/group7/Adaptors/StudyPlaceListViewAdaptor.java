package dk.au.mad22spring.appproject.group7.Adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dk.au.mad22spring.appproject.group7.R;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;

public class StudyPlaceListViewAdaptor extends RecyclerView.Adapter<StudyPlaceListViewAdaptor.StudyPlaceItemViewHolder>{

    private List<StudyPlace> studyPlaces;
    private IStudyPlaceClickedListener studyPlaceClickListener;

    public StudyPlaceListViewAdaptor(IStudyPlaceClickedListener listner){
        studyPlaceClickListener = listner;
        studyPlaces = new ArrayList<>();
    }

    public void updateStudyPlaces(List<StudyPlace> itemLists){
        studyPlaces = itemLists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudyPlaceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.study_place_list_item, parent, false);

        return new StudyPlaceItemViewHolder(v, studyPlaceClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudyPlaceItemViewHolder holder, int position) {
        //Todo add image, position
        final StudyPlace studyPlace = studyPlaces.get(position);

        holder.txtTitle.setText(studyPlace.getTitle());
        holder.txtType.setText(studyPlace.getType().toString());
        holder.txtRating.setText(studyPlace.getUserRating().toString());
        holder.ratbarRating.setRating(studyPlace.getUserRating().floatValue());
        holder.ratbarRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float newRating, boolean bool) {
                if(bool) {
                    studyPlaceClickListener.onUserRatingChanged(studyPlace, newRating);
                    holder.txtRating.setText(""+newRating);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(studyPlaces == null){
            return 0;
        } else {
            return studyPlaces.size();
        }
    }


    public class StudyPlaceItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtType;
        TextView txtLocation;
        TextView txtRating;
        RatingBar ratbarRating;


        private IStudyPlaceClickedListener list;

        public StudyPlaceItemViewHolder(@NonNull View itemView, IStudyPlaceClickedListener listener) {
            super(itemView);
            list = listener;
            imgIcon = itemView.findViewById(R.id.imgstudyplaceimage);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtType = itemView.findViewById(R.id.txtType);
            txtRating = itemView.findViewById(R.id.txtRating);
            ratbarRating = itemView.findViewById(R.id.ratbarRating);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            list.onItemClicked(getAdapterPosition());
        }
    }
}
