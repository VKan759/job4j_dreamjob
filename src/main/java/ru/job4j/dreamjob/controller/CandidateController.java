package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@ThreadSafe
@Controller
@RequestMapping("/candidates")
public class CandidateController {
    private final CandidateService candidateService;
    private final CityService cityService;

    public CandidateController(CandidateService candidateService, CityService cityService) {
        this.candidateService = candidateService;
        this.cityService = cityService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("candidates", candidateService.findAll());
        return "candidates/list";
    }

    @GetMapping("/create")
    public String createResume(Model model) {
        model.addAttribute("cities", cityService.findAll());
        return "candidates/create";
    }

    @PostMapping("/create")
    public String addCandidate(@ModelAttribute Candidate candidate, @RequestParam MultipartFile file,
                               Model model) {
        try {
            candidateService.save(candidate, new FileDto(file.getName(), file.getBytes()));
            return "redirect:/candidates";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute Candidate candidate, @RequestParam MultipartFile file) {
        try {
            boolean updated = candidateService.update(candidate, new FileDto(file.getName(), file.getBytes()));
            if (!updated) {
                model.addAttribute("message", "Кандидат с указанным идентификатором не найден");
                return "errors/404";
            }
            return "redirect:/candidates";
        } catch (IOException e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable int id, Model model, HttpSession session) {
        Optional<Candidate> byId = candidateService.findById(id);
        if (byId.isEmpty()) {
            model.addAttribute("message", "Кандидат с указанным id не найден");
            return "errors/404";
        }
        model.addAttribute("candidate", byId.get());
        model.addAttribute("cities", cityService.findAll());
        return "candidates/one";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        boolean deletedById = candidateService.deleteById(id);
        if (!deletedById) {
            model.addAttribute("message", "Кандидат с указанным id не найден");
            return "errors/404";
        }
        return "redirect:/candidates";
    }
}
