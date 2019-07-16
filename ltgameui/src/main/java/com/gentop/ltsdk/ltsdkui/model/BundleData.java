package com.gentop.ltsdk.ltsdkui.model;

import java.io.Serializable;

public class BundleData implements Serializable {

    private String privacyUrl;
    private String agreementUrl;
    private String googleClientID;
    private String baseURL;
    private String LTAppID;
    private String LTAppKey;
    private String mAdID;
    private String mPackageID;
    private String mFacebookID;
    private boolean mLoginOut;

    public String getPrivacyUrl() {
        return privacyUrl;
    }

    public void setPrivacyUrl(String privacyUrl) {
        this.privacyUrl = privacyUrl;
    }

    public String getAgreementUrl() {
        return agreementUrl;
    }

    public void setAgreementUrl(String agreementUrl) {
        this.agreementUrl = agreementUrl;
    }

    public String getGoogleClientID() {
        return googleClientID;
    }

    public void setGoogleClientID(String googleClientID) {
        this.googleClientID = googleClientID;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getLTAppID() {
        return LTAppID;
    }

    public void setLTAppID(String LTAppID) {
        this.LTAppID = LTAppID;
    }

    public String getLTAppKey() {
        return LTAppKey;
    }

    public void setLTAppKey(String LTAppKey) {
        this.LTAppKey = LTAppKey;
    }

    public String getmAdID() {
        return mAdID;
    }

    public void setmAdID(String mAdID) {
        this.mAdID = mAdID;
    }

    public String getmPackageID() {
        return mPackageID;
    }

    public void setmPackageID(String mPackageID) {
        this.mPackageID = mPackageID;
    }

    public String getmFacebookID() {
        return mFacebookID;
    }

    public void setmFacebookID(String mFacebookID) {
        this.mFacebookID = mFacebookID;
    }

    public boolean ismLoginOut() {
        return mLoginOut;
    }

    public void setmLoginOut(boolean mLoginOut) {
        this.mLoginOut = mLoginOut;
    }
}
