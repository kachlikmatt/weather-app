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
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;


public class WeatherActivity extends ActionBarActivity {

    final WeatherXmlParser parser = new WeatherXmlParser();
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
                        Toast.makeText(getApplicationContext(), "metric",
                                Toast.LENGTH_LONG).show();
                        break;
                case R.id.impericalRB:
                    if (checked)
                        Toast.makeText(getApplicationContext(), "imperical",
                                Toast.LENGTH_LONG).show();
                        break;
            }
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

        parser.parse(in);
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
