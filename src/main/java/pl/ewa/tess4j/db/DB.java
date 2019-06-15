package pl.ewa.tess4j.db;

import java.util.ArrayList;
import java.util.List;

public class DB implements Nameable{

    private String nazwa = "";
    private List<Kategoria> kategorie = new ArrayList<>();

    public DB(List<Kategoria> kategorie) {
        this.kategorie = kategorie;
    }

    public DB() {
    }

    public DB(String nazwa) {
        this.nazwa = nazwa;
    }

    public DB(String nazwa, List<Kategoria> kategorie) {
        this.nazwa = nazwa;
        this.kategorie = kategorie;
    }

    public List<Kategoria> getKategorie() {
        return kategorie;
    }

    public Kategoria getKategoria(Kategoria kategoria) {
        return kategorie.stream().filter(k -> k.equals(kategoria)).findFirst().get();
    }

    public void setKategorie(List<Kategoria> kategorie) {
        this.kategorie = kategorie;
    }

    public void addKategoria(Kategoria kategoria) {
        this.kategorie.add(kategoria);
    }

/*    @Override
    public String toString() {
        return "DB{" +
                "kategorie=" + kategorie.stream().map(k -> k.toString()) +
                '}';
    }*/
    @Override
    public String toString() {
        return nazwa;
    }


    @Override
    public String getName() {
        return nazwa;
    }

    @Override
    public void setName(String nazwa) {
        this.nazwa = nazwa;
    }


}
