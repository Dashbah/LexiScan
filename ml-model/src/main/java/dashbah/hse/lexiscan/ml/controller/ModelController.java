package dashbah.hse.lexiscan.ml.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dashbah.hse.lexiscan.ml.dto.MlModelRq;
import dashbah.hse.lexiscan.ml.dto.MlModelRs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/ml-model")
@RequiredArgsConstructor
@Slf4j
public class ModelController {

    private final Random rnd = new Random();

    @PostMapping("/count")
    public ResponseEntity<MlModelRs> count() {
        var rs = ResponseEntity.ok(MlModelRs.builder()
                .confidence(rnd.nextDouble(0, 1))
                .prediction("Dyslexia")
                .build());
        log.info("response: " + rs);
        return rs;
    }

}