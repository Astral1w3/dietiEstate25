import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
    GeoapifyGeocoderAutocomplete,
    GeoapifyContext
} from '@geoapify/react-geocoder-autocomplete';

import '@geoapify/geocoder-autocomplete/styles/minimal.css'; 
import './SearchBar.css';

const SearchBar = () => {
    const navigate = useNavigate();

    const handlePlaceSelect = (place) => {
        if (place) {
            const props = place.properties;
            const locationName = props.city || props.state || props.postcode || props.name;

            if (locationName) {
                navigate(`/properties?location=${encodeURIComponent(locationName)}`);
            }
        }
    };

     const filterSuggestions = (suggestions) => {
        const allowedTypes = [
            'city', 
            'state',
            'county',
            'postcode',
            'region'
        ];
        return suggestions.filter(suggestion => 
            allowedTypes.includes(suggestion.properties.result_type)
        );
    };


    return (
        <div className="page-search-form">
            
            <GeoapifyContext apiKey={process.env.REACT_APP_GEOAPIFY_API_KEY} className="geopify-context">
            
                <GeoapifyGeocoderAutocomplete
                    placeholder="Search by city or region "
                    
                    lang="it"
                    filterByCountryCode={["it"]}
                    suggestionsFilter={filterSuggestions}
                    placeSelect={handlePlaceSelect}
                    className="geopify-geocoder-autocomplete"
                />

            </GeoapifyContext>

            {
}
            <button type="button" aria-label="Search" className="search-button-static">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
            </button>
        </div>
    );
};

export default SearchBar;