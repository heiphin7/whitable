// Изначальный список ресторанов (Пустой так как подгружаем динамически)
const restaurants = [];

const featureMapping = {
    'Уличные сиденья': 'outdoor',
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

    // Если по заданным фильтрам ничего не найдно, тогда рендерим блок not-found вместо просто пустого grid-а
    if (restaurants.length === 0) {
        restaurantGrid.innerHTML = `
              <div
                class="not-found col-span-full w-full flex flex-col items-center justify-center
                       bg-gray-50 rounded-xl p-10 shadow-md transition-all" style="min-height: calc(100vh - 6rem); width: 1000px;">
                <div class="flex items-center justify-center w-24 h-24 bg-blue-100 rounded-full mb-6">
                  <i class="fas fa-utensils text-5xl text-blue-500"></i>
                </div>
                <h2 class="text-3xl font-bold text-gray-700 mb-3">Рестораны не найдены</h2>
                <p class="text-gray-500 text-lg text-center max-w-md">
                  Попробуйте изменить фильтры или сбросить их, чтобы увидеть больше ресторанов.
                </p>
              </div>
            `;
        return;
    }


    restaurants.forEach(restaurant => {
        // Преобразуем рейтинг в число
        const ratingValue = parseFloat(restaurant.rating) || 0;

        // Задаём текст и класс в зависимости от наличия рейтинга
        let ratingText, ratingClass;
        if (ratingValue > 0) {
            ratingText  = ratingValue.toFixed(1);
            ratingClass = getRatingClass(ratingValue);
        } else {
            ratingText  = 'N/A';
            ratingClass = 'bg-gray-200 text-gray-600';
        }

        const card = document.createElement('div');
        card.className = 'restaurant-card';
        card.style.cursor = 'pointer';
        card.innerHTML = `
      <img src="${restaurant.image}" alt="${restaurant.name}" class="restaurant-image">
      <div class="restaurant-info">
        <div class="restaurant-header">
          <h3 class="restaurant-name">${restaurant.name}</h3>
            <span class="restaurant-rating ${ratingClass}">${ratingText}</span>        
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

        // Добавляем переход по клику на карточку
        card.addEventListener('click', () => {
            window.location.href = `/restaurant/${restaurant.id}`;
        });

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
        outdoor: 'Уличные сиденья',
        delivery: 'Доставка',
        takeout: 'Самовывоз',
        parking: 'Парковка',
        wifi: 'WiFi'
    };
    return names[feature] || feature;
}

// Export the restaurants array
export { restaurants };