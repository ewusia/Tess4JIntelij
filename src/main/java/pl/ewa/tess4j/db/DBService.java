package pl.ewa.tess4j.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class DBService {

    static File dbLocation = new File(System.getProperty("user.home") + File.separator + "db.json");
    static ObjectMapper mapper = new ObjectMapper();
    static DB db;

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static DB getDb() {
        if (db == null) {
            try {
                db = mapper.readValue(dbLocation, DB.class);
            } catch (IOException e) {
                throw new DBException(e);
            }

            //to wywoła zapisanie bazy przed wylaczeniem aplikacji
            Runtime.getRuntime().addShutdownHook(new Thread(() -> saveDB()));
        }
        return db;
    }

    public static void saveDB() {
        try {
            mapper.writeValue(dbLocation, db);
        } catch (IOException e) {
            throw new DBException(e);
        }
    }

    public static void cleanDB() {
        getDb().getKategorie().clear();
    }

    public static void main(String[] args) throws Exception {

        DB db = getDb();
        db.getKategorie().get(0).setNazwa("Mrozonki");
    }

}
