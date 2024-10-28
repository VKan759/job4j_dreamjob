package ru.job4j.dreamjob.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.model.User;

import java.util.Optional;

@Repository
public class Sql2oUserRepository implements UserRepository {
    private final Sql2o sql2o;
    private static final Logger LOGGER = LoggerFactory.getLogger(Sql2oUserRepository.class);

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        try (var connection = sql2o.open()) {
            Query query = connection.createQuery("insert into users(email, name, password) values (:email, :name, :password)",
                            true)
                    .addParameter("name", user.getName())
                    .addParameter("email", user.getEmail())
                    .addParameter("password", user.getPassword());
            int key = query.executeUpdate().getKey(Integer.class);
            user.setId(key);
            return Optional.of(user);
        } catch (Sql2oException e) {
            LOGGER.error("Ошибка добавления пользователя");
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            Query query = connection.createQuery("select * from users where email = :email and password = :password")
                    .addParameter("email", email)
                    .addParameter("password", password);
            User user = query.executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }
    }

    @Override
    public boolean deleteUserByEmail(String email) {
        try (var connection = sql2o.open()) {
            Query query = connection.createQuery("delete from users where email = :email")
                    .addParameter("email", email);
            int result = query.executeUpdate().getResult();
            return result > 0;
        }
    }
}
