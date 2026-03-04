import SearchBar from '../../components/SearchBar/SearchBar';
import './Home.css'; 

const Home = () => {
    return (
        <div className="hero-container">
            <div className="hero-overlay"></div>

            <main className="hero-content">
                <h1>Discover a place<br />you'll love to live</h1>
                
                <div className="search-container">
                    <SearchBar />
                </div>
                
            </main>
        </div>
    );
};

export default Home;