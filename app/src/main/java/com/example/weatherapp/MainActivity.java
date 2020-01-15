package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    URL url;
    URLConnection link;
    InputStream stream;
    InputStreamReader streamReader;
    BufferedReader reader;
    String line = null;
    String info;
    JSONObject jsonObject;
    String zipcode;
    ImageView bigImage;
    EditText zip;
    TextView quoteTheme;
    TextView bigTemp;
    TextView weatherDesc;
    TextView bigDate;
    ListView listView;
    Button submit;
    ImageView BigBig;
    ArrayList<Integer> index;
    ArrayList<Weather> arrayList;
    ArrayList<String>  highTemp;
    ArrayList<String> lowTemp;
    ConstraintLayout constraintLayout;
    CustomAdapter customAdapter;
    TextView city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bigImage = findViewById(R.id.bigImage);
        zip = findViewById(R.id.editText);
        quoteTheme = findViewById(R.id.quoteTheme);
        bigTemp = findViewById(R.id.bigTemp);
        weatherDesc = findViewById(R.id.weatherDesc);
        bigDate = findViewById(R.id.bigDate);
        listView = findViewById(R.id.listView);
        submit = findViewById(R.id.submit);
        BigBig = findViewById(R.id.backGround);
        arrayList = new ArrayList<>();
        index = new ArrayList<>();
        city = findViewById(R.id.city);
        constraintLayout = findViewById(R.id.linearLayout);
        customAdapter = new CustomAdapter(this, R.layout.adapter_custom, arrayList);
        listView.setAdapter(customAdapter);




        zip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                zipcode = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                try{

                }
                catch (Exception e){
                    e.printStackTrace();
                }


            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.clear();
                bigDate.setVisibility(View.VISIBLE);
                quoteTheme.setVisibility(View.VISIBLE);
                bigTemp.setVisibility(View.VISIBLE);
                weatherDesc.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                BigBig.setVisibility(View.VISIBLE);
                bigImage.setVisibility(View.VISIBLE);

                new AsyncThread().execute(zipcode);
            }
        });


    }
    public class AsyncThread extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings) {
            info = "";
            try{
                String zipcode = strings[0];
                url = new URL("http://api.openweathermap.org/data/2.5/forecast?zip="+zipcode+"&APPID=474c75c79c0edb7aceb1da1651b97629");
                link = url.openConnection();
                stream = link.getInputStream();
                streamReader = new InputStreamReader(stream);
                reader = new BufferedReader(streamReader);
                info = reader.readLine();
                Log.d("Tag3", info);

            }catch(Exception e) {
                e.printStackTrace();
                Log.d("Tag1", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                JSONObject jsonObject = new JSONObject(info);
                JSONArray weatherArray = jsonObject.getJSONArray("list");
                JSONObject currentWeather = weatherArray.getJSONObject(0);

                bigTemp.setText((returnTemp(currentWeather.getJSONObject("main").getString("temp").toString())).toString());
                weatherDesc.setText(currentWeather.getJSONArray("weather").getJSONObject(0).getString("main"));
                bigDate.setText(currentWeather.getString("dt_txt").substring(0, currentWeather.getString("dt_txt").indexOf(" ")));
                pickQuote(currentWeather.getJSONArray("weather").getJSONObject(0).getString("main"));
                setTheBackground(currentWeather.getJSONArray("weather").getJSONObject(0).getString("main"));
                Picasso.get().load("http://openweathermap.org/img/wn/"+currentWeather.getJSONArray("weather").getJSONObject(0).getString("icon")+"@2x.png").into(bigImage);
                Log.d("Tag", "here");

                //arrayList = new ArrayList<>();

                for(int i = 0; i < 40; i++){
                    int temp = 0;
                    if(weatherArray.getJSONObject(i).getString("dt_txt").substring(currentWeather.getString("dt_txt").indexOf(" "), currentWeather.getString("dt_txt").length()).equals(" 00:00:00")){
                        index.add(i);
                        /*for(int j = 0; j < 8; j++){
                            tempweatherArray.getJSONObject(i).getJSONObject("main").getString("temp_max");
                        }*/
                    }
                    Log.d("Tag", weatherArray.getJSONObject(i).getString("dt_txt").substring(currentWeather.getString("dt_txt").indexOf(" "), currentWeather.getString("dt_txt").length()));
                    //index.add(i);
                }

                Log.d("Tagsize", index.size()+"");
                JSONObject dayOne = weatherArray.getJSONObject(index.get(0));
                Weather weatherOne = new Weather(returnTemp(dayOne.getJSONObject("main").getString("temp_max")), returnTemp(dayOne.getJSONObject("main").getString("temp_min")), dayOne.getString("dt_txt").substring(0, currentWeather.getString("dt_txt").indexOf(" ")), "http://openweathermap.org/img/wn/"+dayOne.getJSONArray("weather").getJSONObject(0).getString("icon")+"@2x.png");
                JSONObject dayTwo = weatherArray.getJSONObject(index.get(1));
                Weather weatherTwo = new Weather(returnTemp(dayTwo.getJSONObject("main").getString("temp_max")), returnTemp(dayTwo.getJSONObject("main").getString("temp_min")), dayTwo.getString("dt_txt").substring(0, currentWeather.getString("dt_txt").indexOf(" ")), "http://openweathermap.org/img/wn/"+dayTwo.getJSONArray("weather").getJSONObject(0).getString("icon")+"@2x.png");
                JSONObject dayThree = weatherArray.getJSONObject(index.get(2));
                Log.d("Tag1", dayTwo.getJSONArray("weather").getJSONObject(0).getString("main"));
                Weather weatherThree = new Weather(returnTemp(dayThree.getJSONObject("main").getString("temp_max")), returnTemp(dayThree.getJSONObject("main").getString("temp_min")), dayThree.getString("dt_txt").substring(0, currentWeather.getString("dt_txt").indexOf(" ")), "http://openweathermap.org/img/wn/"+dayThree.getJSONArray("weather").getJSONObject(0).getString("icon")+"@2x.png");
                JSONObject dayFour = weatherArray.getJSONObject(index.get(3));
                Weather weatherFour = new Weather(returnTemp(dayFour.getJSONObject("main").getString("temp_max")), returnTemp(dayFour.getJSONObject("main").getString("temp_min")), dayFour.getString("dt_txt").substring(0, currentWeather.getString("dt_txt").indexOf(" ")), "http://openweathermap.org/img/wn/"+dayFour.getJSONArray("weather").getJSONObject(0).getString("icon")+"@2x.png");
                JSONObject dayFive = weatherArray.getJSONObject(index.get(4));
                Weather weatherFive = new Weather(returnTemp(dayFive.getJSONObject("main").getString("temp_max")), returnTemp(dayFive.getJSONObject("main").getString("temp_min")), dayFive.getString("dt_txt").substring(0, currentWeather.getString("dt_txt").indexOf(" ")), "http://openweathermap.org/img/wn/"+dayFive.getJSONArray("weather").getJSONObject(0).getString("icon")+"@2x.png");
                arrayList.add(weatherOne);
                arrayList.add(weatherTwo);
                arrayList.add(weatherThree);
                arrayList.add(weatherFour);
                arrayList.add(weatherFive);
                Log.d("THING", ""+arrayList.size());
                Log.d("THING", arrayList.toString());


                customAdapter.updateWeather(arrayList);
                customAdapter.notifyDataSetChanged();

                listView.setAdapter(customAdapter);

                Log.d("Tag", " "+listView.getAdapter().getCount());

            }
            catch (Exception e){
                e.printStackTrace();
                Log.d("Tag2", e.toString());
            }
        }
    }
    public class CustomAdapter extends ArrayAdapter<Weather> {
        List<Weather> list;
        Context parentContext;
        int xmlResource;
        public CustomAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Weather> objects){
            super(context, resource, objects);
            list = objects;
            parentContext = context;
            xmlResource = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) parentContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.adapter_custom, null);

            TextView highText = view.findViewById(R.id.custom_high);
            TextView lowText = view.findViewById(R.id.custom_low);
            ImageView imageView= view.findViewById(R.id.custom_imageView);
            TextView dateCustom = view.findViewById(R.id.customDate);

            Picasso.get().load(list.get(position).getImageURL()).into(imageView);
            highText.setText(list.get(position).getHigh()+"");
            lowText.setText(list.get(position).getLow()+"");
            dateCustom.setText(list.get(position).getDate());


            return view;
        }
        public void updateWeather(ArrayList<Weather> arrayList) {
            this.list= arrayList;
        }
    }
    public Double returnTemp(String tempDT){
        double temp = Double.parseDouble(tempDT);
        double f = (double)(Math.round((temp-273)*1.8+32));
        return f;
    }
    public String pickQuote(String string){
        String quote = "";
        switch(string){
            case "Rain":
                quoteTheme.setText("It's wetter than the water bottle you're not allowed to have on your desk");
                break;
            case "Clear":
                quoteTheme.setText("It clearer than your desk after removing all your belongings");
                break;
            case "Snow":
                quoteTheme.setText("It's snowing whiter than a clean scantron");
                break;
            case "Drizzle":
                quoteTheme.setText("It's drizzling, like the way College Board drizzles in grammar questions");
                break;
            case "Clouds":
                quoteTheme.setText("It's cloudier than your memory when you forget basic grammar rules");
                break;
            case "Thunderstorm":
                quoteTheme.setText("There's a thunderstorm and lighting. Like your lightning fast reading skills");
                break;
        }
        return quote;
    }
    public void setTheBackground(String string){
        switch (string){
            case "Clear":
                Picasso.get().load(R.drawable.clear).into(BigBig);
                break;
            case "Clouds":
                Picasso.get().load(R.drawable.cloudy).into(BigBig);
                break;
            case "Snow":
                Picasso.get().load(R.drawable.snow).into(BigBig);
                break;
            case "Drizzle":
                Picasso.get().load(R.drawable.drizzle).into(BigBig);
                break;
            case "Rain":
                Picasso.get().load(R.drawable.rain).into(BigBig);
                break;
            case "Thunderstorm":
                Picasso.get().load(R.drawable.thunderstorm).into(BigBig);
                break;
        }
    }

    public class Weather{
        private Double high;
        private Double low;
        private String date;
        private String imageURL;
        public Weather(Double high, Double low, String date, String imageURL) {
            this.date = date;
            this.high = high;
            this.low = low;
            this.imageURL = imageURL;
        }
        public Double getHigh() {
            return high;
        }
        public Double getLow() {
            return low;
        }
        public String getDate() {
            return date;
        }
        public String getImageURL(){
            return imageURL;
        }
        public String toString(){
            return date;
        }
    }

}
