package com.hasta.hams.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MaxAllowedPacketConfigurer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Value("${mysql.max_allowed_packet:64M}")
    private String maxAllowedPacket;

    public MaxAllowedPacketConfigurer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // Set or validate the max_allowed_packet size
        String query = String.format("SET GLOBAL max_allowed_packet = %s", maxAllowedPacket);
        jdbcTemplate.execute(query);
        System.out.println("max_allowed_packet set to: " + maxAllowedPacket);
    }
}
