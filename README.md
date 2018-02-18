```kotlin
System.setProperty("javax.net.ssl.trustStore", "cacerts.jks")
    System.setProperty("javax.net.ssl.trustStorePassword", "changeit")
//    System.setProperty("javax.net.debug", "all")

    val dyson = DysonApi("<EMAIL>", "<PASSWORD>", "GB")

    val devices = dyson.devices()

    println("*** Found Devices: ")
    devices.forEach(Consumer {
        println("Device: \t ${it.serial} \t ${it.name}")
    })

    val serial = "<SERIAL NO>"
    val deviceMetaData = devices.find { it.serial == serial }!!

    DeviceDiscovery().use {
        val (host, port) = it.watchFor(deviceMetaData).get(5, TimeUnit.SECONDS)
        println("*** Resolved Device with ${serial} to $host:$port")
        val device = MQTTDevice(host, port, DysonCredentials.decrypt(deviceMetaData.localCredentials), deviceMetaData)
        DysonAirPurifier(device).use {
            val toggleSwitch = ToggleSwitch.OFF
            it.oscillate(toggleSwitch)
            println("*** Setting oscillate to $toggleSwitch")
        }
    }
```