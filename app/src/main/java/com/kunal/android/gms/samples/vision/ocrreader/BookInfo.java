package com.kunal.android.gms.samples.vision.ocrreader;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kunal.android.gms.samples.vision.ocrreader.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class BookInfo extends AppCompatActivity {

    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    private BookAsyncTask bookAsyncTask;
    private String title="";
    private ImageView imageView;
    private TextView bookName;
    private TextView descText;
    private TextView authorText;
    private TextView ratingText;
    private ProgressBar progressBar;

    private String name="";
    private String author="";
    private String rating="";
    private String desc="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        bookAsyncTask = new BookAsyncTask();
        init();
        title = getIntent().getStringExtra("text");
        progressBar.setVisibility(View.VISIBLE);
    }

    private void init() {
        imageView = findViewById(R.id.book_image);
        bookName = findViewById(R.id.book_name);
        descText = findViewById(R.id.desc_text);
        authorText = findViewById(R.id.author);
        ratingText = findViewById(R.id.rating);
        bookAsyncTask = new BookAsyncTask();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(10);
        bookAsyncTask.execute();
    }

    class BookAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            Uri uri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter("q","intitle:"+title)
                    .build();
            URL url;

            try {
                url = new URL(uri.toString());
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder buffer = new StringBuilder();
                String line = reader.readLine();
                while(line != null){
                    buffer.append(line);
                    line = reader.readLine();
                }
                if(buffer.toString().equals("")){
                    Toast.makeText(BookInfo.this, "No Books Found", Toast.LENGTH_SHORT).show();
                }else {
                    JSONObject book = new JSONObject(buffer.toString());
                    JSONArray items = book.getJSONArray("items");
                        JSONObject first = items.getJSONObject(0);
                        JSONObject volumeInfo = first.getJSONObject("volumeInfo");
                        name = volumeInfo.getString("title");
                        JSONArray authors = volumeInfo.getJSONArray("authors");
                        author = authors.getString(0);
                        rating = volumeInfo.getString("averageRating");
                        desc = volumeInfo.getString("description");
                     }
                    }catch (Exception e){
                        return 1;
                    }
                    return null;
                }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressBar.setVisibility(View.GONE);
            if(name.equals(""))
                bookName.setText("N/A");
            else
                bookName.setText(name);
            if(desc.equals(""))
                descText.setText("N/A");
            else
                descText.setText(desc);
            if(author.equals(""))
                authorText.setText("N/A");
            else
                authorText.setText(author);
            if(rating.equals(""))
                ratingText.setText("N/A");
            else
                ratingText.setText(rating);
        }
    }
}
