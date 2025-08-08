const API_URL = 'http://localhost:8080/v1/api';
 populateCityDropdowns(); // Load cities on page load
// Sign Up
document.getElementById('signupForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('signupUsername').value;
    const email = document.getElementById('signupEmail').value;
    const password = document.getElementById('signupPassword').value;

    const response = await fetch(`${API_URL}/user/signup`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, email, password }),
    });

    const data = await response.json();
    if (data.created) {
        localStorage.setItem('username', username);
        window.location.href = 'dashboard.html';
    } else {
        alert('User creation failed.');
    }
});

// Login
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
  
    
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    const response = await fetch(`${API_URL}/user/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
    });

    const data = await response.json();
    if (data.exist) {
        localStorage.setItem('username', data.username); // Store username in local storage
        localStorage.setItem('email', email); // Store email in local storage
        const loginEmail = document.getElementById("loginEmail").value;
        const username = loginEmail.split("@")[0]; // ✅ now it works
         if (email === "admin@admin.com" && data.username === "admin") {
            window.location.href = 'admin.html'; // redirect to admin dashboard
        } else {
            window.location.href = 'dashboard.html'; // redirect to normal user dashboard
            // window.location.href = 'admin.html'; // redirect to normal user dashboard
        } // Redirect to dashboard
    } else {
        alert(data.message);
    }
});

// Display username
document.addEventListener('DOMContentLoaded', () => {
    const username = localStorage.getItem('username');
    if (username && document.getElementById('usernameDisplay')) {
        document.getElementById('usernameDisplay').innerText = username;
    }

});
// Handle Flight Search
document.getElementById('flight-search-form')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const from = document.getElementById('from').value;
    const to = document.getElementById('to').value;
    const start = document.getElementById('departure-date').value;
    const listings = document.getElementById('flight-listings');
    const loading = document.getElementById('loading');

    if (!from || !to || !start) {
        alert('Please fill all fields.');
        return;
    }

    listings.innerHTML = '';
    loading.classList.remove('hidden');

    try {
        const response = await fetch(`${API_URL}/flight/${from}/${to}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ start }),
        });

        const data = await response.json();
        loading.classList.add('hidden');

        const flights = data.available || [];

        if (flights.length === 0) {
            listings.innerHTML = `<p class="col-span-full text-center text-gray-500">No flights available.</p>`;
            return;
        }

        const cityMap = JSON.parse(localStorage.getItem('cityMap') || '{}');

        flights.forEach(flight => {
            const card = document.createElement('div');
            card.className = 'bg-white p-6 rounded-lg shadow-lg card-hover';
            card.innerHTML = `
                <h3 class="text-xl font-bold mb-2">${flight.name} (${flight.flight_no})</h3>
                <p><strong>From:</strong> ${cityMap[flight.from_city_id] || flight.from_city_id}</p>
                <p><strong>To:</strong> ${cityMap[flight.to_city_id] || flight.to_city_id}</p>
                <p><strong>Departure:</strong> ${flight.departure_date} ${flight.departure_time}</p>
                <p><strong>Arrival:</strong> ${flight.arrival_date} ${flight.arrival_time}</p>
                <p><strong>Price:</strong> ₹${flight.price}</p>
                <button class="mt-4 bg-blue-600 text-white py-2 px-4 rounded btn-hover book-now"
                    data-flight='${JSON.stringify(flight)}'>
                    Book Now
                </button>
            `;
            listings.appendChild(card);
        });

        document.querySelectorAll('.book-now').forEach(button => {
            button.addEventListener('click', (e) => {
                const flight = JSON.parse(e.target.getAttribute('data-flight'));
                localStorage.setItem('selectedFlight', JSON.stringify(flight));
                showBookingConfirmation(flight);
            });
        });

        document.getElementById('search-results').classList.remove('hidden-section');
        document.getElementById('search-results').scrollIntoView({ behavior: 'smooth' });
    } catch (err) {
        console.error(err);
        loading.classList.add('hidden');
        alert('Error fetching flights.');
    }
});

