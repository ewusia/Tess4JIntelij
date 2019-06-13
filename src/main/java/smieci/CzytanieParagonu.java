package smieci;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CzytanieParagonu {

    private static void czytaniePliku() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader("paragon.txt");
        BufferedReader br = new BufferedReader(fr);
        String yo = "Nap.Coca Cola 0,5L A 1x2,29 — 2,20A";
        String linia = "";
        while ((linia = br.readLine()) != null) {
            if (linia.contains(yo)) {
                System.out.println(linia + "\n");
            } else {
                System.out.println("brak");
            }
        }

            /*Nap.Coca Cola 0,5L A 1x2,29 — 2,20A

for(dla kazdej lini paragonu){

	String linia;
	for(dla wszystkich produktow z bazy){
		String produktZBazy;
		if(StringUtils.containsIgnoreCase(linia,produktZBazy){
		Pattern pattern Pattern.compile("\d[xX]\d[,]\d{0,2}");

        Matcher matcher = pattern.matcher(linia);
        boolean matches = matcher.matches();

		if(mateches){
Stirng find = matcher.find.
String [] tab = find.split["x"];
int ilosc = tab[0]
float cene = tab[1]

//tu masz wszystko -> dodajesz do bazy

*/
    }

    public static void main(String[] args) throws IOException {
        czytaniePliku();
    }
}
