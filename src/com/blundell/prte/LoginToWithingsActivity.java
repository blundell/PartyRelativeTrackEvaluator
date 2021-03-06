package com.blundell.prte;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.blundell.prte.base.PrteActivity;
import com.blundell.prte.domain.WithingsAcc;
import com.parse.ParseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginToWithingsActivity extends PrteActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_withings);

        new AsyncTask<String, Void, WithingsAcc>() {
            @Override
            protected WithingsAcc doInBackground(String... params) {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost request = new HttpPost("https://hackathon-api.withings.com/auth?action=login");

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("email", "brighton_11@withings.com"));
                    nameValuePairs.add(new BasicNameValuePair("password", "3CjPt9B4M2"));
//                    nameValuePairs.add(new BasicNameValuePair("email", "brighton_12@withings.com"));
//                    nameValuePairs.add(new BasicNameValuePair("password", "uA69sFtmLa"));
//                    nameValuePairs.add(new BasicNameValuePair("email", "brighton_13@withings.com"));
//                    nameValuePairs.add(new BasicNameValuePair("password", "3FLKAiWN0a"));
//                    nameValuePairs.add(new BasicNameValuePair("email", "brighton_14@withings.com"));
//                    nameValuePairs.add(new BasicNameValuePair("password", "evVP8W6lNS"));
//                    nameValuePairs.add(new BasicNameValuePair("email", "brighton_15@withings.com"));
//                    nameValuePairs.add(new BasicNameValuePair("password", "4rz6Ob28Rj"));
                    request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse httpResponse = httpClient.execute(request);

                    String jsonString = convertStreamToString(httpResponse.getEntity().getContent());
                    Log.d("MatchEvent", "withings login Response " + jsonString);

                    JSONObject jsonObject = new JSONObject(jsonString);

                    JSONObject body = jsonObject.getJSONObject("body");
                    String userId = body.getJSONArray("users").getJSONObject(0).getString("id");
                    String sessionId = body.getString("sessionid");
                    Log.d("MatchEvent", "Withing Login " + sessionId + "/" + userId);

                    return new WithingsAcc().setSessionId(sessionId).setUserId(userId);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            String convertStreamToString(InputStream is) {
                Scanner s = new Scanner(is).useDelimiter("\\A");
                return s.hasNext() ? s.next() : "";
            }

            @Override
            protected void onPostExecute(WithingsAcc withingsAcc) {
                super.onPostExecute(withingsAcc);
                if (withingsAcc == null) {
                    Toast.makeText(LoginToWithingsActivity.this, "Login with Withings failed", 1).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(LoginToWithingsActivity.this, "Login with Facebook first", 1).show();
                    return;
                }
                currentUser.put("WITHINGS_ACC", withingsAcc);
                currentUser.saveInBackground();

                Toast.makeText(LoginToWithingsActivity.this, "Withings Login SUCCESS", 0).show();
                finish();
            }
        }.execute("");
    }
}