// Show booking confirmation
function showBookingConfirmation(flight) {
    const username = localStorage.getItem('username');

    if (!username) {
        alert("Username not found. Please login again.");
        window.location.href = 'login.html'; // redirect if missing
        return;
    }

    const email = username.toLowerCase().replace(/\s+/g, '.') + '@gmail.com';
    const cityMap = JSON.parse(localStorage.getItem('cityMap') || '{}');

    document.getElementById('confirm-name').textContent = username;
    document.getElementById('confirm-seat').textContent = '12A'; // or generate dynamically
    document.getElementById('confirm-from').textContent = cityMap[flight.from_city_id] || flight.from_city_id;
    document.getElementById('confirm-to').textContent = cityMap[flight.to_city_id] || flight.to_city_id;
    document.getElementById('confirm-date').textContent = flight.departure_date;
    document.getElementById('confirm-email').textContent = email;

    hideAllSections();
    document.getElementById('booking-confirmation').classList.remove('hidden-section');
    document.getElementById('booking-confirmation').scrollIntoView({ behavior: 'smooth' });
}

document.getElementById('confirm-booking')?.addEventListener('click', async () => {
    const username = localStorage.getItem('username');
    if (!username) {
        alert('User not logged in!');
        window.location.href = 'login.html';
        return;
    }

    // const email = username.toLowerCase().replace(/\s+/g, '.') + '@gmail.com';
    const email = "aditya@adi.com"
    const flight = JSON.parse(localStorage.getItem('selectedFlight')); // saved below
    const passenger_name = username;
    const passenger_surname = "NA";
    const passenger_email = email;

    const payload = {
        flight_id: flight.id,
        passenger_name,
        passenger_surname,
        passenger_email
    };

    try {
        const res = await fetch(`${API_URL}/flight/${email}/book`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        const result = await res.json();
        console.log(result);

        if (result.booked) {
            alert("Booking successful! Ticket No: " + result.ticketno);
            window.location.href = 'dashboard.html';
        } else {
            alert("Booking failed: " + result.message);
        }
    } catch (err) {
        console.error("Booking error:", err);
        alert("Booking failed due to server error.");
    }
});


// Hide all sections
function hideAllSections() {
    ['hero-section', 'search-flights', 'search-results', 'booking-confirmation', 'tourist-destinations', 'safety-services']
        .forEach(id => {
            const el = document.getElementById(id) || document.querySelector(`.${id}`);
            if (el) el.classList.add('hidden-section');
        });
}

// Back to home button
document.getElementById('back-to-home')?.addEventListener('click', () => {
    ['hero-section', 'search-flights', 'tourist-destinations', 'safety-services'].forEach(id => {
        const el = document.getElementById(id) || document.querySelector(`.${id}`);
        if (el) el.classList.remove('hidden-section');
    });

    document.getElementById('search-results')?.classList.add('hidden-section');
    document.getElementById('booking-confirmation')?.classList.add('hidden-section');
    window.scrollTo({ top: 0, behavior: 'smooth' });
});

// Fetch and populate cities in dropdowns
async function populateCityDropdowns() {
    try {
        const res = await fetch(`${API_URL}/cities`);
        const cities = await res.json();

        const fromSelect = document.getElementById('from');
        const toSelect = document.getElementById('to');
        const cityMap = {};

        cities.forEach(city => {
            // map city id to name
            cityMap[city.id] = city.name;

            const option1 = document.createElement('option');
            option1.value = city.name;
            option1.textContent = city.name;

            const option2 = option1.cloneNode(true);

            fromSelect.appendChild(option1);
            toSelect.appendChild(option2);
        });

        localStorage.setItem('cityMap', JSON.stringify(cityMap));
    } catch (err) {
        console.error("Error fetching cities:", err);
        alert("Could not load cities. Please try again later.");
    }
}

document.getElementById('logoutBtn')?.addEventListener('click', () => {
    localStorage.removeItem('username'); // Clear session data
    window.location.href = 'login.html'; // Redirect to login
});

document.getElementById('back-to-dashboard')?.addEventListener('click', () => {
    hideAllSections();
    ['hero-section', 'search-flights', 'tourist-destinations', 'safety-services']
        .forEach(id => {
            const el = document.getElementById(id) || document.querySelector(`.${id}`);
            if (el) el.classList.remove('hidden-section');
        });
    window.scrollTo({ top: 0, behavior: 'smooth' });
});

//User dashboard btn handler
document.querySelector(".Udash")?.addEventListener('click',() => {
    window.location.href = "admin.html";
});