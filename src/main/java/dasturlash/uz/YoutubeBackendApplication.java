package dasturlash.uz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "dasturlash.uz")
public class YoutubeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(YoutubeBackendApplication.class, args);
        System.out.println("Running...");
    }

}
