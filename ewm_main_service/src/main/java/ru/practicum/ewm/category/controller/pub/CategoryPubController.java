package ru.practicum.ewm.category.controller.pub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/categories")
public class CategoryPubController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryPubController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(required = false, defaultValue = "0") int from,
                                           @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Getting categories from {} size {}", from, size);
        return categoryService.getCategories(PageRequest.of(from, size));
    }

    @GetMapping(value = "/{catId}")
    public CategoryDto getCategory(@PathVariable Integer catId) {
        log.info("Getting category by id = {}", catId);
        return categoryService.getCategory(catId);
    }
}