package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

import static org.assertj.core.api.Assertions.*;

class IndexControllerTest {
    @Test
    void whenRequestGetMappingThenReceive() {
        IndexController controller = new IndexController();
        ConcurrentModel model = new ConcurrentModel();
        assertThat(controller.getIndex(model)).isEqualTo("index");
    }
}