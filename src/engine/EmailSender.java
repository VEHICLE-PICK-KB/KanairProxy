

public class EmailSender {
    //Configuring email sending parameters
    public static void sendEmail(String emailBody) {
        ProxyPalvelu proxyPalvelu = new ProxyPalvelu();
        String reciever = "jukka.juslin@haaga-helia.fi";
        try {
            Email.send(reciever, "Fuel fetcher has been down for 24h", "Taitaa olla bensat alhaalla.");
            System.out.println("Email was sent");
            proxyPalvelu.updateLog("Email sent to " + reciever);
        } catch (Exception ex) {
            System.out.println("Email wasnt sent");
        }
    }
}