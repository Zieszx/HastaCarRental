$(document).ready(function() {
    // Function to fetch notifications and display them in a popup
    function fetchNotifications() {
        $.ajax({
            url: '/api/notifications',
            method: 'GET',
            success: function(notifications) {
                // Sort notifications by date/time in descending order
                notifications.sort((a, b) => new Date(b.notificationDate + 'T' + b.notificationTime) - new Date(a.notificationDate + 'T' + a.notificationTime));

                let notificationsHtml = '<ul class="notification-list">';
                // Display up to the top 5 notifications
                notifications.slice(0, 10).forEach((notification, index) => {
                    // Determine the CSS class based on notification status
                    let liClass = notification.notificationStatus === 'Unread' ? 'unread-notification' : '';

                    notificationsHtml += '<li class="' + liClass + '">';
                    notificationsHtml += '<a href="' + notification.notificationURL + '" data-id="' + notification.notificationID + '">';
                    notificationsHtml += '<strong>' + notification.notificationTitle + '</strong><br>';
                    notificationsHtml += notification.notificationMessage + '<br>';
                    notificationsHtml += '<small>' + notification.notificationDate + ' ' + notification.notificationTime + '</small>';
                    notificationsHtml += '</a>';
                    notificationsHtml += '</li>';
                    // Add <hr> after each notification except the last one
                    if (index < notifications.length - 1) {
                        notificationsHtml += '<hr>';
                    }
                });
                notificationsHtml += '</ul>';
                $('#notificationPopup').html(notificationsHtml);
                $('#notificationPopup').toggle(); // Show/Hide the popup

                // Add click event listener for notification links
                $('.notification-list a').click(function(event) {
                    event.preventDefault();
                    let notificationId = $(this).data('id');
                    let notificationUrl = $(this).attr('href');

                    // Mark the notification as read
                    markAsRead(notificationId, notificationUrl);
                });
            },
            error: function() {
                alert('Failed to fetch notifications.');
            }
        });
    }

    // Function to mark notification as read
    function markAsRead(notificationId, notificationUrl) {
        console.log('Marking notification as read: ' + notificationId);
        console.log('Notification URL: ' + notificationUrl)
        $.ajax({
            url: '/api/notifications/' + notificationId + '/read',
            method: 'POST',
            success: function() {
                window.location.href = notificationUrl;
            },
            error: function() {
                alert('Failed to mark notification as read.');
            }
        });
    }

    // Event listener for the bell icon click
    $('.nav-link.nav-icon-hover.notifi-item').click(function(event) {
        event.stopPropagation(); // Prevent the document click event from firing
        fetchNotifications();
    });

    // Close the notification popup when clicking outside of it
    $(document).click(function(event) {
        if (!$(event.target).closest('.nav-item, #notificationPopup').length) {
            $('#notificationPopup').hide();
        }
    });
});