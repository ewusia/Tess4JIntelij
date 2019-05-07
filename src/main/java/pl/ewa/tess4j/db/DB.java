package pl.ewa.tess4j.db;

import java.util.ArrayList;
import java.util.List;

public class DB {

    private List<Kategoria> kategorie = new ArrayList<>();

    public DB(List<Kategoria> kategorie) {
        this.kategorie = kategorie;
    }

    public DB() {
    }

    public List<Kategoria> getKategorie() {
        return kategorie;
    }

    public void setKategorie(List<Kategoria> kategorie) {
        this.kategorie = kategorie;
    }

    public void addKategoria(Kategoria kategoria) {
        this.kategorie.add(kategoria);
    }

    @Override
    public String toString() {
        return "DB{" +
                "kategorie=" + kategorie.stream().map(k -> k.toString()) +
                '}';
    }
}
