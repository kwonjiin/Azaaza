package com.azaaza.habitpet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // BaseTimeEntity의 @CreatedDate/@LastModifiedDate 자동 채움
@SpringBootApplication
public class HabitpetApplication {

    public static void main(String[] args) {
        SpringApplication.run(HabitpetApplication.class, args);
    }
}
