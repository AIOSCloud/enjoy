package e.word.net;

import e.word.net.view.LoginPage;
import org.apache.log4j.Logger;

public class Client {
    private static Logger logger = Logger.getLogger(Client.class);

    public static void main(String[] args) {
        try {
            LoginPage loginPage = new LoginPage();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("URL format Exception:" + e);
        }
    }
}
