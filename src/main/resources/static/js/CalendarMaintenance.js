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
            fetch('/maintenance/Maintenancejson')
                .then(response => response.json())
                .then(data => {
                    var events = data.map(item => ({
                        id: item.maintenanceID,
                        title: item.maintenanceDesc,
                        start: item.maintenanceDate,
                        extendedProps: {
                            vehicleID: item.vehicleID.vehicleID,
                            maintenanceDesc: item.maintenanceDesc,
                            maintenanceMileeageDuring: item.maintenanceMileeageDuring,
                            maintenanceStatus: item.maintenanceStatus
                        }
                    }));
                    successCallback(events);
                })
                .catch(error => {
                    console.error('Error fetching maintenance data:', error);
                    failureCallback(error);
                });
        },
        eventClick: function(info) {
            var vehicleID = info.event.extendedProps.vehicleID;

            fetch('/vehicle/vehiclejson/' + vehicleID)
                .then(response => response.json())
                .then(vehicle => {
                    Swal.fire({
                        title: 'Maintenance Details',
                        html: `
                            <img src="/vehicle/displayVehicleImage?vehicleID=${vehicle.vehicleID}" class="card-img-top img-fluid img-viewCar mb-2" alt="${vehicle.vehicleModel}">
                            <p><strong>Vehicle Model:</strong> ${vehicle.vehicleModel}</p>
                            <p><strong>Vehicle Brand:</strong> ${vehicle.vehicleBrand}</p>
                            <p><strong>Vehicle Type:</strong> ${vehicle.vehicleType}</p>
                            <p><strong>Vehicle Year:</strong> ${vehicle.vehicleYear}</p>
                            <p><strong>License Plate:</strong> ${vehicle.vehicleLicensePlate}</p>
                            <p><strong>Maintenance Description:</strong> ${info.event.extendedProps.maintenanceDesc}</p>
                            <p><strong>Mileage During Maintenance:</strong> ${info.event.extendedProps.maintenanceMileeageDuring}</p>
                            <p><strong>Maintenance Status:</strong> ${info.event.extendedProps.maintenanceStatus}</p>
                        `,
                    });
                })
                .catch(error => console.error('Error fetching vehicle data:', error));
        }
    });

    calendar.render();
});