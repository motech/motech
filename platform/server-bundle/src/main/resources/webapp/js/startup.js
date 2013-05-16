'use strict';

function setUrl(parent, child, target) {
    var childId = $(child).attr('id');

    $("button", child).each(function () {
        $(this).click(function () {
            var text = $("div[id='" + parent + "'] div[id='" + childId + "'] span").text();
            text = text.substring(text.lastIndexOf(' ') + 1);

            $("input[name='" + target + "']").val(text);
        });
    });
}

$(function () {
    $("div[id='database.urls'] div").each(function () {
        setUrl('database.urls', this, 'databaseUrl');
    });

    $("div[id='queue.urls'] div").each(function () {
        setUrl('queue.urls', this, 'queueUrl');
    });

    $("div[id='scheduler.urls'] div").each(function () {
        setUrl('scheduler.urls', this, 'schedulerUrl');
    });
});