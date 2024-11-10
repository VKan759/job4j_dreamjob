package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserController controller;
    private UserService service;
    private ConcurrentModel model;

    @BeforeEach
    void initServices() {
        service = mock(UserService.class);
        controller = new UserController(service);
        model = new ConcurrentModel();
    }

    @Test
    void whenRequestGetRegistrationPage() {
        assertThat(controller.getRegistrationPage(model)).isEqualTo("users/register");
    }

    @Test
    void whenRegisterUserRequestThenGetUsersRegistrationPage() {
        User user = new User();
        user.setName("User");
        when(service.save(any(User.class))).thenReturn(Optional.of(user));
        String view = controller.register(model, user);
        assertThat(view).isEqualTo("redirect:/users/register");
    }

    @Test
    void whenRegisterEmptyUserRequestThenGetErrorPage() {
        User user = new User();
        when(service.save(any(User.class))).thenReturn(Optional.empty());
        String view = controller.register(model, user);
        Object message = model.getAttribute("message");
        assertThat(message).isEqualTo("Пользователь с такой почтой существует");
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    void whenGetLoginPageRequestThenGetPage() {
        assertThat(controller.getLoginPage()).isEqualTo("users/login");
    }

    @Test
    void whenLoginUserThenRedirectToVacancies() {
        User user = new User(1, "email@gmail.com", "User", "123123");
        when(service.findByEmailAndPassword(any(), anyString())).thenReturn(Optional.of(user));
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        String view = controller.loginUser(user, model, request);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    void whenLoginUserThenGetErrorPage() {
        User user = new User(1, "email@gmail.com", "User", "123123");
        when(service.findByEmailAndPassword(any(), anyString())).thenReturn(Optional.empty());
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        String view = controller.loginUser(user, model, request);
        Object error = model.getAttribute("error");
        assertThat(view).isEqualTo("users/login");
        assertThat(error).isEqualTo("Почта или пароль введены неверно");
    }

    @Test
    void whenLogOutThenRedirectToLoginPage() {
        HttpSession session = mock(HttpSession.class);
        String view = controller.logOut(session);
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}