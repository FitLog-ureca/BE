package com.ureca.fitlog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
@RequiredArgsConstructor
public class DbTestController {
    private final DataSource dataSource;

    @GetMapping("/db-check")
    public String checkConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return "✅ Connected to DB: " + conn.getCatalog();
        } catch (Exception e) {
            return "❌ DB 연결 실패: " + e.getMessage();
        }
    }
}