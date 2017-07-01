package com.example.sanehyadav1.fitnotes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sanehyadav1.fitnotes.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class BannerAdFragment extends Fragment {
    private static final String TAG  =BannerAdFragment.class.getSimpleName();

    public BannerAdFragment(){}

    private AdView mBannerAdView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_banner_ad,container,false);
        Log.d(TAG,"Banner ad view inflated");

        mBannerAdView =(AdView) rootView.findViewById(R.id.bannerAdView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("05ED4ECADC623857E3B49E4449457372")
                .build();
        mBannerAdView.loadAd(adRequest);



        return rootView;
    }

    @Override
    public void onPause() {
        if(mBannerAdView!=null){
            mBannerAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mBannerAdView!=null){
            mBannerAdView.resume();
        }

    }

    @Override
    public void onDestroy() {
        if(mBannerAdView!=null){
            mBannerAdView.destroy();
        }
        super.onDestroy();
    }
}