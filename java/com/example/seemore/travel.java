package com.example.seemore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;


//Main part of the application
//Next task: divide code into functions. For example GPS functions etc.
//Next task: fix variable names and cases

public class travel extends AppCompatActivity {
	
	//declaration of GPS location variables
    float gps = 0;
	public float latBegin = 0;
    public float latEnd = 0;
	//declaration of used layouts, views etc.
    RelativeLayout layout;
    RelativeLayout linearLayout;
    ImageView train;
	TextView test;
	Button btnClosePopup;
	//declaration of other used variables etc.
    public int begin = 1;
    String text;
    int number;
    int counter = 1;
    Context context;
    int resId;
    private PopupWindow pwindo;
	int end;
    String[] words;
    Handler mHandler = new Handler();

	//Initialization of the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_layout2);
		//Getting information from the file about stations (their coordinates) and id names for views
        words = getResources().getStringArray(R.array.information);
        String[] left = getResources().getStringArray(R.array.left);
        String[] right = getResources().getStringArray(R.array.right);
        String[] bubble = getResources().getStringArray(R.array.bubble);
		//Getting Spinner values from previous activity
        Global app = Global.getInstance();
        String beginning = app.getValue(0);
        String ending = app.getValue(1);	
		
		//Initializing GPS location variables
		float lonBegin = 0;
        float lonEnd = 0;
		//Initializing used variables and views
        String name;
        TextView test;
        ImageView image;
		//Initialization for popup display
		context = travel.this;
		
		//Getting GPS lockation and displaying it as a toast
        GPSTracker gpsLocation;
        gpsLocation = new GPSTracker(com.example.seemore.travel.this);
		
        if (gpsLocation.canGetLocation()) {
            double latitude = gpsLocation.getLatitude();
            double longitude = gpsLocation.getLongitude();
        } else {
            gpsLocation.showSettingsAlert();
        }
		
		//Code to dynamically display station names and their positions
		//Getting first and last stations
		//Setting beginning and ending longitude and latitude for train position calculation
        for (int x = 0; x < 8; x++) {
			
            if (words[6 * x].compareTo(beginning) == 0) {
				//placing first station in correct position on the screen
                resId = getResources().getIdentifier("text1right", "id", getPackageName());
                test = (TextView) findViewById(resId);
                test.setText(words[6 * x]);
                begin = x;
				//getting coordinates of the first station
                latBegin = Float.parseFloat(words[2 + x * 6]);
                lonBegin = Float.parseFloat(words[1 + x * 6]);
            }
			
            if (words[6 * x].compareTo(ending) == 0) {
				//placing first station in correct position on the screen
                if (((x - begin) % 2) != 0) {
                    resId = getResources().getIdentifier("text8left", "id", getPackageName());
                    test = (TextView) findViewById(resId);
                    test.setText(words[6 * x]);
                } else {
                    resId = getResources().getIdentifier("text8right", "id", getPackageName());
                    test = (TextView) findViewById(resId);
                    test.setText(words[6 * x]);
                }
				//Number of the last station
                end = x;
				//Getting coordinates of the last station
                latEnd = Float.parseFloat(words[2 + x * 6]);
                lonEnd = Float.parseFloat(words[1 + x * 6]);
            }
			
        }
		
