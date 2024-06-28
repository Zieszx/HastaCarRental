$(document).ready(function() {
    // Select the form element
    var form = $('form.needs-validation');

    // Submit event listener
    form.on('submit', function(event) {
        if (!form[0].checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
        }

        form.addClass('was-validated');
    });

    // Input event listener for each form element
    form.find('.form-control').on('input', function() {
        if (this.checkValidity()) {
            $(this).removeClass('is-invalid');
        } else {
            $(this).addClass('is-invalid');
        }
    });
});