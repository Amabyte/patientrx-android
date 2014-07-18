package com.matrix.patientrx.activity;

import java.io.IOException;

import org.apache.http.Header;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.gson.Gson;
import com.matrix.patientrx.R;
import com.matrix.patientrx.constants.Constants;
import com.matrix.patientrx.http.ProgressJsonHttpResponseHandler;
import com.matrix.patientrx.models.LoginResponse;
import com.matrix.patientrx.utils.Preference;
import com.matrix.patientrx.utils.Utils;

public class GoogleLoginActivity extends Activity {
	private static final String TAG = "GoogleLoginActivity";
	private static final int REQ_SIGN_IN_REQUIRED = 1;
	private AccountManager mAccountManager;
	private String mAccountName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_login);
		mAccountManager = AccountManager.get(this);
		if (getAccountNames().length == 1) {
			selectedAccount(getAccountNames()[0]);
		} else {
			new GoogleAccountChooseDialogFragment().show(getFragmentManager(),
					"google_account_chooser");
		}
	}

	public String[] getAccountNames() {
		mAccountManager = AccountManager.get(this);
		Account[] accounts = mAccountManager
				.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		String[] names = new String[accounts.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = accounts[i].name;
		}
		return names;
	}

	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {

		if (responseCode == RESULT_OK) {
			// We had to sign in - now we can finish off the token request.
			new RetrieveTokenTask().execute(mAccountName);
		}

	}

	private void saveUserDetails(LoginResponse loginResponse) {
		Preference.setString(Constants.USER_NAME, loginResponse.getPatient()
				.getName());
		Preference.setString(Constants.EMAIL_ID, loginResponse.getPatient()
				.getEmail());
		Preference
				.setInt(Constants.USER_ID, loginResponse.getPatient().getId());

	}

	private class RetrieveTokenTask extends AsyncTask<String, Void, String> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(GoogleLoginActivity.this);
			progressDialog.setTitle("Login");
			progressDialog.setMessage("Google Login");
			progressDialog.setCancelable(false);
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String accountName = params[0];
			String scopes = "oauth2:profile email";
			String token = null;
			try {
				token = GoogleAuthUtil.getToken(getApplicationContext(),
						accountName, scopes);

				Log.e("token", "Token:" + token);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			} catch (UserRecoverableAuthException e) {
				startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
			} catch (GoogleAuthException e) {
				Log.e(TAG, e.getMessage());
			}
			return token;
		}

		@Override
		protected void onPostExecute(String token) {
			super.onPostExecute(token);
			if (token == null)
				return;
			progressDialog.dismiss();
			Utils.loginToPatientRx(Constants.GOOGLE_LOGIN, token,
					new ProgressJsonHttpResponseHandler(
							GoogleLoginActivity.this) {

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								org.json.JSONObject response) {
							String sessionId = "";
							LoginResponse loginResponse = new LoginResponse();
							Gson gson = new Gson();

							loginResponse = gson.fromJson(response.toString(),
									loginResponse.getClass());

							for (Header header : headers) {
								// store the Set-Cookie last value
								if (header.getName().equalsIgnoreCase(
										"Set-Cookie")) {
									sessionId = header.getValue();
								}
								// TODO modify this logic
							}
							// save the session id
							Preference.setString(Constants.SESSION_ID,
									sessionId);
							saveUserDetails(loginResponse);
							startActivity(new Intent(GoogleLoginActivity.this,
									HomeScreenActivity.class));
							finish();
						}

						@Override
						public void onFailure(int statusCode,
								org.apache.http.Header[] headers,
								java.lang.Throwable e,
								org.json.JSONObject errorResponse) {
							// Response failed :(
							String err = "onFailure status code:" + statusCode
									+ " response body:" + errorResponse
									+ " error:" + e + " headers:" + headers;
							Log.d("err", err);
							Toast.makeText(getApplicationContext(), err,
									Toast.LENGTH_LONG).show();

						}

					});
		}
	}

	public void accountListCancelled() {
		finish();
	}

	public void selectedAccount(String account) {
		mAccountName = account;
		new RetrieveTokenTask().execute(mAccountName);
	}
}
