package com.learningmachine.android.app.ui.onboarding;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.passphrase.PassphraseManager;
import com.learningmachine.android.app.databinding.FragmentPastePassphraseBinding;
import com.learningmachine.android.app.ui.LMActivity;
import com.learningmachine.android.app.ui.home.HomeActivity;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.StringUtils;

import javax.inject.Inject;

import timber.log.Timber;

public class PastePassphraseFragment extends OnboardingFragment {

    @Inject protected BitcoinManager mBitcoinManager;
    @Inject protected PassphraseManager mPassphraseManager;

    private FragmentPastePassphraseBinding mBinding;

    public static PastePassphraseFragment newInstance() {
        return new PastePassphraseFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(requireContext()).inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_paste_passphrase, container, false);

        if (Build.VERSION.SDK_INT >= 23) {
            mBinding.chooseBackupFileButton.setVisibility(View.VISIBLE);
            mBinding.chooseBackupFileButton.setOnClickListener(view -> retrievePassphraseFromDevice());
        } else {
            retrievePassphraseFromDevice();
        }
        mBinding.pastePassphraseEditText.setFilters(new InputFilter[] {
                new InputFilter.AllCaps() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        String toLowered = String.valueOf(source).toLowerCase();
                        return toLowered.replaceAll("[^a-zA-Z ]", "");
                    }
                }
        });

        mBinding.pastePassphraseEditText.addTextChangedListener(new PastePassphraseTextWatcher());
        mBinding.doneButton.setEnabled(false);
        mBinding.doneButton.setOnClickListener(view -> onDone());

        return mBinding.getRoot();
    }

    private void retrievePassphraseFromDevice() {
        ((LMActivity) requireContext()).askToGetPassphraseFromDevice((passphrase) -> {
            if (passphrase != null) {
                mBinding.pastePassphraseEditText.setText(passphrase);
                onDone();
            } else {
                mBinding.passphraseLabel.requestFocus();
            }
        });
    }

    private void onDone() {
        displayProgressDialog(R.string.onboarding_passphrase_loading);
        String passphrase = mBinding.pastePassphraseEditText.getText().toString();
        Activity activity = requireActivity();

        mBinding.doneButton.setEnabled(false);
        mBinding.pastePassphraseEditText.setEnabled(false);

        AsyncTask.execute(() -> mBitcoinManager.setPassphrase(passphrase)
                .compose(bindToMainThread())
                .subscribe(wallet -> {
                    if(isVisible()) {
                        Log.d("LM", "PastePassphraseFragment isVisible()");
                        activity.runOnUiThread(() -> {
                            if(isVisible()) {
                                // if we return to the app by pasting in our passphrase, we
                                // must have already backed it up!
                                mSharedPreferencesManager.setHasSeenBackupPassphraseBefore(true);
                                mSharedPreferencesManager.setWasReturnUser(true);
                                mSharedPreferencesManager.setFirstLaunch(false);
                                if (!continueDelayedURLsFromDeepLinking()) {
                                    startActivity(new Intent(activity, HomeActivity.class));
                                    activity.finish();
                                }
                            }
                        });
                    }
                    hideProgressDialog();
                }, e -> {
                    Timber.e(e, "Could not set passphrase.");
                    hideProgressDialog();
                    displayErrorsLocal();
                }));
    }

    protected void displayErrorsLocal() {
        mBinding.pastePassphraseEditText.setEnabled(true);
        mBinding.pastePassphraseEditText.setText("");

        DialogUtils.showAlertDialog(this,
                R.drawable.ic_dialog_failure,
                getResources().getString(R.string.onboarding_passphrase_invalid_title),
                getResources().getString(R.string.onboarding_passphrase_invalid_desc),
                null,
                getResources().getString(R.string.ok_button),
                (btnIdx) -> {});
    }


    private class PastePassphraseTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String passphrase = mBinding.pastePassphraseEditText.getText().toString();
            boolean emptyPassphrase = StringUtils.isEmpty(passphrase);
            mBinding.doneButton.setEnabled(!emptyPassphrase);
        }
    }
}
