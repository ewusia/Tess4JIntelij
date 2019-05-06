package pl.ewa.tess4j.db;

import java.util.ArrayList;
import java.util.List;

public class Kategoria {

    private String nazwa = "";

    private List<Produkt> produkty = new ArrayList<>();

    public Kategoria() {
    }

    public Kategoria(String nazwa, List<Produkt> produkty) {
        this.nazwa = nazwa;
        this.produkty = produkty;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public List<Produkt> getProdukty() {
        return produkty;
    }

    public void setProdukty(List<Produkt> produkty) {
        this.produkty = produkty;
    }

    public void addProdukt(Produkt produkt){
        this.produkty.add(produkt);
    }

    public void removeProdukt(Produkt produkt){
        this.produkty.remove(produkt);
    }

    @Override
    public String toString() {
        return "Kategoria{" +
                "nazwa='" + nazwa + '\'' +
                ", produkty=" + produkty.stream().map(p -> p.toString()) +
                '}';
    }
}
