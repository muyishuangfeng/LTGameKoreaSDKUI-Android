package com.gentop.ltsdk.ltsdkui.widget.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.gentop.ltgame.ltgameui.R;
import com.gentop.ltgamesdk.google.GoogleLoginManager;
import com.gentop.ltgamesdk.google.OnGoogleTokenListener;
import com.gentop.ltsdk.common.constant.Constants;
import com.gentop.ltsdk.common.impl.OnLoginSuccessListener;
import com.gentop.ltsdk.common.model.BaseEntry;
import com.gentop.ltsdk.common.model.ResultData;
import com.gentop.ltsdk.common.util.PreferencesUtils;
import com.gentop.ltsdk.facebook.FacebookLoginManager;
import com.gentop.ltsdk.facebook.OnFBCallBack;
import com.gentop.ltsdk.guest.GuestManager;
import com.gentop.ltsdk.ltsdkui.base.BaseFragment;
import com.gentop.ltsdk.ltsdkui.impl.OnResultClickListener;
import com.gentop.ltsdk.ltsdkui.manager.LoginUIManager;
import com.gentop.ltsdk.ltsdkui.model.BundleData;
import com.gentop.ltsdk.ltsdkui.util.ConstantModel;


public class BindFragment extends BaseFragment implements View.OnClickListener {

    LinearLayout mLytGoogle, mLytFaceBook;
    String mAgreementUrl;
    String mPrivacyUrl;
    String googleClientID;
    String LTAppID;
    String LTAppKey;
    String mAdID;
    String mPackageID;
    boolean mServerTest;
    String mFacebookID;
    boolean mIsLoginOut;
    private static final int REQUEST_CODE = 0X01;
    private OnResultClickListener mListener;
    AppCompatImageView mImgBack;


