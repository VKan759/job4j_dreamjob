package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oUserRepositoryTest {

    private static UserRepository userRepository;

    @BeforeAll
    public static void init() {
        Properties properties = new Properties();
        try {
            InputStream resourceAsStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties");
            properties.load(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String datasource = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");

        DatasourceConfiguration configuration = new DatasourceConfiguration();
        DataSource dataSource = configuration.connectionPool(datasource, username, password);
        Sql2o sql2o = configuration.databaseClient(dataSource);
        userRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    void clearDB() {
        userRepository.deleteUserByEmail("gmail@gmail.com");
    }

    @Test
    void whenSaveIsSuccess() {
        User user = new User(0, "gmail@gmail.com", "User", "password");
        assertThat(userRepository.save(user)).isEqualTo(Optional.of(user));
    }

    @Test
    void whenSaveDuplicate() {
        User user = new User(0, "gmail@gmail.com", "User", "password");
        userRepository.save(user);
        assertThatThrownBy(() -> userRepository.save(user)).isInstanceOf(Sql2oException.class);
    }

    @Test
    void findByEmailAndPassword() {
        User user = new User(1, "gmail@gmail.com", "User", "password");
        userRepository.save(user);
        assertThat(userRepository.findByEmailAndPassword("gmail@gmail.com", "password")).isEqualTo(Optional.of(user));
    }
}