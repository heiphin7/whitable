// Sample booking data (in a real application, this would come from a backend)
const bookings = [
    {
        id: 1,
        restaurantName: "La Belle Cuisine",
        dateTime: "2024-03-15T19:30:00",
        guests: 2,
        specialRequests: "Window seat preferred"
    },
    {
        id: 2,
        restaurantName: "Sushi Master",
        dateTime: "2024-03-20T18:00:00",
        guests: 4,
        specialRequests: "Birthday celebration"
    }
];

function formatDateTime(dateTimeStr) {
    const options = {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    };
    return new Date(dateTimeStr).toLocaleDateString('en-US', options);
}

function createBookingCard(booking) {
    const card = document.createElement('div');
    card.className = 'booking-card';
    card.innerHTML = `
        <div class="restaurant-name">${booking.restaurantName}</div>
        <div class="booking-details">
            <p>${formatDateTime(booking.dateTime)}</p>
            <p>${booking.guests} ${booking.guests === 1 ? 'guest' : 'guests'}</p>
            ${booking.specialRequests ? `<p>Note: ${booking.specialRequests}</p>` : ''}
        </div>
        <div class="booking-actions">
            <button class="btn btn-modify" onclick="modifyBooking(${booking.id})">Modify</button>
            <button class="btn btn-cancel" onclick="cancelBooking(${booking.id})">Cancel</button>
        </div>
    `;
    return card;
}

function displayBookings() {
    const container = document.getElementById('bookingsContainer');
    bookings.forEach(booking => {
        container.appendChild(createBookingCard(booking));
    });
}

// Placeholder functions for booking actions
window.modifyBooking = (bookingId) => {
    alert(`Modify booking ${bookingId}`);
};

window.cancelBooking = (bookingId) => {
    if (confirm('Are you sure you want to cancel this booking?')) {
        alert(`Booking ${bookingId} cancelled`);
    }
};

// Initialize the page
document.addEventListener('DOMContentLoaded', displayBookings);