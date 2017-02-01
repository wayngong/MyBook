package com.example.android.mybook;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_CODE_INTERNET = 200;

    final String GOOGLE_BOOK_URL = "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=10";

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BookAsyncTask task = new BookAsyncTask();
        task.execute();
    }

    private class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {
        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {
            URL url = createUrl(GOOGLE_BOOK_URL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                // Make HttpURLConnection to get JSON Response
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
                e.printStackTrace();
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object
            ArrayList<Book> booksArrayList = extractFeatureFromJson(jsonResponse);

            // Check if Book ArrayList is null
            // If so, initialize it
            if(booksArrayList==null){
                booksArrayList = new ArrayList<>();
            }
            // Check If Book ArrayList is Empty
            // If so, create One Default Book for user
            if(booksArrayList.size()==0){
                // Set Notice Info
                String notice = getResources().getString(R.string.error_notice_title);
                booksArrayList.add(new Book(notice, null, null));

                // Set Default Title
                String tempTitle = getResources().getString(R.string.default_book_title);
                // Set Default Author
                ArrayList<String> tempAuthor = new ArrayList<>();
                tempAuthor.add(getResources().getString(R.string.default_book_author));
                // Set Default Publish Time
                String tempPublisTime = getResources().getString(R.string.default_book_publish_time);
                // Set Default Value for Book
                booksArrayList.add(new Book(tempTitle, tempAuthor, tempPublisTime));
            }
            return booksArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> bookArrayList) {
            // update UI
            ListView listView = (ListView) findViewById(R.id.book_list);
            final BookListAdapter bookListAdapter = new BookListAdapter(MainActivity.this, bookArrayList);
            listView.setAdapter(bookListAdapter);
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_GRANTED) {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(5000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == REQUEST_CODE_INTERNET) {
                    // Successful Response
                    Log.i("Response Code", String.valueOf(REQUEST_CODE_INTERNET));
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromInputStream(inputStream);
                } else {
                    // when Internet Response Code is not correct,
                    // use null
                    jsonResponse = null;
                }
            }
            return jsonResponse;
        }

        /* Download Image */
        private Bitmap downloadImage(URL url) {
            HttpURLConnection urlConnection = null;
            Bitmap bitmap = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setReadTimeout(100000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // Connection Okay
                    // Start Downloading
                    InputStream inputStream = urlConnection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
            } catch (IOException e) {
                Log.i(LOG_TAG, "Error Downloading Bitmap");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return bitmap;
        }

        /*
        * Extract Feature from Json Response
        * */
        private ArrayList<Book> extractFeatureFromJson(String bookJSON) {
            if (TextUtils.isEmpty(bookJSON)) {
                return null;
            }
            ArrayList<Book> booksArrayList = new ArrayList<>();

            try {
                JSONObject baseJSONResponse = new JSONObject(bookJSON);
                JSONArray booksArray = baseJSONResponse.getJSONArray("items");

                // if there are results in JSON array
                for (int i = 0; i < booksArray.length(); i++) {
                    // Create Book class for Each Book
                    // Set Info for Each Book Class
                    JSONObject currentBook = booksArray.getJSONObject(i);
                    JSONObject bookInfo = currentBook.getJSONObject("volumeInfo");
                    String bookTitle = bookInfo.getString("title");

                    // Get Book Authors
                    JSONArray authors = bookInfo.getJSONArray("authors");
                    ArrayList<String> authorArrayList = new ArrayList<>();
                    for (int j = 0; j < authors.length(); j++) {
                        // get Book Authors String[]
                        authorArrayList.add(authors.getString(j));
                    }

                    // Get Book Publish Time
                    String bookPublishTime = bookInfo.getString("publishedDate");
                    bookPublishTime = "Publish: " + bookPublishTime;

                    // Get Book Thumbnail
                    JSONObject imageLinks = bookInfo.getJSONObject("imageLinks");
                    String thumbnail = imageLinks.getString("smallThumbnail"); // use the small thumbnail link here
                    URL thumbnailUrl = createUrl(thumbnail);
                    Bitmap bookThumbnail = downloadImage(thumbnailUrl);
                    Book book = null;
                    if (bookThumbnail == null) {
                        // Create Book Class without Thumbnail
                        book = new Book(bookTitle, authorArrayList, bookPublishTime);
                    } else {
                        // Create Book Class with Thumbnail
                        book = new Book(bookTitle, authorArrayList, bookPublishTime, bookThumbnail);
                    }
                    // Add Book into Books
                    booksArrayList.add(book);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return booksArrayList;
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return url;
        }

        /*
        * Transfer InputStream to Json String
        * */
        public String readFromInputStream(InputStream inputStream) {
            StringBuilder stringBuilder = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        stringBuilder.append(line);
                        line = bufferedReader.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return stringBuilder.toString();
        }
    }
}
