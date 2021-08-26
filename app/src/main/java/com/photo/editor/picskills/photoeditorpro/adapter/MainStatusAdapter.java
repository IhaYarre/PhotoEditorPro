package com.photo.editor.picskills.photoeditorpro.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.picskills.photoeditorpro.R;
import com.photo.editor.picskills.photoeditorpro.model.AppDesignModel;

import java.util.ArrayList;

public class MainStatusAdapter extends RecyclerView.Adapter<MainStatusAdapter.StatusViewHolder> {
    private final ArrayList<AppDesignModel> mainStatusList;
    private final MainStatusClickListener listener;

    public MainStatusAdapter(ArrayList<AppDesignModel> mainStatusList,
                             MainStatusClickListener listener) {
        this.mainStatusList = mainStatusList;
        this.listener = listener;
    }

    public interface MainStatusClickListener {
        void mainStatusClick(AppDesignModel drawable, int position);
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.status_design_item, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainStatusAdapter.StatusViewHolder holder, int position) {
        AppDesignModel collageDrawableItem = mainStatusList.get(position);

        holder.collageItem.setImageDrawable(collageDrawableItem.getDrawable());
        holder.txtFilter.setText(collageDrawableItem.getText());

        holder.view.setOnClickListener(v -> listener.mainStatusClick(collageDrawableItem, position));
    }

    @Override
    public int getItemCount() {
        return mainStatusList.size();
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder {
        ImageView collageItem;
        TextView txtFilter;
        View view;

        public StatusViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            collageItem = itemView.findViewById(R.id.edit_pic_item);
            txtFilter = itemView.findViewById(R.id.fiter_name);
        }
    }
}
