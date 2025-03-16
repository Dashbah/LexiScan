package dashbah.hse.lexiscan.app.util;

import java.util.Random;

public class Util {
    public static String generateUid() {
        Random rnd = new Random();
        return "randomized" + rnd.nextInt(100000, 800000);
    }
}