		//Getting stations in between
        for (int x = 1; x < 7; x++) {
			//hiding unused views
            if (x <= begin) {
                name = "layout" + (x + 1);
                resId = getResources().getIdentifier(name, "id", getPackageName());
                layout = (RelativeLayout) findViewById(resId);
                layout.setVisibility(View.GONE);
                resId = getResources().getIdentifier(left[x], "id", getPackageName());
                test = (TextView) findViewById(resId);
                test.setVisibility(View.GONE);
                resId = getResources().getIdentifier(bubble[x], "id", getPackageName());
                image = (ImageView) findViewById(resId);
                image.setVisibility(View.GONE);
            }
			//placing first station in correct position on the screen
            if ((x > begin) && (x < end)) {
                name = "layout" + (x + 1);
                resId = getResources().getIdentifier(name, "id", getPackageName());
                layout = (RelativeLayout) findViewById(resId);
                layout.setY((Float.parseFloat(words[2 + x * 6]) - latBegin) / (latEnd - latBegin) * 592 + 310);
				
                if (((x - begin) % 2) != 0) {
                    resId = getResources().getIdentifier(left[x], "id", getPackageName());
                    test = (TextView) findViewById(resId);
                    test.setText(words[6 * x]);
                } else {
                    resId = getResources().getIdentifier(right[x], "id", getPackageName());
                    test = (TextView) findViewById(resId);
                    test.setText(words[6 * x]);
                }
				
            }
			//hiding unused views if number of stations is less than 8
            if (x >= end) {
                name = "layout" + (x + 1);
                resId = getResources().getIdentifier(name, "id", getPackageName());
                layout = (RelativeLayout) findViewById(resId);
                layout.setVisibility(View.GONE);
            }
			
        }
		
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
		
