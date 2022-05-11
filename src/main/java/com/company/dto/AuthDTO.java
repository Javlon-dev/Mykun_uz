package com.company.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthDTO {

    @NotNull(message = "Email required")
    @Email(message = "Email required")
    @NotBlank(message = "Email required")
    private String email;

//    @NotEmpty
    @NotBlank(message = "Password required")
    @NotNull(message = "Password not be null")
    @Size(min = 4, message = "Password must be 4 or greater than")
    private String password;

}
