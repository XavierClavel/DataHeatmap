package com.xavierclavel.datamapping;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.util.Calendar;
import java.util.Date;

public class XmlManager {

    static String filename = "scan_data";
    public static boolean autoUpdate = false;

    public static List<TimestampedData> dataMemory;
    static String xmlDebugFile;

    public static void Memorize(LatLng position, int network) {
        dataMemory = dataMemory == null ? new ArrayList<>() : dataMemory;
        dataMemory.add(new TimestampedData(getTimestamp(), position, network));
        Log.d("xml manager", "data written | total amount of data : " + dataMemory.size());

        Date currentTime = Calendar.getInstance().getTime();
        Log.d("time", currentTime.toString());

        if (MainActivity.settings_keepData) Write();
    }

    public static void Write() {
        Log.d("xml manager", "starting to write data");
        //printXML();
        try {
            File dir = MainActivity.instance.getFilesDir();
            File file = new File(dir, filename);
            boolean deleted = file.delete();
            Log.d("deleted", "" + deleted);
            FileOutputStream fos = MainActivity.instance.openFileOutput(filename, Context.MODE_APPEND);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8"); //definit le fichier de sortie
            serializer.startDocument("UTF-8", true);
            writeData(serializer);  //reference variable or value variable
            serializer.endDocument();
            serializer.flush();
            fos.close();
            Log.d("xml manager", "end of writing operation");
        } catch (Exception e) {
            Log.d("Xml manager", "failed to write data in xml file");
        }
    }

    public static void writeData(XmlSerializer serializer) {
        try {
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag("", "root");
            for (TimestampedData timestampedData : dataMemory) {

                serializer.startTag("", "measurement");

                serializer.startTag("", "timestamp");
                serializer.text(timestampedData.timestamp);
                serializer.endTag("", "timestamp");

                serializer.startTag("", "locationData");
                serializer.text(timestampedData.position.toString());
                serializer.endTag("", "locationData");

                serializer.startTag("", "mobileNetworkData");
                serializer.text("" + timestampedData.network);
                serializer.endTag("", "mobileNetworkData");

                serializer.endTag("", "measurement");
            }
            serializer.endTag("", "root");
            Log.d("xml manager", "successfully wrote data");
        } catch (Exception e) {
            Log.d("xml manager", "failed to write records on xml file");
        }

    }

    public static String getTimestamp() {
        Long tsLong = System.currentTimeMillis()/1000;
        return tsLong.toString();
    }

    private static String readRawData(String filename) {
        //String string = MainActivity.instance.getString(R.string.filename);

        FileInputStream fis;
        InputStreamReader isr;
        String data = "";
        try {
            fis = MainActivity.instance.openFileInput(filename);

            isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            data = new String(inputBuffer);

            Log.d("data", data);
            //Log.i(TAG, "Read data from file " + filename);
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void Read() {
        Log.d("xml manager", "start reading data");
        //Read data string from file
        String data = readRawData(filename);
        //String data = xmlDebugFile;
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        NodeList items = null;
        Document dom;
        try {
            db = dbf.newDocumentBuilder();
            dom = db.parse(is);
            dom.getDocumentElement().normalize();
            //get all measurement tags
            items = dom.getElementsByTagName("measurement");
            Log.d("xml manager", "nb of xml measurements = " + items.getLength());
            for (int i=0; i<items.getLength(); i++){
                Element measure = (Element)items.item(i);
                //get timestamp
                String timestamp = measure.getElementsByTagName("timestamp").item(0).getTextContent();
                //for all elements in the document
                Log.d("xml manager","Measurement " + i + " with timestamp " + timestamp);
                //get all APs
                String network = measure.getElementsByTagName("mobileNetworkData").item(0).getTextContent();
                Log.d("network", network);
                int networkType =  Integer.parseInt(network);
                String location = measure.getElementsByTagName("locationData").item(0).getTextContent();
                Log.d("location", location);

                location = location.substring(location.lastIndexOf("(") + 1, location.lastIndexOf(")"));

                String[] latlong =  location.split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);

                LatLng position = new LatLng(latitude, longitude);

                Log.d("latitude", ""+latitude);
                Log.d("longitude", "" + longitude);

                HeatmapManager.addDataPoint(position, networkType);
            }
            Log.d("xml parser", "successfully read data");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<MeasurementSummary> ReadHistory() {


        File dir = MainActivity.instance.getFilesDir();
        File file = new File(dir, "history");
        //boolean deleted = file.delete();
        List<MeasurementSummary> measurementSummaries = new ArrayList<>();

        Log.d("xml manager", "start reading data");
        //Read data string from file
        String data = readRawData("history");
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        NodeList items = null;
        Document dom;
        try {
            db = dbf.newDocumentBuilder();
            dom = db.parse(is);
            dom.getDocumentElement().normalize();
            //get all measurement tags
            items = dom.getElementsByTagName("measurement");
            Log.d("xml manager", "nb of xml measurements = " + items.getLength());
            for (int i=0; i<items.getLength(); i++){
                Element measure = (Element)items.item(i);
                //get timestamp
                String date = measure.getElementsByTagName("date").item(0).getTextContent();
                String place = measure.getElementsByTagName("place").item(0).getTextContent();
                String nbPoints = measure.getElementsByTagName("nbPoints").item(0).getTextContent();
                String filename = measure.getElementsByTagName("filename").item(0).getTextContent();

                measurementSummaries.add(new MeasurementSummary(date, place, nbPoints, filename));
            }
            Log.d("xml parser", "successfully read data");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return measurementSummaries;
    }

    public static void WriteHistory(List<MeasurementSummary> measurementSummaries) {
        Log.d("xml manager", "starting to write data");
        //printXML();
        try {
            File dir = MainActivity.instance.getFilesDir();
            File file = new File(dir, "history");
            boolean deleted = file.delete();
            Log.d("deleted", "" + deleted);
            FileOutputStream fos = MainActivity.instance.openFileOutput("history", Context.MODE_APPEND);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8"); //definit le fichier de sortie
            serializer.startDocument("UTF-8", true);
            writeDataHistory(serializer, measurementSummaries);  //reference variable or value variable
            serializer.endDocument();
            serializer.flush();
            fos.close();
            Log.d("xml manager", "end of writing operation");
        } catch (Exception e) {
            Log.d("Xml manager", "failed to write data in xml file");
        }
    }

    public static void writeDataHistory(XmlSerializer serializer, List<MeasurementSummary> measurementSummaries) {
        try {
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag("", "root");
            for (MeasurementSummary measurementSummary : measurementSummaries) {

                serializer.startTag("", "measurement");

                serializer.startTag("", "date");
                serializer.text(measurementSummary.date);
                serializer.endTag("", "date");

                serializer.startTag("", "place");
                serializer.text(measurementSummary.place);
                serializer.endTag("", "place");

                serializer.startTag("", "nbPoints");
                serializer.text(measurementSummary.nbPoints);
                serializer.endTag("", "nbPoints");

                serializer.startTag("","filename");
                serializer.text(measurementSummary.filename);
                serializer.endTag("", "filename");

                serializer.endTag("", "measurement");
            }
            serializer.endTag("", "root");
            Log.d("xml manager", "successfully wrote data");
        } catch (Exception e) {
            Log.d("xml manager", "failed to write records on xml file");
        }

    }
}
