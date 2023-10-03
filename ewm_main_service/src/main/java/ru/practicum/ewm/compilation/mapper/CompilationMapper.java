package ru.practicum.ewm.compilation.mapper;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CompilationMapper {
    public static Compilation toNewEntity(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public static CompilationDto toDto(Compilation compilation, Map<Integer, Integer> views) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(EventMapper.toShortDtos(compilation.getEvents(), views))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Compilation toEntity(UpdateCompilationDto newCompilationDto, List<Event> events,
                                       Compilation compilation) {
        return Compilation.builder()
                .id(compilation.getId())
                .events(events)
                .pinned(newCompilationDto.getPinned())
                .title(Objects.isNull(newCompilationDto.getTitle())
                        ? compilation.getTitle() : newCompilationDto.getTitle())
                .build();
    }

    public static Compilation toEntity(NewCompilationDto newCompilationDto, List<Event> events,
                                       Compilation compilation) {
        return Compilation.builder()
                .id(compilation.getId())
                .events(events)
                .pinned(newCompilationDto.getPinned())
                .title(Objects.isNull(newCompilationDto.getTitle())
                        ? compilation.getTitle() : newCompilationDto.getTitle())
                .build();
    }

    public static List<CompilationDto> toDtos(List<Compilation> compilations, Map<Integer, Integer> views) {
        List<CompilationDto> dtos = new ArrayList<>();
        for (Compilation compilation : compilations) {
            dtos.add(toDto(compilation, views));
        }
        return dtos;
    }
}