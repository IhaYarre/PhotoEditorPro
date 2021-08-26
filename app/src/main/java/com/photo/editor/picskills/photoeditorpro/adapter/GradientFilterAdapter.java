package com.photo.editor.picskills.photoeditorpro.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.picskills.photoeditorpro.R;
import com.photo.editor.picskills.photoeditorpro.model.GradientFilterModel;

import java.util.ArrayList;

public class GradientFilterAdapter extends RecyclerView.Adapter<GradientFilterAdapter.GradientViewHolder> {
    private int rowIndex = -1;
    private ArrayList<GradientFilterModel> gradientFilterModelArrayList;
    private GradientFilterAdapter.GradientFilterClickListener listener;

    public GradientFilterAdapter(ArrayList<GradientFilterModel> gradientFilterModelArrayList, GradientFilterAdapter.GradientFilterClickListener listener) {
        this.gradientFilterModelArrayList = gradientFilterModelArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GradientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_filter_item, parent, false);
        return new GradientFilterAdapter.GradientViewHolder(view);
    }

    public interface GradientFilterClickListener {
        void gradientFilterItemClick(GradientFilterModel gradientModel, int position);
    }

    @Override
    public void onBindViewHolder(@NonNull GradientViewHolder holder, int position) {
        GradientFilterModel collageDrawableItem = gradientFilterModelArrayList.get(position);
        if (position == 0) {
            holder.gradientImageView.setImageDrawable(
                    ContextCompat.getDrawable(
                            holder.gradientText.getContext(),
                            R.drawable.none_icon
                    )
            );
            holder.gradientText.setText(collageDrawableItem.getGradientFilterName());
        } else {
            holder.gradientText.setText(collageDrawableItem.getGradientFilterName());
            setGradientIcon(holder, position);
        }
        holder.view.setOnClickListener(v -> {
            rowIndex = position;
            notifyDataSetChanged();
            listener.gradientFilterItemClick(collageDrawableItem, position);
        });
        if (rowIndex == position) {
            holder.gradientImageView.setPadding(5, 5, 5, 5);
            holder.gradientText.setTextColor(ContextCompat.getColor(holder.gradientText.getContext(), R.color.radial_1));

        } else {
            holder.gradientImageView.setPadding(0, 0, 0, 0);
            holder.gradientText.setTextColor(ContextCompat.getColor(holder.gradientText.getContext(), R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return gradientFilterModelArrayList.size();
    }

    public class GradientViewHolder extends RecyclerView.ViewHolder {
        ImageView gradientImageView;
        TextView gradientText;
        View view;

        public GradientViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            gradientImageView = itemView.findViewById(R.id.edit_pic_item);
            gradientText = itemView.findViewById(R.id.fiter_name);
        }
    }

    private void setGradientIcon(GradientViewHolder holder, int position) {
        if (position == 1) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g1));
        }
        if (position == 2) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g2));
        }
        if (position == 3) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g3));
        }
        if (position == 4) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g4));
        }
        if (position == 5) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g5));
        }
        if (position == 6) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g6));
        }
        if (position == 7) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g7));
        }
        if (position == 8) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g8));
        }
        if (position == 9) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g9));
        }
        if (position == 10) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g10));
        }
        if (position == 11) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g11));
        }
        if (position == 12) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g12));
        }
        if (position == 13) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g13));
        }
        if (position == 14) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g14));
        }
        if (position == 15) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g15));
        }
        if (position == 16) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g16));
        }
        if (position == 17) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g17));
        }
        if (position == 18) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g18));
        }
        if (position == 19) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g19));
        }
        if (position == 20) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g20));
        }
        if (position == 21) {
            holder.gradientImageView.setImageDrawable(AppCompatResources.getDrawable(holder.gradientImageView.getContext(), R.drawable.g21));
        }
    }
}
