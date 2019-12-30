package com.gentop.ltsdk.ltsdkui.widget.fragment;

import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.gentop.ltgame.ltgameui.R;
import com.gentop.ltsdk.common.constant.Constants;
import com.gentop.ltsdk.common.util.PreferencesUtils;
import com.gentop.ltsdk.ltsdkui.base.BaseFragment;
import com.gentop.ltsdk.ltsdkui.model.BundleData;
import com.gentop.ltsdk.ltsdkui.util.UrlUtils;


public class AgreementFragment extends BaseFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    TextView mTxtAgreement, mTxtPrivacy;
    Button mBtnInto;
    AppCompatCheckBox mCkbAgreement, mCkbPrivacy;
    boolean isAgreement = false;
    boolean isPrivacy = false;
    String mAgreementUrl;
    String mPrivacyUrl;
    String googleClientID;
    String LTAppID;
    String LTAppKey;
    String mAdID;
    String mPackageID;
    String mFacebookID;
    boolean mIsLoginOut;
    boolean mServerTest;


    public static AgreementFragment newInstance(BundleData data) {
        Bundle args = new Bundle();
        AgreementFragment fragment = new AgreementFragment();
        args.putSerializable(ARG_NUMBER, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getFragmentId() {
        return R.layout.fragment_agreement;
    }

    @Override
    protected void initView(View view) {
        isAgreement = false;
        isPrivacy = false;
        mTxtAgreement = view.findViewById(R.id.txt_agreement);
        mTxtAgreement.setOnClickListener(this);

        mTxtPrivacy = view.findViewById(R.id.txt_privacy);
        mTxtPrivacy.setOnClickListener(this);

        mCkbAgreement = view.findViewById(R.id.ckb_agreement);
        mCkbAgreement.setOnCheckedChangeListener(this);

        mCkbPrivacy = view.findViewById(R.id.ckb_privacy);
        mCkbPrivacy.setOnCheckedChangeListener(this);


        mBtnInto = view.findViewById(R.id.btn_into_game);
        mBtnInto.setOnClickListener(this);
    }

    @Override
    public void lazyLoadData() {
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
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_into_game) {
            if (isPrivacy && isAgreement) {
                if (TextUtils.isEmpty(PreferencesUtils.getString(mActivity,
                        Constants.USER_AGREEMENT_FLAT))) {
                    PreferencesUtils.putString(mActivity, Constants.USER_AGREEMENT_FLAT, "1");
                    login();
                }

            }
        } else if (view.getId() == R.id.txt_privacy) {
            if (!TextUtils.isEmpty(mPrivacyUrl)) {
                UrlUtils.getInstance().loadUrl(mActivity, mPrivacyUrl);
            }
        } else if (view.getId() == R.id.txt_agreement) {
            if (!TextUtils.isEmpty(mAgreementUrl)) {
                UrlUtils.getInstance().loadUrl(mActivity, mAgreementUrl);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ckb_agreement) {
            isAgreement = isChecked;

        } else if (buttonView.getId() == R.id.ckb_privacy) {
            isPrivacy = isChecked;
        }
        if (isPrivacy && isAgreement) {
            mBtnInto.setBackgroundResource(R.drawable.btn_blue_corner);
        } else {
            mBtnInto.setBackgroundResource(R.drawable.btn_corner);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isPrivacy = false;
        isAgreement = false;
    }

    /**
     * 登录
     */
    private void login() {
        BundleData data = new BundleData();
        data.setAgreementUrl(mAgreementUrl);
        data.setPrivacyUrl(mPrivacyUrl);
        data.setLTAppKey(LTAppKey);
        data.setLTAppID(LTAppID);
        data.setGoogleClientID(googleClientID);
        data.setmAdID(mAdID);
        data.setmPackageID(mPackageID);
        data.setServerTest(mServerTest);
        data.setmFacebookID(mFacebookID);
        data.setmLoginOut(mIsLoginOut);
        getProxyActivity().addFragment(LoginFragment.newInstance(data),
                false,
                true);

    }

}
