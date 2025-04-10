package kc.wheremybuckgoes.controller;

import kc.wheremybuckgoes.dto.SettingsDTO;
import kc.wheremybuckgoes.dto.UserDTO;
import kc.wheremybuckgoes.modal.Settings;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.response.GenericResponse;
import kc.wheremybuckgoes.services.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static kc.wheremybuckgoes.utils.ApplicationHelper.mapToGenericResponse;

@RestController
@RequestMapping("api/v1/home")
@RequiredArgsConstructor
public class HomeController {

    private final SettingsService settingsService;

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

    @GetMapping("/settings")
    public ResponseEntity<GenericResponse<SettingsDTO>>  getSettings(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Settings settings = settingsService.getSettingByUser(user);
        GenericResponse<SettingsDTO> gr = mapToGenericResponse(HttpStatus.OK, settings.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }

    @PostMapping("/settings")
    public ResponseEntity<GenericResponse<SettingsDTO>>  updateSettings(@RequestBody SettingsDTO settingsDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Settings settings = settingsService.getSettingsById(settingsDTO.getSettingsId());
        if(settings == null || !Objects.equals(settings.getCreatedBy().getId(), user.getId())){
            return ResponseEntity.notFound().build();
        }
        Settings updatedSettings = settingsService.createOrUpdateSettings(settingsDTO.convert(user));
        GenericResponse<SettingsDTO> gr = mapToGenericResponse(HttpStatus.OK, updatedSettings.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }
}
