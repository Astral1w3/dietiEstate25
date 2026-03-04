import React, { useEffect, useRef } from 'react';
import './FilterDropdown.css';

const roomOptions = [1, 2, 3, 4, 5, 6, 7, 8, 9]; 
const energyClassOptions = ['A', 'B', 'C', 'D', 'E', 'F', 'G'];

const FilterDropdown = ({ 
    isOpen, 
    onClose, 
    filters, 
    onFilterChange, 
    onApplyFilters, 
    onResetFilters,
    availableServices,
    selectedServices,
    onServiceChange
}) => {
    const dropdownRef = useRef(null);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                onClose();
            }
        };
        if (isOpen) {
            document.addEventListener('mousedown', handleClickOutside);
        }
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [isOpen, onClose]);

    if (!isOpen) {
        return null;
    }

    return (
        <div className="filter-dropdown-container" ref={dropdownRef}>
            <div className="filter-header">
                <h3>Filters</h3>
                <button onClick={onClose} className="close-btn">×</button>
            </div>

            <div className="filter-content">

                
            <div className="filter-section">
                <div className="radio-group-container">
                    
                    <div className="radio-item">
                        <input
                            type="radio"
                            id="type-rent"
                            name="transactionType"
                            value="Rent"
                            checked={filters.transactionType === 'Rent'}
                            onChange={onFilterChange}
                        />
                        <label htmlFor="type-rent">Rent</label>
                    </div>

                    
                    <div className="radio-item">
                        <input
                            type="radio"
                            id="type-sale"
                            name="transactionType"
                            value="Sale"
                            checked={filters.transactionType === 'Sale'}
                            onChange={onFilterChange}
                        />
                        <label htmlFor="type-sale">Sale</label>
                    </div>

                    
                    <div className="radio-item">
                        <input
                            type="radio"
                            id="type-any"
                            name="transactionType"
                            value="any"
                            checked={filters.transactionType === 'any' || !filters.transactionType}
                            onChange={onFilterChange}
                        />
                        <label htmlFor="type-any">Any</label>
                    </div>
                </div>
            </div>

                
                <div className="filter-section">
                    <label>Price (€)</label>
                    <div className="range-inputs">
                        <input type="number" name="minPrice" placeholder="Min Price" value={filters.minPrice} onChange={onFilterChange} className="filter-input" />
                        <input type="number" name="maxPrice" placeholder="Max Price" value={filters.maxPrice} onChange={onFilterChange} className="filter-input" />
                    </div>
                </div>
                
                
                <div className="filter-section">
                    <div className="select-inputs">
                        <div className="input-group">
                            <label htmlFor="rooms">Number of rooms</label>
                            <select id="rooms" name="rooms" value={filters.rooms} onChange={onFilterChange} className="filter-select">
                                <option value="">Any</option>
                                {roomOptions.map(option => (
                                    <option key={option} value={option}>
                                        {option}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="input-group">
                            <label htmlFor="energyClass">Energy Class</label>
                            <select id="energyClass" name="energyClass" value={filters.energyClass} onChange={onFilterChange} className="filter-select">
                                <option value="">Any</option>
                                {energyClassOptions.map(option => (
                                    <option key={option} value={option}>{`${option}`}</option>
                                ))}
                            </select>
                        </div>
                    </div>
                </div>

                
                <div className="filter-section">
                    <label>Services</label>
                    <div className="services-grid">
                        {availableServices && availableServices.map(service => (
                            <div key={service.id} className="service-item">
                                <input type="checkbox" id={`service-${service.id}`} checked={selectedServices.includes(service.id)} onChange={() => onServiceChange(service.id)} />
                                <label htmlFor={`service-${service.id}`}>
                                    {service.emoji} {service.label}
                                </label>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            <div className="filter-footer">
                <button onClick={onResetFilters} className="btn-reset">Reset</button>
                <button onClick={onApplyFilters} className="btn-apply">Apply Filters</button>
            </div>
        </div>
    );
};

export default FilterDropdown;