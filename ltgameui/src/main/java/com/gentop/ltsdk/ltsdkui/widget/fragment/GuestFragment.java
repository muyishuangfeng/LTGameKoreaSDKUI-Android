package com.gentop.ltsdk.ltsdkui.widget.fragment;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gentop.ltgame.ltgameui.R;
import com.gentop.ltsdk.common.constant.Constants;
import com.gentop.ltsdk.common.impl.OnLoginSuccessListener;
import com.gentop.ltsdk.common.model.BaseEntry;
import com.gentop.ltsdk.common.model.ResultData;
import com.gentop.ltsdk.common.util.PreferencesUtils;
import com.gentop.ltsdk.facebook.FacebookUIEventManager;
import com.gentop.ltsdk.guest.GuestManager;
import com.gentop.ltsdk.ltsdkui.base.BaseFragment;
import com.gentop.ltsdk.ltsdkui.impl.OnResultClickListener;
import com.gentop.ltsdk.ltsdkui.manager.LoginUIManager;
import com.gentop.ltsdk.ltsdkui.model.BundleData;
import com.gentop.ltsdk.ltsdkui.util.AnimationUtils;
import com.gentop.ltsdk.ltsdkui.util.ConstantModel;

public class GuestFragment extends BaseFragment implements View.OnClickListener {

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
    private OnResultClickListener mListener;
    Button mBtnSure, mBtnCancel;
    TextView mTxtTitle, mTxtTips, mTxtContent;
    AppCompatImageView mImgLeft, mImgMiddle, mImgRight;
    RelativeLayout mRytGuest, mRytBottom;


    public static GuestFragment newInstance(BundleData data) {
        Bundle args = new Bundle();
        GuestFragment fragment = new GuestFragment();
        args.putSerializable(ARG_NUMBER, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getFragmentId() {
        return R.layout.fragment_guest;
    }


    @Override
    protected void initView(View view) {
        mBtnCancel = view.findViewById(R.id.btn_guest_cancel);
        mBtnCancel.setOnClickListener(this);

        mBtnSure = view.findViewById(R.id.btn_guest_continue);
        mBtnSure.setOnClickListener(this);

        mTxtTitle = view.findViewById(R.id.txt_guest_title);
        mTxtTips = view.findViewById(R.id.txt_guest_tips);

        mImgLeft = view.findViewById(R.id.img_guest_left);
        mImgMiddle = view.findViewById(R.id.img_guest_middle);
        mImgRight = view.findViewById(R.id.img_guest_right);
        mRytGuest = view.findViewById(R.id.ryt_guest);
        mTxtContent = view.findViewById(R.id.txt_guest_content);
        mRytBottom = view.findViewById(R.id.ryt_guest_bottom);
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
        if (view.getId() == R.id.btn_guest_cancel) {//取消
            backLogin();
        } else if (view.getId() == R.id.btn_guest_continue) {//继续
            login();
            GuestManager.guestLogin(mActivity, mServerTest, LTAppID, LTAppKey, mAdID, mPackageID,
                    new OnLoginSuccessListener() {
                        @Override
                        public void onSuccess(BaseEntry<ResultData> result) {
                            if (result.getCode()==200){
                                ResultData mData = new ResultData();
                                mData.setLt_uid(result.getData().getLt_uid());
                                mData.setLt_uid_token(result.getData().getLt_uid_token());
                                mData.setApi_token(result.getData().getApi_token());
                                mData.setLoginType("Guest Login");
                                loginEnd();
                                PreferencesUtils.putString(mActivity, ConstantModel.MSG_LOGIN_TYPE, "Guest Login");
                                PreferencesUtils.putString(mActivity, Constants.USER_GUEST_FLAG, "YES");
                                PreferencesUtils.putString(mActivity, Constants.USER_BIND_FLAG, "YES");
                                PreferencesUtils.putString(mActivity, Constants.USER_API_TOKEN, result.getData().getApi_token());
                                PreferencesUtils.putString(mActivity, Constants.USER_LT_UID, result.getData().getLt_uid());
                                PreferencesUtils.putString(mActivity, Constants.USER_LT_UID_TOKEN, result.getData().getLt_uid_token());
                                LoginUIManager.getInstance().setResult(mData);
                                if (result.getData().getLt_type().equals("register")){
                                    FacebookUIEventManager.getInstance().register(mActivity, 2);
                                }
                                getProxyActivity().finish();
                            }


                        }

                        @Override
                        public void onFailed(String code) {
                            loginEnd();
                            loginFailed();
                        }

                        @Override
                        public void onParameterError(String result) {

                        }

                        @Override
                        public void onAlreadyBind() {

                        }
                    });
        }

    }

    /**
     * 返回
     */
    private void backLogin() {
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
                false, true);
        if (mActivity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            pop();
        }

    }


    /**
     * 登录失败
     */
    private void loginFailed() {
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
        data.setBind(false);
        getProxyActivity().addFragment(LoginFailedFragment.newInstance(data),
                false,
                true);
        if (mActivity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            pop();
        }
    }

    /**
     * 回调
     */
    public void setOnResultClick(OnResultClickListener listener) {
        mListener = listener;
    }

    /**
     * 登录
     */
    private void login() {
        mRytGuest.setVisibility(View.VISIBLE);
        AnimationUtils.changeUI(mActivity, mImgLeft, mImgRight);
        mTxtTitle.setText(getResources().getString(R.string.text_login));
        mTxtTips.setVisibility(View.VISIBLE);
        mTxtContent.setVisibility(View.INVISIBLE);
        mRytBottom.setVisibility(View.GONE);
    }

    /**
     * 登录
     */
    private void loginEnd() {
        mTxtTitle.setText(getResources().getString(R.string.text_sure_guest));
        mTxtTips.setVisibility(View.GONE);
        mTxtContent.setVisibility(View.VISIBLE);
        mRytGuest.setVisibility(View.GONE);
        mRytBottom.setVisibility(View.VISIBLE);
    }
}
