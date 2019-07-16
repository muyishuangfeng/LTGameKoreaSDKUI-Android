package com.gentop.ltsdk.ltsdkui.widget.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gentop.ltgame.ltgameui.R;
import com.gentop.ltgamesdk.google.GoogleLoginManager;
import com.gentop.ltsdk.common.constant.Constants;
import com.gentop.ltsdk.common.impl.OnLoginSuccessListener;
import com.gentop.ltsdk.common.model.BaseEntry;
import com.gentop.ltsdk.common.model.ResultData;
import com.gentop.ltsdk.common.util.PreferencesUtils;
import com.gentop.ltsdk.facebook.FacebookLoginManager;
import com.gentop.ltsdk.ltsdkui.base.BaseFragment;
import com.gentop.ltsdk.ltsdkui.impl.OnResultClickListener;
import com.gentop.ltsdk.ltsdkui.model.BundleData;


public class LoginFragment extends BaseFragment implements View.OnClickListener {

    TextView mLytGoogle, mLytFaceBook;
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
    private static final int REQUEST_CODE = 0X01;
    private OnResultClickListener mListener;


    public static LoginFragment newInstance(BundleData data) {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        args.putSerializable(ARG_NUMBER, data);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getFragmentId() {
        return R.layout.fragment_loign;
    }

    @Override
    protected void initView(View view) {
        mLytGoogle = view.findViewById(R.id.lyt_login_google);
        mLytGoogle.setOnClickListener(this);

        mLytFaceBook = view.findViewById(R.id.lyt_login_facebook);
        mLytFaceBook.setOnClickListener(this);
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
                mBaseUrl = mData.getBaseURL();
                mIsLoginOut = mData.ismLoginOut();

            }
        }

    }

    @Override
    public void onClick(View view) {
        int resID = view.getId();
        if (resID == R.id.lyt_login_facebook) {//facebook
            initFaceBook();
        } else if (resID == R.id.lyt_login_google) {//google
            if (!TextUtils.isEmpty(googleClientID)) {
                GoogleLoginManager.initGoogle(mActivity,
                        googleClientID, REQUEST_CODE);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookLoginManager.onActivityResult(requestCode, resultCode, data);
        if (!TextUtils.isEmpty(LTAppID) &&
                !TextUtils.isEmpty(LTAppKey) && !TextUtils.isEmpty(mAdID)) {
            GoogleLoginManager.onActivityResult(mBaseUrl, requestCode, data, REQUEST_CODE, mActivity,
                    LTAppID, LTAppKey, mAdID, mPackageID,
                    new OnLoginSuccessListener() {
                        @Override
                        public void onSuccess(BaseEntry<ResultData> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (result.getData().getApi_token() != null &&
                                            result.getData().getLt_uid() != null) {
                                        ResultData mData = new ResultData();
                                        mData.setLt_uid(result.getData().getLt_uid());
                                        mData.setLt_uid_token(result.getData().getLt_uid_token());
                                        if (mListener != null) {
                                            mListener.onResult(mData);
                                        }
                                        PreferencesUtils.putString(mActivity, Constants.USER_API_TOKEN, result.getData().getApi_token());
                                        PreferencesUtils.putString(mActivity, Constants.USER_LT_UID, result.getData().getLt_uid());
                                        PreferencesUtils.putString(mActivity, Constants.USER_LT_UID_TOKEN, result.getData().getLt_uid_token());
                                        getProxyActivity().finish();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailed(String result) {
                            loginFailed();
                        }


                        @Override
                        public void onParameterError(String result) {
                            loginFailed();
                        }


                    });
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 登录失败
     */
    private void loginFailed() {
        if (findChildFragment(LoginFailedFragment.class) == null) {
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
            getProxyActivity().addFragment(LoginFailedFragment.newInstance(data),
                    false,
                    true);
        }
    }


    /**
     * 初始化facebook
     */
    private void initFaceBook() {
        if (!TextUtils.isEmpty(LTAppID) &&
                !TextUtils.isEmpty(LTAppKey) && !TextUtils.isEmpty(mAdID)) {
            FacebookLoginManager.initFaceBook(mActivity, mFacebookID, mBaseUrl,
                    LTAppID, LTAppKey, mAdID, mPackageID, mIsLoginOut,
                    new OnLoginSuccessListener() {
                        @Override
                        public void onSuccess(BaseEntry<ResultData> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (result.getData().getApi_token() != null &&
                                            result.getData().getLt_uid() != null) {
                                        ResultData mData = new ResultData();
                                        mData.setLt_uid(result.getData().getLt_uid());
                                        mData.setLt_uid_token(result.getData().getLt_uid_token());
                                        if (mListener != null) {
                                            mListener.onResult(mData);
                                        }
                                        PreferencesUtils.putString(mActivity, Constants.USER_API_TOKEN, result.getData().getApi_token());
                                        PreferencesUtils.putString(mActivity, Constants.USER_LT_UID, result.getData().getLt_uid());
                                        PreferencesUtils.putString(mActivity, Constants.USER_LT_UID_TOKEN, result.getData().getLt_uid_token());
                                        getProxyActivity().finish();
                                    }

                                }
                            }
                        }

                        @Override
                        public void onFailed(String ex) {
                            loginFailed();
                        }


                        @Override
                        public void onParameterError(String result) {
                            loginFailed();
                        }


                    });
        }
    }

    /**
     * 回调
     */
    public void setOnResultClick(OnResultClickListener listener) {
        mListener = listener;
    }

}
