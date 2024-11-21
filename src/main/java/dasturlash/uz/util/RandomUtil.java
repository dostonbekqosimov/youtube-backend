package dasturlash.uz.util;

import java.util.Random;

public class RandomUtil {
   public  static final  Random random = new Random();

    public static int getRandomInt() {
        return random.nextInt(10000,99999);
    }


}
