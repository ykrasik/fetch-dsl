package com.rawcod.fetch

fetchDescriptor("desc1") {
    fetch "d1_c1"
    fetch "d1_c2" descriptor { fetch "*" }
    fetch "d1_c3" descriptor {
        fetch "d1_c31"
        fetch "d1_c32" descriptor { fetch "d1_c321" }
    }

    fetch "d1_c4" descriptor "#desc2"
}

fetchDescriptor("desc2") {
    fetch "d2_c1"
    fetch "d2_c2" descriptor {
        fetch "d2_c21" descriptor { fetch "*" }
    }
    // Check that keywords work
    fetch "final"
    fetch "public"
}
