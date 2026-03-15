package com.boilerplate.home;

import com.boilerplate.annotation.ScopeAllowed;
import com.boilerplate.home.dto.EchoResponseDTO;
import com.boilerplate.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    /**
     * GET /home/echo?message=hello
     *
     * Protected endpoint: requires a valid JWT with the scope "home.echo".
     * Echoes back the message along with the authenticated username.
     */
    @ScopeAllowed({"home.echo"})
    @GetMapping("/echo")
    public EchoResponseDTO echo(
            @RequestParam String message,
            @AuthenticationPrincipal User user) {

        return EchoResponseDTO.builder()
                .echo(message)
                .receivedFrom(user.getUsername())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
