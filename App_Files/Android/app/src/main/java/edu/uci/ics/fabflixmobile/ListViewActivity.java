package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;

public class ListViewActivity extends Activity {
    private Integer page;
    private String response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);


        Bundle bundle = getIntent().getExtras();
        this.page = (Integer)bundle.get("page");
        this.response = (String)bundle.get("response");
        try {
            JSONArray jArr = new JSONArray(this.response);
            final ArrayList<Movie> movies = new ArrayList<>();
            Integer iterCount = Math.min(10*this.page,jArr.length());
            Integer start = (this.page-1)*10;
            for(int i = start ; i < iterCount; i++){
                JSONObject mov = jArr.getJSONObject(i);
                //    public Movie(String title, int year, String director, String genres, String stars) {
                String id = mov.getString("movieId");
                String title = mov.getString("movieTitle");
                Integer year = Integer.parseInt(mov.getString("movieYear"));
                String director = mov.getString("movieDirector");
                String genres = mov.getString("genres");
                String stars = mov.getString("stars");
                movies.add(new Movie(id,title,year,director,genres,stars));
                MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);

                ListView listView = (ListView)findViewById(R.id.list);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Movie movie = movies.get(position);
                        goToSingleMovie(view,movie);
                    }
                });

            }
        } catch (JSONException e) {
            Log.d("MYAPP", "unexpected JSON exception", e);
            // Do something to recover ... or kill the app.
        }

    }

    public void goToSingleMovie(View view,Movie movie) {
        Intent goToIntent = new Intent(this,  MovieActivity.class);
        goToIntent.putExtra("movie", movie);


        startActivity(goToIntent);
    }

    public void goToMovieListNext(View view) {
        Intent goToIntent = new Intent(this,  ListViewActivity.class);

        goToIntent.putExtra("response", this.response);
        goToIntent.putExtra("page", this.page+1);

        startActivity(goToIntent);
    }

    public void goToMovieListPrev(View view) {
        if(this.page != 1) {
            Intent goToIntent = new Intent(this, ListViewActivity.class);

            goToIntent.putExtra("response", this.response);
            goToIntent.putExtra("page", this.page-1);

            startActivity(goToIntent);
        }
    }
}
