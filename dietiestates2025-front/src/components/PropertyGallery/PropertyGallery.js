import React, { useState } from 'react';
import SlideshowModal from '../SlideshowModal/SlideshowModal';
import './PropertyGallery.css';

const PropertyGallery = ({ images }) => {
    const [isSlideshowOpen, setSlideshowOpen] = useState(false);
    const [slideshowStartIndex, setSlideshowStartIndex] = useState(0);


    const openSlideshow = (index) => {
        setSlideshowStartIndex(index);
        setSlideshowOpen(true);
    };

    if (!images || images.length === 0) {
        return null;
    }


    return (
        <>
            <div className="gallery-container">
                {}
                <div className="gallery-main-image" onClick={() => openSlideshow(0)}>
                    <img src={images[0]} alt="Main property view" />
                </div>

                {}
                <div className="gallery-thumbnails">
                    {images.slice(1, 5).map((image, index) => (
                        <div 
                            key={index} 
                            className="gallery-thumbnail"
                            onClick={() => openSlideshow(index + 1)}
                        >
                            <img src={image} alt={`Property view ${index + 2}`} />
                        </div>
                    ))}
                </div>
            </div>

            {}
            {isSlideshowOpen && (
                <SlideshowModal 
                    images={images} 
                    startIndex={slideshowStartIndex}
                    onClose={() => setSlideshowOpen(false)} 
                />
            )}
        </>
    );
};

export default PropertyGallery;
