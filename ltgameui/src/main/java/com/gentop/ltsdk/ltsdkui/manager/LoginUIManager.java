package com.gentop.ltsdk.ltsdkui.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;


import com.gentop.ltgamesdk.google.GoogleLoginManager;
import com.gentop.ltgamesdk.google.OnGoogleSignOutListener;
import com.gentop.ltsdk.common.constant.Constants;
import com.gentop.ltsdk.common.impl.OnAutoLoginCheckListener;
import com.gentop.ltsdk.common.manager.LoginResultManager;
import com.gentop.ltsdk.common.model.BaseEntry;
import com.gentop.ltsdk.common.model.ResultData;
import com.gentop.ltsdk.common.util.PreferencesUtils;
import com.gentop.ltsdk.ltsdkui.impl.OnReLoginInListener;
import com.gentop.ltsdk.ltsdkui.impl.OnResultClickListener;
import com.gentop.ltsdk.ltsdkui.ui.dialog.GeneralDialogUtil;
import com.gentop.ltsdk.ltsdkui.util.ConstantModel;
import com.gentop.ltsdk.ltsdkui.widget.activity.LoginActivity;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 登录工具类
 */
public class LoginUIManager {


    private volatile static LoginUIManager sInstance;
    private OnResultClickListener mListener;

    private LoginUIManager() {
    }


    public static LoginUIManager getInstance() {
        if (sInstance == null) {
            synchronized (LoginUIManager.class) {
                if (sInstance == null) {
                    sInstance = new LoginUIManager();
                }
            }
        }
        return sInstance;
    }


    /**
     * 登录进入
     *
     * @param activity       上下文
     * @param mAgreementUrl  用户协议
     * @param mPrivacyUrl    隐私协议
     * @param googleClientID googleClientID
     * @param LTAppID        乐推AppID
     * @param LTAppKey       乐推AppKey
     * @param mListener      登录接口
     */
    public void loginIn(final Activity activity, final boolean mServerTest, final String mFacebookID,
                        final String mAgreementUrl, final String mPrivacyUrl, final String googleClientID,
                        final String LTAppID, final String LTAppKey, final String adID,
                        final String packageID, final boolean mIsLoginOut,
                        final OnResultClickListener listener,
                        final OnReLoginInListener mListener) {
        if (isLoginStatus(activity)) {
            login(activity, mServerTest, mFacebookID, mAgreementUrl, mPrivacyUrl, googleClientID, LTAppID,
                    LTAppKey, adID, packageID, mIsLoginOut, listener);
        } else {
            Map<String, Object> params = new WeakHashMap<>();
            params.put("lt_uid", PreferencesUtils.getString(activity, Constants.USER_LT_UID));
            params.put("lt_uid_token", PreferencesUtils.getString(activity, Constants.USER_LT_UID_TOKEN));
            params.put("platform_id", packageID);
            LoginResultManager.autoLoginCheck(mServerTest, LTAppID, LTAppKey, params, new OnAutoLoginCheckListener() {
                @Override
                public void onCheckSuccess(BaseEntry result) {
                    if (result != null) {
                        if (result.getCode() == 200) {
                            ResultData resultData = new ResultData();
                            resultData.setLt_uid(PreferencesUtils.getString(activity, Constants.USER_LT_UID));
                            resultData.setLt_uid_token(PreferencesUtils.getString(activity, Constants.USER_LT_UID_TOKEN));
                            resultData.setApi_token(PreferencesUtils.getString(activity, Constants.USER_API_TOKEN));
                            resultData.setLoginType(PreferencesUtils.getString(activity, ConstantModel.MSG_LOGIN_TYPE));
                            mListener.OnLoginResult(resultData);
                        } else if (result.getCode() == 501) {
                            GeneralDialogUtil.showActionDialog(activity, 501);
                        } else if (result.getCode() == 502) {
                            GeneralDialogUtil.showActionDialog(activity, 502);
                        } else if (result.getCode() == 503) {
                            GeneralDialogUtil.showActionDialog(activity, 503);
                        } else if (result.getCode() == 400) {
                            loginOut(activity, mServerTest, mFacebookID, mAgreementUrl, mPrivacyUrl,
                                    googleClientID, LTAppID, LTAppKey, adID, packageID, mIsLoginOut,
                                    listener);
                        }
                    }
                }

                @Override
                public void onCheckFailed(Throwable ex) {
                    ResultData resultData = new ResultData();
                    resultData.setErrorMsg(ex.getMessage());
                    mListener.OnLoginResult(resultData);
                }
            });

        }
    }

