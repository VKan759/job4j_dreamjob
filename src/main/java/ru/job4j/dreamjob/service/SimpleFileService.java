package ru.job4j.dreamjob.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
public class SimpleFileService implements FileService {
    private final FileRepository fileRepository;
    private final String storageDirectory;

    public SimpleFileService(FileRepository fileRepository, @Value("${file.directory}") String storageDirectory) {
        this.fileRepository = fileRepository;
        this.storageDirectory = storageDirectory;
        createStorageDirectory(storageDirectory);
    }

    private void createStorageDirectory(String path) {
        try {
            Files.createDirectories(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getNewFilePath(String sourceName) {
        return storageDirectory + java.io.File.separator + UUID.randomUUID() + sourceName;
    }

    private void writeFileBytes(String path, byte[] content) {
        try {
            Files.write(Path.of(path), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File save(FileDto fileDto) {
        String newFilePath = getNewFilePath(fileDto.getName());
        writeFileBytes(newFilePath, fileDto.getContent());
        return fileRepository.save(new File(fileDto.getName(), newFilePath));
    }

    private byte[] readFileAsBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FileDto> getById(int id) {
        Optional<File> file = fileRepository.getById(id);
        if (file.isEmpty()) {
            return Optional.empty();
        }
        byte[] bytes = readFileAsBytes(file.get().getPath());
        return Optional.of(new FileDto(file.get().getName(), bytes));
    }

    @Override
    public boolean deleteById(int id) {
        Optional<File> file = fileRepository.getById(id);
        if (file.isPresent()) {
            deleteFile(file.get().getPath());
            fileRepository.deleteById(id);
        }
        return fileRepository.deleteById(id);
    }

    private void deleteFile(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
