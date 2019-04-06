package com.tuvv.tword.model

import com.google.gson.annotations.Expose

data class TWord (
        @Expose val id: Int,
        @Expose val text: String?,
        @Expose val meanings: List<Meaning>
)
{

    override fun toString(): String = "id $id - $text"

}