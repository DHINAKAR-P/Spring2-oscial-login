package com.zimidy.exceptions

class ChatException : Exception {

    constructor() {
        // TODO Auto-generated constructor stub
    }
    constructor(exception: String) : super(exception) {} // TODO Auto-generated constructor stub
    constructor(e: Throwable) : super(e) {}
}
