package com.metalarm.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.metalarm.R;
import com.metalarm.model.AdvertismentModel;
import com.metalarm.model.GetAllLineListModel;
import com.metalarm.model.LineModel;
import com.metalarm.model.LoginModel;
import com.metalarm.model.StationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by qtm-kaushik on 4/8/15.
 */
public class Soap {
    private static String TAG = "SOAP";
    private static final String CHARSET = "UTF-8";
    public static final String BASE_URL = "http://webcomservicesinc.com/metraApp/";
    public static final String BASE_URL_API = BASE_URL + "api/";

    private static String getSoapResponsePost(String requestURL, Uri.Builder postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(BASE_URL_API + requestURL);
            Log.d(TAG, url.toString());
            Log.d(TAG, postDataParams.toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(15000);

            // uri builder
            String query = postDataParams.build().getEncodedQuery();

            OutputStream os = httpURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, CHARSET));
            writer.write(query);
            writer.flush();
            writer.close();
            httpURLConnection.connect();

            int resCode = httpURLConnection.getResponseCode();

            if (resCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response = response + line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, response);
        return response;

    }


    private static String getSoapResponseByPost(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(BASE_URL_API + requestURL);
            Log.d(TAG, url.toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(15000);

            httpURLConnection.connect();

            int resCode = httpURLConnection.getResponseCode();

            if (resCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response = response + line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, response);
        return response;

    }

    public static ParsedResponse apiRegistration(Context context, String email, String password, String first_name, String last_name) throws JSONException {
        ParsedResponse p = new ParsedResponse();
        if (Utils.isNetworkAvailable(context)) {

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("email", email)
                    .appendQueryParameter("password", password)
                    .appendQueryParameter("first_name", first_name)
                    .appendQueryParameter("last_name", last_name);

            String res = getSoapResponsePost("user/register", builder);

            if (!TextUtils.isEmpty(res)) {
                JSONObject obj = new JSONObject(res);
                if (obj.getString("statuscode").equals("200")) {
                    p.error = false;
                    p.o = obj.getString("message");
                } else {
                    p.error = true;
                    p.o = obj.getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_prblm_loading_data);
            }

        } else {
            p.error = true;
            p.o = context.getString(R.string.err_no_internet);
        }

        return p;
    }

    public static ParsedResponse apiLogin(Context context, String deviceToken) throws JSONException {
        ParsedResponse p = new ParsedResponse();


        if (Utils.isNetworkAvailable(context)) {

            Uri.Builder builder = new Uri.Builder()
                    // .appendQueryParameter("email", email)
                    //.appendQueryParameter("password", password)
                    .appendQueryParameter("deviceToken", deviceToken)
                    .appendQueryParameter("device", "0");

            String res = getSoapResponsePost("/user/login", builder);

            if (!TextUtils.isEmpty(res)) {
                JSONObject obj = new JSONObject(res);
                if (obj.getString("statuscode").equals("200")) {
                    JSONObject objData = new JSONObject(res);
                    JSONObject objdata = objData.getJSONObject("data");
                    Gson gson = new Gson();
                    LoginModel model = new LoginModel();
                    model = gson.fromJson(objdata.toString(), LoginModel.class);
                    p.error = false;
                    p.o = model;

                } else {
                    p.error = true;
                    p.o = obj.getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_prblm_loading_data);
            }

        } else

        {
            p.error = true;
            p.o = context.getString(R.string.err_no_internet);
        }
        return p;
    }

    public static ParsedResponse apiLine(Context context) throws JSONException {
        ParsedResponse p = new ParsedResponse();


        if (Utils.isNetworkAvailable(context)) {


            String res = getSoapResponseByPost("line/list");
            if (!TextUtils.isEmpty(res)) {
                JSONObject obj = new JSONObject(res);
                if (obj.getString("statuscode").equals("200")) {
                    JSONObject objData = new JSONObject(res);
                    JSONArray arrData = objData.getJSONArray("data");
                    ArrayList<LineModel> arrList = new ArrayList<>();
                    Gson gson = new Gson();
                    for (int i = 0; i < arrData.length(); i++) {
                        JSONObject c = arrData.getJSONObject(i);
                        LineModel model = new LineModel();
                        model = gson.fromJson(c.toString(), LineModel.class);
                        arrList.add(model);
                    }
                    p.error = false;
                    p.o = arrList;

                } else {
                    p.error = true;
                    p.o = obj.getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_prblm_loading_data);
            }

        } else

        {
            p.error = true;
            p.o = context.getString(R.string.err_no_internet);
        }
        return p;
    }


    public static ParsedResponse apiStation(Context context, String line_id) throws JSONException {
        ParsedResponse p = new ParsedResponse();


        if (Utils.isNetworkAvailable(context)) {

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("line_id", line_id);


            String res = getSoapResponsePost("station/list", builder);

            if (!TextUtils.isEmpty(res)) {
                JSONObject obj = new JSONObject(res);
                if (obj.getString("statuscode").equals("200")) {
                    JSONObject objData = new JSONObject(res);
                    JSONArray arrData = objData.getJSONArray("data");
                    ArrayList<StationModel> arrList = new ArrayList<>();
                    Gson gson = new Gson();
                    for (int i = 0; i < arrData.length(); i++) {
                        JSONObject c = arrData.getJSONObject(i);
                        StationModel model = new StationModel();
                        model = gson.fromJson(c.toString(), StationModel.class);
                        arrList.add(model);
                    }
                    p.error = false;
                    p.o = arrList;

                } else {
                    p.error = true;
                    p.o = obj.getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_prblm_loading_data);
            }

        } else

        {
            p.error = true;
            p.o = context.getString(R.string.err_no_internet);
        }
        return p;
    }

    public static ParsedResponse apiAllStation(Context context) throws JSONException {
        ParsedResponse p = new ParsedResponse();
        if (Utils.isNetworkAvailable(context)) {


            String res = getSoapResponseByPost("line/getAlllist/");

            if (!TextUtils.isEmpty(res)) {
                JSONObject obj = new JSONObject(res);
                if (obj.getString("statuscode").equals("200")) {
                    JSONObject objData = new JSONObject(res);
                    JSONArray arrData = objData.getJSONArray("data");
                    ArrayList<GetAllLineListModel> arrList = new ArrayList<>();
                    Gson gson = new Gson();
                    for (int i = 0; i < arrData.length(); i++) {
                        JSONObject c = arrData.getJSONObject(i);
                        GetAllLineListModel model = new GetAllLineListModel();
                        model = gson.fromJson(c.toString(), GetAllLineListModel.class);
                        arrList.add(model);
                    }
                    p.error = false;
                    p.o = arrList;

                } else {
                    p.error = true;
                    p.o = obj.getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_prblm_loading_data);
            }

        } else

        {
            p.error = true;
            p.o = context.getString(R.string.err_no_internet);
        }
        return p;
    }

    public static ParsedResponse apiAdvertisement(Context context, String station_id, String current_time) throws JSONException {
        ParsedResponse p = new ParsedResponse();
        if (Utils.isNetworkAvailable(context)) {

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("station_id", station_id)
                    .appendQueryParameter("current_time", current_time);

            String res = getSoapResponsePost("advertisment/list", builder);

            if (!TextUtils.isEmpty(res)) {
                JSONObject obj = new JSONObject(res);
                if (obj.getString("statuscode").equals("200")) {
                    JSONObject objData = new JSONObject(res);
                    JSONArray arrData = objData.getJSONArray("data");
                    ArrayList<AdvertismentModel> arrList = new ArrayList<>();
                    Gson gson = new Gson();
                    for (int i = 0; i < arrData.length(); i++) {
                        JSONObject c = arrData.getJSONObject(i);
                        AdvertismentModel model = new AdvertismentModel();
                        model = gson.fromJson(c.toString(), AdvertismentModel.class);
                        arrList.add(model);
                    }
                    p.error = false;
                    p.o = arrList;

                } else {
                    p.error = true;
                    p.o = obj.getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_prblm_loading_data);
            }

        } else

        {
            p.error = true;
            p.o = context.getString(R.string.err_no_internet);
        }
        return p;
    }

    public static ParsedResponse apiAddAlarmNotification(Context context, String user_id, String station_id, String timestamp) throws JSONException {
        ParsedResponse p = new ParsedResponse();
        if (Utils.isNetworkAvailable(context)) {

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("user_id", user_id)
                    .appendQueryParameter("device_token", new SessionManager(context).getDeviceToken())
                    .appendQueryParameter("device", "0")
                    .appendQueryParameter("station_id", station_id)
                    .appendQueryParameter("timestamp", timestamp);

            String res = getSoapResponsePost("alaram/dataAdd", builder);

            if (!TextUtils.isEmpty(res)) {
                JSONObject obj = new JSONObject(res);
                if (obj.getString("statuscode").equals("200")) {
                    p.error = false;
                    p.o = obj.getString("message");
                } else {
                    p.error = true;
                    p.o = obj.getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_prblm_loading_data);
            }

        } else {
            p.error = true;
            p.o = context.getString(R.string.err_no_internet);
        }

        return p;
    }

    public static ParsedResponse apiAddRemoveAlarm(Context context, String user_id, String device_token, String station_id, String line_id, String status, String index) throws JSONException {
        ParsedResponse p = new ParsedResponse();
        if (Utils.isNetworkAvailable(context)) {

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("data_rows[" + index + "][user_id]", user_id)
                    .appendQueryParameter("data_rows[" + index + "][device_token]", device_token)
                    .appendQueryParameter("data_rows[" + index + "][line_id]", line_id)
                    .appendQueryParameter("data_rows[" + index + "][station_id]", station_id)
                    .appendQueryParameter("data_rows[" + index + "][status]", status);

            String res = getSoapResponsePost("alaram/logs", builder);

            if (!TextUtils.isEmpty(res)) {
                JSONObject obj = new JSONObject(res);
                if (obj.getString("statuscode").equals("200")) {
                    p.error = false;
                    p.o = obj.getString("message");
                } else {
                    p.error = true;
                    p.o = obj.getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_prblm_loading_data);
            }

        } else {
            p.error = true;
            p.o = context.getString(R.string.err_no_internet);
        }

        return p;
    }

    public static ParsedResponse apiAlarmHit(Context context, String user_id, String station_id, String line_id, String index, String rang_at) throws JSONException {
        ParsedResponse p = new ParsedResponse();
        if (Utils.isNetworkAvailable(context)) {

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("data_rows[" + index + "][user_id]", user_id)
                    .appendQueryParameter("data_rows[" + index + "][device_token]", new SessionManager(context).getDeviceToken())
                    .appendQueryParameter("data_rows[" + index + "][line_id]", line_id)
                    .appendQueryParameter("data_rows[" + index + "][station_id]", station_id)
                    .appendQueryParameter("data_rows[" + index + "][rang_at]", rang_at);

            String res = getSoapResponsePost("alaram/ranglogs", builder);

            if (!TextUtils.isEmpty(res)) {
                JSONObject obj = new JSONObject(res);
                if (obj.getString("statuscode").equals("200")) {
                    p.error = false;
                    p.o = obj.getString("message");
                } else {
                    p.error = true;
                    p.o = obj.getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_prblm_loading_data);
            }

        } else {
            p.error = true;
            p.o = context.getString(R.string.err_no_internet);
        }

        return p;
    }

    public static ParsedResponse apiUploadLogfile(Context context, String upload_file) throws JSONException, IOException {
        ParsedResponse p = new ParsedResponse();
        if (Utils.isNetworkAvailable(context)) {


            MultipartUtility multipart = new MultipartUtility(BASE_URL_API + "user/file", CHARSET);
            multipart.addFilePart("upload_file", new File(upload_file));
            String res = multipart.finish();

            if (res != null) {

                JSONObject obj = new JSONObject(res);

                if (obj.getString("status").equals("200")) {
                    p.error = false;
                    p.o = "Error";
                } else {
                    p.error = true;
                    p.o = "Error";
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_prblm_loading_data);
            }

        } else {
            p.error = true;
            p.o = context.getString(R.string.err_no_internet);
        }

        return p;
    }


}
