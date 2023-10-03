package ru.practicum.ewm.category.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.practicum.ewm.constant.Constant.*;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        checkCategoryName(categoryDto, null);
        Category category = categoryRepository.save(CategoryMapper.toNewEntity(categoryDto));
        log.info("Created category {}", category.getId());
        return CategoryMapper.toDto(category);
    }

    @Override
    public void deleteCategory(Integer catId) {
        Category category = getCategoryEntity(catId);
        checkRelations(category);
        categoryRepository.deleteById(catId);
        log.info("Deleted category {} ", category);
    }

    @Override
    public CategoryDto updateCategory(Integer catId, CategoryDto categoryDto) {
        checkCategoryName(categoryDto, catId);
        Category category = categoryRepository.save(CategoryMapper.toEntity(catId, categoryDto));
        log.info("Updated category {}", category);
        return CategoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(PageRequest page) {
        List<Category> categories = categoryRepository.findAllOrderById(page);
        log.info("Found categories {}", categories);
        return CategoryMapper.toDtos(categories);
    }

    @Override
    public CategoryDto getCategory(Integer catId) {
        Category category = getCategoryEntity(catId);
        return CategoryMapper.toDto(category);
    }

    @Override
    public Category getCategoryEntity(Integer catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_CATEGORY_MSG, NOT_FOUND_ID_REASON));
        log.info("Found category {}", category);
        return category;
    }

    private void checkCategoryName(CategoryDto categoryDto, Integer catId) {
        Optional<Category> category = categoryRepository.findByName(categoryDto.getName());
        if (category.isPresent() && !Objects.equals(catId, category.get().getId())) {
            throw new ConflictException(INCORRECT_DATA_INPUT_MSG, INCORRECT_NAME_UNIQUE_REASON);
        }
    }

    private void checkRelations(Category category) {
        List<Category> categories = categoryRepository.findCategoryRelatedToEvents(category);
        if (categories.size() > 0) {
            throw new ConflictException(INCORRECT_DATA_INPUT_MSG, INCORRECT_CATEGORY_REL_REASON);
        }
    }
}