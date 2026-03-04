import React, { useState, useEffect } from 'react';
import { useSearchParams, Link, useLocation, useNavigate } from 'react-router-dom';

import SearchBar from '../../components/SearchBar/SearchBar';
import PropertyCard from '../../components/PropertyCard/PropertyCard';
import MapDisplay from '../../components/MapDisplay/MapDisplay';
import FilterDropdown from '../../components/FilterDropdown/FilterDropdown';

import './PropertiesPage.css';

import { searchPropertiesByLocation } from '../../services/propertyService';

const availableServices = [
    { id: 'concierge', label: 'Concierge', emoji: '🛎️' },
    { id: 'air conditioning', label: 'Air Conditioning', emoji: '❄️' },
    { id: 'close to schools', label: 'Schools Nearby', emoji: '🏫' },
    { id: 'close to parks', label: 'Parks Nearby', emoji: '🌳' },
    { id: 'close to public transport', label: 'Public Transport', emoji: '🚇' },
    { id: 'elevator', label: 'Elevator', emoji: '🛗' },
    { id: 'heating', label: 'Heating', emoji: '🔥' },
    { id: 'garage', label: 'Garage', emoji: '🚗' },
    { id: 'Cellar', label: 'Cellar', emoji: '🍷' },
    { id: 'balcony', label: 'Balcony', emoji: '🌇' },
    { id: 'terrace', label: 'Terrace', emoji: '🪴' },
];

const initialFilters = {
    minPrice: '', maxPrice: '', rooms: '', energyClass: '', municipality: '', transactionType: 'any'
};

const PropertiesPage = () => {
    const [originalProperties, setOriginalProperties] = useState([]);
    const [displayedProperties, setDisplayedProperties] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [pageInfo, setPageInfo] = useState(null);
    const [hoveredPropertyId, setHoveredPropertyId] = useState(null);
    const [isFilterOpen, setFilterOpen] = useState(false);
    const [filters, setFilters] = useState(initialFilters);
    const [selectedServices, setSelectedServices] = useState([]);
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const location = useLocation();

     useEffect(() => {
        const locationQuery = searchParams.get('location');
        const typeQuery = searchParams.get('type');
        const currentPage = parseInt(searchParams.get('page'), 10) || 0;
        const pageSize = parseInt(searchParams.get('size'), 10) || 25;

        const fetchProperties = async () => {
            if (!locationQuery) {
                setOriginalProperties([]);
                setDisplayedProperties([]);
                setPageInfo(null);
                setIsLoading(false);
                return;
            }
            
            setIsLoading(true);
            setError(null);
            
            try {
                const pageResponse = await searchPropertiesByLocation(locationQuery, currentPage, pageSize);
                const fetchedProperties = pageResponse.content || []; 
                
                setPageInfo({
                    totalPages: pageResponse.totalPages,
                    totalElements: pageResponse.totalElements,
                    currentPage: pageResponse.currentPage,
                });

                let initialData = fetchedProperties;
                const newFilters = { ...initialFilters };

                if (typeQuery && (typeQuery.toLowerCase() === 'sale' || typeQuery.toLowerCase() === 'rent')) {
                    initialData = fetchedProperties.filter(p => p.saleType && p.saleType.toLowerCase() === typeQuery.toLowerCase());
                    newFilters.transactionType = typeQuery;
                }

                setOriginalProperties(fetchedProperties);
                setDisplayedProperties(initialData);
                setFilters(newFilters);

            } catch (err) {
                console.error("Error retrieving properties:", err);
                setError("Unable to load properties. Please try again later.");
            } finally { 
                setIsLoading(false); 
            }
        };

        fetchProperties();
    }, [searchParams]);

    const handlePageChange = (newPage) => {
        const currentParams = new URLSearchParams(location.search);
        currentParams.set('page', newPage);
        navigate(`${location.pathname}?${currentParams.toString()}`);
    };

    const handleApplyFilters = () => {
        let filtered = [...originalProperties];

        if (filters.transactionType && filters.transactionType !== 'any') {
            filtered = filtered.filter(p => p.saleType === filters.transactionType);
        }

        const minPrice = parseFloat(filters.minPrice);
        const maxPrice = parseFloat(filters.maxPrice);
        if (!isNaN(minPrice)) {
            filtered = filtered.filter(p => p.price >= minPrice);
        }
        if (!isNaN(maxPrice)) {
            filtered = filtered.filter(p => p.price <= maxPrice);
        }

        if (filters.rooms && filters.rooms !== "") {
            filtered = filtered.filter(p => p.numberOfRooms === parseInt(filters.rooms, 10));
        }

        if (filters.energyClass) {
            filtered = filtered.filter(p => p.energyClass === filters.energyClass);
        }

        if (selectedServices.length > 0) {
            filtered = filtered.filter(property => {
                if (!property.services || !Array.isArray(property.services)) {
                    return false;
                }
                return selectedServices.every(serviceId => 
                    property.services.some(serviceObject => serviceObject.serviceName === serviceId)
                );
            });
        }

        setDisplayedProperties(filtered);
        setFilterOpen(false);
    };

    const handleResetFilters = () => {
        setFilters(initialFilters);
        setSelectedServices([]);
        setDisplayedProperties(originalProperties);
        setFilterOpen(false);
    };

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters(prev => ({ ...prev, [name]: value }));
    };

    const handleServiceChange = (serviceId) => {
        setSelectedServices(prev => 
            prev.includes(serviceId) ? prev.filter(id => id !== serviceId) : [...prev, serviceId]
        );
    };
    
    const locationQuery = searchParams.get('location');
    const pageTitle = locationQuery ? `Results for: "${locationQuery}"` : "Find a location to get started";
    const backUrlFromSearch = location.pathname + location.search;
    console.log(pageInfo)
