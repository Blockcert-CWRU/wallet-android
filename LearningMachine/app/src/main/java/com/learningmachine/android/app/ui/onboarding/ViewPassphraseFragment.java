package com.learningmachine.android.app.ui.onboarding;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentViewPassphraseBinding;
import com.smallplanet.labalib.Laba;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class ViewPassphraseFragment extends OnboardingFragment {

    @Inject
    protected BitcoinManager mBitcoinManager;

    private FragmentViewPassphraseBinding mBinding;

    private boolean didGeneratePassphrase = false;

    private Timer countingTimer;

    private final TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            runOnActivity(activity -> activity.runOnUiThread(() -> {
                if (!didGeneratePassphrase) {
                    mBinding.onboardingStatusText.setText(getResources().getString(R.string.onboarding_passphrase_status_0));
                }
            }));
        }
    };

    public void startCountingTimer() {
        if (countingTimer != null) {
            return;
        }
        countingTimer = new Timer();
        countingTimer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void stopCountingTimer() {
        if (countingTimer != null) {
            countingTimer.cancel();
        }
        countingTimer = null;
    }

    public static ViewPassphraseFragment newInstance() {
        return new ViewPassphraseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(requireContext())
                .inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_passphrase, container, false);

        mBinding.onboardingDoneButton.setOnClickListener(view -> onDone());
        mBinding.onboardingDoneButton.setEnabled(false);

        mBinding.imageView2.setVisibility(View.INVISIBLE);
        mBinding.onboardingPassphraseDesc.setVisibility(View.INVISIBLE);
        mBinding.onboardingPassphraseTitle.setVisibility(View.INVISIBLE);
        mBinding.onboardingPassphraseContent.setVisibility(View.INVISIBLE);

        mBinding.progressBar.animate();

        return mBinding.getRoot();
    }

    @Override
    public void onUserVisible() {
        super.onUserVisible();


        if (!didGeneratePassphrase) {

            mBinding.onboardingStatusText.setText(R.string.onboarding_passphrase_status_0);
            startCountingTimer();

            Activity activity = requireActivity();

            AsyncTask.execute(() -> mBitcoinManager.getPassphrase().delay(1500, TimeUnit.MILLISECONDS).subscribe(passphrase -> activity.runOnUiThread(() -> {
                stopCountingTimer();

                if (isVisible()) {

                    Log.d("LM", "ViewPassphraseFragment isVisible()");

                    didGeneratePassphrase = true;

                    Laba.Animate(mBinding.onboardingPassphraseDesc, "f0d0,!^!f", () -> null);
                    Laba.Animate(mBinding.onboardingPassphraseTitle, "f0d0,,!^!f", () -> null);
                    Laba.Animate(mBinding.onboardingPassphraseContent, "f0d0,,,!^!f", () -> null);

                    mBinding.progressBar.clearAnimation();
                    mBinding.progressBar.setVisibility(View.GONE);

                    mBinding.imageView2.setVisibility(View.VISIBLE);
                    mBinding.onboardingPassphraseDesc.setVisibility(View.VISIBLE);
                    mBinding.onboardingPassphraseTitle.setVisibility(View.VISIBLE);
                    mBinding.onboardingPassphraseContent.setVisibility(View.VISIBLE);

                    mBinding.onboardingStatusText.setText(R.string.onboarding_passphrase_status_1);
                    mBinding.onboardingPassphraseContent.setText(passphrase);

                    mBinding.onboardingDoneButton.setEnabled(true);
                }
            })));
        } else {
            mBinding.progressBar.clearAnimation();
            mBinding.progressBar.setVisibility(View.GONE);

            mBinding.onboardingPassphraseTitle.setTranslationY(0);
            mBinding.onboardingPassphraseContent.setTranslationY(0);
            mBinding.onboardingDoneButton.setTranslationY(0);
        }

    }

    @Override
    public boolean isBackAllowed() {
        return true;
    }

    private void onDone() {
        ((OnboardingActivity) requireActivity()).onBackupPassphrase();
    }
}
