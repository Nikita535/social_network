package sosal_network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sosal_network.entity.*;
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
    public List<PostImage> convertPostImages(List<MultipartFile> files, User user, Post post)
    {
        return files.stream().map(file-> {
            try {
                Image image=toImageEntity(file,user,false);
                imageRepository.save(image);
                return new PostImage(post,image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    public Image toImageEntity(MultipartFile file, User user,boolean isPreview) throws IOException {
        return new Image(file.getName(), file.getOriginalFilename(), file.getSize(), file.getContentType(),
                file.getBytes(), user, isPreview);
    }


    public void saveImage(MultipartFile file,User user) throws IOException {
        if (file.getSize() != 0) {
            if (!user.getImages().isEmpty()) {
                Image image = imageRepository.findImageByUserAndIsPreview(user,true);
                Image img = toImageEntity(file, user,true);
                img.setId(image.getId());
                img.setUser(image.getUser());
                image = img;
                user.addImageToUser(image);
                imageRepository.save(image);
            } else {
                user.addImageToUser(toImageEntity(file, user,true));
                imageRepository.save(toImageEntity(file, user,true));
            }
        }
    }

    public Image findImageByUserAndIsPreview(User user,boolean isPreview)
    {
        return imageRepository.findImageByUserAndIsPreview(user,isPreview);
    }
}
