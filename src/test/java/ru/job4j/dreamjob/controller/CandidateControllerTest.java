package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CandidateControllerTest {

    private CandidateService candidateService;
    private CityService cityService;
    private CandidateController candidateController;
    private MultipartFile testFile;

    @BeforeEach
    void initServices() {
        candidateService = mock(CandidateService.class);
        cityService = mock(CityService.class);
        candidateController = new CandidateController(candidateService, cityService);
        testFile = new MockMultipartFile("testfile.png", new byte[]{1, 2, 3});
    }

    @Test
    void whenRequestCandidateListPageThenGetCandidatePage() {
        Candidate candidate = new Candidate(1, "First", "Description", LocalDateTime.now(), 1, 1);
        Candidate candidate2 = new Candidate(2, "Second", "Description", LocalDateTime.now(), 1, 1);
        List<Candidate> expectedCandidates = List.of(candidate, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);

        ConcurrentModel concurrentModel = new ConcurrentModel();
        candidateController = new CandidateController(candidateService, cityService);
        String view = candidateController.getAll(concurrentModel);

        assertThat(view).isEqualTo("candidates/list");
        assertThat(concurrentModel.getAttribute("candidates")).isEqualTo(expectedCandidates);
    }

    @Test
    void whenRequestCreationPageThenGetCandidateCreationPage() {
        City moscow = new City(1, "Moscow");
        City almaty = new City(2, "Almaty");
        var expectedCities = List.of(moscow, almaty);
        when(cityService.findAll()).thenReturn(expectedCities);

        ConcurrentModel model = new ConcurrentModel();
        String view = candidateController.createResume(model);
        var cities = model.getAttribute("cities");
        assertThat(cities).isEqualTo(expectedCities);
        assertThat(view).isEqualTo("candidates/create");
    }

    @Test
    void whenPostCandidateWithFileThenSameDataAndRedirectToCandidatePage() throws IOException {
        Candidate candidate1 = new Candidate(1, "First", "1 description", LocalDateTime.now(), 1, 1);
        FileDto fileDto = new FileDto(testFile.getName(), testFile.getBytes());
        ArgumentCaptor<Candidate> candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        ArgumentCaptor<FileDto> fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.save(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(candidate1);

        ConcurrentModel model = new ConcurrentModel();
        String view = candidateController.addCandidate(candidate1, testFile, model);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualDto = fileDtoArgumentCaptor.getValue();

        assertThat(actualDto).usingRecursiveComparison().isEqualTo(fileDto);
        assertThat(actualCandidate).isEqualTo(candidate1);
        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    void whenTestUpdateCandidate() throws IOException {
        Candidate candidate = new Candidate(1, "Candidate", "Description", LocalDateTime.now(), 1, 1);
        FileDto fileDto = new FileDto(testFile.getName(), testFile.getBytes());
        ArgumentCaptor<Candidate> candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        ArgumentCaptor<FileDto> fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.update(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);

        ConcurrentModel model = new ConcurrentModel();
        String view = candidateController.update(model, candidate, testFile);
        Candidate actualCandidate = candidateArgumentCaptor.getValue();
        FileDto actualFileDto = fileDtoArgumentCaptor.getValue();
        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    void whenThrowSomeException() throws IOException {
        RuntimeException expectedEx = new RuntimeException("Failed to save");
        when(candidateService.save(any(), any())).thenThrow(expectedEx);
        Candidate candidate = new Candidate();
        ConcurrentModel model = new ConcurrentModel();
        String view = candidateController.addCandidate(candidate, testFile, model);
        Object actualMessage = model.getAttribute("message");
        assertThat(actualMessage).isEqualTo(expectedEx.getMessage());
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    void whenDeleteByIdIsSuccessful() {
        when(candidateService.deleteById(any(Integer.class))).thenReturn(true);
        ConcurrentModel concurrentModel = new ConcurrentModel();
        String view = candidateController.delete(concurrentModel, 1);
        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    void whenDeleteByIdThenTrowsException() {
        when(candidateService.deleteById(any(Integer.class))).thenReturn(false);
        ConcurrentModel model = new ConcurrentModel();
        String view = candidateController.delete(model, 1);
        Object message = model.getAttribute("message");
        assertThat(view).isEqualTo("errors/404");
        assertThat(message).isEqualTo("Кандидат с указанным id не найден");
    }
}