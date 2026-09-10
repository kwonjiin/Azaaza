package com.azaaza.habitpet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing // BaseTimeEntity의 @CreatedDate/@LastModifiedDate 자동 채움
@EnableScheduling // DiaryScheduler의 매일 새벽 4시 일기 생성 배치
@SpringBootApplication
public class HabitpetApplication {

    public static void main(String[] args) {
        SpringApplication.run(HabitpetApplication.class, args);
    }
}
