package ru.wigm4n.jwt.auth.keycloak.temp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.wigm4n.jwt.auth.keycloak.core.TaJwtProvider;

@RestController
@RequestMapping("api/v1")
@Slf4j
@RequiredArgsConstructor
public class Controller {

    private final TaJwtProvider taJwtProvider;

    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public ResponseEntity<?> getToken() {
        var jwtPair = taJwtProvider.getJwtToken();
        return new ResponseEntity<>("Hi, everyone", HttpStatus.OK);
    }

    @RequestMapping(value = "/anonymous", method = RequestMethod.GET)
    public ResponseEntity<?> getAnonymous() {
        return new ResponseEntity<>("Hi, everyone", HttpStatus.OK);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@RequestHeader String authorization) {
        return new ResponseEntity<>("Hi, user", HttpStatus.OK);
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public ResponseEntity<?> getAdmin(@RequestHeader String authorization) {
        return new ResponseEntity<>("Hi, admin", HttpStatus.OK);
    }

    @RequestMapping(value = "/all-user", method = RequestMethod.GET)
    public ResponseEntity<?> getAllUser(@RequestHeader String authorization) {
        return new ResponseEntity<>("Hi, all users", HttpStatus.OK);
    }
}
