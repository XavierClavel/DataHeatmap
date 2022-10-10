package com.xavierclavel.datamapping;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.WeightedLatLng;

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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XmlManager {

    static String filename = "scan_data";
    public static boolean autoUpdate = false;

    public static List<TimestampedData> dataMemory;
    static String xmlDebugFile;

    public static void Memorize(LatLng position, int nbBluetoothDevices) {
        dataMemory = dataMemory == null ? new ArrayList<>() : dataMemory;
        dataMemory.add(new TimestampedData(getTimestamp(), position, nbBluetoothDevices));
        Log.d("xml manager", "data written | total amount of data : " + dataMemory.size());
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

    public static void printXML() {
        try {
            StringWriter writer = new StringWriter();
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(writer);
            serializer.startDocument(null, Boolean.TRUE);
            writeStringData(serializer);  //reference variable or value variable
            serializer.endDocument();
            serializer.flush();
            xmlDebugFile = writer.toString();
            Log.d("xml manager", xmlDebugFile);
        } catch (Exception e) {
            Log.d("Xml manager", "failed to write data in xml file");
        }
    }

    public static void writeStringData(XmlSerializer serializer) {
        try {
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.text("\n");
            serializer.startTag("", "root");
            serializer.text("\n");
            for (TimestampedData timestampedData : dataMemory) {

                serializer.startTag("", "measurement");
                serializer.text("\n");

                serializer.startTag("", "timestamp");
                serializer.text(timestampedData.timestamp);
                serializer.endTag("", "timestamp");
                serializer.text("\n");

                serializer.startTag("", "locationData");
                serializer.text(timestampedData.position.toString());
                serializer.endTag("", "locationData");
                serializer.text("\n");

                serializer.startTag("", "bluetoothData");
                serializer.text("" + timestampedData.nbBluetoothDevices);
                serializer.endTag("", "bluetoothData");
                serializer.text("\n");

                serializer.endTag("", "measurement");
                serializer.text("\n");
            }
            serializer.endTag("", "root");
            Log.d("xml manager", "successfully wrote data");
        } catch (Exception e) {
            Log.d("xml manager", "failed to write records on xml file");
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

                serializer.startTag("", "bluetoothData");
                serializer.text("" + timestampedData.nbBluetoothDevices);
                serializer.endTag("", "bluetoothData");

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
                NodeList aps= measure.getElementsByTagName("record");
                for (int j = 0; j < aps.getLength(); j++){
                    String ap = aps.item(j).getTextContent();
                    Log.d("xml manager", " "+ap);
                }
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
}
