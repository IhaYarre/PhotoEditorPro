package com.photo.editor.picskills.photoeditorpro.fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.photo.editor.picskills.photoeditorpro.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SliderTwo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SliderTwo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TemplateView templateView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SliderTwo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SliderTwo.
     */
    // TODO: Rename and change types and number of parameters
    public static SliderTwo newInstance(String param1, String param2) {
        SliderTwo fragment = new SliderTwo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_slider_two, container, false);
        templateView = view.findViewById(R.id.adLayout);
        loadAd();
        return view;
    }
    void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        AdLoader adLoader = new AdLoader.Builder(requireActivity(), getString(R.string.admob_native_ads_id))
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    private ColorDrawable background;
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();

                        templateView.setStyles(styles);
                        templateView.setNativeAd(nativeAd);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                        Log.i("tag","Ad failed!");
                    }
                }).build();
        adLoader.loadAd(adRequest);
    }
}