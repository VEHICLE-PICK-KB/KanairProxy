import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Luokka, joka kasittelee emailin lahetyksen
 * 
 * @author Jukka Juslin
 */
public class Email {
    /**
     * Metodi joka hoitaa viestin lahetyksen, kovakoodattuna smtp.helia.fi.
     *
     * @param to
     * @param from
     * @param subject
     * @param content
     * @param salasana
     * @throws Exception
     */
    public static void laheta(String vastaanottajanEmail, String otsikko, String emailinSisalto) throws Exception {

        String from = "";
        String pass = "";
        pass = "Tampere2023";
        from = "jtjuslin";

        String host = "mail.kapsi.fi";

        Properties properties = System.getProperties();
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        properties.put("mail.smtp.starttls.required", "true");

        properties.put("mail.debug", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", "mail.kapsi.fi");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", from);
        properties.put("mail.smtp.from", "jjuslin@gmail.com");
        properties.put("mail.smtp.password", pass);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", "mail.kapsi.fi");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        // Haetaan oletus session olio
        Session session = Session.getDefaultInstance(properties);

        try {
            // Luo oletus MimeMessage olio
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress("jukka.juslin@aalto.fi"));

            // Viestin vastaanottajan asettaminen
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(vastaanottajanEmail));

            // Viestin otsikko eli subject
            message.setSubject(otsikko);

            message.setText(emailinSisalto);

            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());

            System.out.println("Virhe lahetyksesessa.");
        }
    }
}