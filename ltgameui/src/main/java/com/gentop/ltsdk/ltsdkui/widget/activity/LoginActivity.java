package com.gentop.ltsdk.ltsdkui.widget.activity;


import android.os.Bundle;
import android.text.TextUtils;

import com.gentop.ltgame.ltgameui.R;
import com.gentop.ltsdk.common.constant.Constants;
import com.gentop.ltsdk.common.model.ResultData;
import com.gentop.ltsdk.common.util.PreferencesUtils;
import com.gentop.ltsdk.ltsdkui.base.BaseAppActivity;
import com.gentop.ltsdk.ltsdkui.impl.OnResultClickListener;
import com.gentop.ltsdk.ltsdkui.manager.LoginUIManager;
import com.gentop.ltsdk.ltsdkui.model.BundleData;
import com.gentop.ltsdk.ltsdkui.widget.fragment.AgreementFragment;
import com.gentop.ltsdk.ltsdkui.widget.fragment.LoginFragment;


public class LoginActivity extends BaseAppActivity {


    @Override
    protected int getViewId() {
        return R.layout.activity_login;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initView() {
        Bundle bundle = getIntent().getBundleExtra("bundleData");
        String mAgreementUrl = bundle.getString("mAgreementUrl");
        String mPrivacyUrl = bundle.getString("mPrivacyUrl");
        String googleClientID = bundle.getString("googleClientID");
        String LTAppID = bundle.getString("LTAppID");
        String LTAppKey = bundle.getString("LTAppKey");
        String mAdID = bundle.getString("adID");
        String mPackageID = bundle.getString("mPackageID");
        boolean mServerTest = bundle.getBoolean("mServerTest");
        String mFacebookID = bundle.getString("mFacebookID");
        final boolean mIsLoginOut = bundle.getBoolean("mIsLoginOut");

        BundleData data = new BundleData();
        data.setAgreementUrl(mAgreementUrl);
        data.setPrivacyUrl(mPrivacyUrl);
        data.setGoogleClientID(googleClientID);
        data.setLTAppID(LTAppID);
        data.setLTAppKey(LTAppKey);
        data.setmAdID(mAdID);
        data.setmPackageID(mPackageID);
        data.setServerTest(mServerTest);
        data.setmFacebookID(mFacebookID);
        data.setmLoginOut(mIsLoginOut);

        if (!TextUtils.isEmpty(mAgreementUrl) &&
                !TextUtils.isEmpty(mPrivacyUrl)) {
            if (TextUtils.isEmpty(PreferencesUtils.getString(this,
                    Constants.USER_AGREEMENT_FLAT))) {
                if (findFragment(AgreementFragment.class) == null) {
                    addFragment(AgreementFragment.newInstance(data),
                            false,
                            true);
                }
            } else {
                if (findFragment(LoginFragment.class) == null) {
                    LoginFragment fragment = LoginFragment.newInstance(data);
                    fragment.setOnResultClick(new OnResultClickListener() {
                        @Override
                        public void onResult(ResultData result) {
                            LoginUIManager.getInstance().setResult(result);
                        }
                    });
                    addFragment(fragment,
                            false,
                            true);
                }
            }

        }

    }

    @Override
    protected void initData() {
    }


}
