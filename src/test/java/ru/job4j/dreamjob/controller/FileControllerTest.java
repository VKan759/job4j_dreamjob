package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {
    private FileService fileService;
    private FileController fileController;

    @BeforeEach
    void initServices() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    void whenGetFileByIdThenReturnsFile() {
        FileDto fileDto = new FileDto("test file", new byte[]{1, 2, 3});
        when(fileService.getById(1)).thenReturn(Optional.of(fileDto));
        ResponseEntity<?> entity = fileController.getById(1);
        assertThat(entity.getBody()).usingRecursiveComparison().isEqualTo(fileDto);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void whenGetFileByIdThenFileIsEmpty() {
        when(fileService.getById(any(Integer.class))).thenReturn(Optional.empty());
        ResponseEntity<?> entity = fileController.getById(1);
        assertThat(entity.getBody()).usingRecursiveComparison().isNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}