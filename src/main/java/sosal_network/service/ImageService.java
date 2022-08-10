package sosal_network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sosal_network.entity.Image;
import sosal_network.entity.Post;
import sosal_network.entity.PostImage;
import sosal_network.entity.User;
import sosal_network.repository.ImageRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {
    @Autowired
    private UserService userService;

    @Autowired
    private ImageRepository imageRepository;

    public List<PostImage> convertPostImages(List<MultipartFile> files, User user, Post post) {
        return files.stream().map(file -> {
            try {
                Image image = toImageEntity(file, user);
                imageRepository.save(image);
                return new PostImage(post, image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    public Image toImageEntity(MultipartFile file, User user )throws IOException {
        return new Image(file.getName(), file.getOriginalFilename(), file.getSize(), file.getContentType(),
                file.getBytes(), user);
    }


    public void saveImage(MultipartFile file, User user) throws IOException {
        if (file.getSize() != 0) {
            if (user.getImage() != null) {
                Image oldImage =user.getImage();
                Image img = toImageEntity(file, user);
                user.setImage(img);
                userService.save(user);
                imageRepository.deleteById(oldImage.getId());
            } else {
                user.setImage(toImageEntity(file, user));
                userService.save(user);
            }
        }
    }

}
