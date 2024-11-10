package ru.job4j.dreamjob.dto;

import java.util.Arrays;
import java.util.Objects;

public class FileDto {
    private String name;
    private byte[] content;

    public FileDto(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileDto fileDto = (FileDto) o;
        return Objects.equals(name, fileDto.name) && Objects.deepEquals(content, fileDto.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, Arrays.hashCode(content));
    }
}
