package com.tinker.notifire.data.model

open class Zone(
    var id: String? = null,
    var code: String? = null,
    var description: String? = null,
    var name: String? = null,
    var longLat: Point? = null,
    var pushAlertSubscribers: MutableList<PushNotificationSubscriber>? = null,
    var smsAlertSubscribers: MutableList<SMSAlertSubscribers>? = null
)