package br.com.course;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"br.com.course.model"})
public class Startup {

	public static void main(String[] args) {
		SpringApplication.run(Startup.class, args);
		// CreateHash.hash();
	}


}
