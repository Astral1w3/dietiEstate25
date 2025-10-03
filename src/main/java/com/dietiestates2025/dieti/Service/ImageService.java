package com.dietiestates2025.dieti.Service;


import java.io.IOException;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dietiestates2025.dieti.dto.PropertyDTO;
import com.dietiestates2025.dieti.model.Image;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.repositories.ImageRepository;
import com.dietiestates2025.dieti.repositories.PropertyRepository;

@Service
public class ImageService {

    private ImageRepository imageRepository;
    private final DozerBeanMapper dozerBeanMapper;
    PropertyRepository p;

    public ImageService(ImageRepository imageRepository, DozerBeanMapper dozerBeanMapper, PropertyRepository p){
        this.imageRepository = imageRepository;
        this.dozerBeanMapper = dozerBeanMapper;
        this.p = p;
    }

    public Image saveImage(MultipartFile file, PropertyDTO propertyDTO) throws IOException {
        Image image = new Image();
        image.setImage(file.getBytes());
        Property property = dozerBeanMapper.map(propertyDTO, Property.class);
        image.setProperty(property);
        return imageRepository.save(image);
    }

    //for testing
    // public void saveImage(byte[] img) {
    //     try{
    //         Image image = new Image();
    //         image.setImage(img);
    //         image.setProperty(p.findById(1).get());
    //         imageRepository.save(image);
    //     }catch(Exception e){
    //         System.out.println(e);
    //     }

    // }

    public byte[] getImage(int id) {
        return imageRepository.findById(id)
                .map(Image::getImage)
                .orElse(null);
    }
}
