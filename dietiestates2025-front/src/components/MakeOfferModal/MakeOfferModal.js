import React, { useState } from 'react';
import { createOffer } from '../../services/offerService';
import SuccessView from '../SuccessView/SuccessView';
import './MakeOfferModal.css';

const MakeOfferModal = ({ isOpen, onClose, propertyId, currentPrice }) => {
    const [offerPrice, setOfferPrice] = useState('');
    const [error, setError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isSuccess, setIsSuccess] = useState(false);

    if (!isOpen) {
        return null;
    }

    const handleClose = () => {
        setOfferPrice('');
        setError('');
        setIsSuccess(false);
        onClose();
    };

    const handleSubmit = async () => {
        const priceValue = parseFloat(offerPrice);

        if (!priceValue || priceValue <= 0) {
            setError('Please enter a valid amount.');
            return;
        }
        if (priceValue >= currentPrice) {
            setError(`The offer must be lower than the current price.`);
            return;
        }

        setIsSubmitting(true);
        setError('');

        try {
            const offerData = {
                propertyId: propertyId,
                offerPrice: priceValue
            };
            
            await createOffer(offerData);
            
            setIsSuccess(true);

            setTimeout(() => {
                handleClose();
            }, 3000);

        } catch (err) {
            if (err.response && err.response.data && err.response.data.message) {
                setError(err.response.data.message);
            } else {
                setError("A connection error occurred. Please try again later.");
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="modal-overlay" onClick={handleClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                
                {isSuccess ? (
                    <SuccessView title="Offerta Inviata!">
                        <p>
                            Your offer of{' '}
                            <strong>
                                {parseFloat(offerPrice).toLocaleString('it-IT', { style: 'currency', currency: 'EUR' })}
                            </strong>
                            {' '}was sent successfully.
                        </p>
                    </SuccessView>
                ) : (
                    <>
                        <h2>Make your offer</h2>
                        <p>
                            Current price: {currentPrice.toLocaleString('it-IT', { style: 'currency', currency: 'EUR' })}
                        </p>
                        
                        <div className="offer-input-group">
                            <label htmlFor="offerPrice">Your offer (€)</label>
                            <input
                                type="number"
                                id="offerPrice"
                                value={offerPrice}
                                onChange={(e) => setOfferPrice(e.target.value)}
                                placeholder="Es. 450000"
                                disabled={isSubmitting}
                            />
                        </div>
                        
                        {error && <p className="modal-error">{error}</p>}
                        
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={handleClose} disabled={isSubmitting}>
                                Cancel
                            </button>
                            <button 
                                className="btn btn-primary" 
                                onClick={handleSubmit} 
                                disabled={isSubmitting || !offerPrice}
                            >
                                {isSubmitting ? 'Invio...' : 'Invia Offerta'}
                            </button>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};

export default MakeOfferModal;