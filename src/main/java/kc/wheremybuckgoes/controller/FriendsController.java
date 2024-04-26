package kc.wheremybuckgoes.controller;

import kc.wheremybuckgoes.dto.FriendsDTO;
import kc.wheremybuckgoes.modal.Friends;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.response.GenericResponse;
import kc.wheremybuckgoes.services.FriendsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static kc.wheremybuckgoes.utils.ApplicationHelper.mapToGenericResponse;

@RestController
@RequestMapping("api/v1/friends")
@RequiredArgsConstructor
public class FriendsController {
    private final FriendsService friendsService;

    @PostMapping()
    public ResponseEntity<GenericResponse<FriendsDTO>> addFriend(@RequestBody FriendsDTO friendsDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Friends friends = friendsService.addFriends(friendsDTO.convert(user));
        GenericResponse<FriendsDTO> gr = mapToGenericResponse(HttpStatus.OK, friends.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }
    @GetMapping
    public ResponseEntity<GenericResponse<FriendsDTO>> getFriends() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Friends friends = friendsService.getAllFriendsForUser(user);
        GenericResponse<FriendsDTO> gr = mapToGenericResponse(HttpStatus.OK, friends.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }
    @PatchMapping()
    public ResponseEntity<GenericResponse<FriendsDTO>> updateFriends(@RequestBody FriendsDTO friendsDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Friends friends = friendsService.updateFriends(friendsDTO.convert(user));
        GenericResponse<FriendsDTO> gr = mapToGenericResponse(HttpStatus.OK, friends.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }
    @DeleteMapping()
    public ResponseEntity<GenericResponse<String>> updateFriends() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        friendsService.deleteFriends(user);
        GenericResponse<String> gr = mapToGenericResponse(HttpStatus.OK, "DELETED");
        return ResponseEntity.ok().body(gr);
    }
}
