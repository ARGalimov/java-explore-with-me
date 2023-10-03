package ru.practicum.ewm.compilation.controller.pub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/compilations")
public class CompilationPubController {
    private final CompilationService compilationService;

    @Autowired
    public CompilationPubController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping
    public List<CompilationDto> createCompilation(@RequestParam(required = false, defaultValue = "false")
                                                  boolean pinned,
                                                  @RequestParam(required = false, defaultValue = "0") int from,
                                                  @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Getting compilations from {} size {} pinned={}", from, size, pinned);
        return compilationService.getCompilations(pinned, PageRequest.of(from, size));
    }

    @GetMapping(value = "{compId}")
    public CompilationDto createCompilation(@PathVariable Integer compId) {
        log.info("Getting compilation with id={}", compId);
        return compilationService.getCompilation(compId);
    }
}