    public static BindFragment newInstance(BundleData data) {
        Bundle args = new Bundle();
        BindFragment fragment = new BindFragment();
        args.putSerializable(ARG_NUMBER, data);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getFragmentId() {
        return R.layout.fragment_bind;
    }

    @Override
    protected void initView(View view) {
        mLytGoogle = view.findViewById(R.id.lyt_bind_google);
        mLytGoogle.setOnClickListener(this);


        mLytFaceBook = view.findViewById(R.id.lyt_bind_facebook);
        mLytFaceBook.setOnClickListener(this);

        mImgBack = view.findViewById(R.id.img_bind_back);
        mImgBack.setOnClickListener(this);
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

            }
        }

    }

    @Override
    public void onClick(View view) {
        int resID = view.getId();
        if (resID == R.id.lyt_bind_facebook) {//facebook
            initFaceBook();
        } else if (resID == R.id.lyt_bind_google) {//google
            if (!TextUtils.isEmpty(googleClientID)) {
                GoogleLoginManager.initGoogle(mActivity,
                        googleClientID, REQUEST_CODE);
            }
        } else if (resID == R.id.img_bind_back) {
            guestLogin();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookLoginManager.onActivityResult(requestCode, resultCode, data);
        if (!TextUtils.isEmpty(LTAppID) &&
                !TextUtils.isEmpty(LTAppKey) && !TextUtils.isEmpty(mAdID)) {
            GoogleLoginManager.onActivityResult(requestCode, data, REQUEST_CODE,
                    new OnGoogleTokenListener() {
                        @Override
                        public void onSuccess(String token) {
                            GuestManager.bindAccount(mActivity, mServerTest, LTAppID, LTAppKey, mAdID, mPackageID, "google", token, new OnLoginSuccessListener() {
                                @Override
                                public void onSuccess(BaseEntry<ResultData> result) {
                                    if (result != null) {
                                        if (result.getCode() == 200) {
                                            if (result.getData().getApi_token() != null &&
                                                    result.getData().getLt_uid() != null) {
                                                ResultData mData = new ResultData();
                                                mData.setLt_uid(result.getData().getLt_uid());
                                                mData.setLt_uid_token(result.getData().getLt_uid_token());
                                                mData.setApi_token(result.getData().getApi_token());
                                                mData.setLoginType("Google Login");
                                                PreferencesUtils.putString(mActivity, ConstantModel.MSG_LOGIN_TYPE, "Google Login");
                                                PreferencesUtils.putString(mActivity, Constants.USER_BIND_FLAG, "2");
                                                PreferencesUtils.putString(mActivity, Constants.USER_API_TOKEN, result.getData().getApi_token());
                                                PreferencesUtils.putString(mActivity, Constants.USER_LT_UID, result.getData().getLt_uid());
                                                PreferencesUtils.putString(mActivity, Constants.USER_LT_UID_TOKEN, result.getData().getLt_uid_token());
                                                LoginUIManager.getInstance().setResult(mData);
                                                getProxyActivity().finish();
                                            }
                                        } else {
                                            getProxyActivity().finish();
                                        }
                                    }
                                }

                                @Override
                                public void onFailed(String code) {
                                    loginFailed();
                                }

                                @Override
                                public void onParameterError(String result) {

                                }
                            });
                        }

                        @Override
                        public void onFailed(String result) {
                            loginFailed();
                        }

                    });
        }
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
            data.setServerTest(mServerTest);
            data.setmFacebookID(mFacebookID);
            data.setmLoginOut(mIsLoginOut);
            getProxyActivity().addFragment(LoginFailedFragment.newInstance(data),
                    false,
                    true);
        }
    }

    /**
     * 游客登录
     */
    private void guestLogin() {
        pop();
        if (findFragment(GuestTurnFragment.class) == null) {
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
            GuestTurnFragment fragment = GuestTurnFragment.newInstance(data);
            fragment.setOnResultClick(new OnResultClickListener() {
                @Override
                public void onResult(ResultData result) {
                    LoginUIManager.getInstance().setResult(result);
                }
            });
            getProxyActivity().addFragment(fragment,
                    false,
                    false);
        }
    }


    /**
     * 初始化facebook
     */
    private void initFaceBook() {
        if (!TextUtils.isEmpty(LTAppID) &&
                !TextUtils.isEmpty(LTAppKey) && !TextUtils.isEmpty(mAdID)) {
            FacebookLoginManager.bindFB(mActivity, mFacebookID, mIsLoginOut,
                    new OnFBCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            GuestManager.bindAccount(mActivity, mServerTest, LTAppID, LTAppKey, mAdID, mPackageID,
                                    "facebook", result, new OnLoginSuccessListener() {
                                        @Override
                                        public void onSuccess(BaseEntry<ResultData> result) {
                                            if (result != null) {
                                                if (result.getCode() == 200) {
                                                    if (result.getData().getApi_token() != null &&
                                                            result.getData().getLt_uid() != null) {
                                                        ResultData mData = new ResultData();
                                                        mData.setLt_uid(result.getData().getLt_uid());
                                                        mData.setApi_token(result.getData().getApi_token());
                                                        mData.setLt_uid_token(result.getData().getLt_uid_token());
                                                        mData.setLoginType("Facebook Login");
                                                        PreferencesUtils.putString(mActivity, ConstantModel.MSG_LOGIN_TYPE, "Facebook Login");
                                                        PreferencesUtils.putString(mActivity, Constants.USER_BIND_FLAG, "2");
                                                        PreferencesUtils.putString(mActivity, Constants.USER_API_TOKEN, result.getData().getApi_token());
                                                        PreferencesUtils.putString(mActivity, Constants.USER_LT_UID, result.getData().getLt_uid());
                                                        PreferencesUtils.putString(mActivity, Constants.USER_LT_UID_TOKEN, result.getData().getLt_uid_token());
                                                        LoginUIManager.getInstance().setResult(mData);
                                                        getProxyActivity().finish();
                                                    }
                                                } else {
                                                    getProxyActivity().finish();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailed(String code) {

                                        }

                                        @Override
                                        public void onParameterError(String result) {

                                        }
                                    });
                        }

                        @Override
                        public void onFailed(String ex) {
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
