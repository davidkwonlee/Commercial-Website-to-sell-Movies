package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.graphics.Movie;
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

import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

    }


    public void sendSearch(final View view) {

        // Post request form data
        final Map<String, String> params = new HashMap<String, String>();
        String movieSearchName = ((EditText) findViewById(R.id.movie_search)).getText().toString();

        params.put("title", movieSearchName);
        params.put("year", "");
        params.put("director", "");
        params.put("starName", "");
        // no user is logged in, so we must connect to the server

        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;



        //search=true&title=hello+world&year=&director=&star_name=
        final StringRequest searchResults = new StringRequest(Request.Method.POST, "https://18.217.157.13:8443/project1/api/search",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response",response);
                        goToMovieList(view,response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }  // HTTP POST Form Data
        };

        queue.add(searchResults);
    }

    public void goToMovieList(View view,String response) {
        Intent goToIntent = new Intent(this,  ListViewActivity.class);

        goToIntent.putExtra("response", response);
        goToIntent.putExtra("page", 1);

        startActivity(goToIntent);
    }
}
