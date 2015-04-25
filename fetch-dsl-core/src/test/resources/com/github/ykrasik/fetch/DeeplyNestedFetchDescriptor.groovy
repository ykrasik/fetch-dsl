package com.github.ykrasik.fetch

fetchDescriptor("desc4") {
    fetch "c4"
}

fetchDescriptor("desc1") {
    fetch "c1" descriptor "#desc2"
}

fetchDescriptor("desc2") {
    fetch "c2" descriptor "#desc3"
}

fetchDescriptor("desc3") {
    fetch "c3" descriptor "#desc4"
}
