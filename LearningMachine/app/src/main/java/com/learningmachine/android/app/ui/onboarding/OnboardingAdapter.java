package com.learningmachine.android.app.ui.onboarding;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

class OnboardingAdapter extends FragmentStatePagerAdapter {

    private List<OnboardingScreen> mScreens;
    private OnboardingFragment mCurrentFragment;

    OnboardingAdapter(FragmentManager fm, List<OnboardingScreen> screens) {
        super(fm);
        mScreens = screens;
    }

    @Override
    public Fragment getItem(int i) {

        switch (mScreens.get(i)) {
            case ACCOUNT_CHOOSER:
                return AccountChooserFragment.newInstance();

            case VIEW_PASSPHRASE:
                return ViewPassphraseFragment.newInstance();

            case BACKUP_PASSPHRASE:
                return BackupPassphraseFragment.newInstance();

            case PASTE_PASSPHRASE:
                return PastePassphraseFragment.newInstance();

            case WELCOME_BACK:
                return WelcomeBackFragment.newInstance();
        }

        return null;
    }

    @Override
    public int getCount() {
        return mScreens.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        // Workaround for ViewPager's Caching of objects after notifyDataSetChanged is called.
        // We want to reload everything because we are replacing the flow.
        return POSITION_NONE;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentFragment = (OnboardingFragment) object;
    }

    void setScreens(List<OnboardingScreen> screens) {
        mScreens = screens;
    }

    public OnboardingFragment getCurrentFragment() {
        return mCurrentFragment;
    }
}
