package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import org.json.*;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }


    public void login(final View view) {

        // Post request form data
        final Map<String, String> params = new HashMap<String, String>();

        String username = ((EditText) findViewById(R.id.login_name)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();
        params.put("username", username);
        params.put("password", password);
        // no user is logged in, so we must connect to the server

        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        // 10.0.2.2 is the host machine when running the android emulator


        final StringRequest loginRequest = new StringRequest(Request.Method.POST, "https://18.217.157.13:8443/project1/api/android-login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response", response);

                        try {
                            JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("status");
                            if(status.equals("success")){
                                goToSearch(view);
                            }else{
                                ((TextView) findViewById(R.id.error_info)).setText(response);
                            }
                        } catch (JSONException e) {
                            Log.d("MYAPP", "unexpected JSON exception", e);
                            // Do something to recover ... or kill the app.
                        }
                        // Add the request to the RequestQueue.
//                        queue.add(afterLoginRequest);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }  // HTTP POST Form Data
        };
        queue.add(loginRequest);
//        SafetyNet.getClient(this).verifyWithRecaptcha("6Lf0a1wUAAAAAOQNisxxC32Hn6fb7G9JtN6SyurK")
//                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
//                    @Override
//                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
//                        if (!response.getTokenResult().isEmpty()) {
//                            // Add the request to the RequestQueue.
//                            params.put("g-recaptcha-response", response.getTokenResult());
//                            queue.add(loginRequest);
//                        }
//                    }
//                })
//                .addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        if (e instanceof ApiException) {
//                            ApiException apiException = (ApiException) e;
//                            Log.d("Login", "Error message: " +
//                                    CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
//                        } else {
//                            Log.d("Login", "Unknown type of error: " + e.getMessage());
//                        }
//                    }
//                });

    }

    public void goToSearch(View view) {
        Intent goToIntent = new Intent(this, SearchActivity.class);


        startActivity(goToIntent);
    }

}
