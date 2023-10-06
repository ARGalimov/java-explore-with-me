package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto extends UserShortDto {
    @Email(regexp = ".+@.+\\..+")
    @Length(min = 6, max = 254)
    @NotNull
    private String email;

    @Builder(builderMethodName = "childBuilder")
    public UserDto(Integer id, @NotNull String name, String email) {
        super(id, name);
        this.email = email;
    }
}