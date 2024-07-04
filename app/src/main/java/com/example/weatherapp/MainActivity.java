package com.example.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView cityName;
    Button search;
    TextView show;
    String url;

    class getWeather extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line="";
                while((line = bufferedReader.readLine()) != null){
                    result.append(line).append("\n");
                }
                return result.toString();
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");

                double temperatureKelvin = main.getDouble("temp");
                double feelsLikeKelvin = main.getDouble("feels_like");
                double tempMinKelvin = main.getDouble("temp_min");
                double tempMaxKelvin = main.getDouble("temp_max");
                int pressure = main.getInt("pressure");
                int humidity = main.getInt("humidity");

                // Convert Kelvin to Celsius
                double temperatureCelsius = temperatureKelvin - 273.15;
                double feelsLikeCelsius = feelsLikeKelvin - 273.15;
                double tempMinCelsius = tempMinKelvin - 273.15;
                double tempMaxCelsius = tempMaxKelvin - 273.15;

                String weatherInfo = String.format(
                        "Temperature : %.2f°C\nFeels Like : %.2f°C\nMin Temperature : %.2f°C\nMax Temperature : %.2f°C\nPressure : %d hPa\nHumidity : %d%%",
                        temperatureCelsius, feelsLikeCelsius, tempMinCelsius, tempMaxCelsius, pressure, humidity
                );

                show.setText(weatherInfo);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.searchBtn);
        show = findViewById(R.id.weatherDetails);
        final String[] temp = {""};

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityName.getText().toString();
                try{
                    if(city!=null && !city.isEmpty()){
                        url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="; //enter your api
                    }else{
                        Toast.makeText(MainActivity.this, "Enter City", Toast.LENGTH_SHORT).show();
                    }
                    getWeather task = new getWeather();
                    temp[0] = task.execute(url).get();
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(temp[0] == null){
                    show.setText("Unable to find weather");
                }
            }
        });
    }
}
