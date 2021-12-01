package com.agento.mmcleaner.ui;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.agento.mmcleaner.util.LocaleHelper;

public class BaseFragment extends Fragment {
    private View view;
    public BaseActivity mainActivity;

    private boolean isPermissionFragment;

    public LinearLayout button;
    public ImageView image_blick;
    public ImageView iv;
    public ImageView imageLoadIcon;

    public BaseFragment(int fragmentSecondOptimization) {
        super(fragmentSecondOptimization);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
        if (context instanceof BaseActivity) {
            mainActivity = (BaseActivity) context;
        }
    }

    public void startAds() {
        mainActivity.initAdsMain();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {

        super.onPause();
    }


}
