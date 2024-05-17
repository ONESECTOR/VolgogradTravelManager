package com.sector.travelmanager.utils

import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

fun MapView.zoomUp(value: Int = 1){
    this.zoomChange(value)
}

fun MapView.zoomDown(value: Int = 1){
    this.zoomChange(-1 * value)
}

fun MapView.zoomChange(different: Int = 1){
    map.move(
        CameraPosition(map.cameraPosition.target, map.cameraPosition.zoom + different, 0.0f, 0.0f),
        Animation(Animation.Type.SMOOTH, 1F), null
    )
}

fun MapView.zoomSet(value: Int = 1){
    map.move(
        CameraPosition(map.cameraPosition.target, value.toFloat(), 0.0f, 0.0f),
        Animation(Animation.Type.SMOOTH, 1F), null
    )
}