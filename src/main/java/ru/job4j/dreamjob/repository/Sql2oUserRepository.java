package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.User;

import java.util.Optional;

@Repository
public class Sql2oUserRepository implements UserRepository {
    private final Sql2o sql2o;

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
