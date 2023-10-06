package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationDto {
    private Set<Integer> events;
    private Boolean pinned;
    @Length(min = 2, max = 50)
    private String title;

    public boolean getPinned() {
        return Objects.nonNull(pinned) && pinned;
    }
}