    /**
     * 登出
     *
     * @param activity       上下文
     * @param mAgreementUrl  用户协议
     * @param mPrivacyUrl    隐私协议
     * @param googleClientID googleClientID
     * @param LTAppID        乐推AppID
     * @param LTAppKey       乐推AppKey
     */
    public void loginOut(final Activity activity, final boolean mServerTest, final String mFacebookID,
                         final String mAgreementUrl, final String mPrivacyUrl, final String googleClientID,
                         final String LTAppID, final String LTAppKey, final String adID,
                         final String mPackageID, final boolean mIsLoginOut, final OnResultClickListener listener) {
        PreferencesUtils.remove(activity, Constants.USER_LT_UID);
        PreferencesUtils.remove(activity, Constants.USER_LT_UID_TOKEN);
        GoogleLoginManager.GoogleSingOut(activity, googleClientID, new OnGoogleSignOutListener() {
            @Override
            public void onSignOutSuccess() {
                if (TextUtils.isEmpty(PreferencesUtils.getString(activity,
                        Constants.USER_LT_UID)) &&
                        TextUtils.isEmpty(PreferencesUtils.getString(activity,
                                Constants.USER_LT_UID_TOKEN))) {
                    login(activity, mServerTest, mFacebookID, mAgreementUrl, mPrivacyUrl,
                            googleClientID, LTAppID, LTAppKey, adID, mPackageID, mIsLoginOut, listener);
                }
            }
        });
    }

    /**
     * 是否登录成功
     */
    private boolean isLoginStatus(Activity activity) {
        return TextUtils.isEmpty(PreferencesUtils.getString(activity,
                Constants.USER_LT_UID)) &&
                TextUtils.isEmpty(PreferencesUtils.getString(activity,
                        Constants.USER_LT_UID_TOKEN));
    }

    /**
     * 登录方法
     *
     * @param activity       上下文
     * @param mAgreementUrl  用户协议
     * @param mPrivacyUrl    隐私协议
     * @param googleClientID googleClient
     * @param LTAppID        乐推AppID
     * @param LTAppKey       乐推AppKey
     */
    private void login(Activity activity, boolean mServerTest, String mFacebookID, String mAgreementUrl,
                       String mPrivacyUrl, String googleClientID, String LTAppID, String LTAppKey,
                       String adID, String mPackageID, boolean mIsLoginOut, OnResultClickListener listener) {
        this.mListener = listener;
        Intent intent = new Intent(activity, LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("mServerTest", mServerTest);
        bundle.putString("mFacebookID", mFacebookID);
        bundle.putString("mAgreementUrl", mAgreementUrl);
        bundle.putString("mPrivacyUrl", mPrivacyUrl);
        bundle.putString("googleClientID", googleClientID);
        bundle.putString("LTAppID", LTAppID);
        bundle.putString("LTAppKey", LTAppKey);
        bundle.putString("adID", adID);
        bundle.putString("mPackageID", mPackageID);
        bundle.putBoolean("mIsLoginOut", mIsLoginOut);
        intent.putExtra("bundleData", bundle);
        activity.startActivity(intent);
    }


    public void setResult(ResultData result) {
        if (mListener != null) {
            mListener.onResult(result);
        }
    }


}
