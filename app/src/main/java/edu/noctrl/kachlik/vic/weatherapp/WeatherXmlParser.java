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
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();
        parser.require(XmlPullParser.START_TAG, ns, "dwml");
        while (parser.next() != XmlPullParser.END_TAG && parser.getEventType() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("data")) {
                String type = parser.getAttributeValue(null, "type");

                if (type.equals("current observations"))
                    entries.add(readEntry(parser));
                else
                    skip(parser);

            } else {
                skip(parser);
            }

        }
        return entries;
    }

    public static class Entry {
        public final String areaDescription;
        public final String currentCondition;
        public final Double temperature;
        public final Double dewPoint;
        public final Integer humidity;
        public final Double pressure;
        public final Integer visibility;
        public final Integer windSpeed;
        public final String windDirection;
        public final Integer gustSpeed;
        public final String imageUrl;

        private Entry(String areaDescription, String currentCondition, Double temperature,
                      Double dewPoint, Integer humidity, Double pressure, Integer visibility,
                      Integer windSpeed, String windDirection, Integer gustSpeed, String imageUrl) {
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
            this.imageUrl = imageUrl;
        }
    }

    // Parses the contents of a data tag. If it encounters the desired tags, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        String areaDescription = null,
                currentCondition = null,
                windDirection = null,
                tagType = null,
                name = null,
                imageUrl = null;

        Integer
                humidity = null,
                visibility = null,
                windSpeed = null,
                gustSpeed = null;


        Double pressure = null,
                temperature = null,
                dewPoint = null;
        parser.require(XmlPullParser.START_TAG, ns, "data");
        nextStartTag(parser);

        while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG || parser.getName() == null) {
                continue;
            }

            name = parser.getName();
            switch (name) {
                case "area-description":
                    areaDescription = readAreaDescription(parser);
                    break;
                case "weather-conditions":
                    currentCondition = readCondition(parser);
                    break;
                case "direction":
                    windDirection = readDirection(parser);
                    break;
                case "temperature":
                    tagType = parser.getAttributeValue(null, "type");
                    if (tagType.equals("apparent"))
                        temperature = readTemperature(parser);
                    else if (tagType.equals("dew point"))
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
                    tagType = parser.getAttributeValue(null, "type");
                    if (tagType.equals("sustained"))
                        windSpeed = readWindSpeed(parser);
                    else if (tagType.equals("gust"))
                        gustSpeed = readGustSpeed(parser);
                    break;
                case "icon-link":
                    imageUrl = readImage(parser);
                    break;
            }

            nextStartTag(parser);
        }

        return new Entry(areaDescription, currentCondition, temperature, dewPoint, humidity,
                pressure, visibility, windSpeed, windDirection, gustSpeed, imageUrl);
    }

    private Integer readGustSpeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        Integer gustSpeed = null;
        parser.require(XmlPullParser.START_TAG, ns, "wind-speed");
        nextStartTag(parser); //move to the inner value tag
        parser.require(XmlPullParser.START_TAG, ns, "value");
        try {
            gustSpeed = Integer.parseInt(readText(parser));
        } catch (Exception e) {
            gustSpeed = 0;
        }
        return gustSpeed;
    }

    private Integer readWindSpeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        Integer windSpeed = null;
        parser.require(XmlPullParser.START_TAG, ns, "wind-speed");
        nextStartTag(parser); //move to the inner value tag
        parser.require(XmlPullParser.START_TAG, ns, "value");
        try {
            windSpeed = Integer.parseInt(readText(parser));
        } catch (Exception e) {
            windSpeed = 0;
        }
        return windSpeed;
    }

    private Integer readVisibility(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "visibility");
        Double visibility = Double.parseDouble(readText(parser));
        return visibility.intValue();
    }

    private Double readPressure(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "pressure");
        nextStartTag(parser); //move to the inner value tag
        parser.require(XmlPullParser.START_TAG, ns, "value");
        Double pressure = Double.parseDouble(readText(parser));
        return pressure;
    }

    private Integer readHumidity(XmlPullParser parser) throws IOException, XmlPullParserException {
        Integer humidity = 0;
        parser.require(XmlPullParser.START_TAG, ns, "humidity");
        nextStartTag(parser); //move to the inner value tag
        parser.require(XmlPullParser.START_TAG, ns, "value");
        humidity = Integer.parseInt(readText(parser));
        return humidity;
    }

    private Double readDewPoint(XmlPullParser parser) throws IOException, XmlPullParserException {
        Double dewPoint = 0.0;
        parser.require(XmlPullParser.START_TAG, ns, "temperature");
        String tagType = parser.getAttributeValue(null, "type");
        nextStartTag(parser); //move to the inner value tag
        parser.require(XmlPullParser.START_TAG, ns, "value");
        dewPoint = Double.parseDouble(readText(parser));
        return dewPoint;
    }

    private Double readTemperature(XmlPullParser parser) throws IOException, XmlPullParserException {
        Double temperature = null;
        parser.require(XmlPullParser.START_TAG, ns, "temperature");
        String tagType = parser.getAttributeValue(null, "type");
        nextStartTag(parser);
        parser.require(XmlPullParser.START_TAG, ns, "value");
        temperature = Double.parseDouble(readText(parser));
        return temperature;
    }

    private String readDirection(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "direction");
        nextStartTag(parser); //move to the inner value tag
        parser.require(XmlPullParser.START_TAG, ns, "value");
        String windDirection = readText(parser);
        return windDirection;
    }

    private String readCondition(XmlPullParser parser) throws IOException, XmlPullParserException {
        String condition = "";
        parser.require(XmlPullParser.START_TAG, ns, "weather-conditions");
        String tagType = parser.getAttributeValue(null, "weather-summary");
        if (tagType != null)
            condition = readText(parser);
        return condition;
    }

    private String readAreaDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "area-description");
        String areaDescription = readText(parser);
        return areaDescription;
    }

    private String readImage(XmlPullParser parser) throws IOException, XmlPullParserException {
        String iconUrl = "";

        parser.require(XmlPullParser.START_TAG, ns, "icon-link");
        iconUrl = readText(parser);
        return iconUrl;
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

    private void nextStartTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        int tag = parser.next();

        while (tag != XmlPullParser.START_TAG && tag != XmlPullParser.END_DOCUMENT)
            tag = parser.next();
    }

    private void nextEndTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        while (parser.next() != XmlPullParser.END_TAG) ;
    }
}

