
import React, { useState, useEffect, useCallback } from 'react';
import './SlideshowModal.css';

const SlideshowModal = ({ images, onClose }) => {
    const [currentIndex, setCurrentIndex] = useState(0);

    const goToNext = useCallback(() => {
        const isLastSlide = currentIndex === images.length - 1;
        const newIndex = isLastSlide ? 0 : currentIndex + 1;
        setCurrentIndex(newIndex);
    }, [currentIndex, images]);

    const goToPrevious = useCallback(() => {
        const isFirstSlide = currentIndex === 0;
        const newIndex = isFirstSlide ? images.length - 1 : currentIndex - 1;
        setCurrentIndex(newIndex);
    }, [currentIndex, images]);

    useEffect(() => {
        const handleKeyDown = (event) => {
            if (event.key === 'ArrowRight') {
                goToNext();
            } else if (event.key === 'ArrowLeft') {
                goToPrevious();
            } else if (event.key === 'Escape') {
                onClose();
            }
        };

        window.addEventListener('keydown', handleKeyDown);

        return () => {
            window.removeEventListener('keydown', handleKeyDown);
        };
    }, [goToNext, goToPrevious, onClose]);


    return (
        <div className="slideshow-modal-overlay" onClick={onClose}>
            {}
            <div className="slideshow-content" onClick={(e) => e.stopPropagation()}>
                
                {}
                <button className="slideshow-close-btn" onClick={onClose}>×</button>

                {}
                <button className="slideshow-nav-btn prev" onClick={goToPrevious}>‹</button>
                
                {}
                <img 
                    src={images[currentIndex]} 
                    alt={`Property view ${currentIndex + 1}`} 
                    className="slideshow-image"
                />

                {}
                <button className="slideshow-nav-btn next" onClick={goToNext}>›</button>

                {}
                <div className="slideshow-counter">
                    {currentIndex + 1} / {images.length}
                </div>
            </div>
        </div>
    );
};

export default SlideshowModal;