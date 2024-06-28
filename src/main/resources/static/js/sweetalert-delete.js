function confirmDelete(deleteId, args) {
    const swalWithBootstrapButtons = Swal.mixin({
        customClass: {
            confirmButton: "btn btn-success",
            cancelButton: "btn btn-danger"
        },
        buttonsStyling: false
    });

    swalWithBootstrapButtons.fire({
        title: "Are you sure?",
        text: "You won't be able to revert this!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Yes, delete it!",
        cancelButtonText: "No, cancel!",
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            // Send AJAX request to delete collaboration
            $.ajax({
                type: "GET",
                url: args + deleteId,
                success: function(data) {
                    // Handle success response
                    swalWithBootstrapButtons.fire({
                        title: "Deleted!",
                        text: "Your file has been deleted.",
                        icon: "success"
                    }).then(() => {
                        location.reload();
                    });
                },
                error: function(error) {
                    // Handle error response
                    swalWithBootstrapButtons.fire({
                        title: "Error",
                        text: "An error occurred while deleting this data.",
                        icon: "error"
                    });
                }
            });
        } else if (
            result.dismiss === Swal.DismissReason.cancel
        ) {
            swalWithBootstrapButtons.fire({
                title: "Cancelled",
                text: "Your Data file is safe :)",
                icon: "error"
            });
        }
    });
}

$(document).ready(function() {
    $('.delete-btn').on('click', function() {
        // Get collaboration ID from the button's data attribute or other appropriate way
        var deleteId = $(this).data('delete-id');
        var args = $(this).data('args');
        confirmDelete(deleteId, args);
    });
});