package com.learningmachine.android.app.ui.settings.passphrase;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentRevealPassphraseBinding;
import com.learningmachine.android.app.ui.LMActivity;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.util.DialogUtils;
import com.smallplanet.labalib.Laba;

import javax.inject.Inject;

public class RevealPassphraseFragment extends LMFragment {

    @Inject BitcoinManager mBitcoinManager;
    private FragmentRevealPassphraseBinding mBinding;

    public static Fragment newInstance() {
        return new RevealPassphraseFragment();
    }

    private String mPassphrase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(nonNullContext())
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_reveal_passphrase,
                container,
                false);

        mBitcoinManager.getPassphrase()
                .compose(bindToMainThread())
                .subscribe(this::configureCurrentPassphraseTextView);


        mBinding.onboardingEmailButton.setOnClickListener(view -> onEmail());
        mBinding.onboardingSaveButton.setOnClickListener(view -> onSave());

        mBinding.onboardingSaveCheckmark.setVisibility(View.INVISIBLE);
        mBinding.onboardingEmailCheckmark.setVisibility(View.INVISIBLE);

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void configureCurrentPassphraseTextView(String currentPassphrase) {
        if (mBinding == null) {
            return;
        }

        mPassphrase = currentPassphrase;

        mBinding.onboardingPassphraseContent.setText(currentPassphrase);
    }



    protected void onSave() {
        ((LMActivity)nonNullActivity()).askToSavePassphraseToDevice(mPassphrase, (passphrase) -> {
            if(passphrase == null) {
                if(Build.VERSION.SDK_INT >= 23) {
                    return;
                }
                DialogUtils.showAlertDialog( this,
                        R.drawable.ic_dialog_failure,
                        getResources().getString(R.string.onboarding_passphrase_permissions_error_title),
                        getResources().getString(R.string.onboarding_passphrase_permissions_error),
                        getResources().getString(R.string.ok_button),
                        null,
                        (btnIdx) -> HandleBackupOptionCompleted(null));
                return;
            }

            DialogUtils.showAlertDialog(this,
                    R.drawable.ic_dialog_success,
                    getResources().getString(R.string.onboarding_passphrase_complete_title),
                    getResources().getString(R.string.onboarding_passphrase_save_complete),
                    getResources().getString(R.string.ok_button),
                    null,
                    (btnIdx) -> {
                        if(mBinding != null) {
                            HandleBackupOptionCompleted(mBinding.onboardingSaveCheckmark);
                        }
                    }, (cancel) -> {
                        if(mBinding != null) {
                            HandleBackupOptionCompleted(mBinding.onboardingSaveCheckmark);
                        }
                    });
        });
    }

    protected void onEmail() {
        DialogUtils.showAlertDialog( this,
                0,
                getResources().getString(R.string.onboarding_passphrase_email_before_title),
                getResources().getString(R.string.onboarding_passphrase_email_before),
                getResources().getString(R.string.onboarding_passphrase_cancel),
                getResources().getString(R.string.ok_button),
                (btnIdx) -> {

                    if(btnIdx.equals(0)) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Blockcerts Backup");
                        intent.putExtra(Intent.EXTRA_TEXT, mPassphrase);
                        Intent mailer = Intent.createChooser(intent, null);
                        startActivity(mailer);

                        if (mBinding != null) {
                            HandleBackupOptionCompleted(mBinding.onboardingEmailCheckmark);
                        }
                    }
                });
    }

    public void HandleBackupOptionCompleted(View view) {
        if(view != null) {
            Laba.Animate(view, "!s!f!>", () -> null);
            view.setVisibility(View.VISIBLE);
        }
    }

}
