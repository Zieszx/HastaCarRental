$(document).ready(function() {

    // =====================================
    // profitReservation for Car Reservation
    // =====================================
    var profitReservation = {
        series: [{
            name: "Profit for Car Reservation (RM) ",
            data: [], // Initialize with empty data
        }],
        colors: ["var(--bs-primary)"],
        chart: {
            type: "bar",
            fontFamily: "Plus Jakarta Sans', sans-serif",
            foreColor: "#adb0bb",
            width: "100%",
            height: 350,
            stacked: true,
            toolbar: {
                show: false,
            },
        },

        plotOptions: {
            bar: {
                horizontal: false,
                columnWidth: "35%",
                borderRadius: [6],
                borderRadiusApplication: 'end',
                borderRadiusWhenStacked: 'all'
            },
        },
        dataLabels: {
            enabled: false,
        },
        stroke: {
            curve: "smooth",
            width: 6,
            colors: ["transparent"],
        },

        grid: {
            borderColor: "transparent",
            padding: { top: 0, bottom: -8, left: 20, right: 20 },
        },
        tooltip: {
            theme: "dark",
        },
        toolbar: {
            show: false,
        },
        xaxis: {
            categories: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
            axisBorder: {
                show: false,
            },
            axisTicks: {
                show: false,
            },
        },
        legend: {
            show: true,
        },
        fill: {
            opacity: 1,
        },
    };

    // Function to fetch reservation data where reservationStatus is 'Returned'
    function fetchReservationData() {
        $.ajax({
            url: '/reservation/Reservationjson',
            dataType: 'json',
            success: function(data) {
                // Filter reservations where status is 'Returned'
                const returnedReservations = data.filter(function(reservation) {
                    return reservation.reservationStatus === 'Returned';
                });

                // Initialize an array to store monthly profits
                const monthlyProfits = Array(12).fill(0);

                // Calculate profit for each month based on returned reservations
                returnedReservations.forEach(function(reservation) {
                    const reservationStartDate = new Date(reservation.reservationStartDate);
                    const monthIndex = reservationStartDate.getMonth();
                    console.log(monthIndex);
                    // Profit based on payment amount and the status is 'Returned'
                    let profit = calculateReservationProfit(reservation);
                    monthlyProfits[monthIndex] += profit;
                });

                // Update chart data with monthly profits
                profitReservation.series[0].data = monthlyProfits;

                // Render the chart
                const chart = new ApexCharts($("#profitReservation")[0], profitReservation);
                chart.render();
            },
            error: function(error) {
                console.error('Error fetching reservation data:', error);
            }
        });
    }

    // Example function to calculate profit based on payment details
    function calculateReservationProfit(reservation) {
        // Assuming paymentID is present in Reservation and paymentAmount in Payment
        if (reservation.paymentID && reservation.paymentID.paymentAmount) {
            return reservation.paymentID.paymentAmount;
        } else {
            return 0; // Handle cases where payment information is incomplete or not available
        }
    }

    // Initial fetch and render
    fetchReservationData();


    // =====================================
    // Total Maintenance Services
    // =====================================
    // Function to fetch maintenance data and update charts
    function fetchMaintenanceDataAndUpdateUI() {
        $.ajax({
            url: '/maintenance/Maintenancejson',
            dataType: 'json',
            success: function(data) {
                // Initialize counters for each status
                var completeCount = 0;
                var submittedCount = 0;
                var inProgressCount = 0;
                var totalCount = 0; // Total count of all maintenance records

                // Iterate through maintenance data to count each status
                data.forEach(function(maintenance) {
                    switch (maintenance.maintenanceStatus) {
                        case 'Complete':
                            completeCount++;
                            break;
                        case 'Submitted':
                            submittedCount++;
                            break;
                        case 'In Progress':
                            inProgressCount++;
                            break;
                        default:
                            // Handle other status types if needed
                            break;
                    }
                });

                // Calculate total count
                totalCount = data.length;

                // Update the totalMaintenance chart data
                var totalMaintenanceChart = {
                    series: [completeCount, submittedCount, inProgressCount],
                    labels: ["Complete", "Submitted", "In Progress"],
                    chart: {
                        height: 170,
                        type: "donut",
                        fontFamily: "Plus Jakarta Sans', sans-serif",
                        foreColor: "#c6d1e9",
                    },
                    tooltip: {
                        theme: "dark",
                        fillSeriesColor: false,
                    },
                    colors: ["var(--bs-primary)", "#fb977d", "#28a745"], // Bootstrap colors
                    dataLabels: {
                        enabled: false,
                    },
                    legend: {
                        show: false,
                    },
                    stroke: {
                        show: false,
                    },
                    responsive: [{
                        breakpoint: 991,
                        options: {
                            chart: {
                                width: 150,
                            },
                        },
                    }],
                    plotOptions: {
                        pie: {
                            donut: {
                                size: '80%',
                                background: "none",
                                labels: {
                                    show: true,
                                    name: {
                                        show: true,
                                        fontSize: "12px",
                                        color: undefined,
                                        offsetY: 5,
                                    },
                                    value: {
                                        show: false,
                                        color: "#98aab4",
                                    },
                                },
                            },
                        },
                    },
                };

                // Render the ApexCharts for total maintenance
                var chart = new ApexCharts($("#totalMaintenance")[0], totalMaintenanceChart);
                chart.render();

                // Update total maintenance count in the UI
                $('#totalMaintenanceCount').text(totalCount);
            },
            error: function(error) {
                console.error('Error fetching maintenance data:', error);
            }
        });
    }

    // Initial fetch and render
    fetchMaintenanceDataAndUpdateUI();

    // =====================================
    // Total Reservation
    // =====================================
    // Function to fetch reservation data and update UI
    function fetchReservationDataAndUpdateUI() {
        $.ajax({
            url: '/reservation/Reservationjson',
            dataType: 'json',
            success: function(data) {
                // Initialize variables for total profits and total reservations
                var totalProfits = 0;
                var totalReservations = data.length; // Total count of reservations

                // Calculate total profits
                data.forEach(function(reservation) {
                    // Assuming profit is derived from payment amount, adjust this based on your data structure
                    if (reservation.paymentID && reservation.paymentID.paymentAmount && reservation.reservationStatus === 'Returned') {
                        totalProfits += reservation.paymentID.paymentAmount;
                    }
                });

                // Format total profits as "RM6,820" (example formatting)
                var formattedTotalProfits = "RM " + totalProfits.toLocaleString('en-MY');
                var formattedTotalReservation = "Total : " + totalReservations.toLocaleString('en-MY');

                // Update UI elements
                $('#totalProfits').text(formattedTotalProfits);
                $('#totalReservation').text(formattedTotalReservation);

                // Update sparkline chart data (assuming you want to display trends over months)
                updateSparklineChart(data);
            },
            error: function(error) {
                console.error('Error fetching reservation data:', error);
            }
        });
    }

    // Function to update sparkline chart with reservation data trends
    function updateSparklineChart(data) {
        // Initialize array for monthly counts
        var monthlyCounts = Array.from({ length: 12 }, () => 0); // Initialize array for 12 months

        // Extract data for sparkline (example: reservations per month)
        data.forEach(function(reservation) {
            // Extract reservation date and convert to Date object
            var reservationDate = new Date(reservation.reservationStartDate);

            // Get month (1-12)
            var month = reservationDate.getMonth() + 1;

            // Increment count for the respective month
            monthlyCounts[month - 1]++;
        });

        // Update sparkline chart series data
        var totalReservationChart = {
            series: [{
                name: "totalReservations",
                data: monthlyCounts,
            }],
            chart: {
                id: "totalReservationChart",
                type: "line",
                height: 60,
                sparkline: {
                    enabled: true,
                },
                group: "sparklines",
                fontFamily: "Plus Jakarta Sans', sans-serif",
                foreColor: "#adb0bb",
            },
            stroke: {
                curve: "smooth",
                width: 2,
            },
            fill: {
                colors: ["#8763da"],
                type: "solid",
                opacity: 0.2,
            },
            markers: {
                size: 0,
            },
            tooltip: {
                theme: "dark",
                fixed: {
                    enabled: true,
                    position: "right",
                },
                x: {
                    show: false,
                },
            },
        };

        // Render the ApexCharts for total reservation
        var chart = new ApexCharts(document.querySelector("#totalReservationChart"), totalReservationChart);
        chart.render();
    }

    // Initial fetch and update
    fetchReservationDataAndUpdateUI();

});