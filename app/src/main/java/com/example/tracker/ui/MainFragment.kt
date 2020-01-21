package com.example.tracker.ui

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.tracker.R
import com.example.tracker.base.BaseFragment
import com.example.tracker.data.model.Exercise
import com.example.tracker.data.model.Location
import com.example.tracker.utils.API_KEY
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.geojson.*
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import kotlinx.android.synthetic.main.fragment_main.*
import java.lang.Exception

class MainFragment : BaseFragment<MainViewModel>(), LocationEngineCallback<LocationEngineResult> {

    private lateinit var mapboxMap: MapboxMap
    private lateinit var style: Style
    private var points = mutableListOf<Point>()

    override fun getLayoutId(): Int = R.layout.fragment_main

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Mapbox.getInstance(context!!, API_KEY)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exercises = mutableListOf<String>("Walking", "Running", "Cycling")
        val adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, exercises)
        etExercise.setAdapter(adapter)
        btnTrack.setOnClickListener {
            viewModel.saveStartTimte()
        }
        btnStopTrack.setOnClickListener {
            viewModel.stopTracking()
        }
        mapView.getMapAsync {
            mapboxMap = it
            it.setStyle(Style.MAPBOX_STREETS) { style ->
                this.style = style

                fabMyLocation.setOnClickListener { getMyLocation(style) }
                viewModel.exercises.observe(this, Observer {
                    addLines(style, it)
                })
                getMyLocation(style)
            }
        }

        viewModel.currentTimeSaved.observe(this, Observer {

        })

        viewModel.isTrackingRunning.observe(this, Observer {
            if(it) {
                btnTrack.visibility = INVISIBLE
                btnStopTrack.visibility = VISIBLE
                startTrackingLocation()
            } else {
                btnTrack.visibility = VISIBLE
                btnStopTrack.visibility = GONE
                stopTrackingLocation()
            }
        })
        viewModel.distance.observe(this, Observer {
            tvDistance.text = "$it km"
        })
        viewModel.isTrackingRunning()
        viewModel.getHistoryOfToday()

    }

    override fun onSuccess(result: LocationEngineResult?) {
        mapboxMap.locationComponent.forceLocationUpdate(result?.lastLocation)
    }

    override fun onFailure(exception: Exception) {

    }

    private fun getMyLocation(style: Style) {

        Dexter.withActivity(activity)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object: PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {

                    val customLocationComponentOptions = LocationComponentOptions.builder(context!!)
                        .trackingGesturesManagement(true)
                        .accuracyColor(
                            ContextCompat.getColor(
                                context!!,
                                android.R.color.holo_blue_light
                            )
                        )
                        .build()

                    val locationComponentActivationOptions =
                        LocationComponentActivationOptions.builder(context!!, style)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build()

                    mapboxMap.locationComponent.apply {

                        activateLocationComponent(locationComponentActivationOptions)

                        isLocationComponentEnabled = true
                        cameraMode = CameraMode.TRACKING
                        zoomWhileTracking(10.0, 2000)
                        renderMode = RenderMode.COMPASS
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }

            }).check()
    }

    private fun addLines(style: Style, exercises: MutableList<Exercise>) {

        exercises.forEach { exercise ->
            val points = mutableListOf<Point>()
            exercise.locations.forEach {
                points.add(Point.fromLngLat(it.longitude.toDouble(), it.latitude.toDouble()))
            }
            val collection = FeatureCollection.fromFeatures(
                mutableListOf(
                    Feature.fromGeometry(
                        LineString.fromLngLats(points)
                    )
                )
            )
            if(style.getSource(exercise.date) != null){
                style.removeLayer(exercise.date)
                style.removeSource(style.getSource(exercise.date)!!)
            }
            style.addSource(GeoJsonSource(exercise.date, collection))
            style.addLayer(LineLayer(exercise.date, exercise.date)
                .withProperties(
                    PropertyFactory.lineDasharray(arrayOf(0.01f, 0.1f)),
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                    PropertyFactory.lineWidth(5f),
                    PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
                ))
        }

    }

    private fun startTrackingLocation() {
        val intent = Intent(context, TrackingService::class.java)

        context?.applicationContext?.startService(intent)
    }

    private fun stopTrackingLocation() {
        val intent = Intent(context, TrackingService::class.java)
        context?.applicationContext?.stopService(intent)
    }
}