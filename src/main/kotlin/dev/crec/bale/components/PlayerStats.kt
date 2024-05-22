package dev.crec.bale.components

import kotlinx.html.*

suspend fun MAIN.playerStats() {
    div {
        input(InputType.search, name = "search", classes = "text-black ring-2 ring-black p-2") {
            attributes["hx-post"] = "/stats/search"
            attributes["hx-target"] = "#stats-suggestions"
            attributes["hx-trigger"] = "input changed delay:200ms, search"
            attributes["hx-swap"] = "outerHTML"
            attributes["list"] = "stats-suggestions"
            placeholder = "Search for stats"
        }

        dataList {
            id = "stats-suggestions"
        }
    }
//    DatabaseSingleton.query {
//        DefaultStats.selectAll().orderBy(DefaultStats.stat.charLength() to SortOrder.ASC).forEach { row ->
//            val stat = row[DefaultStats.stat]
//            div {
//                +"$stat"
//            }
//        }
//    }
}

//suspend fun DIV.statsSuggestions(val inputText: String) {
//    DatabaseSingleton.query {
//        DefaultStats.selectAll().orderBy(DefaultStats.stat.charLength() to SortOrder.ASC).forEach { row ->
//            val stat = row[DefaultStats.stat]
//            div {
//                +"$stat"
//            }
//        }
//    }
//}
//
