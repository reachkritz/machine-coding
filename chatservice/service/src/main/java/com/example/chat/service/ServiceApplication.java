package com.example.chat.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceApplication {
	static class LiveThread implements Runnable {

        @Override
        public void run() {
            while(true){
				System.out.println("Hello");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                }
			}
        }

	}

	public static void main(String[] args) {
		LiveThread t = new LiveThread();
		Thread t1 = new Thread(t);
		t1.start();
		SpringApplication.run(ServiceApplication.class, args);
	}
}
