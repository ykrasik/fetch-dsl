package com.github.ykrasik.fetch

// Both forward and backward references
descriptor('desc4') {
    c4 '#desc5'
}

descriptor('desc2') {
    c2 '#desc3'
}

descriptor('desc3') {
    c3 '#desc4'
}

descriptor('desc1') {
    c1 '#desc2'
}

descriptor('desc5') {
    c5
}