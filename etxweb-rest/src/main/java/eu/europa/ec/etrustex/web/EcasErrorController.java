package eu.europa.ec.etrustex.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class EcasErrorController {

    private static final String NOT_FOUND_ERROR = "{} - Not Found error!. ";
    private static final String AUTHENTICATION_ERROR = "{} - Authentication error!. ";

    /*
     * In SHS target url contains /etrustex/web/ ctx path
     */
    @GetMapping("/etrustex/web/authentication-error")
    public ResponseEntity<Void> handle401(HttpServletRequest request) {
        log.error(AUTHENTICATION_ERROR, request.getRequestURL());
        return new ResponseEntity<>( HttpStatus.UNAUTHORIZED );
    }

    @GetMapping("/authentication-error")
    public ResponseEntity<Void> handleDev401(HttpServletRequest request) {
        return handle401(request);
    }

    @GetMapping("/etrustex/web/not-found")
    public ResponseEntity<Void> handle404(HttpServletRequest request) {
        log.error(NOT_FOUND_ERROR, request.getRequestURL());
        return new ResponseEntity<>( HttpStatus.NOT_FOUND );
    }

    @GetMapping("/not-found")
    public ResponseEntity<Void> handleDev404(HttpServletRequest request) {
        return  handle404(request);
    }
}
