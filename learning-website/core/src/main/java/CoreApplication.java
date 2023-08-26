import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.learningwebsite"})
public class CoreApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // This method can be used to run code after the application context is set up.
        // You can place your initialization or startup code here.
    }
}
