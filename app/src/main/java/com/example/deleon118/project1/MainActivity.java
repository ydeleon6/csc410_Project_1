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
    private static String ns = null;

    private static HashMap<String, String> forecastMap;
    //declare all of the fields you're gonna mess with
    Button go;
    ImageView centerPic;
    TextView displayConditions;
    EditText enterZip;
    TextView temperature;
    TextView dew;
    TextView humidity;
    TextView pressure;
    TextView visibility;
    TextView windspeed;
    TextView gust;
    TextView currTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //declare all of the fields you're gonna mess with
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
        InputStream input;
        forecastMap = new HashMap<>(); //initialize map
        XmlPullParserFactory factory;
        XmlPullParser xml;
        try{
            //create parser
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xml = factory.newPullParser();
            input = getAssets().open("60018.xml", AssetManager.ACCESS_BUFFER);

            xml.setInput(input, null);
            //xml.nextTag();
            Log.i("--- TEST ---", "About to enter the parser");
            parse(xml); //populate yo map
            populateFromMap(); //put shit in they place

        }
        catch(Exception e){
            e.printStackTrace();
            Log.i("no","well fuck you");
        }

    }
    public void populateFromMap(){
        //fill text fields
        temperature.setText(forecastMap.get("apparent")+" F");
        dew.setText(forecastMap.get("dew")+" F");
        humidity.setText(forecastMap.get("humidity"));
        displayConditions.setText(forecastMap.get("weather-summary"));
        pressure.setText(forecastMap.get("pressure") + " in");
        visibility.setText(forecastMap.get("visibility")+" mi");


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
                            //eventType = parser.next(); //get next start tag?
                            String contents = readText(parser);
                            Log.i("--- TEST ---", contents);
                            forecastMap.put("dew", contents);
                        } else if (attr.equals("apparent")) {
                            String contents = readText(parser);
                            Log.i("--- TEST ---", contents);
                            forecastMap.put("apparent", contents);
                        }
                    }
                        break;
                    case "direction":
                    case "pressure":
                    case "humidity": {
                        String result = readText(parser);
                        Log.i("--- TEST ---", result+"?");
                        forecastMap.put(tagname,result);
                    }
                    case "weather-conditions":{
                        if(parser.getAttributeValue(ns, "weather-summary") != null){
                            //its the other thing
                            String attr = parser.getAttributeValue(ns, "weather-summary");
                            Log.i("---- TEST ----",attr);
                            forecastMap.put("weather-summary",attr);
                        }
                        else{
                            //next tag is value. Unfortunately, the value is in fucking visibility
                            parser.nextTag();
                            Log.i("--- TEST ---", "visibility");
                        }
                    }
                    break;
                    case "visibility": {
                        String result = "";
                        parser.require(XmlPullParser.START_TAG, ns, "visibility");
                        if (parser.next() == XmlPullParser.TEXT) {
                            result = parser.getText();
                            parser.nextTag();
                        }
                        parser.require(XmlPullParser.END_TAG, ns, "visibility");
                        Log.i("---- TEST ----", "visibility was "+result);
                        forecastMap.put("visibility", result);
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

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        Log.i("---- TEST ----","READING???");
        String result = "";
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, ns, "value");
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "value");
        return result;
    }

}
