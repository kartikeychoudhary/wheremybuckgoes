package kc.wheremybuckgoes.controller;

import kc.wheremybuckgoes.dto.UserDTO;
import kc.wheremybuckgoes.modal.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/home")
@RequiredArgsConstructor
public class HomeController {

    @GetMapping
    public ResponseEntity<String>  atHome(){
        return ResponseEntity.ok().body("Home");
    }

    @GetMapping("/config")
    public ResponseEntity<UserDTO> getConfig(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(user.convertToDTO());
    }
}
