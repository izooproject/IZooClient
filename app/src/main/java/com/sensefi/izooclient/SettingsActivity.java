package com.sensefi.izooclient;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sensefi.izooclient.database.DatabaseHelper;
import com.sensefi.izooclient.view.SettingsView;

import java.util.ArrayList;
import java.util.List;



public class SettingsActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    private SaveTask mAuthTask = null;

    // UI references.
    private EditText mIpAddress;
    private EditText mPort;
    private View mProgressView;
    private View mSettingsFormView;
    Intent intent;
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        databaseHelper = new DatabaseHelper(this);
        mIpAddress = (EditText) findViewById(R.id.ipAddress);
        mPort = (EditText) findViewById(R.id.port);
        Button mSaveButton = (Button) findViewById(R.id.save_button);

        setSettingsValue();

        mSaveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(view.getContext(), LoginActivity.class);
                attemptSave();
            }
        });

        mSettingsFormView = findViewById(R.id.settings_form);
        mProgressView = findViewById(R.id.save_progress);
    }

    private void setSettingsValue() {
        List<SettingsView> settingsViewList = databaseHelper.getAllStudentsList();
        SettingsView settingsView = settingsViewList.get(0);
        mIpAddress.setText(settingsView.getIpAddress());
        mPort.setText(settingsView.getPort());
    }
    private void attemptSave() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mIpAddress.setError(null);
        mPort.setError(null);


        String ipAddress = mIpAddress.getText().toString();
        String port = mPort.getText().toString();

        boolean cancel = false;
        View focusView = null;

        Log.d("ipAddress", ipAddress);
        Log.d("port", port);

        if (TextUtils.isEmpty(port) ) {
            mPort.setError(getString(R.string.error_field_required));
            focusView = mPort;
            cancel = true;
        }


        if (TextUtils.isEmpty(ipAddress)) {
            mIpAddress.setError(getString(R.string.error_field_required));
            focusView = mIpAddress;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {

            showProgress(true);
            mAuthTask = new SaveTask(ipAddress, port);
            mAuthTask.execute((Void) null);
        }
    }



    /**
     * Shows the progress UI and hides the Settings form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSettingsFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSettingsFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSettingsFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSettingsFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }




    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class SaveTask extends AsyncTask<Void, Void, Boolean> {

        private final String mIpAddress;
        private final String mPort;

        SaveTask(String ipAddress, String port) {
            mIpAddress = ipAddress;
            mPort = port;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            SettingsView settingsView = new SettingsView();
            settingsView.setIpAddress(mIpAddress);
            settingsView.setPort(mPort);
            Log.d("DB Status", "Pre Calling DB");
            databaseHelper.deleteAllEntry();
            return databaseHelper.addSettingsDetail(settingsView);

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Log.d("DB Status", "DataBase Insertion Success");
                Toast.makeText(getApplicationContext(), "Settings Added Succesfully", Toast.LENGTH_LONG).show();
                startActivity(intent);
            } else {
                Log.d("DB Status", "DataBase Problem");
                Toast.makeText(getApplicationContext(), "Techinical Error!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

