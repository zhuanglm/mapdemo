package com.paywith.offersdemo.domain.model

/**
 * Convenient access classes for filter & sort options
 * Created by PayWith Developers on 2019-11-27.
 * developers@paywith.com
 *
 * This class defines a sealed hierarchy for search modifiers, which include filters and sort options.
 * It provides a structured way to manage and access these options within the application.
 *
 * @property title The display title of the search modifier.
 * @property query The query string associated with the search modifier, used for API requests.
 * @property position The position of the search modifier in a list or UI element.
 */

sealed class SearchModifier(val title: String, val query: String, val position: Int) {

    /**
     * Defines dynamic filters received from the backend via the getOfferTags() endpoint
     */
    data class Filter(
        private val itemPosition: Int,
        private val displayName: String,
        private val queryName: String
    ) : SearchModifier(displayName, queryName, itemPosition) {

        companion object {
            const val DEFAULT_FILTER_POSITION: Int = 0
            const val DEFAULT_FILTER_TITLE: String = "All"
            const val DEFAULT_FILTER_QUERY: String = DEFAULT_FILTER_TITLE

            fun getDefaultFilter(): Filter = Filter(
                DEFAULT_FILTER_POSITION,
                DEFAULT_FILTER_TITLE,
                DEFAULT_FILTER_QUERY
            )

            fun fromQuery(query: String, filters: List<Filter>): SearchModifier {
                val filter = filters.filter { it.query == query }
                if (filter.isNotEmpty()) {
                    return (filter[0])
                }

                return getDefaultFilter()
            }
        }
    }

    /**
     * Hard-coded sort options, the only two specified currently are Best Loyalty and Closest, which should be
     * accessed via the getters in this class
     */
    data class Sort(
        private val itemPosition: Int,
        private val displayName: String,
        private val queryName: String
    ) : SearchModifier(displayName, queryName, itemPosition) {

        companion object {
            const val SORT_CLOSEST_POSITION: Int = 0
            const val SORT_CLOSEST_TITLE: String = "Closest"
            const val SORT_CLOSEST_QUERY: String = "closest"
            const val DEFAULT_SORT_QUERY: String = SORT_CLOSEST_QUERY

            fun getSortClosest(): SearchModifier = Sort(
                SORT_CLOSEST_POSITION,
                SORT_CLOSEST_TITLE,
                SORT_CLOSEST_QUERY
            )

            const val SORT_BEST_LOYALTY_POSITION: Int = 1
            const val SORT_BEST_LOYALTY_TITLE: String = "Best Loyalty"
            const val SORT_BEST_LOYALTY_QUERY: String = "loyalty_offer_amount"

            fun getSortBestLoyalty(): SearchModifier = Sort(
                SORT_BEST_LOYALTY_POSITION,
                SORT_BEST_LOYALTY_TITLE,
                SORT_BEST_LOYALTY_QUERY
            )

            fun fromQuery(query: String): SearchModifier {
                return when (query) {
                    SORT_BEST_LOYALTY_QUERY -> getSortBestLoyalty()
                    SORT_CLOSEST_QUERY -> getSortClosest()
                    else -> getSortClosest()
                }
            }
        }
    }
}
