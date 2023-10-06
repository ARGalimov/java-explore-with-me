package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    private Set<Integer> events;
    private Boolean pinned;
    @NotNull(groups = Marker.OnCreate.class)
    @NotBlank
    @Length(min = 2, max = 50)
    private String title;

    public Boolean getPinned() {
        return !Objects.isNull(pinned) && pinned;
    }
}