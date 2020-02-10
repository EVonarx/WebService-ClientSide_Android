package com.example.accesstowebservices;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.evonarx.accesstowebservicesBusiness.Article;
import com.owlike.genson.Genson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {

    private EditText txtIdentifier;
    private EditText txtDescription;
    private EditText txtBrand;
    private EditText txtPrice;
    private Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtIdentifier = (EditText) findViewById(R.id.txtIdentifier);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        txtBrand = (EditText) findViewById(R.id.txtBrand);
        txtPrice = (EditText) findViewById(R.id.txtPrice);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(btnUpdateListener);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {


                    /*If you are referring your localhost on your system from the Android emulator
                    then you have to use http://10.0.2.2:8080/ Because Android emulator runs in a Virtual Machine therefore
                    here 127.0.0.1 or localhost will be emulator's own loopback address
                     */
                    URL url = new URL("http://10.0.2.2:8080/612-JEE-Project-maven-jaxrs-0.0.1-SNAPSHOT/rest/article/get/1");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    Scanner scanner = new Scanner(in);
                    final Article article = new Genson().deserialize(scanner.nextLine(), Article.class);
                    Log.i("Exchange Json", "Result == "+ article);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtIdentifier.setText("" + article.getIdArticle());
                            txtDescription.setText(article.getDescription());
                            txtBrand.setText(article.getBrand());
                            txtPrice.setText("" + article.getPrice());
                        }
                    });

                    in.close();

                } catch (Exception e) {
                    Log.e("Exchange Json", ": could not find Http Server", e);

                } finally {
                    if (urlConnection != null) urlConnection.disconnect();
                }
            }
        }).start();

    }

    private View.OnClickListener btnUpdateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           //new Thread(new Runnable() {
                //@Override
                //public void run() {
                    Article art = new Article(Integer.parseInt(txtIdentifier.getText().toString()), txtDescription.getText().toString(), txtBrand.getText().toString(), Double.parseDouble(txtPrice.getText().toString()));

                    String message = new Genson().serialize(art);
                    Log.i("Exchange Json", "Message == "+ message);

                    HttpURLConnection urlConnection = null;

                    try {
                    /*If you are referring your localhost on your system from the Android emulator
                    then you have to use http://10.0.2.2:8080/ Because Android emulator runs in a Virtual Machine therefore
                    here 127.0.0.1 or localhost will be emulator's own loopback address
                     */
                        URL url = new URL("http://10.0.2.2:8080/612-JEE-Project-maven-jaxrs-0.0.1-SNAPSHOT/rest/article/update");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("PUT");
                        urlConnection.setDoOutput(true);
                        urlConnection.setRequestProperty("Content-Type", "application/json");

                        OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                        out.write(message.getBytes());
                        out.close();

                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        Scanner scanner = new Scanner(in);
                        Log.i("Exchange Json", "Result == "+ scanner.nextLine());
                        in.close();

                    } catch (Exception e) {
                        Log.e("Exchange Json", ": could not find Http Server", e);

                    } finally {
                        if (urlConnection != null) urlConnection.disconnect();
                    }
                //}


           // });
        }
    };

}
