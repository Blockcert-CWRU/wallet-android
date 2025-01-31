package com.learningmachine.android.app.ui.onboarding;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentAccountChooserBinding;
import com.smallplanet.labalib.Laba;

import timber.log.Timber;

public class AccountChooserFragment extends OnboardingFragment {

    private Callback mCallback;
    private FragmentAccountChooserBinding mBinding;

    public interface Callback {
        void onNewAccount();
        void onExistingAccount();
    }

    public static AccountChooserFragment newInstance() {
        return new AccountChooserFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (Callback) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_chooser, container, false);

        Laba.Animate(mBinding.newAccountButton, "!^300", () -> null);
        Laba.Animate(mBinding.existingAccountButton, "!^300", () -> null);

        String fileName = "android.resource://" + requireActivity().getPackageName() + "/raw/background";

        mBinding.backgroundVideoCover.setAlpha(1.0f);
        mBinding.backgroundVideo.setVideoURI(Uri.parse(fileName));
        mBinding.backgroundVideo.setOnPreparedListener(mp -> {
            Timber.d("SETTING VIDEO SCALING MODE");
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            mp.setLooping(true);
            mp.setScreenOnWhilePlaying(false);

            Laba.Animate(mBinding.backgroundVideoCover, "d1|f0", null);
        });
        mBinding.backgroundVideo.start();

        mBinding.newAccountButton.setOnClickListener(view -> mCallback.onNewAccount());
        mBinding.existingAccountButton.setOnClickListener(view -> mCallback.onExistingAccount());

        mSharedPreferencesManager.setFirstLaunch(true);

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        mBinding.backgroundVideoCover.setAlpha(1.0f);
        mBinding.backgroundVideo.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBinding.backgroundVideoCover.setAlpha(1.0f);
        mBinding.backgroundVideo.stopPlayback();

    }

    @Override
    public void onStop() {
        super.onStop();
        mBinding.backgroundVideoCover.setAlpha(1.0f);
        mBinding.backgroundVideo.stopPlayback();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
