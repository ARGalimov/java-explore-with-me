package ru.practicum.ewm.compilation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.storage.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.*;

import static ru.practicum.ewm.constant.Constant.NOT_FOUND_COMPILATION_MSG;
import static ru.practicum.ewm.constant.Constant.NOT_FOUND_ID_REASON;

@Service
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, EventService eventService) {
        this.compilationRepository = compilationRepository;
        this.eventService = eventService;
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = getEvents(newCompilationDto);
        Compilation compilation = compilationRepository.save(CompilationMapper.toNewEntity(newCompilationDto, events));
        log.info("Created compilation {}", compilation);
        Map<Integer, Integer> views = eventService.getStats(events);
        return CompilationMapper.toDto(compilation, views);
    }

    @Override
    public void deleteById(Integer compId) {
        if (compilationRepository.findById(compId).isPresent()) {
            compilationRepository.deleteById(compId);
        } else throw new NotFoundException(NOT_FOUND_COMPILATION_MSG, NOT_FOUND_ID_REASON);
    }

    @Override
    public CompilationDto updateCompilation(UpdateCompilationDto updateCompilationDto, Integer compId) {
        if (compilationRepository.findById(compId).isPresent()) {
            List<Event> events = new ArrayList<>();
            if (updateCompilationDto != null && updateCompilationDto.getEvents() != null) {
                List<Integer> eventsId = new ArrayList<>(updateCompilationDto.getEvents());
                events = eventService.findByIds(eventsId);
            }
            Compilation compilation1 = compilationRepository.findById(compId)
                    .orElseThrow(() -> new NotFoundException("", ""));
            Compilation compilation = CompilationMapper.toEntity(updateCompilationDto, events, compilation1);
            compilation = compilationRepository.save(compilation);
            log.info("Updated compilation {}", compilation);
            Map<Integer, Integer> views = eventService.getStats(events);
            return CompilationMapper.toDto(compilation, views);
        } else throw new NotFoundException(NOT_FOUND_COMPILATION_MSG, NOT_FOUND_ID_REASON);
    }

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, PageRequest page) {
        List<Compilation> compilations = compilationRepository.findAllByPinnedIs(pinned, page);
        Set<Event> events = new HashSet<>();
        for (Compilation compilation : compilations) {
            events.addAll(compilation.getEvents());
        }
        Map<Integer, Integer> views = new HashMap<>(eventService.getStats(new ArrayList<>(events)));
        return CompilationMapper.toDtos(compilations, views);
    }

    @Override
    public CompilationDto getCompilation(Integer compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_COMPILATION_MSG, NOT_FOUND_ID_REASON));
        Map<Integer, Integer> views = eventService.getStats(compilation.getEvents());
        return CompilationMapper.toDto(compilation, views);
    }

    private List<Event> getEvents(NewCompilationDto newCompilationDto) {
        if (newCompilationDto != null && newCompilationDto.getEvents() != null) {
            List<Integer> eventsId = new ArrayList<>(newCompilationDto.getEvents());
            return eventService.findByIds(eventsId);
        } else {
            return Collections.emptyList();
        }
    }
}