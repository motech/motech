document.onkeypress = function(e) {
    e = e || window.event;
    var keyCode = e.which || e.keyCode;
    if (keyCode !== 8 && keyCode !== 46) {
        return;
    }

    var sel = rangy.getSelection();
    if (sel.rangeCount === 0) {
        return;
    }

    var selRange = sel.getRangeAt(0);
    if (!selRange.collapsed) {
        return;
    }

    var nonEditable = selRange.startContainer.previousSibling ? selRange.startContainer.previousSibling : $('.nonEditable').last()[0];

    if (!nonEditable) {
        return;
    }

    if(nonEditable.nodeType===3) {
        return;
    }

    var range = rangy.createRange();
    range.collapseAfter(nonEditable);

    if (selRange.compareBoundaryPoints(range.START_TO_END, range) == -1) {
        return;
    }

    range.setEnd(selRange.startContainer, selRange.startOffset);
    if (range.toString() === "") {
        selRange.collapseBefore(nonEditable);
        nonEditable.parentNode.removeChild(nonEditable);
        sel.setSingleRange(selRange);
        return false;
    }
};
