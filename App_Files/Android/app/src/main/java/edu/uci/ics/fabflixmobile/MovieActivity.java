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

public class MovieActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
//        Bundle bundle = getIntent().getExtras();
        edu.uci.ics.fabflixmobile.Movie movie = (edu.uci.ics.fabflixmobile.Movie) getIntent().getExtras().getSerializable("movie");
        ((TextView) findViewById(R.id.title)).setText(movie.getTitle());
        ((TextView) findViewById(R.id.year)).setText(movie.getYear().toString());
        ((TextView) findViewById(R.id.director)).setText(movie.getDirector());
        ((TextView) findViewById(R.id.genres)).setText(movie.getGenres());
        ((TextView) findViewById(R.id.stars)).setText(movie.getStars());

    }




}
