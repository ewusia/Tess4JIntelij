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

    public boolean znajdzSprzed(String text) throws IOException {

        String foundPattern = null;
        Pattern pattern = Pattern.compile("Sprzed");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            foundPattern = matcher.group(0);
            //System.out.println(foundPattern);
        } else {
            //System.out.println("Nie znaleziono wzorca");
        }
        return true;
    }
}
