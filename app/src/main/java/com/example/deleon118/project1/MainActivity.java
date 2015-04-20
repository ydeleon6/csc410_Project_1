package com.example.deleon118.project1;

import android.content.Context;
import android.content.res.AssetManager;
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
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity {
    //declare the fields you'll need for the parser
    private InputStream input;
    private XmlPullParserFactory factory;
    private XmlPullParser xml;
    private String ns = null;
    private HashMap<String, String> forecastMap; //initialize map

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
    private final int IMPERIAL = 0;
    private final int METRIC = 1;
    private int currentSystem = IMPERIAL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //instantiate all of the fields
        go = (Button)findViewById(R.id.button);
        centerPic = (ImageView)findViewById(R.id.pic);
        displayConditions = (TextView)findViewById(R.id.displayConditions);
        //enterZip = (EditText)findViewById(R.id.editZip);
        temperature = (TextView)findViewById(R.id.displayTemp);
        dew = (TextView)findViewById(R.id.displayDew);
        humidity = (TextView)findViewById(R.id.displayHumidity);
        pressure = (TextView)findViewById(R.id.displayPressure);
        visibility = (TextView)findViewById(R.id.displayVisibility);
        windspeed = (TextView)findViewById(R.id.displayWindspeed);
        gust = (TextView)findViewById(R.id.displayGust);
        currTime= (TextView)findViewById(R.id.currentTimeDisplay);
        /*
        60505 = Aurora, IL
        89044 = Las Vegas, NV
        80301 = Boulder, CO
        93922 = Carmel, CA
         */
        //populateGUI("60505"); //initially populate using 60505.xml
        //attach the button and button handler for future handling.
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //first, get the value of the zip code thing
                String zip = getZipOrToast();
                //do the damn thang
                if(zip != null) {
                    populateGUI(zip);
                }
                else {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,"ERROR: input one of the 5 valid zipcodes.",duration);
                    toast.show();
                }
            }
        });

    }
    public String getZipOrToast(){
        enterZip = (EditText)findViewById(R.id.editZip);
        String zip = enterZip.getText().toString().trim();

        if(zip.equals("60505") || zip.equals("80301")||
                zip.equals("93922") || zip.equals("89044")){
            return zip;
        }
        return null;
    }
    public void convert(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton)view).isChecked();
        String zip = getZipOrToast();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.metric:
                if (checked){
                    currentSystem = METRIC;
                }
                    break;
            case R.id.imperial:
                if (checked){
                    currentSystem = IMPERIAL;
                }
                   break;
        }
        populateGUI(zip);
    }
    private void populateGUI(String zip){

        forecastMap = new HashMap<>(); //initialize map
        try{
            //create parser
            String zipXML = zip+".xml";
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xml = factory.newPullParser();
            Log.i("--- TEST ---", "opening file: "+zipXML);
            input = getAssets().open(zipXML, AssetManager.ACCESS_BUFFER);
            xml.setInput(input, null);
            Log.i("--- TEST ---", "About to enter the parser");
            parse(xml); //populate yo map
            input.close(); //close stream
        }
        catch(Exception e){
            e.printStackTrace();
            Log.i("--- TEST ---", "ERROR"+e.toString());
        }
        //got this from here: http://stackoverflow.com/questions/11737607/how-to-set-the-image-from-drawable-dynamically-in-android
        int res = getResources().getIdentifier("@drawable/"+forecastMap.get("icon-link"), "drawable", this.getPackageName());
        centerPic.setImageResource(res);
        displayConditions.setText(forecastMap.get("weather-summary"));
        currTime.setText(forecastMap.get("currentTime"));
        humidity.setText(forecastMap.get("humidity") + "%");
        gust.setText(forecastMap.get("gust"));
        Log.i("--- TEST ---", "CONVERSION");
        //convert
        DecimalFormat df2 = new DecimalFormat("##0.00");
        double temp = Double.parseDouble(forecastMap.get("apparent"));
        double dewTemp = Double.parseDouble(forecastMap.get("dew"));
        double mercury = Double.parseDouble(forecastMap.get("pressure"));
        double visDist = Double.parseDouble(forecastMap.get("visibility"));
        double windSpeed = Double.parseDouble(forecastMap.get("windspeed"));
        //F to C
        Log.i("--- TEST ---", "No parsing errors");
        if(currentSystem == METRIC) {
            temp = Double.valueOf( df2.format(((temp - 32) * 5) / 9) );
            dewTemp = Double.valueOf( df2.format(((dewTemp - 32) * 5) / 9) );
            mercury = Double.valueOf( df2.format(mercury * 25.4) ); //inches to mm
            windSpeed = Double.valueOf( df2.format(windSpeed * 1.852) ) ; //kn to km/h
            visDist = Double.valueOf( df2.format(visDist * 1.609) );
            temperature.setText(temp+" C");
            dew.setText(dewTemp+" C");
            pressure.setText(mercury + " mm");
            visibility.setText(visDist+" km");
            windspeed.setText(windSpeed + " km/h");
        }
        else {
            temperature.setText(temp + " F");
            dew.setText(dewTemp + " F");
            pressure.setText(mercury + " in");
            visibility.setText(forecastMap.get("visibility") + " mi");
            windspeed.setText(windSpeed + " kn");
        }

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
    int eventType = parser.getEventType();
        boolean check = false;
        //until you get to the end of the doc
        while (eventType != XmlPullParser.END_DOCUMENT) {
        //switch on event type
        switch (eventType) {
            case XmlPullParser.START_TAG:
                String tagname = parser.getName();
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
                            forecastMap.put("dew", contents);
                        } else if (attr.equals("apparent")) {
                            String contents = readText(parser,"value");
                            forecastMap.put("apparent", contents);
                        }
                    }
                        break;
                    case "direction":
                    case "pressure":
                    case "humidity": {
                        String result = readText(parser, "value");
                        forecastMap.put(tagname,result);
                    }
                    case "weather-conditions":{
                        if(parser.getAttributeValue(ns, "weather-summary") != null){
                            //its the other thing
                            String attr = parser.getAttributeValue(ns, "weather-summary");
                            forecastMap.put("weather-summary",attr);
                        }
                        else if(tagname.equals("weather-conditions")){
                            //next tag is value. Unfortunately, the value is in visibility
                        }
                    }
                    break;
                    case "icon-link": {
                        String result = readText(parser, tagname);
                        String array[] = result.split("\\/"); //split on /
                        String filename = array[array.length-1].split("\\.")[0]; //just name, no extension
                        forecastMap.put("icon-link",filename);
                    }
                    break;
                    case "visibility": {
                        String result = readText(parser, "visibility");
                        forecastMap.put("visibility", result);
                    }
                    break;
                    case "start-valid-time":{
                        String result = readText(parser, "start-valid-time");
                        forecastMap.put("currentTime", result);
                    }
                    break;
                    case "wind-speed":{
                        String result = "";
                        if(parser.getAttributeValue(ns, "type").equals("gust")){
                            result = readText(parser,"value");
                            forecastMap.put("gust", result);
                        }
                        else if(parser.getAttributeValue(ns,"type").equals("sustained")){
                            result = readText(parser,"value");
                            forecastMap.put("windspeed", result);
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
        String result = "";
        if(tag.equals("value")) {
            parser.nextTag(); //next tag will actually be <value>
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