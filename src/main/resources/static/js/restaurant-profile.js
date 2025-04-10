// Import restaurants data
import { restaurants } from './restaurants.js';

// Get URL parameters
const urlParams = new URLSearchParams(window.location.search);
const restaurantId = urlParams.get('id') || 1; // Default to first restaurant if no ID provided

// Find the current restaurant
const currentRestaurant = restaurants.find(r => r.id === Number(restaurantId)) || restaurants[0];

// Update page content with restaurant data
function updateRestaurantContent(restaurant) {
    // Update hero section
    document.getElementById('restaurantImage').src = restaurant.image;
    document.getElementById('restaurantName').textContent = restaurant.name;
    document.getElementById('restaurantDescription').textContent = restaurant.description;

    const ratingElement = document.getElementById('restaurantRating');
    ratingElement.textContent = restaurant.rating;
    ratingElement.className = `rating ${getRatingClass(restaurant.rating)}`;
}

// Get similar restaurants (excluding current one)
function getSimilarRestaurants(currentRestaurant, count = 5) {
    return restaurants
        .filter(r => r.id !== currentRestaurant.id)
        .sort(() => Math.random() - 0.5)
        .slice(0, count);
}

// Render similar restaurants
function renderSimilarRestaurants(similarRestaurants) {
    const container = document.getElementById('similarRestaurants');
    container.innerHTML = '';

    similarRestaurants.forEach(restaurant => {
        const ratingClass = getRatingClass(restaurant.rating);

        const card = document.createElement('div');
        card.className = 'restaurant-card';
        card.innerHTML = `
      <img src="${restaurant.image}" alt="${restaurant.name}" class="restaurant-image">
      <div class="restaurant-info">
        <div class="restaurant-header">
          <h3 class="restaurant-name">${restaurant.name}</h3>
          <span class="restaurant-rating ${ratingClass}">${restaurant.rating}</span>
        </div>
        <p class="restaurant-description">${restaurant.description}</p>
        <div class="restaurant-meta">
          <span class="restaurant-price">${restaurant.price}</span>
          <span><i class="fas fa-map-marker-alt"></i> ${restaurant.location}</span>
        </div>
      </div>
    `;

        // Add click event to navigate to the restaurant's profile
        card.addEventListener('click', () => {
            window.location.href = `/restaurant-profile.html?id=${restaurant.id}`;
        });

        container.appendChild(card);
    });
}

// Helper function to get rating class
function getRatingClass(rating) {
    if (rating >= 4.5) return 'rating-5';
    if (rating >= 4.0) return 'rating-4';
    if (rating >= 3.0) return 'rating-3';
    return 'rating-2';
}

// Handle reservation form submission
const reservationForm = document.querySelector('.reservation-form');
reservationForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const formData = {
        date: document.getElementById('date').value,
        time: document.getElementById('time').value,
        guests: document.getElementById('guests').value
    };

    // Here you would typically send this data to your backend
    console.log('Reservation details:', formData);
    alert('Reservation request submitted successfully!');
    reservationForm.reset();
});

// Initialize page
function init() {
    updateRestaurantContent(currentRestaurant);
    const similarRestaurants = getSimilarRestaurants(currentRestaurant);
    renderSimilarRestaurants(similarRestaurants);
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', init);