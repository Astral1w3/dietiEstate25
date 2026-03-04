
import React from 'react';
import './ConfirmationModal.css';

const ConfirmationModal = ({
    isOpen,
    onClose,
    onConfirm,
    title,
    children
}) => {
    
    if (!isOpen) {
        return null;
    }

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={e => e.stopPropagation()}>
                
                <h3 className="modal-title">{title}</h3>
                
                <div className="modal-body">
                    {children}
                </div>

                <div className="modal-actions">
                    <button className="btn btn-secondary" onClick={onClose}>
                        Cancel
                    </button>
                    <button className="btn btn-primary" onClick={onConfirm}>
                        OK
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmationModal;