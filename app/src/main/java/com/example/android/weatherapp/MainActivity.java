package com.example.android.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    TextView resultText;
    Button button1;
    Button button2;
    String api_id;
    public void again(View view){
        cityName.setText("");
        resultText.setText("");
        button2.setVisibility(View.INVISIBLE);
        button1.setVisibility(View.VISIBLE);
    }

    public void findWeather(View view){
        Log.i("cityname:",cityName.getText().toString());
        //to hide the keyboard
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);
        try {
            String encoded = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        api_id="";

        DownloadTask task = new DownloadTask();
        task.execute("https://samples.openweathermap.org/data/2.5/weather?q="+cityName.getText().toString()+"&appid="+api_id);
        button1.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = (EditText)findViewById(R.id.cityName);
        resultText = (TextView)findViewById(R.id.resultText);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.again);
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls){
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data!=-1){
                    char c = (char) data;
                    result+=c;
                    data = reader.read();
                }
                return result;

            }
            catch(Exception e){
                Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_LONG);
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                JSONObject jsonObject= new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                String message = "";
                JSONArray arr = new JSONArray(weatherInfo);
                for(int i=0;i<arr.length();i++){
                    JSONObject json = arr.getJSONObject(i);
                    String main = "";
                    String desc = "";
                    main = json.getString("main");
                    desc = json.getString("description");
                    if(main!="" && desc!=""){
                        message += main+":"+desc+"\r\n";
                    }
                }
                if(message!=""){
                    resultText.setText(message);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Invalid city or no data available,yet!",Toast.LENGTH_LONG);
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
