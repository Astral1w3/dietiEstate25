
import React from 'react';
import './PropertyCard.css';

import { FaEye } from 'react-icons/fa';
const amenityMap = {
    'close to parks': { label: 'Parks Nearby', emoji: '🌳' },
    'close to schools': { label: 'Schools Nearby', emoji: '🏫' },
    'close to public transport': { label: 'Public Transport', emoji: '🚇' },
};

const PropertyCard = ({ property, onMouseEnter, onMouseLeave }) => {
    const imageUrl = property.imageUrls && property.imageUrls.length > 0 
        ? property.imageUrls[0]
        : `${process.env.PUBLIC_URL}/notFound.jpg`;

    const displayAddress = property.address
        ? `${property.address.street}, ${property.address.houseNumber}`
        : 'Address not available';

    const displayLocation = property.address
        ? `${property.address.municipality.municipalityName}`
        : 'Location not available';

    const displayZipCode = property.address
        ? `${property.address.municipality.zipCode}`
        : 'Zip Code not available';
    
    const formattedPrice = property.price 
        ? new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' }).format(property.price)
        : 'Price not available';

    const nearbyAmenities = property.services
        ? property.services
            .map(service => amenityMap[service.serviceName])
            .filter(Boolean) 
        : [];

    return (
        <div className="property-card" onMouseEnter={onMouseEnter} onMouseLeave={onMouseLeave}>
            <div className="card-image-container">
                <img src={imageUrl} alt={displayAddress} />
                {property.tags && property.tags.map(tag => (
                    <span key={tag} className="card-tag">{tag}</span>
                ))}
            </div>
            <div className="card-details">
                <p className="card-price">{formattedPrice}</p>
                <p className="card-specs">
                    <span>{property.numberOfRooms || 'N/A'} rooms</span>
                    <span> • {property.squareMeters || 'N/A'} m²</span>
                    <span className="card-views">
                    <FaEye /> 
                    {property.propertyStats ? property.propertyStats.numberOfViews : 0}
                    </span>
                </p>
                <p className="card-address">{displayAddress}</p>
                <p className="card-city">{displayLocation} - {displayZipCode}</p>

                {nearbyAmenities.map((amenity, index) => (
                            <span key={`${amenity.label}-${index}`} className="amenity-tag">
                                {amenity.emoji} {amenity.label}
                            </span>
                        ))}

                <button className="card-cta-btn">More Details</button>
            </div>
        </div>
    );
};

export default PropertyCard;
