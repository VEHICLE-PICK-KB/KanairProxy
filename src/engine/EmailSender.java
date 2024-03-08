package engine;

public class EmailSender {

    public static void sendEmail() {
        String sisalto = "Testi sähköposti";
        try {
            Email.laheta("juho.suppola@myy.haaga-helia.fi", "Testi", sisalto);
        } catch (Exception ex) {
            System.out.println("Email wasnt sent");
        }
        System.out.println("Email was sent");
    }

}