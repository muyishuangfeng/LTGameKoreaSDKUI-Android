package com.gentop.ltsdk.ltsdkui.widget.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
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
import com.gentop.ltsdk.ltsdkui.manager.LoginUIManager;
import com.gentop.ltsdk.ltsdkui.model.BundleData;
import com.gentop.ltsdk.ltsdkui.util.ConstantModel;


public class LoginFragment extends BaseFragment implements View.OnClickListener {

    LinearLayout mLytGoogle, mLytFaceBook;
    TextView mTxtGuest;
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

        mTxtGuest = view.findViewById(R.id.txt_visitor);
        mTxtGuest.setOnClickListener(this);

        mLytFaceBook = view.findViewById(R.id.lyt_login_facebook);
        mLytFaceBook.setOnClickListener(this);

        if (!TextUtils.isEmpty(PreferencesUtils.getString(mActivity, Constants.USER_BIND_FLAG)) &&
                TextUtils.equals(PreferencesUtils.getString(mActivity, Constants.USER_BIND_FLAG), "2")) {//绑定过不显示游客
           mTxtGuest.setVisibility(View.GONE);
        }
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
        if (resID == R.id.lyt_login_facebook) {//facebook
            initFaceBook();
        } else if (resID == R.id.lyt_login_google) {//google
            if (!TextUtils.isEmpty(googleClientID)) {
                GoogleLoginManager.initGoogle(mActivity,
                        googleClientID, REQUEST_CODE);
            }
        } else if (resID == R.id.txt_visitor) {//游客登录
            if (!TextUtils.isEmpty(PreferencesUtils.getString(mActivity, Constants.USER_GUEST_FLAG)) &&
                    TextUtils.equals(PreferencesUtils.getString(mActivity, Constants.USER_GUEST_FLAG), "2")) {//游客登录过
                guestTurn();
            } else {
                guestLogin();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookLoginManager.onActivityResult(requestCode, resultCode, data);
        if (!TextUtils.isEmpty(LTAppID) &&
                !TextUtils.isEmpty(LTAppKey) && !TextUtils.isEmpty(mAdID)) {
            GoogleLoginManager.onActivityResult(mServerTest, requestCode, data, REQUEST_CODE, mActivity,
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
                                        mData.setApi_token(result.getData().getApi_token());
                                        mData.setLoginType("Google Login");
                                        if (mListener != null) {
                                            mListener.onResult(mData);
                                        }
                                        PreferencesUtils.putString(mActivity, ConstantModel.MSG_LOGIN_TYPE, "Google Login");
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
        if (findFragment(GuestFragment.class) == null) {
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
            GuestFragment fragment = GuestFragment.newInstance(data);
            fragment.setOnResultClick(new OnResultClickListener() {
                @Override
                public void onResult(ResultData result) {
                    LoginUIManager.getInstance().setResult(result);
                }
            });
            getProxyActivity().addFragment(fragment,
                    false,
                    true);
        }
    }

    /**
     * 游客转正
     */
    private void guestTurn() {
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
                    true,
                    true);
        }
    }


    /**
     * 初始化facebook
     */
    private void initFaceBook() {
        if (!TextUtils.isEmpty(LTAppID) &&
                !TextUtils.isEmpty(LTAppKey) && !TextUtils.isEmpty(mAdID)) {
            FacebookLoginManager.initFaceBook(mActivity, mFacebookID, mServerTest,
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
                                        mData.setApi_token(result.getData().getApi_token());
                                        mData.setLt_uid_token(result.getData().getLt_uid_token());
                                        mData.setLoginType("Facebook Login");
                                        if (mListener != null) {
                                            mListener.onResult(mData);
                                        }
                                        PreferencesUtils.putString(mActivity, ConstantModel.MSG_LOGIN_TYPE, "Facebook Login");
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
