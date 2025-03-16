package dashbah.hse.lexiscan.app.controller;

import dashbah.hse.lexiscan.app.dto.ImageDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping
    public String showUploadForm(Model model) {
        model.addAttribute("imageDto", new ImageDto());
        return "index"; // имя вашего HTML-файла без расширения
    }
}
