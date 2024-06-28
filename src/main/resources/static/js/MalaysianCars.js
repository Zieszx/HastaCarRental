$(document).ready(function() {
    $.getJSON('../json/MalaysianVehicle.json', function(data) {
        var brands = $('#vehicleBrand');
        var models = $('#vehicleModel');

        // Populate the brand dropdown
        data.forEach(function(brand) {
            var option = $('<option>', {
                value: brand.brand,
                text: brand.brand
            });

            if (brand.brand === savedBrand) {
                option.attr('selected', 'selected');
            }

            brands.append(option);
        });

        // Function to populate model dropdown based on selected brand
        function populateModels(selectedBrand) {
            models.empty();
            models.append($('<option>', {
                value: '',
                text: 'Please select a model'
            }));

            if (selectedBrand) {
                var brand = data.find(b => b.brand === selectedBrand);
                brand.models.forEach(function(model) {
                    var option = $('<option>', {
                        value: model,
                        text: model
                    });

                    if (model === savedModel) {
                        option.attr('selected', 'selected');
                    }

                    models.append(option);
                });
            }
        }

        // Initial population of models based on saved brand
        populateModels(savedBrand);

        // Update the model dropdown when the brand dropdown changes
        brands.change(function() {
            var selectedBrand = $(this).val();
            populateModels(selectedBrand);
        });
    });
});