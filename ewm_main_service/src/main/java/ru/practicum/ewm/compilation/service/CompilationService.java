package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteById(Integer compId);

    CompilationDto updateCompilation(UpdateCompilationDto updateCompilationDto, Integer compId);

    List<CompilationDto> getCompilations(boolean pinned, PageRequest page);

    CompilationDto getCompilation(Integer compId);
}