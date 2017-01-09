package com.metalarm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.metalarm.utils.MetalarmSync_Log;
import com.metalarm.utils.ParsedResponse;
import com.metalarm.utils.Soap;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

public class SettingActitivty extends BaseActivity {

    private CheckBox mCbSound, mCbVibration;
    private TextView mTvDine;
    private Button btnUploadLog;
    public String TAG = "yyy";
    private static final int REQUEST_WRITE_STORAGE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiing_actitivty);


        setup();

    }

    private void setup() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(SettingActitivty.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        btnUploadLog = (Button) findViewById(R.id.btnUploadLog);
        if (!hasPermission) {
            btnUploadLog.setError("Please tap on this button in order to grant write log permission.");
        }
        btnUploadLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPermission = (ContextCompat.checkSelfPermission(SettingActitivty.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                if (!hasPermission) {
                    ActivityCompat.requestPermissions(SettingActitivty.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_STORAGE);
                } else {
                    //  startLog();
                    uploadLog();
                }
            }
        });

        mTvDine = (TextView) findViewById(R.id.toolbar_done);
        mCbSound = (CheckBox) findViewById(R.id.ckSound);
        mCbVibration = (CheckBox) findViewById(R.id.ckVib);

        mCbSound.setChecked(mSessionManager.prefGetSound());
        mCbVibration.setChecked(mSessionManager.prefGetVib());


        mTvDine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mCbSound.isChecked()) {
                    mSessionManager.prefStoreSound(true);
                } else {
                    mSessionManager.prefStoreSound(false);
                }


                if (mCbVibration.isChecked()) {
                    mSessionManager.prefStoreVib(true);
                } else {
                    mSessionManager.prefStoreVib(false);
                }

                finish();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features what required the permission
                    Log.e("ttt", "if");
                    //uploadLog();
                } else {
                    Log.e("ttt", "else");
                    Toast.makeText(SettingActitivty.this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    btnUploadLog.setError("permission not granted. Tap again.");
                }
            }
        }

    }

    public void uploadLog() {
        File f = new File(MetalarmSync_Log.filePath, MetalarmSync_Log.file);
        new UploadLogFileTask(f.getAbsolutePath()).execute();
    }

    private class UploadLogFileTask extends AsyncTask<Void, Void, Void> {
        private boolean error;
        private String path;
        private String msg;
        private ProgressDialog progressDialog;

        UploadLogFileTask(String path) {
            super();
            this.path = path;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SettingActitivty.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                ParsedResponse p = Soap.apiUploadLogfile(SettingActitivty.this, path);
                error = p.error;
                if (error) {
                    msg = (String) p.o;
                }
            } catch (JSONException | IOException e) {
                error = true;
                msg = e.getMessage();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (!error) {
                Toast.makeText(SettingActitivty.this, "Log uploaded successfully.", Toast.LENGTH_SHORT).show();
                // clear log data if no error.
                MetalarmSync_Log.clearLogData();
            } else {
                if (TextUtils.isEmpty(msg))
                    msg = "Unable to upload log please try again later.";
                Toast.makeText(SettingActitivty.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

