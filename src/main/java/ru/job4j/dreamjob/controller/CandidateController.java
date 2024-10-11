package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

import java.util.Optional;

@ThreadSafe
@Controller
@RequestMapping("/candidates")
public class CandidateController {
    private final CandidateService candidateRepository;
    private final CityService cityService;

    public CandidateController(CandidateService candidateRepository, CityService cityService) {
        this.candidateRepository = candidateRepository;
        this.cityService = cityService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("candidates", candidateRepository.findAll());
        return "candidates/list";
    }

    @GetMapping("/create")
    public String createResume(Model model) {
        model.addAttribute("cities", cityService.findAll());
        return "candidates/create";
    }

    @PostMapping("/create")
    public String addCandidate(@ModelAttribute Candidate candidate) {
        candidateRepository.save(candidate);
        return "redirect:/candidates";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute Candidate candidate) {
        boolean updated = candidateRepository.update(candidate);
        if (!updated) {
            model.addAttribute("message", "Кандидат с указанным идентификатором не найден");
            return "errors/404";
        }
        return "redirect:/candidates";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable int id, Model model) {
        Optional<Candidate> byId = candidateRepository.findById(id);
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
        boolean deletedById = candidateRepository.deleteById(id);
        if (!deletedById) {
            model.addAttribute("message", "Кандидат с указанным id не найден");
            return "errors/404";
        }
        return "redirect:/candidates";
    }
}
