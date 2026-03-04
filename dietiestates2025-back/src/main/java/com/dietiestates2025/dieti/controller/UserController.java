package com.dietiestates2025.dieti.controller;

import com.dietiestates2025.dieti.Service.UserService;
import com.dietiestates2025.dieti.dto.ApiResponse;
import com.dietiestates2025.dieti.dto.HasPasswordResponseDTO;
import com.dietiestates2025.dieti.dto.PasswordChangeRequestDTO;
import com.dietiestates2025.dieti.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userEmail:.+}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String userEmail) {
        UserDTO userDTO = userService.getUserByEmail(userEmail);
        return ResponseEntity.ok(userDTO);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody PasswordChangeRequestDTO request) {
        userService.changeUserPassword(request);
        return ResponseEntity.ok(new ApiResponse(true, "Password aggiornata con successo."));
    }

    @GetMapping("/{userEmail:.+}/has-password")
    public ResponseEntity<HasPasswordResponseDTO> checkUserPassword(@PathVariable String userEmail) {
        boolean hasPassword = userService.userHasPassword(userEmail);
        return ResponseEntity.ok(new HasPasswordResponseDTO(hasPassword));
    }
}