package com.gentop.ltsdk.ltsdkui.widget.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gentop.ltgame.ltgameui.R;
import com.gentop.ltsdk.ltsdkui.base.BaseFragment;
import com.gentop.ltsdk.ltsdkui.model.BundleData;
import com.gentop.ltsdk.ltsdkui.ui.ProgressView;


public class LoginFailedFragment extends BaseFragment {

    LinearLayout mLytFailed;
    String mAgreementUrl;
    String mPrivacyUrl;
    String googleClientID;
    String LTAppID;
    String LTAppKey;
    String mAdID;
    String mPackageID;
    String mFacebookID;
    boolean mServerTest;
    boolean mIsLoginOut;
    boolean mIsBind;
    ProgressView mPgbLoading;
    TextView mTxtTips, mTxtContent;

    public static LoginFailedFragment newInstance(BundleData data) {
        Bundle args = new Bundle();
        LoginFailedFragment fragment = new LoginFailedFragment();
        args.putSerializable(ARG_NUMBER, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getFragmentId() {
        return R.layout.fragment_login_failed;
    }

    @Override
    protected void initView(View view) {
        mLytFailed = view.findViewById(R.id.lyt_login_failed);
        mPgbLoading = view.findViewById(R.id.pgb_loading);
        mTxtTips = view.findViewById(R.id.txt_login_failed_tips);
        mTxtContent = view.findViewById(R.id.txt_login_failed_content);
    }

    @Override
    public void lazyLoadData() {
        super.lazyLoadData();
        Bundle args = getArguments();
        if (args != null) {
            BundleData mData = (BundleData) args.getSerializable(ARG_NUMBER);
            if (mData != null) {
                mAgreementUrl = mData.getAgreementUrl();
                mPrivacyUrl = mData.getPrivacyUrl();
                googleClientID = mData.getGoogleClientID();
                LTAppID = mData.getLTAppID();
                LTAppKey = mData.getLTAppKey();
                mAdID = mData.getmAdID();
                mPackageID = mData.getmPackageID();
                mFacebookID = mData.getmFacebookID();
                mServerTest = mData.getServerTest();
                mIsLoginOut = mData.ismLoginOut();
                mIsBind = mData.getBind();
                initData(mPrivacyUrl, mAgreementUrl);
            }
        }
        if (mIsBind) {
            mTxtTips.setText(getResources().getString(R.string.text_bind_failed));
            mTxtContent.setText(getResources().getString(R.string.text_bind_content));
        } else {
            mTxtTips.setText(getResources().getString(R.string.text_login_way));
            mTxtContent.setText(getResources().getString(R.string.text_login_failed));
        }
    }

    /**
     * 跳转
     */
    private void initData(final String mPrivacyUrl, final String mAgreementUrl) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BundleData data = new BundleData();
                data.setPrivacyUrl(mPrivacyUrl);
                data.setAgreementUrl(mAgreementUrl);
                data.setLTAppKey(LTAppKey);
                data.setLTAppID(LTAppID);
                data.setGoogleClientID(googleClientID);
                data.setmAdID(mAdID);
                data.setmPackageID(mPackageID);
                data.setmFacebookID(mFacebookID);
                data.setServerTest(mServerTest);
                data.setmLoginOut(mIsLoginOut);
                getProxyActivity().addFragment(LoginFragment.newInstance(data),
                        false,
                        true);
            }
        }, 2000);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPgbLoading.stopAnimation();
    }
}
