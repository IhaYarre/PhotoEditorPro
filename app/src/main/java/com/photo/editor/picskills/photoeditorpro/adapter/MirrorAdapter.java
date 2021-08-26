package com.photo.editor.picskills.photoeditorpro.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.picskills.photoeditorpro.R;
import com.photo.editor.picskills.photoeditorpro.model.AppDesignModel;
import com.photo.editor.picskills.photoeditorpro.model.SimpleFilterModel;

import java.util.ArrayList;

public class MirrorAdapter extends RecyclerView.Adapter<MirrorAdapter.MirrorViewHolder> {
    private ArrayList<AppDesignModel> mirrorArrayList;
    private MirrorAdapter.MirrorClickListener listener;
    private int rowIndex = -1;

    public MirrorAdapter(ArrayList<AppDesignModel> mirrorArrayList, MirrorAdapter.MirrorClickListener listener) {
        this.mirrorArrayList = mirrorArrayList;
        this.listener = listener;
    }

    public interface MirrorClickListener {
        void mirrorClickListener(AppDesignModel model, int position);
    }

    @NonNull
    @Override
    public MirrorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_filter_item, parent, false);
        return new MirrorViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MirrorViewHolder holder, int position) {
        AppDesignModel collageDrawableItem = mirrorArrayList.get(position);
        if (position == 0) {
            holder.collageItem.setImageDrawable(collageDrawableItem.getDrawable());
            holder.txtFilter.setText(collageDrawableItem.getText());
        } else {
            holder.collageItem.setImageDrawable(collageDrawableItem.getDrawable());
            holder.txtFilter.setText(collageDrawableItem.getText());
        }
        holder.view.setOnClickListener(v -> {
            rowIndex = position;
            notifyDataSetChanged();
            listener.mirrorClickListener(collageDrawableItem, position);
        });
        if (rowIndex == position) {
            holder.collageItem.setPadding(5, 5, 5, 5);
            holder.txtFilter.setTextColor(ContextCompat.getColor(holder.txtFilter.getContext(), R.color.radial_1));

        } else {
            holder.collageItem.setPadding(0, 0, 0, 0);
            holder.txtFilter.setTextColor(ContextCompat.getColor(holder.txtFilter.getContext(), R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return mirrorArrayList.size();
    }

    public class MirrorViewHolder extends RecyclerView.ViewHolder {
        ImageView collageItem;
        TextView txtFilter;
        View view;

        public MirrorViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            collageItem = itemView.findViewById(R.id.edit_pic_item);
            txtFilter = itemView.findViewById(R.id.fiter_name);
        }
    }
}