package pl.ewa.tess4j.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Produkt implements Nameable {

    private String nazwa = "";
    private float cena = 0.0f;
    private List<Sklep> sklepy = new ArrayList<>();


    public Produkt(String nazwa, float cena, List<Sklep> sklepy) {
        this.nazwa = nazwa;
        this.cena = cena;
        this.sklepy = sklepy;
    }

    public Produkt() {
    }

    public Produkt(String nazwa) {
        this.nazwa = nazwa;
    }

    public float getCena() {
        return cena;
    }

    public void setCena(float cena) {
        this.cena = cena;
    }

    public List<Sklep> getSklepy() {
        return sklepy;
    }

    public Optional<Sklep> getSklep(String sklep) {
        return this.sklepy.stream().filter(s -> s.getName().equals(sklep)).findFirst();
    }

    public void setSklepy(List<Sklep> sklepy) {
        this.sklepy = sklepy;
    }

    public void addSklep(Sklep sklep) {
        this.sklepy.add(sklep);
    }

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
