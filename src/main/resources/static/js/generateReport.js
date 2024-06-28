// Function to initialize DataTable with specified options and row click event handling
function initializeDataTable(tableId) {
    var table = $(tableId).DataTable({
        deferRender: true,
        scrollCollapse: true,
        fixedColumns: true,
        paging: false,
        dom: 'Bfrtip',
        responsive: true,
        fixedHeader: {
            header: true,
            footer: true
        },
        buttons: [
            'excel',
            'pdf'
        ]
    });

    table.on('click', 'tbody tr', function() {
        var $row = table.row(this).nodes().to$();
        var hasClass = $row.hasClass('selected');
        if (hasClass) {
            $row.removeClass('selected')
        } else {
            $row.addClass('selected')
        }
    });

    table.rows().every(function() {
        this.nodes().to$().removeClass('selected')
    });
}

// Initialize DataTables for all tables
$(document).ready(function() {
    initializeDataTable('#customertb');
    initializeDataTable('#vehicletb');
    initializeDataTable('#maintenancetb');
    initializeDataTable('#stafftb');
});