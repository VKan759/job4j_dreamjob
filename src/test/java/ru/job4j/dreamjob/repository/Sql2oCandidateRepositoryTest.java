package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

class Sql2oCandidateRepositoryTest {
    private static Sql2oCandidateRepository candidateRepository;
    private static Sql2oFileRepository fileRepository;
    private static File file;

    @BeforeAll
    static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oVacancyRepositoryTest.class.getClassLoader().getResourceAsStream("connection"
                + ".properties")) {
            properties.load(inputStream);
        }

        String datasource = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");

        DatasourceConfiguration configuration = new DatasourceConfiguration();
        DataSource dataSource = configuration.connectionPool(datasource, username, password);
        Sql2o sql2o = configuration.databaseClient(dataSource);

        candidateRepository = new Sql2oCandidateRepository(sql2o);
        fileRepository = new Sql2oFileRepository(sql2o);
        file = new File("test", "test");
        fileRepository.save(file);
    }

    @AfterAll
    public static void deleteFile() {
        fileRepository.deleteById(file.getId());
    }

    @AfterEach
    public void clearCandidates() {
        candidateRepository.findAll().forEach(candidate -> candidateRepository.deleteById(candidate.getId()));
    }

    @Test
    public void whenSaveSomeAndGetSome() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = new Candidate();
        candidate.setName("name");
        candidate.setFileId(file.getId());
        candidate.setDescription("description");
        candidate.setCreationDate(creationDate);
        candidate.setCityId(1);
        Candidate saved = candidateRepository.save(candidate);
        assertThat(saved).usingRecursiveComparison().isEqualTo(candidate);
    }

    @Test
    public void whenSaveSeveralAndGetSeveral() {
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate1 = new Candidate();
        candidate1.setName("First");
        candidate1.setFileId(file.getId());
        candidate1.setDescription("First description");
        candidate1.setCreationDate(localDateTime);
        candidate1.setCityId(1);
        Candidate candidate2 = new Candidate();
        candidate2.setName("Second");
        candidate2.setFileId(file.getId());
        candidate2.setDescription("Second description");
        candidate2.setCreationDate(localDateTime);
        candidate2.setCityId(1);
        Candidate candidate3 = new Candidate();
        candidate3.setName("Third");
        candidate3.setFileId(file.getId());
        candidate3.setDescription("Third description");
        candidate3.setCreationDate(localDateTime);
        candidate3.setCityId(3);
        List<Candidate> candidates = List.of(candidate1, candidate2, candidate3);
        candidates.forEach(candidate -> candidateRepository.save(candidate));
        assertThat(candidateRepository.findAll()).usingRecursiveComparison().isEqualTo(candidates);
    }

    @Test
    public void whenNothingToSave() {
        Collection<Candidate> all = candidateRepository.findAll();
        assertThat(all).usingRecursiveComparison().isEqualTo(List.of());
        Optional<Candidate> byId = candidateRepository.findById(9);
        assertThat(byId).isEqualTo(Optional.empty());
    }

    @Test
    public void whenSaveDeleteAndGetEmpty() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = new Candidate();
        candidate.setName("name");
        candidate.setFileId(file.getId());
        candidate.setDescription("description");
        candidate.setCreationDate(creationDate);
        candidate.setCityId(1);
        Candidate saved = candidateRepository.save(candidate);
        candidateRepository.deleteById(1);
        Optional<Candidate> byId = candidateRepository.findById(1);
        assertThat(byId).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDeleteByInvalidIdIsFalse() {
        assertThat(candidateRepository.deleteById(9)).isFalse();
    }

    @Test
    public void whenUpdateCandidateAndGetUpdated() {
        Candidate candidate = new Candidate(0, "first", "Description",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), 1, file.getId());
        candidateRepository.save(candidate);
        Candidate updated = new Candidate(candidate.getId(), "Updated", "Updated description",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), 1, file.getId());
        boolean result = candidateRepository.update(updated);
        assertThat(result).isTrue();
        assertThat(candidateRepository.findById(updated.getId()).get()).usingRecursiveComparison().isEqualTo(updated);
    }

    @Test
    public void whenUpdateNotExistingCandidateIsFalse() {
        Candidate update = new Candidate(0, "Updated", "Description",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), 1, file.getId());
        assertThat(candidateRepository.update(update)).isFalse();
    }
}