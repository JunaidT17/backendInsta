package com.example.projetinsta.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;

@Configuration
public class Config {

    //@Bean permet d'en faire un component que tu pourras appeler avec un autowired
    @Bean
    public JavaMailSender javaMailSender() {

//Permet de mettre des infos de config du serveur mail qui permet d'envoyer le mail au user
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp-relay.sendinblue.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("pakossdu93@gmail.com");
        javaMailSender.setPassword("9Z0CyER2If1YMpLG");
        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.debug", "true");
        return javaMailSender;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}