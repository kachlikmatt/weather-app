package edu.noctrl.kachlik.vic.weatherapp;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;


public class WeatherActivity extends ActionBarActivity {

    final WeatherXmlParser parser = new WeatherXmlParser();
    WeatherXmlParser.Entry weatherEntry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        final Button button = (Button) findViewById(R.id.goBTN);

        button.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                EditText mEdit = (EditText) findViewById(R.id.zipField);
                String zip = mEdit.getText().toString();
                try {
                    assetChooser(zip);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        });
    }
        public void onRadioButtonClicked(View view) {
            // Is the button now checked?
            boolean checked = ((RadioButton) view).isChecked();

            // Check which radio button was clicked
            switch(view.getId()) {
                case R.id.metricRb:
                    if (checked)
                        metricConverter();
                        break;
                case R.id.impericalRB:
                    if (checked)
                        imperialConverter();
                        break;
            }
        }
    //converts from imperial to metric
    private void  metricConverter()
    {
        double temp = weatherEntry.temperature;
        double dewPoint = weatherEntry.dewPoint;
        double visibility = weatherEntry.visibility;
        double windSpeed = weatherEntry.windSpeed;
        double gusts = weatherEntry.gustSpeed;
        double pressure = weatherEntry.pressure;
        int humidity = weatherEntry.humidity;

        String windD = "";
        int xmlWindD = Integer.parseInt(weatherEntry.windDirection);
        if(xmlWindD <= 360 && xmlWindD >= 271)
        {
            windD = "N";
        }
        else if(xmlWindD <= 270 && xmlWindD >= 181)
        {
            windD = "W";
        }
        else if (xmlWindD <= 180 && xmlWindD >= 90)
        {
            windD = "S";
        }
        else
        {
            windD = "E";
        }


        temp = (temp -32) * (5/9.0);
        dewPoint = (dewPoint -32) * (5/9.0);
        visibility = visibility * 1.6;
        windSpeed = windSpeed * 1.6;
        gusts = gusts * 1.6;
        pressure = pressure * 25.4;
        
        TextView t =  (TextView)findViewById(R.id.temp);
        t.setText(temp + "C");

        t = (TextView)findViewById(R.id.dewPointTemp);
        t.setText(dewPoint + "C");
        t =  (TextView)findViewById(R.id.visibility);
        t.setText(visibility + "km");
        t = (TextView)findViewById(R.id.windSpeed);
        t.setText(windD+" @ " +windSpeed + "kmh");
        t = (TextView)findViewById(R.id.gusts);
        t.setText(gusts + "kmh");
        t =  (TextView)findViewById(R.id.pressure);
        t.setText(pressure + "mm");
        t =  (TextView)findViewById(R.id.humidity);
        t.setText(humidity + "%");




    }
    //uses saved values from xml to be placed back
    private void imperialConverter()
    {
        double temp = weatherEntry.temperature;
        double dewPoint = weatherEntry.dewPoint;
        double visibility = weatherEntry.visibility;
        double windSpeed = weatherEntry.windSpeed;
        double gusts = weatherEntry.gustSpeed;
        double pressure = weatherEntry.pressure;
        int humidity = weatherEntry.humidity;
        String windD = "";
        int xmlWindD = Integer.parseInt(weatherEntry.windDirection);
        if(xmlWindD <= 360 && xmlWindD >= 271)
        {
            windD = "N";
        }
        else if(xmlWindD <= 270 && xmlWindD >= 181)
        {
            windD = "W";
        }
        else if (xmlWindD <= 180 && xmlWindD >= 90)
        {
            windD = "S";
        }
        else
        {
            windD = "E";
        }

        TextView t =  (TextView)findViewById(R.id.temp);
        t.setText(temp + "F");

        t = (TextView)findViewById(R.id.dewPointTemp);
        t.setText(dewPoint + "F");
        t =  (TextView)findViewById(R.id.visibility);
        t.setText(visibility + "mi");
        t = (TextView)findViewById(R.id.windSpeed);

        t.setText(windD+" @ " + windSpeed + "mph");
        t = (TextView)findViewById(R.id.gusts);
        t.setText(gusts + "mph");
        t =  (TextView)findViewById(R.id.pressure);
        t.setText(pressure + "in");
        t =  (TextView)findViewById(R.id.humidity);
        t.setText(humidity + "%");
        t =  (TextView)findViewById(R.id.currCondition);
        t.setText(weatherEntry.currentCondition + "blank");
        t= (TextView)findViewById(R.id.currLocation);
        t.setText((CharSequence) weatherEntry.areaDescription);
        t =  (TextView)findViewById(R.id.currTime);
        Calendar rightNow = Calendar.getInstance();
        t.setText(rightNow.getTime() + "");
    }

    /*

    assetChooser
    finds which assest the user wants
    based off zipcode then parses that file
     */
    public void assetChooser(String zipCode) throws IOException, XmlPullParserException {

        String city = "";
        final String BUENAVISTA = "buenaVista";
        final String FORTWAYNE = "fortWayne";
        final String HOLLYWOOD = "hollywood";
        final String LINCOLNWOOD = "lincolnwood";
        final String MERRILFIELD = "merrilField";


        if(zipCode.equals("32880"))
        {
            city = BUENAVISTA;
        }
        else if(zipCode.equals("46825"))
        {
            city = FORTWAYNE;
        }
        else if(zipCode.equals("90210"))
        {
            city = HOLLYWOOD;
        }
        else if(zipCode.equals("60640"))
        {
            city = LINCOLNWOOD;
        }
        else
        {
            city = MERRILFIELD;
        }
        city += ".xml";
        AssetManager assetManager = getAssets();
        InputStream in = assetManager.open(city);


         weatherEntry = (WeatherXmlParser.Entry) parser.parse(in).get(0);


        imperialConverter();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather, menu);
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
}