return (
        <>
            <div className="properties-page-layout">
                <div className="listings-column">
                    <SearchBar />
                    <div className="listings-header">
                        <h2>{pageTitle}</h2>
                        <div className="listings-actions">
                            <span>{pageInfo ? pageInfo.totalElements : 0} results</span>
                            <div className="filter-wrapper">
                                <button onClick={() => setFilterOpen(prev => !prev)} className="btn-filter">
                                    Filters ▼
                                </button>
                                <FilterDropdown
                                    isOpen={isFilterOpen}
                                    onClose={() => setFilterOpen(false)}
                                    filters={filters}
                                    onFilterChange={handleFilterChange}
                                    onApplyFilters={handleApplyFilters}
                                    onResetFilters={handleResetFilters}
                                    availableServices={availableServices}
                                    selectedServices={selectedServices}
                                    onServiceChange={handleServiceChange}
                                />
                            </div>
                        </div>
                    </div>

                    <div className="property-grid">
                        {isLoading && <p>Loading...</p>}
                        {error && <p style={{ color: 'red' }}>{error}</p>}
                        {!isLoading && !error && displayedProperties.length > 0 ? (
                            displayedProperties.map(property => (
                                <Link 
                                    to={`/property/${property.idProperty}`} 
                                    key={`prop-card-${property.idProperty}`} 
                                    className="property-card-link"
                                    state={{ from: backUrlFromSearch }}
                                >
                                    <PropertyCard 
                                        property={property}
                                        onMouseEnter={() => setHoveredPropertyId(property.idProperty)}
                                        onMouseLeave={() => setHoveredPropertyId(null)}
                                    />
                                </Link>
                            ))
                        ) : (
                            !isLoading && !error && <p>No results found. Try a new search.</p>
                        )}
                    </div>
                    
                    
                    <div className="pagination-controls">
                        {pageInfo && pageInfo.totalPages > 1 && (
                            <>
                                <button
                                    onClick={() => handlePageChange(pageInfo.currentPage - 1)}
                                    disabled={pageInfo.currentPage === 0}
                                >
                                    &laquo; Previous
                                </button>

                                <span>
                                    {
}
                                    Page {pageInfo.currentPage + 1} of {pageInfo.totalPages}
                                </span>

                                <button
                                    onClick={() => handlePageChange(pageInfo.currentPage + 1)}
                                    disabled={pageInfo.currentPage + 1 >= pageInfo.totalPages}
                                >
                                    Next &raquo;
                                </button>
                            </>
                        )}
                    </div>
                </div>

                <div className="map-column">
                    <MapDisplay 
                        properties={displayedProperties} 
                        hoveredPropertyId={hoveredPropertyId}
                        backUrl={backUrlFromSearch}
                        onMarkerEnter={setHoveredPropertyId}
                        onMarkerLeave={() => setHoveredPropertyId(null)}
                    />
                </div>
            </div>
        </>
    );
};

export default PropertiesPage;