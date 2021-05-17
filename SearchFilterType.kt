package com.oneibc.feature.jurisdiction

enum class SearchFilterType {
    JURISDICTION_1{
        override fun localization(): String = "Jurisdiction"
    },
    FAQ{
        override fun localization(): String = "FAQ"
    },
    UNKNOWN{
        override fun localization(): String = "Unknown"
    };
    abstract fun localization():String
}