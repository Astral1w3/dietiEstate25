package com.dietiestates2025.dieti.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dietiestates2025.dieti.Service.UserService;
import com.dietiestates2025.dieti.dto.HasPasswordResponseDTO;
import com.dietiestates2025.dieti.dto.PasswordChangeRequestDTO;
import com.dietiestates2025.dieti.dto.UserDTO;


@RestController
@RequestMapping("/api")
public class UserController {

    UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    public void print(){
        System.out.println("UserController");
    }

    @GetMapping("/user/{userEmail:.+}") 
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String userEmail){
        UserDTO userDTO = userService.getUserByEmail(userEmail);
        return ResponseEntity.ok(userDTO);
    }

     @PatchMapping("/user/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequestDTO request) {
        try {
            userService.changeUserPassword(request);
            return ResponseEntity.ok().body("Password aggiornata con successo.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante l'aggiornamento della password.");
        }
    }

    @GetMapping("/user/{userEmail:.+}/has-password")
    public ResponseEntity<HasPasswordResponseDTO> checkUserPassword(@PathVariable String userEmail) {
        System.out.println("chiamata api");
        boolean hasPassword = userService.userHasPassword(userEmail);
        return ResponseEntity.ok(new HasPasswordResponseDTO(hasPassword));
    }
    
}
