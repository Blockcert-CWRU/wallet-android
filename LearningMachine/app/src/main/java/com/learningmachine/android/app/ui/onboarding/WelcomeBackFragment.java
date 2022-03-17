package com.learningmachine.android.app.ui.onboarding;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentWelcomeBackBinding;
import com.smallplanet.labalib.Laba;

public class WelcomeBackFragment extends OnboardingFragment {

    public static WelcomeBackFragment newInstance() {
        return new WelcomeBackFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentWelcomeBackBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome_back, container, false);
        mBinding.continueButton.setOnClickListener(view -> ((OnboardingActivity) requireActivity()).onContinuePastWelcomeScreen());
        Laba.Animate(mBinding.continueButton, "!^300", () -> null);
        return mBinding.getRoot();
    }
}
