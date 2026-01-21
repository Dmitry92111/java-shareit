package ru.practicum.shareit.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("db")
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class DbIntegrationTestBase {
}
