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
import com.photo.editor.picskills.photoeditorpro.model.SimpleFilterModel;

import java.util.ArrayList;

public class SimpleFilterAdapter extends RecyclerView.Adapter<SimpleFilterAdapter.SimpleViewHolder> {
    private ArrayList<SimpleFilterModel> simpleFilterArrayListJson;
    private SimpleFilterClickListener listener;
    private int rowIndex = -1;

    public SimpleFilterAdapter(ArrayList<SimpleFilterModel> simpleFilterArrayListJson, SimpleFilterClickListener listener) {
        this.simpleFilterArrayListJson = simpleFilterArrayListJson;
        this.listener = listener;
    }

    public interface SimpleFilterClickListener {
        void simpleFilterItemClick(SimpleFilterModel drawable, int position);
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_filter_item, parent, false);
        return new SimpleFilterAdapter.SimpleViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
        SimpleFilterModel collageDrawableItem = simpleFilterArrayListJson.get(position);
        if (position == 0) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.none_icon));
            holder.txtFilter.setText(collageDrawableItem.getFilterCategory());
        } else {
            holder.txtFilter.setText(collageDrawableItem.getFilterCategory());
            setIcon(position, holder);
        }
        holder.view.setOnClickListener(v -> {
            rowIndex = position;
            notifyDataSetChanged();
            listener.simpleFilterItemClick(collageDrawableItem, position);
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
        return simpleFilterArrayListJson.size();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        ImageView collageItem;
        TextView txtFilter;
        View view;

        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            collageItem = itemView.findViewById(R.id.edit_pic_item);
            txtFilter = itemView.findViewById(R.id.fiter_name);
        }
    }

    public void setIcon(int position, SimpleViewHolder holder) {
        if (position == 1) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.t_f));
        }
        if (position == 2) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.y_t));
        }
        if (position == 3) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.t_f));
        }
        if (position == 4) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.f_f));
        }
        if (position == 5) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.f_i_f));
        } if (position == 6) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.s_f));
        }
        if (position == 7) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.s_e_f));
        } if (position == 8) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.e_g_f));
        }
        if (position == 9) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.f_n));
        }
        if (position == 10) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.l_b));
        }
        if (position == 11) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.y_1));
        }
        if (position == 12) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.y_2));
        }
        if (position == 13) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.y_3));
        }
        if (position == 14) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.y_4));
        }
        if (position == 15) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.y_5));
        }
        if (position == 16) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.r_1));
        }
        if (position == 17) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.r_2));
        }
        if (position == 18) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.r_3));
        }
        if (position == 19) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.r_4));
        }
        if (position == 20) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.l_p_1));
        }
        if (position == 21) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.l_p_2));
        }
        if (position == 22) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.l_p_3));
        } if (position == 23) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.l_p_4));
        }
        if (position == 24) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.m_s_1));
        }
        if (position == 25) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.m_s_2));
        }
        if (position == 26) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.m_s_3));
        }
        if (position == 27) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.m_s_4));
        }
        if (position == 28) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.g_1));
        }
        if (position == 29) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.g_2));
        }
        if (position == 30) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.g_3));
        }
        if (position == 31) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.g_4));
        }
        if (position == 32) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.dbr_1));
        }
        if (position == 33) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.dbr_2));
        }
        if (position == 34) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.dbr_3));
        }
        if (position == 35) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.dbr_4));
        }
        if (position == 36) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.bs_1));
        }
        if (position == 37) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.bs_2));
        }
        if (position == 38) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.bs_3));
        }
        if (position == 39) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.bs_4));
        }
        if (position == 40) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.bl_1));
        }
        if (position == 41) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.bl_2));
        }
        if (position == 42) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.bl_3));
        }
        if (position == 43) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.bl_4));
        }
        if (position == 44) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.bl_5));
        }
        if (position == 45) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.b_1));
        }
        if (position == 46) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.b_2));
        }
        if (position == 47) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.b_3));
        } if (position == 48) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.b_4));
        }
        if (position == 49) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.b_5));
        }
        if (position == 50) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.br_1));
        }
        if (position == 51) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.br_2));
        }
        if (position == 52) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.br_3));
        }
        if (position == 53) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.br_4));
        }
        if (position == 54) {
            holder.collageItem.setImageDrawable(AppCompatResources.getDrawable(holder.collageItem.getContext(), R.drawable.br_5));
        }
    }
}
