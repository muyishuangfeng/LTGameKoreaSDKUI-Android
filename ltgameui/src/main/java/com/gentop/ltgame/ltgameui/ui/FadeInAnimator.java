package com.gentop.ltgame.ltgameui.ui;

import android.os.Parcel;
import android.os.Parcelable;


import com.gentop.ltgame.ltgameui.R;

import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class FadeInAnimator extends FragmentAnimator implements Parcelable {

    public FadeInAnimator() {
        enter = R.anim.h_fragment_enter;
        exit = R.anim.h_fragment_exit;
        popEnter = R.anim.h_fragment_pop_enter;
        popExit = R.anim.h_fragment_pop_exit;
    }

    protected FadeInAnimator(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FadeInAnimator> CREATOR = new Creator<FadeInAnimator>() {
        @Override
        public FadeInAnimator createFromParcel(Parcel in) {
            return new FadeInAnimator(in);
        }

        @Override
        public FadeInAnimator[] newArray(int size) {
            return new FadeInAnimator[size];
        }
    };
}
