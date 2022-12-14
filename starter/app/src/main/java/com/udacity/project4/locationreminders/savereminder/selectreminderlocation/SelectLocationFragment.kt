package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(),OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map:GoogleMap
    private var marker:Marker?=null
    private val REQUEST_LOCATION_PERMISSION=1
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        val mapFragment=childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

//        TODO: call this function after the user confirms on the selected location
        binding.saveLocation.setOnClickListener {
            onLocationSelected()
        }
        return binding.root
    }

    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        marker?.let {
            _viewModel.latitude.value=it.position.latitude
            _viewModel.longitude.value=it.position.longitude
            _viewModel.reminderSelectedLocationStr.value=it.title
            _viewModel.navigationCommand.value=NavigationCommand.Back
        }
    }

    //TODO: add the map setup implementation
    override fun onMapReady(p0: GoogleMap) {
        map=p0
        setPoiClick(map)
        setMapLongClick(map)
        styleMap(map)
        enableMyLocation()
    }
    //TODO: put a marker to location that the user selected
    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            //clear any previous markers
            map.clear()
            marker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            marker?.showInfoWindow()
        }
    }
    //TODO: zoom to the user location after taking his permission
    //returns true if the application has access to user's location
    private fun isPermissionGranted():Boolean{
        return ContextCompat
            .checkSelfPermission(requireContext()
                ,Manifest.permission.ACCESS_FINE_LOCATION)===PackageManager.PERMISSION_GRANTED
    }
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled=true
            zoomToMyLocation()
        }
        else {
            this.requestPermissions(
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
            else{
                explain()
            }
        }
    }
    private fun explain() {
        if (
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            AlertDialog.Builder(requireActivity())
                .setTitle("Location permision")
                .setMessage(R.string.permission_denied_explanation)
                .setPositiveButton("OK"){ _, _ ->
                    enableMyLocation()
                }
                .create()
                .show()

        } else {
            enableMyLocation()
        }
    }
    //zooms to the user's current location
    @SuppressLint("MissingPermission")
    private fun zoomToMyLocation(){
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location?->
            if (location!=null){
                val myLocation=LatLng(location.latitude,location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15f))
            }
        }
    }
    //TODO: add style to the map
    private fun styleMap(map: GoogleMap){
        try{
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.map_style))
        }
        catch (e:Resources.NotFoundException){
        }
    }
    //add it for the testing
    private fun setMapLongClick(map: GoogleMap){
        map.setOnMapLongClickListener { latLng ->
            //clear any previous markers
            map.clear()
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            marker=map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
            )
            marker?.showInfoWindow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType=GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType=GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType=GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType=GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


}
