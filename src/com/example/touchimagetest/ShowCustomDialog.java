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
	 * �õ��Զ����progressDialog
	 * @param context
	 * @param msg
	 */
	public Dialog loadingDialog(Activity context, String message) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.progress_dialog, null);// �õ�����view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// ���ز���
		// main.xml�е�ImageView
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// ��ʾ����
		// ���ض���
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.dialog_animation);
		// ʹ��ImageView��ʾ����
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		if(TextUtils.isEmpty(message)){
			tipTextView.setVisibility(View.GONE);
		}else{
			tipTextView.setVisibility(View.VISIBLE);
			tipTextView.setText(message);// ���ü�����Ϣ
		}
		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// �����Զ�����ʽdialog

		int screenWidth = MyUtil.getPhoneWidth(context);
		int screenHeigh = MyUtil.getPhoneHeigh(context);
		int wdith = Math.min(screenWidth, screenHeigh) / 2;

//		loadingDialog.setCancelable(false);// �������á����ؼ���ȡ��
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(wdith, wdith));// ���ò���
		loadingDialog.setCanceledOnTouchOutside(false);
		return loadingDialog;

	}
}
