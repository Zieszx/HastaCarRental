$(document).ready(function() {
    // Load all cars initially
    loadCars();

    // Event listeners for search and filters
    $('#searchInput').on('keyup', function() {
        loadCars();
    });

    $('#statusFilter').on('change', function() {
        loadCars();
    });

    $('#typeFilter').on('change', function() {
        loadCars();
    });

    $('#sortDirection').on('change', function() {
        loadCars();
    });

    function loadCars() {
        var searchVal = $('#searchInput').val();
        var statusVal = $('#statusFilter').val();
        var typeVal = $('#typeFilter').val();
        var sortDirection = $('#sortDirection').val();

        $.ajax({
            url: '/reservation/getAllCars',
            type: 'GET',
            dataType: 'json',
            data: {
                search: searchVal,
                status: statusVal,
                type: typeVal,
                sort: sortDirection
            },
            success: function(data) {
                displayCars(data);
            },
            error: function(xhr, status, error) {
                console.error(xhr.responseText);
            }
        });
    }

    function displayCars(cars) {
        var carList = $('#carList');
        carList.empty(); // Clear existing content

        $.each(cars, function(index, car) {
            carList.append(
                '<div class="col-md-4 mb-4">' +
                '<div class="card">' +
                '<img src="/vehicle/displayVehicleImage?Vehicle_ID=' + car.vehicle_ID + '" class="card-img-top img-fluid img-viewCar" alt="' + car.vehicle_Model + '">' +
                '<div class="card-body">' +
                '<div class="text-center mt-3">' +
                '<h5 class="card-title">' + car.vehicle_Brand + ' ' + car.vehicle_Model + '</h5>' +
                '<p class="card-text">' + car.vehicle_LicensePlate + '</p>' +
                '<p class="card-text">RM ' + car.reserved_perHours + '/hour</p>' +
                '<p class="card-text">' + car.vehicle_Type + '</p>' +
                '</div>' +
                '<div class="text-center mt-3">' +
                (car.vehicle_Status === 'Available' ? '<a href="#" class="btn btn-primary">Book</a>' :
                    (car.vehicle_Status === 'Maintenance' ? '<button class="btn btn-warning" disabled>Maintenance</button>' :
                        '<button class="btn btn-danger" disabled>Unavailable</button>')) +
                '</div>' +
                '</div>' +
                '</div>' +
                '</div>'
            );
        });
    }
});