package com.laborganized.LabOrganized;

import org.springframework.boot.SpringApplication;

public class TestLabOrganizedApplication {

	public static void main(String[] args) {
		SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
