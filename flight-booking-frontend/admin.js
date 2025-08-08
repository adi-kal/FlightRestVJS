const API_URL = 'http://localhost:8080/v1/api';

document.addEventListener('DOMContentLoaded', () => {
  // ✅ Check if its admin or not
  if(localStorage.getItem("username") !== "admin" && localStorage.getItem("email") !== "admin@admin.com"){
    
    loadUserData();

  }else{
    loadAdminData();
    loadCities();
    loadBookedFlights();
    loadBookedUsers();
  }
});


// ✅ Load non Admin User
async function loadUserData() {
  const tableBody = document.querySelector('#bookedFlightsTable tbody');
  
  document.getElementById('#addFlightSec').style.visibility = "hidden";
  
  document.getElementById('#addCitySec')?.style.visibility = "hidden";
  
  document.getElementById('#delUserSec')?.style.visibility = "hidden";
  
  document.getElementById('#memSec')?.style.visibility = "hidden";
  
  document.getElementById('#bookedFlightSec');
  tableBody.innerHTML = '';

  try {
    const res = await fetch(`${API_URL}/user/${localStorage.getItem('email').toString()}/fetch`);
    const flights = await res.json().then(data => {
      let {user_info} = data;
      let {cities_info,flight_info,traveler_info,seatno} = user_info;
      console.log(cities_info,flight_info,traveler_info,seatno);
        
      let row = document.createElement('tr');

      for(let i = 0; i < 3; i++){
        let tempi = i;
        let temp = i + i;

        row.appendChild(document.createElement('td').innerHTML = `<td>${flight_info[i].flight_no}</td>`);
        row.appendChild(document.createElement('td').innerHTML = `<td>${flight_info[i].name}</td>`);

        for(let j = i + i; j <= (temp + (i % 2 == 0 ? ++tempi : tempi++)); j++){
          row.appendChild(document.createElement('td').innerHTML = `<td>${cities_info[j].name}</td>`);
          // row.appendChild(document.createElement('td').innerHTML = `<td>${cityMap[f.to_city] || f.to_city_id}</td>`);
          // row.innerHTML = `
            
          //   <td>${cityMap[f.to_city] || f.to_city_id}</td>
          // `;
          tableBody?.appendChild(row);
        }

        row = row + `
          <td>${flight_info[i].departure_date}</td>
          <td>${flight_info[i].departure_time}</td>
        `
      }
    });

    // if (!Array.isArray(flights)) throw new Error("Not array");

    // for(let f in flights){
    //   const row = document.createElement('tr');
    //   // const cityMap = JSON.parse(localStorage.getItem('cityMap') || '{}');
    //   row.innerHTML = `
    //     <td>${flight_info.flight_no}</td>
    //     <td>${f.name}</td>
    //     <td>${cityMap[f.from_city] || f.from_city_id}</td>
    //     <td>${cityMap[f.to_city] || f.to_city_id}</td>
    //     <td>${f.departure_date}</td>
    //     <td>${f.departure_time}</td>`;
    //   tableBody?.appendChild(row);
    // }

    // flights.forEach(f => {
    //   const row = document.createElement('tr');
    //   const cityMap = JSON.parse(localStorage.getItem('cityMap') || '{}');
    //   row.innerHTML = `
    //     <td>${f.flight_no}</td>
    //     <td>${f.name}</td>
    //     <td>${cityMap[f.from_city] || f.from_city_id}</td>
    //     <td>${cityMap[f.to_city] || f.to_city_id}</td>
    //     <td>${f.departure_date}</td>
    //     <td>${f.departure_time}</td>`;
    //   tableBody?.appendChild(row);
    // });
  } catch (err) {
    console.error('Error loading booked flights:');
    alert('Failed to load booked flights.');
  }
}


// ✅ Load Admin Bookings (Flights and Users Table)
async function loadAdminData() {
  try {
    const res = await fetch(`${API_URL}/admin/bookings`);
    const bookings = await res.json();

    const tableBody = document.getElementById('flights-table-body');
    const userTableBody = document.getElementById('users-table-body');

    if (!Array.isArray(bookings)) throw new Error("Bookings not an array");

    bookings.forEach(b => {
      // Flights Table
      const flightRow = document.createElement('tr');
      flightRow.innerHTML = `
        <td>${b.flight_id}</td>
        <td>${b.ticketno}</td>
        <td>${b.date}</td>
        <td>${b.time}</td>
        <td>${b.price}</td>
        <td>${b.from_city_id}</td>
        <td>${b.to_city_id}</td>
      `;
      tableBody?.appendChild(flightRow);

      // Users Table
      const userRow = document.createElement('tr');
      userRow.innerHTML = `
        <td>${b.member_id}</td>
        <td>${b.traveler_id}</td>
        <td>${b.ticketno}</td>
      `;
      userTableBody?.appendChild(userRow);
    });

  } catch (err) {
    console.error("Failed to load bookings", err);
  }
}

