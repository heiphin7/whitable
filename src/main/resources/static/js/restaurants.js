// TODO: Загрузки с базы данных
// Sample restaurant data
// Features: outdoor, parking, wifi, takeout, delivery

// Изначальный список ресторанов
const restaurants = [
    {
        id: 1,
        name: "La Piazza Italiana",
        image: "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=500",
        description: "Authentic Italian cuisine in a warm, rustic atmosphere. Our handmade pasta and wood-fired pizzas will transport you straight to Italy.",
        rating: 4.8,
        price: "$$",
        cuisine: "italian",
        location: "Manhattan",
        features: ["outdoor", "parking", "wifi"],
    },
    {
        id: 2,
        name: "Sakura Japanese",
        image: "https://images.unsplash.com/photo-1502301103665-0b95cc738daf?w=500",
        description: "Experience the finest Japanese cuisine with our expert sushi chefs and traditional dishes prepared with authentic ingredients.",
        rating: 4.9,
        price: "$$$",
        cuisine: "japanese",
        location: "Brooklyn",
        features: ["delivery", "takeout"],
    },
    {
        id: 3,
        name: "Taj Mahal",
        image: "https://images.unsplash.com/photo-1517244683847-7456b63c5969?w=500",
        description: "Discover the rich flavors of India with our extensive menu of traditional curries, tandoori specialties, and fresh-baked naan.",
        rating: 4.5,
        price: "$$",
        cuisine: "indian",
        location: "Queens",
        features: ["delivery", "takeout", "wifi"],
    },
    {
        id: 4,
        name: "El Mariachi",
        image: "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=500",
        description: "Authentic Mexican street food and traditional dishes served in a vibrant, colorful atmosphere with live mariachi music on weekends.",
        rating: 4.3,
        price: "$$",
        cuisine: "mexican",
        location: "Brooklyn",
        features: ["outdoor", "delivery"],
    },
    {
        id: 5,
        name: "Le Petit Bistro",
        image: "https://images.unsplash.com/photo-1550966871-3ed3cdb5ed0c?w=500",
        description: "Classic French cuisine in an intimate setting. Our seasonal menu features traditional bistro favorites and an extensive wine list.",
        rating: 4.7,
        price: "$$$$",
        cuisine: "french",
        location: "Manhattan",
        features: ["outdoor", "parking"],
    },
    {
        id: 6,
        name: "Sushi Master",
        image: "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=500",
        description: "Premium sushi and sashimi prepared by master chefs, featuring daily fresh fish selections and innovative fusion rolls.",
        rating: 4.6,
        price: "$$$",
        cuisine: "japanese",
        location: "Manhattan",
        features: ["takeout", "wifi"],
    },
    {
        id: 7,
        name: "Spice Route",
        image: "https://images.unsplash.com/photo-1505253758473-96b7015fcd40?w=500",
        description: "A culinary journey through India's diverse regions, offering both traditional favorites and contemporary interpretations.",
        rating: 4.4,
        price: "$$",
        cuisine: "indian",
        location: "Queens",
        features: ["delivery", "takeout", "parking"],
    },
    {
        id: 8,
        name: "Trattoria Roma",
        image: "https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=500",
        description: "Family-style Italian dining featuring recipes passed down through generations, with an emphasis on fresh, local ingredients.",
        rating: 4.2,
        price: "$$",
        cuisine: "italian",
        location: "Brooklyn",
        features: ["outdoor", "wifi"],
    }
];

const featureMapping = {
    'Парковка': 'parking',
    'Wi-Fi': 'wifi',
    'Доставка': 'delivery',
    'Самовывоз': 'takeout'
};

/**
 * Запрашивает список ресторанов с /api/restaurant/findAll
 * и добавляет их к массиву restaurants
 */
// Асинхронная функция, которая делает запрос и возвращает данные
async function getData() {
    try {
        const response = await fetch('/api/restaurant/findAll');
        if (!response.ok) {
            throw new Error(`Ошибка при запросе: ${response.status}`);
        }

        const data = await response.json();
        console.log('Полученные данные:', data);
        return data;
    } catch (error) {
        console.error('Произошла ошибка:', error);
        // Если произошла ошибка, вернем пустой массив, чтобы не ломать основной код
        return [];
    }
}

// Самовызывающаяся функция, где мы дожидаемся getData()
(async () => {
    console.log('Перед запросом у нас', restaurants.length, 'ресторан(ов).');

    // Ждем данные с бэкенда
    const result = await getData();
    console.log('Явно дожидаемся результата и работаем дальше с данными:', result);

    // Допустим, вы хотите «добавить» (push) в конец массива restaurants
    // или «заменить» его полностью. Ниже пример "добавить".
    result.forEach(item => {
        // Переведем русские фичи в английские (если какой-то фичи нет в словаре, оставим как есть)
        const mappedFeatures = (item.features || []).map(f =>
            featureMapping[f] ? featureMapping[f] : f
        );

        // Формируем объект в том же стиле, что и ваши статические рестораны
        restaurants.push({
            id: item.id,
            name: item.name,
            image: item.image,
            description: item.description,
            rating: item.rating,
            price: item.price,
            cuisine: item.cuisine,
            location: item.location,
            features: mappedFeatures
        });
    });

    console.log('После запроса у нас', restaurants.length, 'ресторан(ов).');
    console.log('Все рестораны теперь:', restaurants);

    init();
})();

