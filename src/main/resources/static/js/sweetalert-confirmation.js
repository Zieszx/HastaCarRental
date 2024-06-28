$(document).ready(function() {
    $('.confirm-btn').on('click', function(event) {
        event.preventDefault(); // Prevent default form submission

        var confirmation = $(this).data('args');
        var form = $('#registerform')[0];

        if (form.checkValidity()) {
            $.ajax({
                type: "POST",
                url: confirmation,
                data: $('#registerform').serialize(),
                success: function(response) {
                    Swal.fire({
                        title: "Success!",
                        text: "You did a marvelous work!",
                        icon: "success",
                        showCancelButton: false,
                        confirmButtonText: "OK",
                    }).then((result) => {
                        if (result.isConfirmed) {
                            location.reload();
                        }
                    });
                },
                error: function(xhr, status, error) {
                    Swal.fire({
                        title: "Error!",
                        text: "An error occurred while processing your request.",
                        icon: "error",
                        showCancelButton: false,
                        confirmButtonText: "OK",
                    });
                }
            });
        } else {
            form.classList.add('was-validated');
            Swal.fire({
                title: "Error!",
                text: "Please fill in all required fields correctly.",
                icon: "error",
                showCancelButton: false,
                confirmButtonText: "OK",
            });
        }
    });
});