// ✅ Load Cities into Dropdowns
async function loadCities() {
  const fromCity = document.getElementById('fromCity');
  const toCity = document.getElementById('toCity');

  try {
    const res = await fetch(`${API_URL}/cities`);
    const cities = await res.json();

    fromCity.innerHTML = '';
    toCity.innerHTML = '';

    cities.forEach(city => {
      const option1 = document.createElement('option');
      option1.value = city.id;
      option1.textContent = city.name;

      const option2 = option1.cloneNode(true);

      fromCity?.appendChild(option1);
      toCity?.appendChild(option2);
    });
  } catch (err) {
    console.error('Failed to load cities:', err);
    alert('Could not load cities.');
  }
}

// ✅ Add Flight
document.getElementById('addFlightForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();

  const flight = {
    flight_no: document.getElementById('flightNo').value,
    name: document.getElementById('flightName').value,
    capacity: parseInt(document.getElementById('capacity').value),
    price: parseInt(document.getElementById('price').value),
    departure_date: document.getElementById('departureDate').value,
    departure_time: document.getElementById('departureTime').value,
    arrival_time: document.getElementById('arrivalTime').value,
    from_city_id: parseInt(document.getElementById('fromCity').value),
    to_city_id: parseInt(document.getElementById('toCity').value),
  };

  try {
    const res = await fetch(`${API_URL}/admin/flight/add`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(flight)
    });

    const data = await res.json();
    alert(data.message || 'Flight added successfully');
  } catch (err) {
    console.error('Error adding flight:', err);
    alert('Failed to add flight.');
  }
});

// ✅ Add City
document.getElementById('addCityForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const cityName = document.getElementById('cityName').value;

  try {
    const res = await fetch(`${API_URL}/admin/cities/add`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: cityName })
    });

    const data = await res.json();
    alert(data.message || 'City added successfully');
    loadCities();
  } catch (err) {
    console.error('Error adding city:', err);
    alert('Failed to add city.');
  }
});

// ✅ Delete User
document.getElementById('deleteUserForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const email = document.getElementById('deleteUserEmail')?.value;

  try {
    const res = await fetch(`${API_URL}/admin/user/delete`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email })
    });

    const data = await res.json();
    alert(data.message || 'User deleted');
  } catch (err) {
    console.error('Error deleting user:', err);
    alert('Failed to delete user.');
  }
});

// ✅ Load Booked Flights
async function loadBookedFlights() {
  const tableBody = document.querySelector('#bookedFlightsTable tbody');
  tableBody.innerHTML = '';

  try {
    const res = await fetch(`${API_URL}/admin/booked-flights`);
    const flights = await res.json();

    if (!Array.isArray(flights)) throw new Error("Not array");

    flights.forEach(f => {
      const row = document.createElement('tr');
      const cityMap = JSON.parse(localStorage.getItem('cityMap') || '{}');
      row.innerHTML = `
        <td>${f.flight_no}</td>
        <td>${f.name}</td>
        <td>${cityMap[f.from_city] || f.from_city_id}</td>
        <td>${cityMap[f.to_city] || f.to_city_id}</td>
        <td>${f.departure_date}</td>
        <td>${f.departure_time}</td>`;
      tableBody?.appendChild(row);
    });
  } catch (err) {
    console.error('Error loading booked flights:', err);
    alert('Failed to load booked flights.');
  }
}

// ✅ Load Booked Users
async function loadBookedUsers() {
  const tableBody = document.querySelector('#bookedUsersTable tbody');
  tableBody.innerHTML = '';

  try {
    const res = await fetch(`${API_URL}/admin/booked-users`);
    const users = await res.json();

    if (!Array.isArray(users)) throw new Error("Not array");

    users.forEach(u => {
      const row = document.createElement('tr');
      row.innerHTML = `
        <td>${u.email}</td>
        <td>${u.name}</td>
        <td>${u.flight_no}</td>
      `;
      tableBody?.appendChild(row);
    });
  } catch (err) {
    console.error('Error loading booked users:', err);
    alert('Failed to load booked users.');
  }
}

document.getElementById('logoutBtn')?.addEventListener('click', () => {
    localStorage.removeItem('username'); // Clear session data
    window.location.href = 'login.html'; // Redirect to login
});

            // <td>${cityMap[f.from_city] || f.from_city_id}</td>
            // <td>${cityMap[f.to_city] || f.to_city_id}</td>