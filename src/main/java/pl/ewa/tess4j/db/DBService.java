package pl.ewa.tess4j.db;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DBService {

    static File dbLocation = new File(System.getProperty("user.home")+File.separator+"db.json");
    static ObjectMapper mapper = new ObjectMapper();
    static DB db;

    public static DB getDb(){
        if(db == null) {
            try {
                db = mapper.readValue(dbLocation, DB.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }return db;
    }

    public static void saveDB(){
        try {
            mapper.writeValue(dbLocation,db);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        saveDB();
    }

    public static void main(String[] args) throws Exception {

        DB db = getDb();
        db.getKategorie().get(0).setNazwa("huj");
        saveDB();

        /*Sklep auchan = new Sklep("Auhan");
        Produkt laciate = new Produkt("Laciate", 4.0f, Arrays.asList(auchan));
        Produkt mars = new Produkt("Mars", 1.0f, Arrays.asList(auchan));

        Kategoria slodyczne = new Kategoria("Slodycze", Arrays.asList(mars));
        Kategoria nabial = new Kategoria("Nabial", Arrays.asList(laciate));


        DB db = new DB(Arrays.asList(slodyczne,nabial));


        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(dbLocation, db);*/
    }


}