		//calculation of train movement on the screen
        gps = latBegin;
        train = (ImageView) findViewById(R.id.train);
        train.setY((gps - latBegin) / (latEnd - latBegin) * 592 + 310);
		//counter to avoid popup window at first station
        counter = begin + 1;
		//making train move at time intervals
        mHandler.postDelayed(runnable, 100);
		
    }

	//Code for delayed train movement 
    private Runnable runnable = new Runnable() {
        public void run() {
			//changing train position by simulation of gps coordinate change
            gps = gps + 0.0005f;
            train = (ImageView) findViewById(R.id.train);
            train.setY((gps - latBegin) / (latEnd - latBegin) * 592 + 310);
			
			//checking if the train is close to one of the stations to show popup window
            for (int i = begin; i <= end; i++) {
				//fulfilling means train is close to the station
                if ((gps > Float.parseFloat(words[2 + i * 6]) - 0.11) && (counter == i) && (gps < Float.parseFloat(words[2 + i * 6]))) {
                    counter++;
					//number of the station will be used in popup window
                    number = i;
					//Getting JSON from Eventbrite API to know if there events happening in the city
                    String event = ((new events()).getEventData(getApplicationContext(), words[1 + number * 6], words[2 + number * 6]));
                    float count = 0;
					
					//checking if JSON is not empty
                    try {
                        JSONObject jsonRootObject = new JSONObject(event);
                        JSONObject mainObj = jsonRootObject.getJSONObject("pagination");
                        count = Float.valueOf(mainObj.optString("object_count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
					//Checking if it is not the last station
                    if ((words[3 + i * 6] != null && !words[3 + i * 6].isEmpty()) || (number == end) || (count > 0)) {
                        if (pwindo != null && pwindo.isShowing() == true) {
							//checking if popup window is open. If true, window is closse
                            if (pwindo.isShowing()) {
                                pwindo.dismiss();
                            }
                        }
						//opening popup window
                        initiatePopupWindow();
                    }
					
                }
				
            }
			//train moves further
            mHandler.postDelayed(runnable, 100);
            
			//stopping movement of the train if it reaches the last station
			if (gps > latEnd) {
                mHandler.removeCallbacks(runnable);
            }
			
        }
		
    };

	//function filling popup window
    private void initiatePopupWindow() {
		
		//catching errors
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//using popup window layout
            View layout = inflater.inflate(R.layout.screen_popup, (ViewGroup) findViewById(R.id.popup_element));
            pwindo = new PopupWindow(layout, 700, LayoutParams.FILL_PARENT, true);
            pwindo.setBackgroundDrawable(new BitmapDrawable());
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            ((ImageView) pwindo.getContentView().findViewById(R.id.imageView)).setVisibility(View.GONE);
			
			//showing image of the city
            try {
                ((ImageView) pwindo.getContentView().findViewById(R.id.imageView)).setVisibility(View.VISIBLE);
                resId = getResources().getIdentifier(words[number * 6].toLowerCase(), "drawable", getPackageName());
                ((ImageView) pwindo.getContentView().findViewById(R.id.imageView)).setImageResource(resId);
            } catch (NullPointerException e) {
            }
			
			//getting information about the city from wikipedia API
            String info = ((new wikiInfo()).getWikiInfo(words[number * 6]));
			//extracting information about the city from JSON file
            try {
                JSONObject object = new JSONObject(info);
                JSONObject obj1 = object.getJSONObject("query");
                JSONObject obj2 = obj1.getJSONObject("pages");
                JSONObject obj3 = null;
                Iterator<String> keys = obj2.keys();
				
                while (keys.hasNext()) {
                    String keyValue = (String) keys.next();
                    obj3 = obj2.getJSONObject(keyValue);
                }
				
                String desc = obj3.getString("extract");
				//popup windows for last and intermediate stations is different. Some views are hidden
                ((ImageButton)  pwindo.getContentView().findViewById(R.id.taxi2)).setVisibility(View.GONE);
                ((Button) pwindo.getContentView().findViewById(R.id.ShowLess)).setVisibility(View.GONE);
				//two views to show more or less information. Depending on customer pressing "show more" or "show less" button
				//Task change into one view using .setMaxLines();
                WebView More =  ((WebView) pwindo.getContentView().findViewById(R.id.CommentMore));
                More.loadData("<p style=\"text-align: justify\">" + desc + "</p>", "text/html", "UTF-8");
                WebSettings webSettings = More.getSettings();
                webSettings.setDefaultFontSize(12);
                WebView Less =  ((WebView) pwindo.getContentView().findViewById(R.id.CommentLess));
                Less.loadData("<p style=\"text-align: justify\">" + desc + "</p>", "text/html", "UTF-8");
                webSettings = Less.getSettings();
                webSettings.setDefaultFontSize(12);
                (pwindo.getContentView().findViewById(R.id.CommentMore)).setVisibility(View.GONE);
                Less.setBackgroundColor(Color.parseColor("#F8F3ED"));
                More.setBackgroundColor(Color.parseColor("#F8F3ED"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
			
			//Show more information button
            Button show = (Button) pwindo.getContentView().findViewById(R.id.Show);
            show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    ((Button) pwindo.getContentView().findViewById(R.id.Show)).setVisibility(View.GONE);
                    ((Button) pwindo.getContentView().findViewById(R.id.ShowLess)).setVisibility(View.VISIBLE);
                    ( pwindo.getContentView().findViewById(R.id.CommentMore)).setVisibility(View.VISIBLE);
                    ( pwindo.getContentView().findViewById(R.id.CommentLess)).setVisibility(View.GONE);
                }
            });
			
			//Show less information button
            Button showLess = (Button) pwindo.getContentView().findViewById(R.id.ShowLess);
            showLess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    ((Button) pwindo.getContentView().findViewById(R.id.ShowLess)).setVisibility(View.GONE);
                    ((Button) pwindo.getContentView().findViewById(R.id.Show)).setVisibility(View.VISIBLE);
                    ( pwindo.getContentView().findViewById(R.id.CommentMore)).setVisibility(View.GONE);
                    ( pwindo.getContentView().findViewById(R.id.CommentLess)).setVisibility(View.VISIBLE);
                }
            });
			
			//Display city name
            ((TextView) pwindo.getContentView().findViewById(R.id.City)).setText(words[number * 6]);
            ((TextView) pwindo.getContentView().findViewById(R.id.name)).setText(words[number * 6]);
			//Get events in the city using Eventbrite API
            String event = ((new events()).getEventData(getApplicationContext(), words[1 + number * 6], words[2 + number * 6]));
            float count = 0;
			
			//get information from Eventbrite JSON
            try {
                JSONObject jsonRootObject = new JSONObject(event);
                JSONObject mainObj = jsonRootObject.getJSONObject("pagination");
                count = Float.valueOf(mainObj.optString("object_count"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
			
			//display details of only first three events in the city
            if (count > 0) {
                int i = 0;
                while ((i < 3) && (i < count)) {
                    String description = "";
                    JSONObject jsonRootObject = new JSONObject(event);
                    JSONArray jsonArray = jsonRootObject.optJSONArray("events");
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONObject information = jsonObject.getJSONObject("name");
                    String text = information.getString("text");
                    String name = "event" + (i+1) + "name";
                    final String hyperlink = jsonObject.getString("url");
                    resId = getResources().getIdentifier(name, "id", getPackageName());
                    ((TextView) pwindo.getContentView().findViewById(resId)).setText(text);
                    name = "event" + ( i+1) +"link";
                    resId = getResources().getIdentifier(name, "id", getPackageName());
					//open browser for more information about event if textview is pressed
					//display event name
                    test = (TextView) pwindo.getContentView().findViewById(resId);
                    test.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(hyperlink));
                            startActivity(browserIntent);
                        }
                    });
					
					//display event description
                    JSONObject description1 = jsonObject.getJSONObject("description");
                    String description2 = description1.getString("text");
                    name = "event" + ( i+1) +"description";
                    resId = getResources().getIdentifier(name, "id", getPackageName());
                    ((TextView) pwindo.getContentView().findViewById(resId)).setText(description2);

					//show event start time
                    information = jsonObject.getJSONObject("start");
                    String time = information.getString("local");
                    time = time.substring(0, 10);
                    name = "event" + (i+1) + "time";
                    resId = getResources().getIdentifier(name, "id", getPackageName());

                    if ((text.equals("")) || (text == null) || (text.isEmpty())) {
                        ((TextView) pwindo.getContentView().findViewById(resId)).setVisibility(View.GONE);
                    } else {
                        ((TextView) pwindo.getContentView().findViewById(resId)).setText(time);
                    }
					
					//show event logo or eventbrite image otherwise
                    try {
                        information = jsonObject.getJSONObject("logo");
                        text = information.getString("url");
                        name = "event" + (i+1) + "image";
                        resId = getResources().getIdentifier(name, "id", getPackageName());
                        new LoadImagefromUrl().execute(new LoadImagefromUrlModel(text, resId));
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }
                    i++;
                }
				
				//hide views if there are less than 3 events happening in the next 2 weeks
                while (i < 3) {
                    text = "event" + (i+1)+"layout";
                    resId = getResources().getIdentifier(text, "id", getPackageName());
                    ((LinearLayout) pwindo.getContentView().findViewById(resId)).setVisibility(View.GONE);
                    i++;
                }

            }
			
			//in the last station extra information is shown. Taxi, restaurant, weather etc.
            if (number == end) {

				//stop showing button to buy tickets
                ((Button) pwindo.getContentView().findViewById(R.id.GWR)).setVisibility(View.GONE);
				//initialize variables for different weather information
                String description = "";
                String temperature = "";
                String humidity = "";
                String wind = "";
                String icon ="";
				
				//get current weather data at the destination from Openweather API
				//show weather icon
                String data = ((new WeatherHttpClient()).getWeatherData(words[number * 6]));
                try {
                    JSONObject jsonRootObject = new JSONObject(data);
                    JSONArray jsonArray = jsonRootObject.optJSONArray("weather");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    description = jsonObject.optString("main").toString();
                    icon = jsonObject.optString("icon").toString();
                    new LoadImagefromUrl().execute(new LoadImagefromUrlModel("http://openweathermap.org/img/w/"+icon+".png", R.id.weatherimage));
                    JSONObject mainObj = jsonRootObject.getJSONObject("main");
                    temperature = mainObj.optString("temp").toString();
                    humidity = mainObj.optString("humidity").toString();
                    JSONObject speed = jsonRootObject.getJSONObject("wind");
                    wind = speed.optString("speed").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
				
				//display temperature, weather description, wind speed, humidity
                ((TextView) pwindo.getContentView().findViewById(R.id.weathertemperature)).setText(Integer.toString(Math.round(Float.parseFloat(temperature))));
                ((TextView) pwindo.getContentView().findViewById(R.id.weatherdescription)).setText(description);
                ((TextView) pwindo.getContentView().findViewById(R.id.weatherwind)).setText("Wind speed: "+wind+" m/s");
                ((TextView) pwindo.getContentView().findViewById(R.id.weatherhumidity)).setText("Humidity: "+humidity+"%");
				
				//show taxi button and enable call function to a number saved in data
                ((ImageButton)  pwindo.getContentView().findViewById(R.id.taxi2)).setVisibility(View.VISIBLE);
                ImageButton taxi = ((ImageButton) pwindo.getContentView().findViewById(R.id.taxi2));

                taxi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + words[4 + number * 6]));
                            startActivity(intent);
                        }
                });
                
				//Show restaurant. On click transfer customer to google maps
				(TextView) pwindo.getContentView().findViewById(R.id.restaurantname)).setText(words[number * 6 + 5]);
                TextView test2 = (TextView) pwindo.getContentView().findViewById(R.id.restaurantlocation);
                test2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + words[number * 6 + 5]+","+words[number * 6]);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                });

            }
			//if not the last station, then hide temperature and restaurant
            else
            {
				((LinearLayout) pwindo.getContentView().findViewById(R.id.weatherlayout)).setVisibility(View.GONE);
				((LinearLayout) pwindo.getContentView().findViewById(R.id.restaurant2)).setVisibility(View.GONE);
            }
			
			//close window if BACK button is pressed
			pwindo.setOutsideTouchable(true);
            pwindo.setFocusable(true);
            pwindo.getContentView().setFocusableInTouchMode(true);
            pwindo.getContentView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
						pwindo.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		//in intermediate stations show GWR button which directs to gwr website to buy tickets
        Button button = (Button) pwindo.getContentView().findViewById(R.id.GWR);
        ((Button) pwindo.getContentView().findViewById(R.id.GWR)).setText("GET TICKETS TO "+words[number * 6]);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://tickets.gwr.com/gw/en/JourneyPlanning/MixingDeck"));
                    startActivity(browserIntent);
                }
            }
		);
    }

	//Getting images from URL
    private class LoadImagefromUrlModel {
        final int viewId;
        final String url;
        Bitmap bitmap;

        /*
         * @param url the url
         * @param viewId the id of the imageview to display the loadded bitmap
         */
        public LoadImagefromUrlModel(String url, int viewId) {
            this.url = url;
            this.viewId = viewId;
        }
    }

    private class LoadImagefromUrl extends AsyncTask< LoadImagefromUrlModel, Void, LoadImagefromUrlModel > {

        @Override
        protected LoadImagefromUrlModel doInBackground( LoadImagefromUrlModel... params ) {
            LoadImagefromUrlModel model = params[0];
            // load the bitmap
            model.bitmap = loadBitmap( model.url );
            return model;
        }
        @Override
        protected void onPostExecute( LoadImagefromUrlModel result ) {
            super.onPostExecute(result);
            // find the imageview
            ImageView ivPreview = (ImageView) pwindo.getContentView().findViewById(result.viewId);
            if (ivPreview != null) {
                ivPreview.setImageBitmap(result.bitmap);
            }
        }
    }

    public Bitmap loadBitmap( String url ) {
        URL newurl = null;
        Bitmap bitmap = null;
        try {
            newurl = new URL( url );
            bitmap = BitmapFactory.decodeStream( newurl.openConnection( ).getInputStream( ) );
        } catch ( MalformedURLException e ) {
            e.printStackTrace( );
        } catch ( IOException e ) {
            e.printStackTrace( );
        }catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

