package com.learningmachine.android.app.ui.settings;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.learningmachine.android.app.BuildConfig;
import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentSettingsBinding;
import com.learningmachine.android.app.dialog.AlertDialogFragment;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.LMWebActivity;
import com.learningmachine.android.app.ui.cert.AddCertificateActivity;
import com.learningmachine.android.app.ui.issuer.AddIssuerActivity;
import com.learningmachine.android.app.ui.onboarding.OnboardingActivity;
import com.learningmachine.android.app.ui.settings.passphrase.RevealPassphraseActivity;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.FileLoggingTree;
import com.learningmachine.android.app.util.FileUtils;

import java.io.File;

import javax.inject.Inject;

import timber.log.Timber;


public class SettingsFragment extends LMFragment {

    private static final int REQUEST_OPEN = 201;

    @Inject protected BitcoinManager mBitcoinManager;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(nonNullContext()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSettingsBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_settings,
                container,
                false);

        binding.settingsRevealPassphraseTextView.setOnClickListener(v -> {
            Timber.i("My passphrase tapped in settings");
            Intent intent = RevealPassphraseActivity.newIntent(getContext());
            startActivity(intent);
        });

        setupReplacePassphrase(binding);

        binding.settingsAddIssuerTextView.setOnClickListener(v -> {
            Timber.i("Add Issuer tapped in settings");
            Intent intent = AddIssuerActivity.newIntent(getContext());
            startActivity(intent);
        });

        binding.settingsAddCredentialTextView.setOnClickListener(v -> {
            Timber.i("Add Credential tapped in settings");
            DialogUtils.showCustomSheet(getContext(), this,
                    R.layout.dialog_add_by_file_or_url,
                    0,
                    "",
                    "",
                    "",
                    "",
                    (btnIdx) -> {
                        if (btnIdx.equals(0)) {
                            Timber.i("Add Credential from URL tapped in settings");
                        } else {
                            Timber.i("User has chosen to add a certificate from file");
                        }
                        Intent intent = AddCertificateActivity.newIntent(nonNullContext(), (int)btnIdx, null);
                        startActivity(intent);
                    },
                    (dialogContent) -> {});

        });

        binding.settingsEmailLogsTextView.setOnClickListener(v -> {
            Timber.i("Share device logs");
            FileLoggingTree.saveLogToFile(getContext());

            File file = FileUtils.getLogsFile(getContext(), false);

            Uri fileUri = FileProvider.getUriForFile(
                    nonNullContext(),
                    "com.learningmachine.android.app.fileprovider",
                    file);


            //send file using email
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            // the attachment
            String type = nonNullContext().getContentResolver().getType(fileUri);
            emailIntent.setType(type);
            emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            emailIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            // the mail subject
            emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Logcat content for Blockcerts");
            emailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(emailIntent , "Send email..."));
        });

        binding.settingsAboutPassphraseTextView.setOnClickListener(v -> {
            Timber.i("About passphrase tapped in settings");
            String actionBarTitle = getString(R.string.about_passphrases_title);
            String endPoint = getString(R.string.about_passphrases_endpoint);
            Intent intent = LMWebActivity.newIntent(getContext(), actionBarTitle, endPoint);
            startActivity(intent);
        });

        binding.settingsPrivacyPolicyTextView.setOnClickListener(v -> {
            Timber.i("Privacy statement tapped in settings");
            String actionBarTitle = getString(R.string.settings_privacy_policy);
            String endPoint = getString(R.string.settings_privacy_policy_endpoint);
            Intent intent = LMWebActivity.newIntent(getContext(), actionBarTitle, endPoint);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    @Override
    public void onStop() {
        Timber.i("Dismissing the settings screen");
        super.onStop();
    }

    private void setupReplacePassphrase(FragmentSettingsBinding binding) {
        if (!BuildConfig.DEBUG) {
            binding.settingsLogoutSeparator.setVisibility(View.GONE);
            binding.settingsLogout.setVisibility(View.GONE);
            return;
        }

        binding.settingsLogout.setOnClickListener(v -> {
            String message = getResources().getString(R.string.settings_logout_legacy_message);
            AlertDialogFragment.Callback callback = btnIdx -> {
                if(btnIdx.equals(1)) {
                    mBitcoinManager.resetEverything();

                    Intent intent = new Intent(nonNullActivity(), OnboardingActivity.class);
                    startActivity(intent);
                    nonNullActivity().finish();
                }
            };

            DialogUtils.showAlertDialog( this,
                    R.drawable.ic_dialog_failure,
                    getResources().getString(R.string.settings_logout_title),
                    message,
                    getResources().getString(R.string.settings_logout_button_title),
                    getResources().getString(R.string.onboarding_passphrase_cancel),
                    callback);
        });
    }
}


