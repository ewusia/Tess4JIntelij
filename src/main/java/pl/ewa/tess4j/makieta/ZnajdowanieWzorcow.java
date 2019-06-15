package pl.ewa.tess4j.makieta;

import pl.ewa.tess4j.db.Sklep;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZnajdowanieWzorcow {

    String text;

    public ZnajdowanieWzorcow(String text) {
        this.text = text;
    }

    public String znajdzParagon(String text) throws IOException {

        String foundPattern = null;
        Pattern pattern = Pattern.compile("P[AH]R[AH]GON");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            foundPattern = matcher.group(0);
            //System.out.println(foundPattern);
        } else {
            //System.out.println("Nie znaleziono wzorca");
        }
        return foundPattern;
    }

    public String znajdzSprzed(String text) throws IOException {

        String sprzed = null;
        Pattern patternSprzed = Pattern.compile("Sprzed");
        Matcher matcherSprzed = patternSprzed.matcher(text);
        if (matcherSprzed.find()) {
            sprzed = matcherSprzed.group(0);
            //foundPattern = "---";
            //System.out.println(foundPattern);
        } else {
            //System.out.println("Nie znaleziono wzorca");
        }
        return sprzed;
    }

    public static String znajdzSklep(String text) throws IOException {

        //String sklep = "";
        String[] ary = text.split(" ");
        String sklep = ary[0];
        //System.out.println("Nie znaleziono wzorca");

        return sklep;
    }

    public static void main(String[] args) throws IOException {

        String text = "BIEDRONKA \"CODZIENNIE NISKIE CENY” 2028\n" +
                "40-007 Katowice ul Uniuersytecka 12\n" +
                "JERONINO MARTING DYSTRYBUCJA 5.A.\n" +
                "62-025 KOSTRZYN UL.ZNINNA 5\n" +
                "NIP. 779-19-11-327\n" +
                "2010-05-31 1011294\n" +
                "PARAGON FISKALNY\n" +
                "Nap.Coca Cola 0,5L A 1x2,29 — 2,20A\n" +
                "(zek. Z Ryżen 506 A 1x1,29 — 1,20A\n" +
                "Sprzed. opodatk, A 3,56\n" +
                "Kuota PTU A 22 % 0,65\n" +
                "ŁĄCZNA KUOTA PTU 0,65\n" +
                "5sUMA 3,58\n" +
                "Gotóuka 5,00\n" +
                "Reszta 14\n" +
                "0360 tKasa 3 — Kasjer nr 131 13:46\n" +
                "Z RET 04104507\n" +
                "Ne sys.: 762\n";

        Object s = znajdzSklep(text);
        System.out.println(s);

        /*
        String splited = new String("aaa bbb ccc");
        String[] splitedArray = null;
        splitedArray = splited.split(" ");
        for (int i = 0 ; i < splitedArray.length ; i++) {
            System.out.println(splitedArray [i]);
        }*/

    }
}
