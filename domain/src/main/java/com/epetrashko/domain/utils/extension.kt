package com.epetrashko.domain.utils

fun <T> Set<T>.addMutably(element: T) =
    toMutableSet().apply { add(element) }