import React, { useState, useEffect } from 'react';
import { useParams, Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

import { getPropertyById, trackPropertyView } from '../../services/propertyService';

import PropertyGallery from '../../components/PropertyGallery/PropertyGallery';
import MapDisplay from '../../components/MapDisplay/MapDisplay';
import BookingVisitModal from '../../components/BookingVisitModal/BookingVisitModal';
import MakeOfferModal from '../../components/MakeOfferModal/MakeOfferModal';

import './PropertyDetailPage.css';
/**
 * Componente React per la visualizzazione della pagina di dettaglio di una singola proprietà immobiliare.
 * Recupera i dati della proprietà basandosi sull'ID presente nell'URL, ne traccia la visualizzazione,
 * e mostra informazioni complete come galleria di immagini, dettagli, mappa e opzioni di interazione
 * (richiesta di visite, invio di offerte). Gestisce gli stati di caricamento e di errore.
 * @returns {JSX.Element} Il layout completo della pagina di dettaglio della proprietà.
 */
const PropertyDetailPage = () => {
    const [property, setProperty] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    const [isTourModalOpen, setIsTourModalOpen] = useState(false);
    const [isOfferModalOpen, setIsOfferModalOpen] = useState(false);

    const { propertyId } = useParams();
    const { isAuthenticated } = useAuth();
    const location = useLocation();

    useEffect(() => {
        /**
         * Funzione asincrona per tracciare la visualizzazione di una proprietà e successivamente caricarne i dati.
         * Invoca il servizio `trackPropertyView` e, in caso di successo, procede con il recupero dei dati
         * tramite `getPropertyById`, aggiornando lo stato del componente.
         */
        const fetchPropertyAndTrackView = async () => {
            if (propertyId) {
                try {
                    await trackPropertyView(propertyId);

                    setLoading(true);
                    const data = await getPropertyById(propertyId);
                    setProperty(data);
                    setError(null);
                } catch (err) {
                    console.error("Error retrieving or tracking property:", err);
                    setError("Property not found or loading error.");
                    setProperty(null);
                } finally {
                    setLoading(false);
                }
            }
        }; 
            fetchPropertyAndTrackView();
    }, [propertyId]);
    
    /**
     * URL di fallback per il link "Back to Search".
     * Utilizza la posizione precedente dallo stato della navigazione o torna alla pagina delle proprietà.
     * @type {string}
     */
    const backLinkUrl = location.state?.from || '/properties';

    useEffect(() => {
        const fetchProperty = async () => {
            if (!propertyId) return;
            try {
                setLoading(true);
                const data = await getPropertyById(propertyId);
                setProperty(data);
                setError(null);
            } catch (err) {
                console.error("Error retrieving property details:", err);
                setError("Property not found or loading error.");
                setProperty(null);
            } finally {
                setLoading(false);
            }
        };

        fetchProperty();
    }, [propertyId]);


    if (loading) return <div className="property-detail-container"><h2>Loading...</h2></div>;
    if (error) return <div className="property-detail-container"><h2>{error}</h2></div>;
    if (!property) return <div className="property-detail-container"><h2>Property not found.</h2></div>;

    /**
     * Verifica se la proprietà è disponibile per l'affitto.
     * @type {boolean}
     */
    const isForRent = property.saleTypes.some(st => st.saleType.toLowerCase() === 'rent');

    /**
     * Compone l'indirizzo completo della proprietà in un formato leggibile.
     * @type {string}
     */
    const fullAddress = `${property.address.street}, ${property.address.houseNumber} - ${property.address.municipality.municipalityName} (${property.address.municipality.province.acronym})`;
    /**
     * Estrae la lista dei nomi dei servizi/caratteristiche della proprietà.
     * @type {string[]}
     */
    const featureList = property.services.map(service => service.serviceName);
    return (
        <div className="property-detail-container">
            <main className="property-detail-main">
                <Link to={backLinkUrl} className="back-to-search-link">← Back to Search</Link>
             
                <PropertyGallery images={property.imageUrls} />
                
                <div className="detail-grid">
                    <div className="info-primary">
                        <h1>{fullAddress}</h1>
                        <h2>
                            
                            {property.price.toLocaleString('it-IT', { style: 'currency', currency: 'EUR' })}
                            {isForRent && ' / month'}
                        </h2>
                        <div className="stats">
                            <span>{property.numberOfRooms} {property.numberOfRooms === 1 ? 'Locale' : 'Locali'}</span>
                            <span>{property.squareMeters} m²</span>
                            <span>Energy Class {property.energyClass}</span>
                        </div>
                    </div>

                    <div className="contact-box">
                        <button className="btn btn-primary btn-contact" onClick={() => setIsTourModalOpen(true)} disabled={!isAuthenticated}>
                            Request a Visit
                        </button>
                        {!isForRent && (
                            <button className="btn btn-primary btn-contact" onClick={() => setIsOfferModalOpen(true)} disabled={!isAuthenticated}>
                                Make an Offer
                            </button>
                        )}
                        {!isAuthenticated && (<p className="auth-message">You must log in to perform this action.</p>)}
                    </div>
                </div>

                {isAuthenticated && (
                    <>
                        <BookingVisitModal
                            isOpen={isTourModalOpen}
                            onClose={() => setIsTourModalOpen(false)}
                            propertyId={property.idProperty}
                        />
                        <MakeOfferModal
                            isOpen={isOfferModalOpen}
                            onClose={() => setIsOfferModalOpen(false)}
                            propertyId={property.idProperty}
                            currentPrice={property.price}
                        />
                    </>
                )}
                 

                <section className="info-section">
                    <h2>Description</h2>
                    <p>{property.description}</p>
                </section>
                
                <section className="info-section">
                    <h2>Main Features</h2>
                    <ul>{featureList.map((feature, i) => <li key={i}>{feature}</li>)}</ul>
                </section>

                <section className="info-section">
                    <h2>Position</h2>
                    <div className="map-wrapper"><MapDisplay properties={[property]} hoveredPropertyId={property.idProperty} /></div>
                </section>
            </main>
        </div>
    );
};

export default PropertyDetailPage;