package com.learningmachine.android.app.ui.onboarding;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentBackupPassphraseBinding;
import com.learningmachine.android.app.dialog.AlertDialogFragment;
import com.learningmachine.android.app.ui.home.HomeActivity;
import com.learningmachine.android.app.util.DialogUtils;
import com.smallplanet.labalib.Laba;

import javax.inject.Inject;

public class BackupPassphraseFragment extends OnboardingFragment {

    @Inject protected BitcoinManager mBitcoinManager;

    protected FragmentBackupPassphraseBinding mBinding;
    protected String mPassphrase;

    private int numberOfBackupOptionsUsed = 0;

    public static BackupPassphraseFragment newInstance() {
        return new BackupPassphraseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(requireContext())
                .inject(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("p", mPassphrase);
        savedInstanceState.putBoolean("onboardingSaveCheckmark", mBinding.onboardingSaveCheckmark.getVisibility() == View.VISIBLE);
        savedInstanceState.putBoolean("onboardingWriteCheckmark", mBinding.onboardingWriteCheckmark.getVisibility() == View.VISIBLE);
        savedInstanceState.putBoolean("onboardingEmailCheckmark", mBinding.onboardingEmailCheckmark.getVisibility() == View.VISIBLE);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_backup_passphrase, container, false);

        mBinding.onboardingDoneButton.setOnClickListener(view -> onDone());
        mBinding.onboardingEmailButton.setOnClickListener(view -> onEmail());
        mBinding.onboardingSaveButton.setOnClickListener(view -> onSave());
        mBinding.onboardingWriteButton.setOnClickListener(view -> onWrite());

        int numCompleted = 3;
        if (savedInstanceState == null || !savedInstanceState.getBoolean("onboardingSaveCheckmark")) {
            mBinding.onboardingSaveCheckmark.setVisibility(View.INVISIBLE);
            numCompleted--;
        }
        if (savedInstanceState == null || !savedInstanceState.getBoolean("onboardingEmailCheckmark")) {
            mBinding.onboardingEmailCheckmark.setVisibility(View.INVISIBLE);
            numCompleted--;
        }
        if (savedInstanceState == null || !savedInstanceState.getBoolean("onboardingWriteCheckmark")) {
            mBinding.onboardingWriteCheckmark.setVisibility(View.INVISIBLE);
            numCompleted--;
        }

        if (savedInstanceState != null) {
            mPassphrase = savedInstanceState.getString("p");
        }

        if(numCompleted < 2) {
            mBinding.onboardingDoneButton.setText(R.string.select_two_to_continue);
            mBinding.onboardingDoneButton.setEnabled(false);
        }

        return mBinding.getRoot();
    }

    @Override
    public void onUserVisible() {
        super.onUserVisible();

        mBitcoinManager.getPassphrase().subscribe(passphrase -> mPassphrase = passphrase);

    }

    @Override
    public boolean isBackAllowed() {
        return true;
    }

    private void onDone() {

        mSharedPreferencesManager.setHasSeenBackupPassphraseBefore(true);
        mSharedPreferencesManager.setWasReturnUser(false);
        mSharedPreferencesManager.setFirstLaunch(false);
        if (!continueDelayedURLsFromDeepLinking()) {
            startActivity(new Intent(requireActivity(), HomeActivity.class));
            requireActivity().finish();
        }
    }

    protected void onSave() {
        ((OnboardingActivity) requireActivity()).askToSavePassphraseToDevice(mPassphrase, (passphrase) -> {
            if (passphrase == null) {
                if (Build.VERSION.SDK_INT >= 23) {
                    return;
                }
                DialogUtils.showAlertDialog(this,
                        R.drawable.ic_dialog_failure,
                        getResources().getString(R.string.onboarding_passphrase_permissions_error_title),
                        getResources().getString(R.string.onboarding_passphrase_permissions_error),
                        getResources().getString(R.string.ok_button),
                        null,
                        (btnIdx) -> HandleBackupOptionCompleted(null));
                return;
            }

            DialogUtils.showAlertDialog( this,
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

    protected void onWrite() {
        AlertDialogFragment fragment = DialogUtils.showCustomSheet(getContext(), this,
                R.layout.dialog_write_passphrase,
                R.drawable.ic_writedown,
                getResources().getString(R.string.onboarding_passphrase_write_title),
                getResources().getString(R.string.onboarding_passphrase_write_message),
                getResources().getString(R.string.onboarding_passphrase_write_confirmation),
                null,
                (btnIdx) -> {
                    if(mBinding != null) {
                        HandleBackupOptionCompleted(mBinding.onboardingWriteCheckmark);
                    }
                },
                (dialogContent) -> {

                    // Set the content of the passphrase text field
                    View view = (View)dialogContent;
                    TextView passphraseView = view.findViewById(R.id.onboarding_passphrase_content);
                    passphraseView.setText(mPassphrase);

                    // For this dialog, we want to fill the whole screen regardless of the size of the content
                    // 1) Dialog width should be 80% of the width of the screen
                    Point appUsableSize = getAppUsableScreenSize(requireContext());

                    int idealDialogWidth = appUsableSize.x;
                    int idealDialogHeight = (int)(appUsableSize.y - Laba.dp2px(24));

                    view.setMinimumWidth(idealDialogWidth);
                    view.setMinimumHeight(idealDialogHeight);

                    view.setLayoutParams(new FrameLayout.LayoutParams(idealDialogWidth, idealDialogHeight));
                });

        fragment.forceFullscreen = true;

    }


    public void HandleBackupOptionCompleted(View view) {
        if(view != null) {

            if (view.getVisibility() == View.VISIBLE) {
                return;
            }
            Laba.Animate(view, "!s!f!>", () -> null);
            view.setVisibility(View.VISIBLE);

            numberOfBackupOptionsUsed++;

            if (numberOfBackupOptionsUsed >= 2 && !mBinding.onboardingDoneButton.isEnabled()) {
                mBinding.onboardingDoneButton.setText(R.string.onboarding_backup_done_button);
                mBinding.onboardingDoneButton.setEnabled(true);
            }
        }
    }

    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        return size;
    }
}
