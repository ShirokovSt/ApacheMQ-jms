package com.example.project4.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable


class PersonDto (
    @JsonProperty("name")
    val name: String,
    @JsonProperty("last-name")
    val lastName: String
): Serializable


