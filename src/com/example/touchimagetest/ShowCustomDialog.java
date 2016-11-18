package com.example.touchimagetest;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.touchimagetest.utils.MyUtil;

public class ShowCustomDialog {
	private static ShowCustomDialog dialog;
	public static ShowCustomDialog getInstance() {
		if (dialog == null) {
			dialog = new ShowCustomDialog();
		}
		return dialog;
	}
	/**
	 * 得到自定义的progressDialog
	 * @param context
	 * @param msg
	 */
	public Dialog loadingDialog(Activity context, String message) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.progress_dialog, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		// main.xml中的ImageView
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.dialog_animation);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		if(TextUtils.isEmpty(message)){
			tipTextView.setVisibility(View.GONE);
		}else{
			tipTextView.setVisibility(View.VISIBLE);
			tipTextView.setText(message);// 设置加载信息
		}
		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

		int screenWidth = MyUtil.getPhoneWidth(context);
		int screenHeigh = MyUtil.getPhoneHeigh(context);
		int wdith = Math.min(screenWidth, screenHeigh) / 2;

//		loadingDialog.setCancelable(false);// 不可以用“返回键”取消
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(wdith, wdith));// 设置布局
		loadingDialog.setCanceledOnTouchOutside(false);
		return loadingDialog;

	}
}
