package ru.fensy.dev.extensions

import graphql.schema.DataFetchingEnvironment

fun DataFetchingEnvironment.pageNumber() = this.queryDirectives.getImmediateAppliedDirective("page")
    .firstOrNull()?.getArgument("pageNumber")?.getValue<Int>() ?: 1

fun DataFetchingEnvironment.pageSize(default: Int) = this.queryDirectives.getImmediateAppliedDirective("page")
    .firstOrNull()?.getArgument("pageSize")?.getValue<Int>() ?: default