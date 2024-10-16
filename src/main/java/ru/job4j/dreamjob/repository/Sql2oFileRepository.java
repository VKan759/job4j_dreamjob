package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.File;

import java.util.Optional;

@Repository
public class Sql2oFileRepository implements FileRepository {
    Sql2o sql2o;

    public Sql2oFileRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public File save(File file) {
        try (var connection = sql2o.open()) {
            Query query = connection.createQuery("Insert into files (name, path) values (:name, :path)", true)
                    .addParameter("name", file.getName())
                    .addParameter("path", file.getPath());
            int generatedKey = query.executeUpdate().getKey(Integer.class);
            file.setId(generatedKey);
            return file;
        }
    }

    @Override
    public Optional<File> getById(int id) {
        try (var connection = sql2o.open()) {
            Query query = connection.createQuery("select * from files where id = :id");
            var file = query.addParameter("id", id).executeAndFetchFirst(File.class);
            return Optional.ofNullable(file);
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            Query query = connection.createQuery("delete from files where id = :id");
            return query.addParameter("id", id).executeUpdate().getResult() > 0;
        }
    }
}
