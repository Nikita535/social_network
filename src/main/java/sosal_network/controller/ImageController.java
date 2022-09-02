package sosal_network.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sosal_network.entity.Image;
import sosal_network.repository.ImageRepository;
import sosal_network.service.ImageService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageRepository imageRepository;
    @Autowired
    private ImageService imageService;

    @GetMapping("/image/{id}")
    private ResponseEntity<?> getImageById(@PathVariable Long id) {
        Image image = imageRepository.findImageById(id);
        return ResponseEntity.ok()
                .header("fileName", image.getOriginalFileName())
                .contentType(MediaType.valueOf(image.getContentType()))
                .contentLength(image.getSize())
                .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
    }

    @RequestMapping(value = "/image/create", method = RequestMethod.POST)
    @ResponseBody
    public List<Image> processReloadData(@RequestParam("file") List<MultipartFile> files) {
        return files.stream().map(file -> {
            try {
                Image image = imageService.toImageEntity(file);
                imageService.save(image);
                return image;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

}
