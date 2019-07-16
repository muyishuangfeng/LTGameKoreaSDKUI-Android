package com.gentop.ltsdk.ltsdkui.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.gentop.ltgame.ltgameui.R;


public class GeneralCenterDialog extends AlertDialog implements View.OnClickListener {

    private CharSequence mContent;
    private OnGeneralClickListener mListener;

    GeneralCenterDialog(@NonNull Context context) {
        super(context, R.style.ActionSheet);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_general_center_layout);
        //设置背景透明，不然会出现白色直角问题
        Window window = getWindow();
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        //初始化布局控件
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        //对话框描述信息
        TextView mTxtContent = findViewById(R.id.txt_general_content);
        //确定
        TextView mTxtSure = findViewById(R.id.txt_general_sure);
        //******************通用设置*********************//
        //设置标题、描述及确定按钮的文本内容
        assert mTxtContent != null;
        mTxtContent.setText(mContent);
        assert mTxtSure != null;
        mTxtSure.setOnClickListener(this);

    }


    /**
     * 内容
     */
    public void setContent(CharSequence mContent) {
        this.mContent = mContent;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txt_general_sure) {//确定
            if (mListener != null) {
                mListener.OnGeneralClick();
            }
            dismiss();
        }

    }

    /**
     * 接口
     */
    public interface OnGeneralClickListener {
        void OnGeneralClick();
    }

    public void setOnGeneralClickListener(OnGeneralClickListener confirmListener) {
        this.mListener = confirmListener;
    }

}
