package com.learningmachine.android.app.ui.onboarding;

import static com.learningmachine.android.app.ui.onboarding.OnboardingScreen.ACCOUNT_CHOOSER;
import static com.learningmachine.android.app.ui.onboarding.OnboardingScreen.BACKUP_PASSPHRASE;
import static com.learningmachine.android.app.ui.onboarding.OnboardingScreen.PASTE_PASSPHRASE;
import static com.learningmachine.android.app.ui.onboarding.OnboardingScreen.VIEW_PASSPHRASE;
import static com.learningmachine.android.app.ui.onboarding.OnboardingScreen.WELCOME_BACK;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OnboardingFlow implements Serializable {

    private int mPosition;
    private final List<OnboardingScreen> mScreens;

    public enum FlowType {
        UNKNOWN(Collections.singletonList(ACCOUNT_CHOOSER)),
        BACKUP_ONLY(Arrays.asList(WELCOME_BACK, BACKUP_PASSPHRASE)),
        NEW_ACCOUNT(Arrays.asList(ACCOUNT_CHOOSER, VIEW_PASSPHRASE, BACKUP_PASSPHRASE)),
        EXISTING_ACCOUNT(Arrays.asList(ACCOUNT_CHOOSER, PASTE_PASSPHRASE));


        private final List<OnboardingScreen> mScreens;

        FlowType(List<OnboardingScreen> mScreens) {
            this.mScreens = mScreens;
        }

        public List<OnboardingScreen> getScreens() {
            return mScreens;
        }
    }

    public OnboardingFlow(FlowType flowType) {
        mScreens = flowType.getScreens();
    }

    public List<OnboardingScreen> getScreens() {
        return mScreens;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

}
