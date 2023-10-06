package ru.practicum.ewm.category.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(path = "/admin/categories")
@Validated
public class CategoryAdminController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryAdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Creating category {}", categoryDto.toString());
        return categoryService.createCategory(categoryDto);

    }

    @DeleteMapping(value = "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Integer catId) {
        log.info("Deleting category with Id={}", catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping(value = "/{catId}")
    public CategoryDto updateCategory(@PathVariable Integer catId, @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Updating category id={}", catId);
        return categoryService.updateCategory(catId, categoryDto);
    }
}