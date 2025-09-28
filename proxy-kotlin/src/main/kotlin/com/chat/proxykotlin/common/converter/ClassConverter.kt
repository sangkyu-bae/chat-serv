package com.chat.proxykotlin.common.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object  ClassConverter {
    inline fun <reified T> Map<String, String>.convertTo(): T {
        val mapper = jacksonObjectMapper()
        return mapper.convertValue(this)
    }
    inline fun <reified T> ObjectMapper.readList(values: List<String>): List<T> {
        return values.map { this.readValue(it, T::class.java) }
    }

}