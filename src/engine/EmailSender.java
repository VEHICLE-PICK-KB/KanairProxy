package engine;

public class EmailSender {

    public static void sendEmail(String emailBody) {
        ProxyPalvelu proxyPalvelu = new ProxyPalvelu();
        String reciever = "polttoaineprojektiop2@gmail.com";
        try {
            Email.send(reciever, "Testi", emailBody);
            System.out.println("Email was sent");
            proxyPalvelu.updateLog("Email sent to " + reciever);
        } catch (Exception ex) {
            System.out.println("Email wasnt sent");
        }
    }
}