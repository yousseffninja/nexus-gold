package com.rocketeers.nexus_gold.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "Request object for updating user profile information")
public class UpdateProfileRequest {

    @Schema(example = "Gaming enthusiast and collector", description = "Short biography about the user")
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    @Schema(example = "United States", description = "Country of the user")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Schema(description = "Profile avatar image file to upload", type = "string", format = "binary")
    private MultipartFile avatar;
}