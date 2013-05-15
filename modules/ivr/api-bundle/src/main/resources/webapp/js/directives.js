'use strict';

angular.module('motech-ivr').directive('tooltip', function () {

    return {
        restrict:'A',
        link:function (scope, element, attr) {

            var tooltipContainer = $($($(element).parents('div')[0]).children('.tooltip')[0]);

            element.popover({
                trigger:'click',
                placement:'bottom',
                html:true,
                title:$(tooltipContainer.children()[0]),
                content:$(tooltipContainer.children()[1])
            });
        }
    }
});

angular.module('motech-ivr').directive('typeahead', function () {

    return {
        restrict:'A',
        link:function (scope, element, attr) {
            $.get("../ivr/api/calllog/phone-numbers", function (data) {
                element.typeahead({
                    source:data
                });
            });
        }
    }
});



angular.module('motech-ivr').directive('ngSlider', function (CalllogMaxDuration) {
    return function (scope, element, attributes) {
        CalllogMaxDuration.get(function (data) {

            var sliderElement = $(element);

            var getSliderMin = function () {
                return sliderElement.slider("values", 0);
            };
            var getSliderMax = function () {
                return sliderElement.slider("values", 1);
            };
            var setSliderMin = function (val) {
                sliderElement.slider("values", 0, val);
            };
            var setSliderMax = function (val) {
                sliderElement.slider("values", 1, val);
            };
            var setSliderInputs = function (min, max) {
                $(".slider-control[slider-point='min']").val(min);
                $(".slider-control[slider-point='max']").val(max);
            };

            scope.maxDuration = data.maxDuration;

            sliderElement.slider({
                range:true,
                min:0,
                max:scope.maxDuration,
                values:[0, scope.maxDuration],
                slide:function (event, ui) {
                    setSliderInputs(ui.values[0], ui.values[1]);
                },
                change:function (event, ui) {
                    setSliderInputs(ui.values[0], ui.values[1]);
                }
            });
            setSliderInputs(getSliderMin(), getSliderMax());

            $(".slider-control").blur(function (e) {
                var sliderTextControl = $(e.target);
                var val = parseInt(sliderTextControl.val().match(/\d+/));
                if (val === NaN) {
                    return;
                }
                switch (sliderTextControl.attr("slider-point")) {
                    case "min":
                        if (val >= 0 && val < getSliderMax()) {
                            setSliderMin(val);
                        } else if (val >= getSliderMax() && val < scope.maxDuration) {
                            setSliderMin(val);
                            setSliderMax(val);
                        } else {
                            sliderTextControl.val(getSliderMin());
                        }
                        break;
                    case "max":
                        if (val <= scope.maxDuration && val > getSliderMin()) {
                            setSliderMax(val);
                        } else if (val <= getSliderMin()) {
                            setSliderMax(val);
                            setSliderMin(val);
                        } else {
                            sliderTextControl.val(getSliderMax());
                        }
                        break;
                }
            });

        });

    }
});
