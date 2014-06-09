package com.matrix.patientrx.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.matrix.patientrx.R;

/**
 * The Class DialogManager.
 */
public class DialogManager {

	/** The dialog. */
	private AlertDialog dialog = null;

	/** The progress dialog. */
	private ProgressDialog progressDialog = null;

	/**
	 * Show progress dialog.
	 * 
	 * @param context
	 *            the context
	 * @param msg
	 *            the msg
	 */
	public void showProgressDialog(final Context context, String msg) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setCancelable(false);
		}
		if (!progressDialog.isShowing()) {
			progressDialog.setMessage(msg);
			progressDialog.show();
		}

	}

	/**
	 * Show progress dialog.
	 * 
	 * @param context
	 *            the context
	 * @param title
	 *            the title
	 * @param msg
	 *            the msg
	 */
	public void showProgressDialog(final Context context, String title,
			String msg) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setCancelable(false);
		}
		if (!progressDialog.isShowing()) {
			progressDialog.setTitle(title);
			progressDialog.setMessage(msg);
			progressDialog.show();
		}

	}

	/**
	 * Removes the progress dialog.
	 */
	public void removeProgressDialog() {
		if (null != progressDialog && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	/**
	 * Show alert dialog.
	 * 
	 * @param context
	 *            the context
	 * @param title
	 *            the title
	 * @param msg
	 *            the msg
	 * @param btnText
	 *            the btn text
	 * @param onClickListener
	 *            the on click listener
	 */
	public void showAlertDialog(final Context context, String title,
			String msg, String btnText,
			DialogInterface.OnClickListener onClickListener) {

		AlertDialog.Builder alertErrorBuilder = new AlertDialog.Builder(context);
		alertErrorBuilder.setMessage(msg).setCancelable(false).setTitle(title)
				.setNeutralButton(btnText, onClickListener);
		dialog = alertErrorBuilder.create();
		dialog.show();

	}

	/**
	 * Show alert dialog.
	 * 
	 * @param context
	 *            the context
	 * @param title
	 *            the title
	 * @param msg
	 *            the msg
	 * @param positiveBtnName
	 *            the positive btn name
	 * @param negativeBtnName
	 *            the negative btn name
	 * @param postiveBtnListener
	 *            the postive btn listener
	 * @param negativeBtnListener
	 *            the negative btn listener
	 */
	public void showAlertDialog(final Context context, String title,
			String msg, String positiveBtnName, String negativeBtnName,
			DialogInterface.OnClickListener postiveBtnListener,
			DialogInterface.OnClickListener negativeBtnListener) {

		AlertDialog.Builder alertErrorBuilder = new AlertDialog.Builder(context);
		alertErrorBuilder.setMessage(msg).setCancelable(false).setTitle(title);
		alertErrorBuilder.setNegativeButton(negativeBtnName,
				negativeBtnListener);
		alertErrorBuilder
				.setPositiveButton(positiveBtnName, postiveBtnListener);
		dialog = alertErrorBuilder.create();
		dialog.show();

	}

	public void showNetworkErrorDialog(final Context context,
			DialogInterface.OnClickListener onClickListener) {

		AlertDialog.Builder alertErrorBuilder = new AlertDialog.Builder(context);
		alertErrorBuilder
				.setMessage(context.getString(R.string.text_network_error))
				.setCancelable(false)
				.setTitle(context.getString(R.string.text_alert))
				.setNeutralButton(context.getString(R.string.text_ok),
						onClickListener);
		dialog = alertErrorBuilder.create();
		dialog.show();

	}

	/**
	 * Dismiss alert dialog.
	 */
	public void dismissAlertDialog() {
		if (null != dialog && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
	
}

