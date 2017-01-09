package com.metalarm;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.metalarm.model.LoginModel;
import com.metalarm.utils.ParsedResponse;
import com.metalarm.utils.Soap;

import org.json.JSONException;

public class LoginPageActivity extends BaseActivity {


    Button btnLogin;
    LinearLayout btnSignUp;
    EditText edEmail, edPassword;
    String mail, passwrod;
    //  ArrayList<LoginModel> arrList = new ArrayList<>();
    LoginModel loginModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        isLoggedIn();

        btnSignUp = (LinearLayout) findViewById(R.id.btnSignUp);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mail = edEmail.getText().toString();
                passwrod = edPassword.getText().toString();

                //checkValidation();


            }
        });

        edEmail = (EditText) findViewById(R.id.edEmail);
        edPassword = (EditText) findViewById(R.id.edPassword);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                finish();

            }
        });
    }

    private void isLoggedIn() {

        if (mSessionManager.IsLoggedIn()) {
            Log.e(getLocalClassName(), "UserLoggedIn");
            startActivity(new Intent(getApplicationContext(), ListActivity.class));

        } else {
            Log.e(getLocalClassName(), "Not LogIN");


        }

    }

    public class apiLogin extends AsyncTask<Void, Void, Void> {

        boolean error;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                ParsedResponse p = Soap.apiLogin(LoginPageActivity.this, mSessionManager.getDeviceToken());
                error = p.error;
                if (!error) {
                    loginModel = (LoginModel) p.o;

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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();
            if (!error) {
                mSessionManager.login(loginModel);
                Toast.makeText(LoginPageActivity.this, "Success", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(i);
                finish();
            } else {
                Log.e(getLocalClassName(), "" + msg);
            }
        }
    }

    private void checkValidation() {

        if (edEmail.getText().toString().length() == 0 || edPassword.getText().toString().length() == 0) {
            showDialog("Please enter all fields");
        } else if (!isValidEmail(edEmail.getText().toString())) {
            showDialog("Please enter valid email");
        } else {

        }
    }

}
