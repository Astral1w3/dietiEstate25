import React from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { useNavigate } from 'react-router-dom';

const GEOAPIFY_API_KEY = process.env.REACT_APP_GEOAPIFY_API_KEY;

const defaultIcon = L.icon({
    iconUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png',
    shadowSize: [41, 41]
});

const hoveredIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    shadowSize: [41, 41]
});

const MapDisplay = ({ properties, hoveredPropertyId, backUrl, onMarkerEnter, onMarkerLeave }) => {
    const navigate = useNavigate();

    const validProperties = properties.filter(p => 
        p.address && 
        typeof p.address.latitude === 'number' && 
        typeof p.address.longitude === 'number'
    );

    const mapCenter = validProperties.length > 0
        ? [validProperties[0].address.latitude, validProperties[0].address.longitude]
        : [40.8518, 14.2681]; 

    const mapKey = validProperties.length > 0
        ? `${validProperties[0].idProperty}-${validProperties.length}`
        : "default-map";

    if (!GEOAPIFY_API_KEY || GEOAPIFY_API_KEY === "LA_TUA_API_KEY_DI_GEOAPIFY") {
        return <div style={{padding: '20px', backgroundColor: '#fff2f2'}}>
            <strong>Attention:</strong> Enter your Geoapify API key into the MapDisplay.js file to display the map.
        </div>
    }

    return (
        <MapContainer key={mapKey} center={mapCenter} zoom={13} style={{ height: '100%', width: '100%' }}>  
            <TileLayer
                url={`https://maps.geoapify.com/v1/tile/positron/{z}/{x}/{y}.png?apiKey=${GEOAPIFY_API_KEY}`}
                attribution='Powered by <a href="https://www.geoapify.com/" target="_blank">Geoapify</a> | © OpenStreetMap contributors'
            />

            {validProperties.map(property => {
                const popupAddress = `${property.address.street}, ${property.address.municipality.municipalityName}`;
                const formattedPrice = new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' }).format(property.price);

                return (
                    <Marker 
                        key={property.idProperty} 
                        position={[property.address.latitude, property.address.longitude]}
                        icon={property.idProperty === hoveredPropertyId ? hoveredIcon : defaultIcon}
                        eventHandlers={{
                            click: () => {
                                navigate(`/property/${property.idProperty}`, { state: { from: backUrl } });
                            },
                            mouseover: (event) => {
                                if (onMarkerEnter) onMarkerEnter(property.idProperty);
                                event.target.openPopup();
                            },
                            mouseout: (event) => {
                                if (onMarkerLeave) onMarkerLeave();
                                event.target.closePopup();
                            }
                        }}
                    >
                        <Popup>
                            <strong>{formattedPrice}</strong><br/>
                            {popupAddress}
                        </Popup>
                    </Marker>
                );
            })}
        </MapContainer>
    );
};

export default MapDisplay;
