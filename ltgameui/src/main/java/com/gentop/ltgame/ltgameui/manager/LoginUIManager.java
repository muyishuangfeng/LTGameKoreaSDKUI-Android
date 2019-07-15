package com.gentop.ltgame.ltgameui.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;


import com.gentop.ltgame.ltgamecommon.constant.Constants;
import com.gentop.ltgame.ltgamecommon.impl.OnAutoLoginCheckListener;
import com.gentop.ltgame.ltgamecommon.manager.LoginResultManager;
import com.gentop.ltgame.ltgamecommon.model.BaseEntry;
import com.gentop.ltgame.ltgamecommon.model.ResultData;
import com.gentop.ltgame.ltgamecommon.util.PreferencesUtils;
import com.gentop.ltgame.ltgamegoogle.GoogleLoginManager;
import com.gentop.ltgame.ltgamegoogle.OnGoogleSignOutListener;
import com.gentop.ltgame.ltgameui.impl.OnReLoginInListener;
import com.gentop.ltgame.ltgameui.impl.OnResultClickListener;
import com.gentop.ltgame.ltgameui.ui.dialog.GeneralDialogUtil;
import com.gentop.ltgame.ltgameui.widget.activity.LoginActivity;

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
    public void loginIn(final Activity activity, final String url, final String mFacebookID,
                        final String mAgreementUrl, final String mPrivacyUrl, final String googleClientID,
                        final String LTAppID, final String LTAppKey, final String adID,
                        final String packageID, final boolean mIsLoginOut,
                        final OnResultClickListener listener,
                        final OnReLoginInListener mListener) {
        if (isLoginStatus(activity)) {
            login(activity, url, mFacebookID, mAgreementUrl, mPrivacyUrl, googleClientID, LTAppID,
                    LTAppKey, adID, packageID, mIsLoginOut, listener);
        } else {
            Map<String, Object> params = new WeakHashMap<>();
            params.put("lt_uid", PreferencesUtils.getString(activity, Constants.USER_LT_UID));
            params.put("lt_uid_token", PreferencesUtils.getString(activity, Constants.USER_LT_UID_TOKEN));
            params.put("platform_id", packageID);
            LoginResultManager.autoLoginCheck(url, LTAppID, LTAppKey, params, new OnAutoLoginCheckListener() {
                @Override
                public void onCheckSuccess(BaseEntry result) {
                    if (result != null) {
                        if (result.getCode() == 200) {
                            ResultData resultData = new ResultData();
                            resultData.setLt_uid(PreferencesUtils.getString(activity, Constants.USER_LT_UID));
                            resultData.setLt_uid_token(PreferencesUtils.getString(activity, Constants.USER_LT_UID_TOKEN));
                            mListener.OnLoginResult(resultData);
                        } else if (result.getCode() == 501) {
                            GeneralDialogUtil.showActionDialog(activity, 501);
                        } else if (result.getCode() == 502) {
                            GeneralDialogUtil.showActionDialog(activity, 502);
                        } else if (result.getCode() == 503) {
                            GeneralDialogUtil.showActionDialog(activity, 503);
                        } else if (result.getCode() == 400) {
                            loginOut(activity, url, mFacebookID, mAgreementUrl, mPrivacyUrl,
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
    public void loginOut(final Activity activity, final String baseUrl, final String mFacebookID,
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
                    login(activity, baseUrl, mFacebookID, mAgreementUrl, mPrivacyUrl,
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
    private void login(Activity activity, String baseUrl, String mFacebookID, String mAgreementUrl,
                       String mPrivacyUrl, String googleClientID, String LTAppID, String LTAppKey,
                       String adID, String mPackageID, boolean mIsLoginOut, OnResultClickListener listener) {
        this.mListener = listener;
        Intent intent = new Intent(activity, LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("baseUrl", baseUrl);
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
