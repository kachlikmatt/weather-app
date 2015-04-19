package edu.noctrl.kachlik.vic.weatherapp;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 4/18/2015.
 */
public class WeatherXmlParser {

    // We don't use namespaces
    private static final String ns = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try
        {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "dwml");
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("data"))
            {
                String type = parser.getAttributeValue(null, "type");
                if (type.equals("current observations"))
                    entries.add(readEntry(parser));
            } else
            {
                skip(parser);
            }

            /*String link = "";
            parser.require(XmlPullParser.START_TAG, ns, "link");
            String tag = parser.getName();
            String relType = parser.getAttributeValue(null, "rel");
            if (tag.equals("link")) {
                if (relType.equals("alternate")){
                    link = parser.getAttributeValue(null, "href");
                    parser.nextTag();
                }
            }
            parser.require(XmlPullParser.END_TAG, ns, "link");
            return link;*/



        }
        return entries;
    }

    public static class Entry
    {
        public final String areaDescription;
        public final String currentCondition;
        public final Integer temperature;
        public final Integer dewPoint;
        public final Integer humidity;
        public final Integer pressure;
        public final Integer visibility;
        public final Integer windSpeed;
        public final String windDirection;
        public final Integer gustSpeed;

        private Entry(String areaDescription, String currentCondition, Integer temperature,
                      Integer dewPoint, Integer humidity, Integer pressure, Integer visibility,
                      Integer windSpeed, String windDirection, Integer gustSpeed)
        {
            this.areaDescription = areaDescription;
            this.currentCondition = currentCondition;
            this.temperature = temperature;
            this.dewPoint = dewPoint;
            this.humidity = humidity;
            this.pressure = pressure;
            this.visibility = visibility;
            this.windSpeed = windSpeed;
            this.windDirection = windDirection;
            this.gustSpeed = gustSpeed;
        }
    }

    // Parses the contents of a data tag. If it encounters the desired tags, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "data");

        /*String title = null;
        String summary = null;
        String link = null;*/

        String areaDescription = null,
               currentCondition = null,
               windDirection = null;

        Integer temperature = null,
                dewPoint = null,
                humidity = null,
                pressure = null,
                visibility = null,
                windSpeed = null,
                gustSpeed = null;

        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            switch(name)
            {
                case "area-descripion":
                    areaDescription = readAreaDescription(parser);
                    break;
                case "weather-conditions":
                    currentCondition = readCondition(parser);
                    break;
                case "direction":
                    windDirection = readDirection(parser);
                    break;
                case "temperature":
                    temperature = readTemperature(parser);
                    dewPoint = readDewPoint(parser);
                    break;
                case "humidity":
                    humidity = readHumidity(parser);
                    break;
                case "pressure":
                    pressure = readPressure(parser);
                    break;
                case "visibility":
                    visibility = readVisibility(parser);
                    break;
                case "wind-speed":
                    windSpeed = readWindSpeed(parser);
                    gustSpeed = readGustSpeed(parser);
                    break;
                default:
                    skip(parser);
            }
        }
        return new Entry(areaDescription, currentCondition, temperature, dewPoint, humidity,
                         pressure, visibility, windSpeed, windDirection, gustSpeed);
    }

    private Integer readGustSpeed(XmlPullParser parser) {
        return null;
    }

    private Integer readWindSpeed(XmlPullParser parser) {
        return null;
    }

    private Integer readVisibility(XmlPullParser parser) {
        return null;
    }

    private Integer readPressure(XmlPullParser parser) {
        return null;
    }

    private Integer readHumidity(XmlPullParser parser) {
        return null;
    }

    private Integer readDewPoint(XmlPullParser parser) {
        return null;
    }

    private Integer readTemperature(XmlPullParser parser) {
        return null;
    }

    private String readDirection(XmlPullParser parser) {
        return null;
    }

    private String readCondition(XmlPullParser parser) throws IOException, XmlPullParserException {

        return null;
    }

    private String readAreaDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        return null;
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")){
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    // Processes summary tags in the feed.
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "summary");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "summary");
        return summary;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}

