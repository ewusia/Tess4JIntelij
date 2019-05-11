package pl.ewa.tess4j.db;

public class Sklep implements Nameable {

    private String nazwa = "";

    public Sklep(String nazwa) {
        this.nazwa = nazwa;
    }

    public Sklep() {
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
        this.nazwa = nazwa;    }
}
