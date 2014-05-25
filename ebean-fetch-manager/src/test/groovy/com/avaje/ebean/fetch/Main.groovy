package com.avaje.ebean.fetch

fetchDescriptor("advProject") {
    fetch "advProjectRid"
    fetch "advProjectVipList" descriptor { fetch "*" }
    fetch "paymentCurrencyId" descriptor {
        fetch "currencyAbbreviationClosure"
        fetch "bakashka" descriptor { fetch "haha" }
    }
    fetch "advSiteCurrencyId" descriptor {
        fetch "currencyAbbreviation"
    }

    fetch "advCampaignsList" descriptor "#advCampaign"
}

fetchDescriptor("advCampaign") {
    fetch "advCampaignId"
    fetch "advThingsList" descriptor {
        fetch "test" descriptor { fetch "*"}
    }
    fetch "final"
    fetch "public"
}

//fetchDescriptor("advProject") {
//    advProjectRid
//    advProjectVipList { "*" }
//    paymentCurrencyId {
//        currencyAbbreviationClosure
//        bakashka {
//            haha
//        }
//    }
//    advSiteCurrencyId {
//        currencyAbbreviation
//    }
//
//    advCampaignsList "#advCampaign"
//}
//
//fetchDescriptor("advCampaign") {
//    "final"
//    "public"
//    advCampaignId
//    advThingsList {
//        test { "*" }
//    }
//}
