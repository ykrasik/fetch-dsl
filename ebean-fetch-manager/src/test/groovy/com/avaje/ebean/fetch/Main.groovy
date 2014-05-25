package com.avaje.ebean.fetch

fetchDescriptor("advProject") {
    fetch col: "advProjectRid"
    fetch col: "advProjectVipList", descriptor: { fetch col: "*" }
    fetch col: "paymentCurrencyId", descriptor: {
        fetch col: "currencyAbbreviationClosure"
        fetch col: "bakashka", descriptor: { fetch col: "haha" }
    }
    fetch col: "advSiteCurrencyId", descriptor: {
        fetch col: "currencyAbbreviation"
    }

    fetch col: "advCampaignsList", descriptor: "#advCampaign"
}

fetchDescriptor("advCampaign") {
    fetch col: "advCampaignId"
    fetch col: "advThingsList", descriptor: { fetch col: "test", descriptor: { fetch col: "*"} }
}

//fetchDescriptor("advProject") {
//    fetch "advProjectRid"
//    fetch "advProjectVipList" descriptor { fetch "*" }
//    fetch "paymentCurrencyId" descriptor {
//        fetch "currencyAbbreviationClosure"
//        fetch "bakashka" descriptor { fetch "haha" }
//    }
//    fetch "advSiteCurrencyId" descriptor {
//        fetch "currencyAbbreviation"
//    }
//
//    fetch "advCampaignsList" descriptor "#advCampaign"
//}
//
//fetchDescriptor("advCampaign") {
//    fetch "advCampaignId"
//    fetch "advThingsList" descriptor { fetch "kaka" descriptor { fetch "*"} }
//}

//builder.fetchDescriptor("advProject") {
//    advProjectRid
//    advProjectVipList { "*" }
//    paymentCurrencyId {
//        currencyAbbreviationClosure {}
//    }
//    advSiteCurrencyId { currencyAbbreviation {} }
//
//    advCampaignsList "#advCampaign"
//}
