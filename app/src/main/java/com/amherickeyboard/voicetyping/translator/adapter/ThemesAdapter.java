package com.amherickeyboard.voicetyping.translator.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amherickeyboard.voicetyping.translator.Models.ThemesModel;
import com.amherickeyboard.voicetyping.translator.NativeAds.NativeAd;
import com.amherickeyboard.voicetyping.translator.R;
import com.amherickeyboard.voicetyping.translator.ThemesSharedPreference;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ThemesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    NativeAd nativeAd;
    NativeAdView nativeAdView;
    ArrayList<ThemesModel> themesImages;
    private OnItemClickListener mListener;
    int selected_theme_index = -1;

    public ThemesAdapter(Activity context, ArrayList<ThemesModel> themesImages) {
        this.context = context;
        this.themesImages = themesImages;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v = inflater.inflate(R.layout.customgrid, parent, false);
                viewHolder = new ViewHolder(v);
                break;
            }
            case 2: {
                View v = inflater.inflate(R.layout.recyclerview_ads, parent, false);
                viewHolder = new AdViewHolder(v);
                break;
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        selected_theme_index = new ThemesSharedPreference(context).getThemePosition();

        switch (holder.getItemViewType()) {
            case 1: {
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.title.setText(themesImages.get(position).getTitle());
                viewHolder.description.setText(themesImages.get(position).getDescription());
                viewHolder.imageview.setImageResource(themesImages.get(position).getImage());
                viewHolder.applyTheme.setOnClickListener(v -> {
                    if (position <= 1) {
                        mListener.onItemClick(position);
                    } else {
                        mListener.onItemClick(position - 1);
                    }
                    notifyDataSetChanged();
                });

                if (position <= 1) {
                    if (position == selected_theme_index) {
                        viewHolder.applyTheme.setBackgroundTintList(context.getResources().getColorStateList(R.color.black));
                        viewHolder.applyTheme.setText("Applied");
                    } else {
                        viewHolder.applyTheme.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPrimary));
                        viewHolder.applyTheme.setText("Apply");
                    }
                } else {
                    if (position - 1 == selected_theme_index) {
                        viewHolder.applyTheme.setBackgroundTintList(context.getResources().getColorStateList(R.color.black));
                        viewHolder.applyTheme.setText("Applied");
                    } else {
                        viewHolder.applyTheme.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPrimary));
                        viewHolder.applyTheme.setText("Apply");
                    }
                }
                break;
            }
            case 2: {
                AdViewHolder viewHolder = (AdViewHolder) holder;
                nativeAdView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.ad_unified_small, null);
                nativeAd = new NativeAd();
                nativeAd.refreshAd(context, nativeAdView, viewHolder.adFrame, viewHolder.shimmerFrameLayout);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return themesImages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (themesImages.get(position).getViewType() == 2) {
            return 2;
        }
        return 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        RoundedImageView imageview;
        Button applyTheme;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.themeTitle);
            description = itemView.findViewById(R.id.themeDescription);
            imageview = itemView.findViewById(R.id.imageview);
            applyTheme = itemView.findViewById(R.id.buttonTheme);
        }
    }

    private static class AdViewHolder extends RecyclerView.ViewHolder {
        FrameLayout adFrame;
        ShimmerFrameLayout shimmerFrameLayout;

        public AdViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            adFrame = itemView.findViewById(R.id.ad_frame_home);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
