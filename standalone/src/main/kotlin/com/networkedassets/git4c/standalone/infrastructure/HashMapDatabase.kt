package com.networkedassets.git4c.standalone.infrastructure

import com.networkedassets.git4c.core.bussiness.Database

abstract class HashMapDatabase<T> : HashMapDataSource<T>(), Database<T>