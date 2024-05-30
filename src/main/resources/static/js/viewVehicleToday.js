$(document).ready(function() {
    loadCars();

    $('#searchInput, #typeFilter, #sortDirection').on('change keyup', function() {
        loadCars();
    });

    function loadCars() {
        var searchVal = $('#searchInput').val();
        var statusVal = $('#statusFilter').val();
        var typeVal = $('#typeFilter').val();
        var sortDirection = $('#sortDirection').val();

        $.ajax({
            url: '/reservation/getAvailableCarsToday',
            type: 'GET',
            dataType: 'json',
            data: {
                search: searchVal,
                status: statusVal,
                type: typeVal,
                sort: sortDirection,
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
        carList.empty();

        $.each(cars, function(index, car) {
            carList.append(
                '<div class="col-md-4 mb-4">' +
                '<div class="card">' +
                '<img src="/vehicle/displayVehicleImage?vehicleID=' + car.vehicleID + '" class="card-img-top img-fluid img-viewCar" alt="' + car.vehicleModel + '">' +
                '<div class="card-body">' +
                '<div class="text-center mt-3">' +
                '<h5 class="card-title">' + car.vehicleBrand + ' ' + car.vehicleModel + '</h5>' +
                '<p class="card-text">' + car.vehicleLicensePlate + '</p>' +
                '<p class="card-text">RM ' + car.vehicleReservedperHours + '/hour</p>' +
                '<p class="card-text">' + car.vehicleType + '</p>' +
                '</div>' +
                '<div class="text-center mt-3">' +
                (car.vehicleStatus === 'Available' ? '<a href="/reservation/reservedCar/specificDate?vehicleID=' + car.vehicleID + '" class="btn btn-primary">Book</a>' :
                    (car.vehicleStatus === 'Maintenance' ? '<button class="btn btn-warning" disabled>Maintenance</button>' :
                        '<button class="btn btn-danger" disabled>Unavailable</button>')) +
                '</div>' +
                '</div>' +
                '</div>' +
                '</div>'
            );
        });
    }
});