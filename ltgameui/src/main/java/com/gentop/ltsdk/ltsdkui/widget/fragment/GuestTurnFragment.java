package com.gentop.ltsdk.ltsdkui.widget.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gentop.ltgame.ltgameui.R;
import com.gentop.ltsdk.common.constant.Constants;
import com.gentop.ltsdk.common.impl.OnLoginSuccessListener;
import com.gentop.ltsdk.common.model.BaseEntry;
import com.gentop.ltsdk.common.model.ResultData;
import com.gentop.ltsdk.common.util.PreferencesUtils;
import com.gentop.ltsdk.guest.GuestManager;
import com.gentop.ltsdk.ltsdkui.base.BaseFragment;
import com.gentop.ltsdk.ltsdkui.impl.OnResultClickListener;
import com.gentop.ltsdk.ltsdkui.manager.LoginUIManager;
import com.gentop.ltsdk.ltsdkui.model.BundleData;

public class GuestTurnFragment extends BaseFragment implements View.OnClickListener {

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
    Button mBtnSwitch, mBtnBind;
    TextView mTxtContinue;


    public static GuestTurnFragment newInstance(BundleData data) {
        Bundle args = new Bundle();
        GuestTurnFragment fragment = new GuestTurnFragment();
        args.putSerializable(ARG_NUMBER, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getFragmentId() {
        return R.layout.fragment_guest_turn;
    }


    @Override
    protected void initView(View view) {
        mBtnSwitch = view.findViewById(R.id.btn_guest_turn_switch);
        mBtnSwitch.setOnClickListener(this);

        mBtnBind = view.findViewById(R.id.btn_guest_turn_bind);
        mBtnBind.setOnClickListener(this);

        mTxtContinue = view.findViewById(R.id.txt_guest_turn_continue);
        mTxtContinue.setOnClickListener(this);

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
        if (view.getId() == R.id.btn_guest_turn_switch) {//取消
            backLogin();
        } else if (view.getId() == R.id.btn_guest_turn_bind) {//绑定
            bind();
        } else if (view.getId() == R.id.txt_guest_turn_continue) {//继续
            GuestManager.guestLogin(mActivity, mServerTest, LTAppID, LTAppKey, mAdID, mPackageID,
                    new OnLoginSuccessListener() {
                        @Override
                        public void onSuccess(BaseEntry<ResultData> result) {
                            ResultData mData = new ResultData();
                            mData.setLt_uid(result.getData().getLt_uid());
                            mData.setLt_uid_token(result.getData().getLt_uid_token());
                            mData.setApi_token(result.getData().getApi_token());
                            if (mListener != null) {
                                mListener.onResult(mData);
                            }
                            PreferencesUtils.putString(mActivity, Constants.USER_GUEST_FLAG, "2");
                            PreferencesUtils.putString(mActivity, Constants.USER_API_TOKEN, result.getData().getApi_token());
                            PreferencesUtils.putString(mActivity, Constants.USER_LT_UID, result.getData().getLt_uid());
                            PreferencesUtils.putString(mActivity, Constants.USER_LT_UID_TOKEN, result.getData().getLt_uid_token());
                            getProxyActivity().finish();
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
                false,
                true);
        pop();

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
     * 绑定
     */
    private void bind() {
        if (findChildFragment(BindFragment.class) == null) {
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
            BindFragment fragment = BindFragment.newInstance(data);
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
     * 回调
     */
    public void setOnResultClick(OnResultClickListener listener) {
        mListener = listener;
    }


}
