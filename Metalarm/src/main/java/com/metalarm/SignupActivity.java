package com.metalarm;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.metalarm.model.LoginModel;
import com.metalarm.utils.ParsedResponse;
import com.metalarm.utils.Soap;
import com.metalarm.utils.Utils;

import org.json.JSONException;

public class SignupActivity extends BaseActivity {

    EditText edEmail, edPass, edLastName, edFirstname;
    Button btnSignUp;
    String email, pass, fname, lname;
    Toolbar toolbar;
    TextView tvToolbarTitle;
    CheckBox cbPassword;
    ImageView ivShowpass;
    int p = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setUp();
    }

    private void setUp() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        tvToolbarTitle.setText("Signup");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        edEmail = (EditText) findViewById(R.id.edEmail);
        edPass = (EditText) findViewById(R.id.edPassword);
        edLastName = (EditText) findViewById(R.id.edlastName);
        edFirstname = (EditText) findViewById(R.id.edFirstname);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        ivShowpass = (ImageView) findViewById(R.id.ivShowpass);
        ivShowpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (p == 0) {
                    edPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    edPass.setSelection(edPass.getText().length());
                    p = 1;
                } else if (p == 1) {
                    edPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    edPass.setSelection(edPass.getText().length());
                    p = 0;
                }

            }
        });
       /* cbPassword = (CheckBox) findViewById(R.id.cbPasssword);
        cbPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {
                    edPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    edPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });*/

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edEmail.getText().toString();
                pass = edPass.getText().toString();
                fname = edFirstname.getText().toString();
                lname = edLastName.getText().toString();

                checkValidation();


            }
        });

    }

    private void checkValidation() {

        if (edEmail.getText().toString().length() == 0 || edPass.getText().toString().length() == 0 ||
                edFirstname.getText().toString().length() == 0 ||
                edLastName.getText().toString().length() == 0) {
            if (edEmail.getText().toString().length() == 0) {
                showDialog("Please enter email addresss");
            } else if (!isValidEmail(edEmail.getText().toString())) {
                showDialog("Please enter valid email");
            } else if (edPass.getText().toString().length() == 0) {
                showDialog("Please enter password");
            } else if (edPass.getText().toString().length() < 6) {
                showDialog("Plassword must be 6 characters long");
            } else if (edFirstname.getText().toString().length() == 0) {
                showDialog("Please enter firstName");
            } else if (edLastName.getText().toString().length() == 0) {
                showDialog("Please enter LastName");
            }

        } else {
            new apiregister().execute();

        }
    }

    private class apiregister extends AsyncTask<Void, Void, Void> {
        boolean error;
        String msg;
        LoginModel model;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();
            Log.e(getLocalClassName(), "" + msg);
            if (!error) {
                startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));
                finish();
                Log.e(getLocalClassName(), "" + error);
            } else {
                showDialog(msg);
            }


        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                ParsedResponse p = Soap.apiRegistration(SignupActivity.this,email, pass, fname, lname);
                error = p.error;
                if (!error) {
                    msg = (String) p.o;

                } else {
                    msg = (String) p.o;

                }


            } catch (JSONException e) {
                e.printStackTrace();

                error = true;
                msg = e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {

            mProgressDialog.show();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
