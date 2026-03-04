import React, { useState, useMemo, useEffect } from 'react';
import { Link } from 'react-router-dom';
import 'jspdf-autotable';
import { FaTags, FaRegClock, FaRegCheckCircle, FaRegTimesCircle } from 'react-icons/fa';

import StatCard from '../StatCard/StatCard';
import SuccessView from '../SuccessView/SuccessView';
import ConfirmationModal from '../ConfirmationModal/ConfirmationModal';

import { getMyOffers, acceptOffer, declineOffer } from '../../services/offerService';
import './OffersView.css';

const OffersView = () => {
    const [offers, setOffers] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [filter, setFilter] = useState('All');
    const [isSuccess, setIsSuccess] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [actionToConfirm, setActionToConfirm] = useState(null);

    const fetchOffers = async () => {
        try {
            const data = await getMyOffers();
            setOffers(data);
            setError(null);
        } catch (err) {
            setError("Failed to load offers. Please try again.");
            console.error(err);
        }
    };

    useEffect(() => {
        const initialFetch = async () => {
            setIsLoading(true);
            await fetchOffers();
            setIsLoading(false);
        };
        initialFetch();
    }, []);

    const filteredOffers = useMemo(() => {
        if (filter === 'All') return offers;
        return offers.filter(o => o.state === filter);
    }, [filter, offers]);

    const handleUpdateOffer = (offerId, action) => {
        setActionToConfirm({ offerId, action });
        setIsModalOpen(true);
    };

    const handleConfirmAction = async () => {
        if (!actionToConfirm) return;

        const { offerId, action } = actionToConfirm;
        
        setIsModalOpen(false);
        
        try {
            const actionToPerform = action === 'accept' ? acceptOffer : declineOffer;
            await actionToPerform(offerId);

            setSuccessMessage(`Offer has been successfully ${action === 'accept' ? 'accepted' : 'declined'}.`);
            setIsSuccess(true);
        } catch (err) {
            alert(`Error: ${err.response?.data?.message || 'Could not update the offer.'}`);
        } finally {
            setActionToConfirm(null);
            
            setTimeout(() => {
                setIsSuccess(false);
                fetchOffers();
            }, 2500);
        }
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setActionToConfirm(null);
    };

    const totalOffers = offers.length;
    const pendingCount = offers.filter(o => o.state === 'Pending').length;
    const acceptedCount = offers.filter(o => o.state === 'Accepted').length;
    const rejectedCount = offers.filter(o => o.state === 'Declined' || o.state === 'Rejected').length;

    if (isLoading) return <div className="dashboard-view"><h2>Loading offers...</h2></div>;
    if (error) return <div className="dashboard-view"><h2 className="error-message">{error}</h2></div>;

    if (isSuccess) {
        return (
            <SuccessView title="Update Successful!">
                <p>{successMessage}</p>
            </SuccessView>
        );
    }

    return (
        <div className="dashboard-view">
            <h2>Manage Offers</h2>
            <hr />

            <div className="stats-grid">
                <StatCard icon={<FaTags />} title="Total Offers Received" value={totalOffers} />
                <StatCard icon={<FaRegClock />} title="Pending Review" value={pendingCount} />
                <StatCard icon={<FaRegCheckCircle />} title="Accepted Offers" value={acceptedCount} />
                <StatCard icon={<FaRegTimesCircle />} title="Declined Offers" value={rejectedCount} />
            </div>

            <div className="dashboard-section">
                <div className="section-header">
                    <h3>All Offers</h3>
                    <div className="filter-buttons">
                        <button onClick={() => setFilter('All')} className={filter === 'All' ? 'active' : ''}>All</button>
                        <button onClick={() => setFilter('Pending')} className={filter === 'Pending' ? 'active' : ''}>Pending</button>
                        <button onClick={() => setFilter('Accepted')} className={filter === 'Accepted' ? 'active' : ''}>Accepted</button>
                        <button onClick={() => setFilter('Declined')} className={filter === 'Declined' ? 'active' : ''}>Declined</button>
                    </div>
                </div>

                <div className="table-container">
                    {filteredOffers.length > 0 ? (
                        <table className="data-table">
                            <thead>
                                <tr>
                                    <th>Property</th>
                                    <th>Client</th>
                                    <th>Prices</th>
                                    <th>Offer Date</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredOffers.map(offer => (
                                    <tr key={offer.id_offer}>
                                        <td>
                                            <Link to={`/property/${offer.id_property}`} className="property-link">
                                                {offer.propertyAddress}
                                            </Link>
                                        </td>
                                        <td className="client-info">
                                            <div>{offer.clientName}</div>
                                            <small>{offer.clientEmail}</small>
                                        </td>
                                        <td>
                                            <div className="price-comparison">
                                                <span className="listing-price">
                                                    €{new Intl.NumberFormat('it-IT').format(offer.listingPrice)}
                                                </span>
                                                <span className="offer-price">
                                                    €{new Intl.NumberFormat('it-IT').format(offer.offer_price)}
                                                </span>
                                            </div>
                                        </td>
                                        <td>{new Date(offer.offer_date).toLocaleDateString('it-IT')}</td>
                                        <td>
                                            <span className={`status-badge status-${offer.state.toLowerCase()}`}>
                                                {offer.state}
                                            </span>
                                        </td>
                                        <td className="actions-cell">
                                            {offer.state === 'Pending' && (
                                                <>
                                                    <button 
                                                        className="btn-action confirm"
                                                        onClick={() => handleUpdateOffer(offer.id_offer, 'accept')}
                                                    >
                                                        Accept
                                                    </button>
                                                    <button 
                                                        className="btn-action reject"
                                                        onClick={() => handleUpdateOffer(offer.id_offer, 'decline')}
                                                    >
                                                        Decline
                                                    </button>
                                                </>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    ) : (
                        <p>No offers found for the selected filter.</p>
                    )}
                </div>
            </div>

            <ConfirmationModal
                isOpen={isModalOpen}
                onClose={handleCloseModal}
                onConfirm={handleConfirmAction}
                title="Confirm Action"
            >
                <p>
                    Are you sure you want to <strong>{actionToConfirm?.action}</strong> this offer?
                </p>
            </ConfirmationModal>
        </div>
    );
};

export default OffersView;