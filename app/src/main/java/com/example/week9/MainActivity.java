package com.example.week9;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public Spinner theaters;
    NodeList cityList;
    DocumentBuilder builder = null;
    Theathers teatteri = Theathers.getInstance();
    Theater tt;
    int ttid, id;
    String date, nimi, toimipiste, startime, endtime, alkuaika, loppuaika, moviename, kaupunki;
    public ListView lv;
    ArrayList<String> showArray = new ArrayList<>();
    ArrayList<String> cityShowArray = new ArrayList<>();
    EditText dateInsert, startH, endH, movieSelected;
    Button searchB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.Movies);
        theaters = (Spinner) findViewById(R.id.theaters);
        dateInsert = (EditText) findViewById(R.id.giveDate);
        startH = (EditText) findViewById(R.id.giveStartHour);
        endH = (EditText) findViewById(R.id.giveEndHour);
        movieSelected = (EditText) findViewById(R.id.insertMovieName);
        searchB = (Button) findViewById(R.id.searchButton);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        readXML();

        theaters.setOnItemSelectedListener(this);

        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDateAndTime();
            }
        });

    }
    public boolean getMovieName(){
        moviename = movieSelected.getText().toString();
        if (moviename.isEmpty()){
            return false;
        }
        return true;
    }


        public void getDateAndTime(){
        date = dateInsert.getText().toString();
        startime = startH.getText().toString();
        endtime = endH.getText().toString();
        if(date.isEmpty()){
           SimpleDateFormat dF = new SimpleDateFormat("dd.MM.yyyy");
           date = dF.format(Calendar.getInstance().getTime());
        }
        if(startime.isEmpty()){
            startime = "07:00";
        }
        if(endtime.isEmpty()){
            endtime = "23:59";
        }
        if((getMovieName() == true) && toimipiste.equals("Valitse alue/teatteri")){
            getCitySch();
        }
        else if(getMovieName() == true){
            getCitySch();
        }
        else{
        getSchedule();
        }}


        public void readXML(){
            try {
                builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                String urlString = "https://www.finnkino.fi/xml/TheatreAreas/";
                Document doc = builder.parse(urlString);
                doc.getDocumentElement().normalize();
                NodeList cityList = doc.getDocumentElement().getElementsByTagName("TheatreArea");
                for(int i = 0; i<cityList.getLength(); i++) {
                    Node node = cityList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        id = Integer.parseInt(element.getElementsByTagName("ID").item(0).getTextContent());
                        nimi = element.getElementsByTagName("Name").item(0).getTextContent();
                        teatteri.addTheater(nimi, id);
                    }
                }

                ArrayAdapter<Theater> theaterAdapter = new ArrayAdapter<Theater>(this, android.R.layout.simple_spinner_dropdown_item, teatteri.teatterit);
                theaterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                theaters.setAdapter(theaterAdapter);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("########################DONE############################");
            }
        }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            theaters = (Spinner)parent;
            if(theaters.getId() == R.id.theaters) {
                toimipiste = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < teatteri.teatterit.size(); i++) {
                    tt = teatteri.teatterit.get(i);
                    if (tt.getName().equals(toimipiste)) {
                        ttid = tt.getId();
                    }
                }
            }
        }
        // Kysy aluksi haluttu päivämäärä, ja sen jälkeen näytä tiedot.
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void getCitySch() {
        try {
            cityShowArray.clear();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String info;
            Date b, e, b2, e2;
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            for (int i = 0; i < teatteri.teatterit.size(); i++) {
                ttid = teatteri.teatterit.get(i).getId();
                String url = "https://www.finnkino.fi/xml/Schedule/?area=" + ttid + "&dt=" + date;
                Document doc = builder.parse(url);
                doc.getDocumentElement().normalize();
                NodeList cshowList = doc.getDocumentElement().getElementsByTagName("Show");
                for (int x = 0; x < cshowList.getLength(); x++) {
                    Node cnode = cshowList.item(x);
                    if (cnode.getNodeType() == Node.ELEMENT_NODE) {
                        Element celement = (Element) cnode;
                        nimi = celement.getElementsByTagName("Title").item(0).getTextContent();
                        alkuaika = (celement.getElementsByTagName("dttmShowStart").item(0).getTextContent()).substring(11, 16);
                        loppuaika = (celement.getElementsByTagName("dttmShowEnd").item(0).getTextContent()).substring(11, 16);
                        kaupunki = celement.getElementsByTagName("Theatre").item(0).getTextContent();
                        b = sdf.parse(alkuaika);
                        e = sdf.parse(loppuaika);
                        b2 = sdf.parse(startime);
                        e2 = sdf.parse(endtime);
                        if (nimi.equals(moviename)) {
                            if (b2.before(b) && e2.after(e)) {
                                info = "Kaupunki " + kaupunki + " elokuva " + nimi + " kello " + alkuaika + "-" + loppuaika + ".";
                                cityShowArray.add(info);
                            } else if (b2.equals(b) && e2.after(e)) {
                                info = "Kaupunki " + kaupunki + " elokuva " + nimi + " kello " + alkuaika + "-" + loppuaika + ".";
                                cityShowArray.add(info);
                            } else if (b2.before(b) && e2.equals(e)) {
                                info = "Kaupunki " + kaupunki + " elokuva " + nimi + " kello " + alkuaika + "-" + loppuaika + ".";
                                cityShowArray.add(info);
                            } else if (b2.equals(b) && e2.equals(e)) {
                                info = "Kaupunki " + kaupunki + " elokuva " + nimi + " kello " + alkuaika + "-" + loppuaika + ".";
                                cityShowArray.add(info);
                            }
                        }
                    }
                }
                if (cityShowArray.isEmpty()) {
                    ArrayList<String> errorMsg = new ArrayList<>();
                    String text = "Valitettavasti valitulla elokuvalla teatterilla ja päivämäärällä ei ole elokuvatarjontaa, ole hyvä ja kokeile toinen päivä";
                    errorMsg.add(text);
                    ArrayAdapter movieAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, errorMsg);
                    lv.setAdapter(movieAdapter);
                } else{
                    ArrayAdapter movieAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, cityShowArray);
                    lv.setAdapter(movieAdapter);
                }
                lv.setVisibility(View.VISIBLE);
                lv.deferNotifyDataSetChanged();
                }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    //https://www.finnkino.fi/xml/Schedule/?area=<teatterinID>&dt=<päivämäärä pp.kk.vvvv>

    public void getSchedule(){
        try {
            String info;
            Date b,e,b2,e2;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            showArray.clear();
                builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                String url = "https://www.finnkino.fi/xml/Schedule/?area=" + ttid + "&dt=" + date;
                System.out.println(url);
                Document doc = builder.parse(url);
                doc.getDocumentElement().normalize();
                NodeList showList = doc.getDocumentElement().getElementsByTagName("Show");
                for (int i = 0; i < showList.getLength(); i++) {
                    Node snode = showList.item(i);
                    if (snode.getNodeType() == Node.ELEMENT_NODE) {
                        Element selement = (Element) snode;
                        nimi = selement.getElementsByTagName("Title").item(0).getTextContent();
                        alkuaika = (selement.getElementsByTagName("dttmShowStart").item(0).getTextContent()).substring(11, 16);
                        loppuaika = (selement.getElementsByTagName("dttmShowEnd").item(0).getTextContent()).substring(11, 16);
                        b = sdf.parse(alkuaika);
                        e = sdf.parse(loppuaika);
                        b2 = sdf.parse(startime);
                        e2 = sdf.parse(endtime);
                        if (b2.before(b) && e2.after(e)) {
                            info = "Elokuva " + nimi + " kello " + alkuaika + "-" + loppuaika + ".";
                            showArray.add(info);
                        } else if (b2.equals(b) && e2.after(e)) {
                            info = "Elokuva " + nimi + " kello " + alkuaika + "-" + loppuaika + ".";
                            showArray.add(info);
                        } else if (b2.before(b) && e2.equals(e)) {
                            info = "Elokuva " + nimi + " kello " + alkuaika + "-" + loppuaika + ".";
                            showArray.add(info);
                        } else if (b2.equals(b) && e2.equals(e)) {
                            info = "Elokuva " + nimi + " kello " + alkuaika + "-" + loppuaika + ".";
                            showArray.add(info);
                        }
                    }
                }
                if (showArray.isEmpty()) {
                    ArrayList<String> errorMsg = new ArrayList<>();
                    String text = "Valitettavasti valitulla teatterilla ja päivämäärällä ei ole elokuvatarjontaa, ole hyvä ja kokeile toinen päivä";
                    errorMsg.add(text);
                    ArrayAdapter movieAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, errorMsg);
                    lv.setAdapter(movieAdapter);
                } else {
                    ArrayAdapter movieAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, showArray);
                    lv.setAdapter(movieAdapter);
                }
            lv.setVisibility(View.VISIBLE);
            lv.deferNotifyDataSetChanged();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
