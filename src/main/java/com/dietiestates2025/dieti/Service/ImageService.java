package com.dietiestates2025.dieti.Service;

import java.io.IOException;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dietiestates2025.dieti.dto.PropertyDTO;
import com.dietiestates2025.dieti.model.Image;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.repositories.ImageRepository;

@Service
public class ImageService {

    private ImageRepository imageRepository;
    private final DozerBeanMapper dozerBeanMapper;

    public ImageService(ImageRepository imageRepository, DozerBeanMapper dozerBeanMapper){
        this.imageRepository = imageRepository;
        this.dozerBeanMapper = dozerBeanMapper;
    }

    public Image saveImage(MultipartFile file, PropertyDTO propertyDTO) throws IOException {
        Image image = new Image();
        image.setImage(file.getBytes());
        Property property = dozerBeanMapper.map(propertyDTO, Property.class);
        image.setProperty(property);
        return imageRepository.save(image);
    }

    public byte[] getImage(Long id) {
        return imageRepository.findById(id)
                .map(Image::getImage)
                .orElse(null);
    }
}
