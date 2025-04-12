package com.ai.chatbot;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatbotApplication {

	public static void main(String[] args) {
		// 환경설정 로드
		Dotenv dotenv = Dotenv.load();
		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
		System.setProperty("GPT_API_KEY", dotenv.get("GPT_API_KEY"));

		SpringApplication.run(ChatbotApplication.class, args);
	}

}
