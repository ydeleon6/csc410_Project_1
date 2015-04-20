package com.example.deleon118.project1;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
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
    private RadioButton metricRadio, imperialRadio;

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
        metricRadio = (RadioButton)findViewById(R.id.metric);
        imperialRadio = (RadioButton)findViewById(R.id.imperial);

        go.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                populateGUI(enterZip.getText() + ".xml");
                imperialRadio.toggle();
            }
        });

        //populateGUI("60505.xml"); //initially populate using 60505.xml
        //attach the button and button handler for future handling.

    }

    //from android.developer.com, radio button
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        String getCurrUnits = temperature.getText().toString();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.metric:
                if (checked)
                    if(getCurrUnits.charAt(getCurrUnits.length() - 1) == 'F')
                        toMetric();
                break;
            case R.id.imperial:
                if (checked)
                    if(getCurrUnits.charAt(getCurrUnits.length() - 1) == 'C')
                        toImp();
                break;
        }
    }

    public void toMetric(){
        double temp, dewpoint, pres, vis, spd, gst;
        DecimalFormat df = new DecimalFormat("###0.0");

        //gets string component of text fields, removes apha chars (from StackOverflow), and parses int
        temp = Double.parseDouble(temperature.getText().toString().replaceAll("[//A-Za-z ]*", ""));
        dewpoint = Double.parseDouble(dew.getText().toString().replaceAll("[//A-Za-z ]*", ""));
        pres = Double.parseDouble(pressure.getText().toString().replaceAll("[//A-Za-z ]*", ""));
        vis = Double.parseDouble(visibility.getText().toString().replaceAll("[//A-Za-z ]*", ""));
        spd = Double.parseDouble(windspeed.getText().toString().replaceAll("[//A-Za-z ]*", ""));


        temp = (((temp - 32)*5)/9) + 0.05;
        dewpoint = (((dewpoint - 32)*5)/9) + 0.05;
        pres = (pres * 2.54) + 0.05;
        vis = (vis * 1.60934) + 0.05;
        spd = (spd * 1.60934) + 0.05;

        temperature.setText(df.format(temp) + " C");
        dew.setText(df.format(dewpoint) + " C");
        pressure.setText(df.format(pres) + " mb");
        visibility.setText(df.format(vis) + " km");
        windspeed.setText(df.format(spd) + " km/h");

        if(gust.getText().equals("NA mph")){
            gust.setText("NA km/h");
        }
        else{
            gst = Double.parseDouble(gust.getText().toString().replaceAll("[//A-Za-z ]*", ""));
            gst = (gst * 1.60934) + 0.05;
            gust.setText(df.format(gst) + "km/h");
        }
    }
    public void toImp(){
        double temp, dewpoint, pres, vis, spd, gst;
        DecimalFormat df = new DecimalFormat("###0.0");

        //gets string component of text fields, removes apha chars (from StackOverflow), and parses int
        temp = Double.parseDouble(temperature.getText().toString().replaceAll("[//A-Za-z ]*", ""));
        dewpoint = Double.parseDouble(dew.getText().toString().replaceAll("[//A-Za-z ]*", ""));
        pres = Double.parseDouble(pressure.getText().toString().replaceAll("[//A-Za-z ]*", ""));
        vis = Double.parseDouble(visibility.getText().toString().replaceAll("[//A-Za-z ]*", ""));
        spd = Double.parseDouble(windspeed.getText().toString().replaceAll("[//A-Za-z ]*", ""));

        temp = (((temp*9)/5)+32) + 0.05;
        dewpoint = (((dewpoint*9)/5)+32) + 0.05;
        pres = (pres / 2.54) + 0.05;
        vis = (vis / 1.60934) + 0.05;
        spd = (spd / 1.60934) + 0.05;

        temperature.setText(df.format(temp) + " F");
        dew.setText(df.format(dewpoint) + " F");
        pressure.setText(df.format(pres) + " in");
        visibility.setText(df.format(vis) + " mi");
        windspeed.setText(df.format(spd) + " mph");

        if(gust.getText().equals("NA km/h")){
            gust.setText("NA mph");
        }
        else{
            gst = Double.parseDouble(gust.getText().toString().replaceAll("[//A-Za-z ]*", ""));
            gst = (gst / 1.60934) + 0.05;
            gust.setText(df.format(gst) + "mph");
        }
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
        InputStream is = null;
        try {
            is = (InputStream) new URL(forecastMap.get("icon-link")).getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable d = Drawable.createFromStream(is, "src_name");
        //fill text fields
        centerPic.setImageDrawable(d);
        temperature.setText(forecastMap.get("apparent")+" F");
        dew.setText(forecastMap.get("dew")+" F");
        humidity.setText(forecastMap.get("humidity") + "%");
        displayConditions.setText(forecastMap.get("weather-summary"));
        pressure.setText(forecastMap.get("pressure") + " in");
        visibility.setText(forecastMap.get("visibility")+" mi");
        windspeed.setText(forecastMap.get("windspeed") + " mph");
        gust.setText(forecastMap.get("gust") + " mph");
        currTime.setText(" " + forecastMap.get("time").substring(11, 19));
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
                                //String array[] = result.split("\\/"); //split on /
                                //String filename = array[array.length-1]; //filename and get the name alone with .split("\\.")[0]
                                Log.i("--- TEST ---", "got the icon link");
                                Log.i("--- TEST ---", result);
                                //Log.i("--- TEST ---",filename);
                                forecastMap.put("icon-link",result);
                            }
                            break;
                            case "start-valid-time":{
                                String result = readText(parser, tagname);
                                forecastMap.put("time", result);
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
