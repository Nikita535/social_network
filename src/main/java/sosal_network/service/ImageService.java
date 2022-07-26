package sosal_network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sosal_network.aop.LoggableAroundMethod.Loggable;
import sosal_network.entity.Image;
import sosal_network.entity.User;
import sosal_network.repository.ImageRepository;

import java.io.IOException;

@Service
public class ImageService {
    @Autowired
    private UserService userService;

    @Autowired
    private ImageRepository imageRepository;

    @Loggable
    public Image toImageEntity(MultipartFile file) throws IOException {
        return new Image(file.getName(), file.getOriginalFilename(), file.getSize(), file.getContentType(),
                file.getBytes());
    }


    @Loggable
    public void saveImage(MultipartFile file, User user) throws IOException {
        if (file.getSize() != 0) {
            if (user.getImage() != null) {
                Image oldImage = user.getImage();
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

    public void save(Image image) {
        imageRepository.save(image);
    }
}
