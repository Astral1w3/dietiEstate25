import React from 'react';
import './StatCard.css';

const StatCard = ({ icon, title, value, change }) => {
    return (
        <div className="stat-card">
            <div className="stat-card-icon">{icon}</div>
            <div className="stat-card-info">
                <p className="stat-card-title">{title}</p>
                <h3 className="stat-card-value">{value}</h3>
                {change && <p className="stat-card-change">{change}</p>}
            </div>
        </div>
    );
};

export default StatCard;