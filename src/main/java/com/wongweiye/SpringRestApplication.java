package com.wongweiye;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Indexed;

// @Indexed, this will pre create index for all the required component scan bean put into to the META-INF/spring.components file,
// so when application started it will look at spring.components to load the beans, so can optimize a bit startup time
@Indexed
@SpringBootApplication
public class SpringRestApplication {

	public static void main(String[] args) {

		SpringApplication.run(SpringRestApplication.class, args);

	}

}
