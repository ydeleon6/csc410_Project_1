package com.example.deleon118.project1;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity {
    //declare the fields you'll need for the parser
    private static String ns = null;
    private static InputStream input;
    private static HashMap<String, String> forecastMap = new HashMap<>(); //initialize map
    private static XmlPullParserFactory factory;
    private static XmlPullParser xml;
    //declare all of the fields you're gonna mess with
    private Button go;
    private ImageView centerPic;
    private TextView displayConditions;
    private EditText enterZip;
    private TextView temperature;
    private TextView dew;
    private TextView humidity;
    private TextView pressure;
    private TextView visibility;
    private TextView windspeed;
    private TextView gust;
    private TextView currTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //instantiate all of the fields
        go = (Button)findViewById(R.id.button);
        centerPic = (ImageView)findViewById(R.id.pic);
        displayConditions = (TextView)findViewById(R.id.displayConditions);
        enterZip = (EditText)findViewById(R.id.editZip);
        temperature = (TextView)findViewById(R.id.displayTemp);
        dew = (TextView)findViewById(R.id.displayDew);
        humidity = (TextView)findViewById(R.id.displayHumidity);
        pressure = (TextView)findViewById(R.id.displayPressure);
        visibility = (TextView)findViewById(R.id.displayVisibility);
        windspeed = (TextView)findViewById(R.id.displayWindspeed);
        gust = (TextView)findViewById(R.id.displayGust);
        currTime= (TextView)findViewById(R.id.currentTimeDisplay);

        populateGUI("60505.xml"); //initially populate using 60505.xml
        //attach the button and button handler for future handling.

    }

    private void populateGUI(String zip){
        try{
            //create parser
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xml = factory.newPullParser();
            input = getAssets().open(zip, AssetManager.ACCESS_BUFFER);
            xml.setInput(input, null);
            Log.i("--- TEST ---", "About to enter the parser");
            parse(xml); //populate yo map
        }
        catch(Exception e){
            e.printStackTrace();
            Log.i("--- TEST ---", "ERROR");
        }
        //got this from here: http://stackoverflow.com/questions/11737607/how-to-set-the-image-from-drawable-dynamically-in-android
        int res = getResources().getIdentifier(forecastMap.get("icon-link"), "drawable", this.getPackageName());
        //fill text fields
        centerPic.setImageResource(res);
        temperature.setText(forecastMap.get("apparent")+" F");
        dew.setText(forecastMap.get("dew")+" F");
        humidity.setText(forecastMap.get("humidity"));
        displayConditions.setText(forecastMap.get("weather-summary"));
        pressure.setText(forecastMap.get("pressure") + " in");
        visibility.setText(forecastMap.get("visibility")+" mi");
        windspeed.setText(forecastMap.get("windspeed"));
        gust.setText(forecastMap.get("gust"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
    Log.i("--- TEST ---", "requiring feed...");
    int eventType = parser.getEventType();
        boolean check = false;
        //until you get to the end of the doc
        while (eventType != XmlPullParser.END_DOCUMENT) {
        //switch on event type
        switch (eventType) {
            case XmlPullParser.START_TAG:
                String tagname = parser.getName();
                Log.i("Tag names are ", tagname);
                if (tagname.equalsIgnoreCase("data")) {
                    if (parser.getAttributeValue(null, "type").equals("current observations")) {
                        check = true;
                    }
                }
                if(check){
                     //check tagz.
                switch(tagname) {
                    case "temperature": {
                        String attr = parser.getAttributeValue(ns, "type");
                        if (attr.equals("dew point")) {
                            String contents = readText(parser,"value");
                            Log.i("--- TEST ---", contents);
                            forecastMap.put("dew", contents);
                        } else if (attr.equals("apparent")) {
                            String contents = readText(parser,"value");
                            Log.i("--- TEST ---", contents);
                            forecastMap.put("apparent", contents);
                        }
                    }
                        break;
                    case "direction":
                    case "pressure":
                    case "humidity": {
                        String result = readText(parser, "value");
                        Log.i("--- TEST ---", result+"?");
                        forecastMap.put(tagname,result);
                    }
                    case "weather-conditions":{
                        if(parser.getAttributeValue(ns, "weather-summary") != null){
                            //its the other thing
                            String attr = parser.getAttributeValue(ns, "weather-summary");
                            Log.i("--- TEST ---",attr);
                            forecastMap.put("weather-summary",attr);
                        }
                        else if(tagname.equals("weather-conditions")){
                            //next tag is value. Unfortunately, the value is in fucking visibility
                            Log.i("--- TEST ---", "On to bisibilitee");
                        }
                    }
                    break;
                    case "icon-link": {
                        String result = readText(parser, tagname);
                        String array[] = result.split("\\/"); //split on /
                        String filename = array[array.length-1]; //filename and get the name alone with .split("\\.")[0]
                        Log.i("--- TEST ---", "got the icon link");
                        Log.i("--- TEST ---", result);
                        Log.i("--- TEST ---",filename);
                        forecastMap.put("icon-link",filename);
                    }
                    break;
                    case "visibility": {
                        String result = readText(parser, "visibility");
                        Log.i("--- TEST ---", "visibility was "+result);
                        forecastMap.put("visibility", result);
                    }
                    break;
                    case "wind-speed":{
                        Log.i("--- TEST ---", "INSIDE WIND_SPEED");
                        String result = "";
                        if(parser.getAttributeValue(ns, "type").equals("gust")){
                            result = readText(parser,"value");
                            forecastMap.put("gust", result);
                            Log.i("--- TEST ---", "gusts of "+result);
                        }
                        else if(parser.getAttributeValue(ns,"type").equals("sustained")){
                            result = readText(parser,"value");
                            forecastMap.put("windspeed", result);
                            Log.i("--- TEST ---", "got windspeed of "+result);
                        }
                    }
                    break;
                }
                }
                break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        Log.i("--- TEST ---", "DONE.");
    }

    private String readText(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        Log.i("--- TEST ---","READING???");
        String result = "";
        if(tag.equals("value")) {
            parser.nextTag();
        }
        parser.require(XmlPullParser.START_TAG, ns, tag);
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return result;
    }
}