// State management for filters and sorting
let activeFilters = {
    cuisine: [],
    price: [],
    rating: [],
    features: []
};

let activeSorting = 'rating-desc';

// DOM Elements
const restaurantGrid = document.getElementById('restaurantGrid');
const restaurantCount = document.getElementById('restaurantCount');
const sortSelect = document.getElementById('sortSelect');
const filterCheckboxes = document.querySelectorAll('input[type="checkbox"]');

// Initialize the page
function init() {
    renderRestaurants(restaurants);
    setupEventListeners();
}

// Setup event listeners
function setupEventListeners() {
    // Sorting
    sortSelect.addEventListener('change', (e) => {
        activeSorting = e.target.value;
        applyFiltersAndSort();
    });

    // Filtering
    filterCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            updateActiveFilters();
            applyFiltersAndSort();
        });
    });
}

// Update active filters based on checkbox state
function updateActiveFilters() {
    activeFilters = {
        cuisine: Array.from(document.querySelectorAll('input[name="cuisine"]:checked')).map(cb => cb.value),
        price: Array.from(document.querySelectorAll('input[name="price"]:checked')).map(cb => cb.value),
        rating: Array.from(document.querySelectorAll('input[name="rating"]:checked')).map(cb => Number(cb.value)),
        features: Array.from(document.querySelectorAll('input[name="features"]:checked')).map(cb => cb.value)
    };
}

// Filter restaurants based on active filters
function filterRestaurants(restaurants) {
    return restaurants.filter(restaurant => {
        // Cuisine filter
        if (activeFilters.cuisine.length && !activeFilters.cuisine.includes(restaurant.cuisine)) {
            return false;
        }

        // Price filter
        if (activeFilters.price.length && !activeFilters.price.includes(restaurant.price)) {
            return false;
        }

        // Rating filter
        if (activeFilters.rating.length) {
            const passesRating = activeFilters.rating.some(rating => restaurant.rating >= rating);
            if (!passesRating) return false;
        }

        // Features filter
        if (activeFilters.features.length) {
            const hasAllFeatures = activeFilters.features.every(feature =>
                restaurant.features.includes(feature)
            );
            if (!hasAllFeatures) return false;
        }

        return true;
    });
}

// Sort restaurants based on active sorting
function sortRestaurants(restaurants) {
    const sortedRestaurants = [...restaurants];

    switch (activeSorting) {
        case 'rating-desc':
            return sortedRestaurants.sort((a, b) => b.rating - a.rating);
        case 'rating-asc':
            return sortedRestaurants.sort((a, b) => a.rating - b.rating);
        case 'price-desc':
            return sortedRestaurants.sort((a, b) => b.price.length - a.price.length);
        case 'price-asc':
            return sortedRestaurants.sort((a, b) => a.price.length - b.price.length);
        case 'name-asc':
            return sortedRestaurants.sort((a, b) => a.name.localeCompare(b.name));
        case 'name-desc':
            return sortedRestaurants.sort((a, b) => b.name.localeCompare(a.name));
        default:
            return sortedRestaurants;
    }
}

// Apply both filters and sorting
function applyFiltersAndSort() {
    const filteredRestaurants = filterRestaurants(restaurants);
    const sortedAndFilteredRestaurants = sortRestaurants(filteredRestaurants);
    renderRestaurants(sortedAndFilteredRestaurants);
}

// Render restaurants to the grid
function renderRestaurants(restaurants) {
    restaurantGrid.innerHTML = '';
    restaurantCount.textContent = restaurants.length;

    restaurants.forEach(restaurant => {
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
        <div class="restaurant-features">
          ${restaurant.features.map(feature => `
            <span class="feature-tag">
              ${getFeatureIcon(feature)} ${formatFeatureName(feature)}
            </span>
          `).join('')}
        </div>
      </div>
    `;

        restaurantGrid.appendChild(card);
    });
}

// Helper function to get rating class
function getRatingClass(rating) {
    if (rating >= 4.5) return 'rating-5';
    if (rating >= 4.0) return 'rating-4';
    if (rating >= 3.0) return 'rating-3';
    return 'rating-2';
}

// Helper function to get feature icon
function getFeatureIcon(feature) {
    const icons = {
        outdoor: '<i class="fas fa-umbrella-beach"></i>',
        delivery: '<i class="fas fa-motorcycle"></i>',
        takeout: '<i class="fas fa-shopping-bag"></i>',
        parking: '<i class="fas fa-parking"></i>',
        wifi: '<i class="fas fa-wifi"></i>'
    };
    return icons[feature] || '';
}

// Helper function to format feature name
function formatFeatureName(feature) {
    const names = {
        outdoor: 'Outdoor',
        delivery: 'Delivery',
        takeout: 'Takeout',
        parking: 'Parking',
        wifi: 'WiFi'
    };
    return names[feature] || feature;
}

// Export the restaurants array
export { restaurants };