package dashbah.hse.lexiscan.ml.controller;

import dashbah.hse.lexiscan.ml.dto.MlModelRq;
import dashbah.hse.lexiscan.ml.dto.MlModelRs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/m-model")
@RequiredArgsConstructor
public class ModelController {

    private final Random rnd = new Random();

    @PostMapping("/count")
    public ResponseEntity<MlModelRs> count(@RequestBody MlModelRq mlModelRq) {
        return ResponseEntity.ok(MlModelRs.builder()
                .rquid(mlModelRq.getRquid())
                .percentage(rnd.nextDouble(0, 1))
                .build());
    }

}