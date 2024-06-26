document.addEventListener('DOMContentLoaded', function() {
    var calendarEl = document.getElementById('calendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        timeZone: "UTC",
        headerToolbar: {
            left: "prev,next today",
            center: "title",
            right: "dayGridMonth,timeGridWeek,timeGridDay,listWeek"
        },
        navLinks: true,
        editable: true,
        dayMaxEvents: true,
        events: function(fetchInfo, successCallback, failureCallback) {
            var allEvents = [];

            // Fetch Maintenance Events
            $.ajax({
                url: '/maintenance/Maintenancejson',
                dataType: 'json',
                success: function(maintenanceData) {
                    var maintenanceEvents = maintenanceData.map(function(item) {
                        return {
                            id: 'maintenance-' + item.maintenanceID,
                            title: item.maintenanceDesc,
                            start: item.maintenanceDate,
                            extendedProps: {
                                type: 'maintenance',
                                maintenanceID: item.maintenanceID,
                                vehicleID: item.vehicleID.vehicleID,
                                maintenanceDesc: item.maintenanceDesc,
                                maintenanceMileeageDuring: item.maintenanceMileeageDuring,
                                maintenanceStatus: item.maintenanceStatus
                            },
                            color: '#097969', // Color for maintenance events
                            textColor: 'white' // Text color for maintenance events
                        };
                    });
                    allEvents = allEvents.concat(maintenanceEvents);

                    // Fetch Reservation Events
                    $.ajax({
                        url: '/reservation/Reservationjson',
                        dataType: 'json',
                        success: function(reservationData) {
                            var reservationEvents = reservationData.map(function(item) {
                                return {
                                    id: 'reservation-' + item.reservationID,
                                    title: item.vehicleID.vehicleBrand + ' ' + item.vehicleID.vehicleModel,
                                    start: item.reservationStartDate,
                                    end: item.reservationEndDate,
                                    extendedProps: {
                                        type: 'reservation',
                                        reservationID: item.reservationID,
                                        customerID: item.customerID.custID,
                                        vehicleID: item.vehicleID.vehicleID,
                                        reservationPickupLocation: item.reservationPickupLocation,
                                        reservationReturnLocation: item.reservationReturnLocation,
                                        reservationStatus: item.reservationStatus,
                                        reservationReasonDeleted: item.reservationReasonDeleted
                                    },
                                    color: '#CC5500', // Color for reservation events
                                    textColor: 'white' // Text color for reservation events
                                };
                            });
                            allEvents = allEvents.concat(reservationEvents);

                            successCallback(allEvents);
                        },
                        error: function(error) {
                            console.error('Error fetching reservation data:', error);
                            failureCallback(error);
                        }
                    });
                },
                error: function(error) {
                    console.error('Error fetching maintenance data:', error);
                    failureCallback(error);
                }
            });
        },
        eventClick: function(info) {
            var eventInfo = info.event.extendedProps;
            if (eventInfo.type === 'maintenance') {
                // Handle maintenance event click
                $.ajax({
                    url: '/vehicle/vehiclejson/' + eventInfo.vehicleID,
                    dataType: 'json',
                    success: function(vehicle) {
                        Swal.fire({
                            title: 'Maintenance Details',
                            html: `
                                <img src="/vehicle/displayVehicleImage?vehicleID=${eventInfo.vehicleID}" class="card-img-top img-fluid img-viewCar mb-2" alt="${vehicle.vehicleModel}">
                                <hr>
                                <h4 class="text-center card-header mb-2">Vehicle Details</h4>
                                <p><strong>Vehicle Model:</strong> ${vehicle.vehicleModel}</p>
                                <p><strong>Vehicle Brand:</strong> ${vehicle.vehicleBrand}</p>
                                <p><strong>Vehicle Type:</strong> ${vehicle.vehicleType}</p>
                                <p><strong>Vehicle Year:</strong> ${vehicle.vehicleYear}</p>
                                <p><strong>License Plate:</strong> ${vehicle.vehicleLicensePlate}</p>
                                <hr>
                                <h4 class="text-center card-header mb-2">Maintenance Details</h4>
                                <p><strong>Maintenance Description:</strong> ${eventInfo.maintenanceDesc}</p>
                                <p><strong>Mileage During Maintenance:</strong> ${eventInfo.maintenanceMileeageDuring}</p>
                                <p><strong>Maintenance Status:</strong> ${eventInfo.maintenanceStatus}</p>
                                <hr>
                            `,
                        });
                    },
                    error: function(error) {
                        console.error('Error fetching vehicle data:', error);
                    }
                });
            } else if (eventInfo.type === 'reservation') {
                // Handle reservation event click
                $.ajax({
                    url: '/reservation/Reservationjson/' + eventInfo.reservationID,
                    dataType: 'json',
                    success: function(reservation) {
                        $.ajax({
                            url: '/customer/Customerjson/' + eventInfo.customerID,
                            dataType: 'json',
                            success: function(customer) {
                                $.ajax({
                                    url: '/vehicle/vehiclejson/' + eventInfo.vehicleID,
                                    dataType: 'json',
                                    success: function(vehicle) {
                                        Swal.fire({
                                            title: 'Reservation Details',
                                            html: `
                                                <img src="/vehicle/displayVehicleImage?vehicleID=${eventInfo.vehicleID}" class="card-img-top img-fluid img-viewCar mb-2" alt="${vehicle.vehicleModel}">
                                                <hr>
                                                <h4 class="text-center card-header mb-2">Vehicle Details</h4>
                                                <p><strong>Vehicle Model:</strong> ${vehicle.vehicleModel}</p>
                                                <p><strong>Vehicle Brand:</strong> ${vehicle.vehicleBrand}</p>
                                                <p><strong>Vehicle Type:</strong> ${vehicle.vehicleType}</p>
                                                <p><strong>Vehicle Year:</strong> ${vehicle.vehicleYear}</p>
                                                <p><strong>License Plate:</strong> ${vehicle.vehicleLicensePlate}</p>
                                                <hr>
                                                <h4 class="text-center card-header mb-2">Reservation Details</h4>
                                                <p><strong>Reservation Start Date:</strong> ${reservation.reservationStartDate}</p>
                                                <p><strong>Reservation End Date:</strong> ${reservation.reservationEndDate}</p>
                                                <p><strong>Reservation Pickup Location:</strong> ${eventInfo.reservationPickupLocation}</p>
                                                <p><strong>Reservation Return Location:</strong> ${eventInfo.reservationReturnLocation}</p>
                                                <p><strong>Reservation Status:</strong> ${eventInfo.reservationStatus}</p>
                                                <p><strong>Reservation Reason Deleted:</strong> ${eventInfo.reservationReasonDeleted}</p>
                                                <hr>
                                                <h4 class="text-center card-header mb-2">Customer Details</h4>
                                                <p><strong>Customer Name:</strong> ${customer.custName}</p>
                                                <p><strong>Customer Phone:</strong> ${customer.custPhone}</p>
                                                <p><strong>Customer Email:</strong> ${customer.custEmail}</p>
                                                <hr>
                                            `,
                                        });
                                    },
                                    error: function(error) {
                                        console.error('Error fetching vehicle data:', error);
                                    }
                                });
                            },
                            error: function(error) {
                                console.error('Error fetching customer data:', error);
                            }
                        });
                    },
                    error: function(error) {
                        console.error('Error fetching reservation data:', error);
                    }
                });
            }
        }
    });

    calendar.render(); // Render the calendar after initialization
});