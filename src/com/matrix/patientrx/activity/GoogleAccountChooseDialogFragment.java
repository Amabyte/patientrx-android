package com.matrix.patientrx.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.matrix.patientrx.R;

public class GoogleAccountChooseDialogFragment extends DialogFragment implements
		OnItemClickListener {

	private GoogleLoginActivity activity;
	private ListView accountListView;
	private boolean isLoginOk = false;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		activity = (GoogleLoginActivity) getActivity();
		AlertDialog b = new AlertDialog.Builder(getActivity())
				.setTitle("Google login")
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								activity.accountListCancelled();
							}
						}).create();
		accountListView = (ListView) getActivity().getLayoutInflater().inflate(
				R.layout.account_list, null);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, activity.getAccountNames());
		accountListView.setAdapter(adapter);
		accountListView.setOnItemClickListener(this);
		b.setView(accountListView);
		return b;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		isLoginOk = true;
		activity.selectedAccount(activity.getAccountNames()[(int) id]);
		dismiss();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!isLoginOk)
			activity.accountListCancelled();
	}
}