package com.gentop.ltgame.ltgameui.widget.fragment;

import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.gentop.ltgame.ltgamecommon.constant.Constants;
import com.gentop.ltgame.ltgamecommon.util.PreferencesUtils;
import com.gentop.ltgame.ltgameui.R;
import com.gentop.ltgame.ltgameui.base.BaseFragment;
import com.gentop.ltgame.ltgameui.model.BundleData;
import com.gentop.ltgame.ltgameui.util.UrlUtils;


public class AgreementFragment extends BaseFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    AppCompatImageView mImgAgreement,mImgPrivacy;
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
    String mBaseUrl;
    String mFacebookID;
    boolean mIsLoginOut;


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
        mImgAgreement = view.findViewById(R.id.img_agreement);
        mImgAgreement.setOnClickListener(this);

        mImgPrivacy = view.findViewById(R.id.img_privacy);
        mImgPrivacy.setOnClickListener(this);

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
                mBaseUrl = mData.getBaseURL();
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
        } else if (view.getId() == R.id.img_privacy) {
            if (!TextUtils.isEmpty(mPrivacyUrl)) {
                UrlUtils.getInstance().loadUrl(mActivity, mPrivacyUrl);
            }
        } else if (view.getId() == R.id.img_agreement) {
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
        if (findChildFragment(LoginFragment.class) == null) {
            BundleData data = new BundleData();
            data.setAgreementUrl(mAgreementUrl);
            data.setPrivacyUrl(mPrivacyUrl);
            data.setLTAppKey(LTAppKey);
            data.setLTAppID(LTAppID);
            data.setGoogleClientID(googleClientID);
            data.setmAdID(mAdID);
            data.setmPackageID(mPackageID);
            data.setBaseURL(mBaseUrl);
            data.setmFacebookID(mFacebookID);
            data.setmLoginOut(mIsLoginOut);
            getProxyActivity().addFragment(LoginFragment.newInstance(data),
                    false,
                    true);
        }
    }

}
