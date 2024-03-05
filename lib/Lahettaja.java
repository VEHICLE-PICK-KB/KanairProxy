import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Lahettaja {

    public static void main(String[] args) {
        Email email = new Email();
        System.out.println("Starting EMAIL wait");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        System.out.println("Ending EMAIL wait");

        String sisalto = "Viimeinen kuulutus!\r\n" +
                "\r\n" +
                "OHJELMOINTIKURSSI\r\n" +
                "\r\n" +
                "Helsingin seudun kesäyliopisto järjestää lähiopetuksena viiden päivän\r\n" +
                "ohjelmoinnin peruskurssin Pasilassa, Haaga-Helian rakennukseen\r\n" +
                "yhteydessä olevassa HBC-talossa 16. - 20.8. ma-pe kello\r\n" +
                "16.00 - 20.00.\r\n" +
                "\r\n" +
                "Tässä olisi hyvä tapa aloittaa lukuvuosi 2021 - 2022 perinteisesti\r\n" +
                "lähiopetuksessa muiden opiskelijoiden parissa. Nopea aloitus keskeisimpään sisältöön, kun toiset vasta aloittavat seuraavalla viikolla 16 viikon rupeamaa.\r\n"
                +
                "\r\n" +
                "Kurssi vastaa Haaga-Helian Ohjelmointi 1 kurssin sisältöä ja\r\n" +
                "kurssilla voi korvata tuon Haaga-Heliassa lukukausittain järjestetyn\r\n" +
                "kurssin. Osallistujamäärä on rajoitettu vain 15 henkeen, jolloin opetus on turvallisempaa Koronan suhteen.\r\n"
                +
                "\r\n" +
                "Kurssin hinta opiskelijalle on 85 euroa, muille 110 euroa.\r\n" +
                "\r\n" +
                "Kurssin on tarkoitus olla innostava ja löytää ymmärrystä siihen mitä\r\n" +
                "kaikkea ohjelmointitaidoilla voi tehdä.\r\n" +
                "\r\n" +
                "Olen kirjoittanut Javasta 2 kansallisessa käytössä olevaa oppikirjaa:\r\n" +
                "javaohjelmointi.net - näitä kursseja olen vetänyt 15 vuotta ja\r\n" +
                "palaute on ollut hyvää. \r\n" +
                "\r\n" +
                "Lisätietoa ja ilmoittautuminen:\r\n" +
                "https://kesayliopistopalvelut.fi/helsinginseutu/course.php?l=fi&t=5937\r\n" +
                "\r\n" +
                "Vinkkaathan tästä myös kaverillesi - etenkin juuri opintonsa aloittavalle, kuka tahansa saa osallistua, ei\r\n"
                +
                "tarvitse olla Haaga-Helian opiskelija!\r\n" +
                "\r\n" +
                "t. Jukka Juslin, kurssin opettaja\r\n" +
                "";

        Scanner scan = null;
        // try {
        // scan = new Scanner(new File("osoitteet.txt"));
        // } catch (FileNotFoundException e1) {
        // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
        /*
         * while (scan.hasNext()) {
         * System.out.println("Starting EMAIL wait");
         * try {
         * Thread.sleep(2000);
         * } catch (InterruptedException e1) {
         * // TODO Auto-generated catch block
         * e1.printStackTrace();
         * }
         * System.out.println("Ending EMAIL wait");
         * String rivi = scan.nextLine();
         * String[] solut = rivi.split(":");
         * String vastaanottaja = solut[0];
         * vastaanottaja = vastaanottaja + "@myy.haaga-helia.fi";
         * System.out.println("DEBUG VASTAANOTTAJA");
         * System.out.println(vastaanottaja);
         * System.out.println("DEBUG VASTAANOTTAJA LOPPUU");
         * try {
         * email.laheta(vastaanottaja,
         * "Ohjelmointi 1 kesäyliopistossa ma 16.8. - pe 20.8.2021 ", sisalto);
         * } catch (Exception e) {
         * // TODO Auto-generated catch block
         * e.printStackTrace();
         * }
         * }
         */
        try {
            email.laheta("jukka.juslin@haaga-helia.fi", "Testi", "sisalto");
        } catch (Exception ex) {

        }
    }

}