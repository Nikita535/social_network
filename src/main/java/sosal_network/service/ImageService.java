package sosal_network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sosal_network.entity.Image;
import sosal_network.entity.Post;
import sosal_network.entity.PostImage;
import sosal_network.entity.User;
import sosal_network.repository.ImageRepository;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {
    @Autowired
    private UserService userService;

    @Autowired
    private ImageRepository imageRepository;

    public List<PostImage> convertPostImages(List<MultipartFile> files, Post post) {
        return files.stream().map(file -> {
            try {
                Image image = toImageEntity(file);
                imageRepository.save(image);
                return new PostImage(post, image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    public Image toImageEntity(MultipartFile file )throws IOException {
        return new Image(file.getName(), file.getOriginalFilename(), file.getSize(), file.getContentType(),
                file.getBytes());
    }


    public void saveImage(MultipartFile file, User user) throws IOException {
        if (file.getSize() != 0) {
            if (user.getImage() != null) {
                Image oldImage =user.getImage();
                Image img = toImageEntity(file);
                user.setImage(img);
                userService.save(user);
                imageRepository.deleteById(oldImage.getId());
            } else {
                user.setImage(toImageEntity(file));
                userService.save(user);
            }
        }
    }
}
