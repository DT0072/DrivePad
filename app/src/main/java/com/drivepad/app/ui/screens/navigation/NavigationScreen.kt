package com.drivepad.app.ui.screens.navigation

import android.Manifest
import android.app.Activity
import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.drivepad.app.BuildConfig
import com.drivepad.app.navigation.NavigationSearchResult
import com.drivepad.app.ui.theme.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.navigation.NavigationApi
import com.google.android.libraries.navigation.NavigationView
import com.google.android.libraries.navigation.Navigator
import com.google.android.libraries.navigation.RoutingOptions
import com.google.android.libraries.navigation.Waypoint
import kotlin.math.*

@Composable
fun NavigationScreen(
    location: NavigationMapLocation,
    isSearching: Boolean,
    searchError: String?,
    searchResults: List<NavigationSearchResult>,
    originLocation: NavigationMapLocation?,
    onSearch: (String) -> Unit,
    onSelectResult: (NavigationSearchResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    var query by rememberSaveable { mutableStateOf("") }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    var navigator by remember { mutableStateOf<Navigator?>(null) }
    var guidanceStatus by remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasLocationPermission = it
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(activity, hasLocationPermission) {
        if (!BuildConfig.GOOGLE_NAVIGATION_ENABLED || !hasLocationPermission || activity == null) return@LaunchedEffect
        guidanceStatus = "Preparing in-app navigation"
        NavigationApi.getNavigator(activity, object : NavigationApi.NavigatorListener {
            override fun onNavigatorReady(readyNavigator: Navigator) {
                navigator = readyNavigator
                guidanceStatus = null
            }

            override fun onError(errorCode: Int) {
                guidanceStatus = navigationErrorMessage(errorCode)
            }
        })
    }

    val activeNavigator = navigator
    DisposableEffect(activeNavigator) {
        onDispose {
            activeNavigator?.stopGuidance()
            activeNavigator?.cleanup()
        }
    }

    val startGuidance = {
        val activeNavigator = navigator
        if (BuildConfig.GOOGLE_NAVIGATION_ENABLED && activeNavigator != null) {
            guidanceStatus = "Calculating route"
            val destination = Waypoint.builder()
                .setLatLng(location.latitude, location.longitude)
                .setTitle(location.label)
                .build()
            val routingOptions = RoutingOptions().travelMode(RoutingOptions.TravelMode.DRIVING)
            activeNavigator.setDestination(destination, routingOptions).setOnResultListener { status ->
                if (status == Navigator.RouteStatus.OK) {
                    activeNavigator.startGuidance()
                    guidanceStatus = null
                } else {
                    guidanceStatus = routeErrorMessage(status)
                }
            }
        } else if (!BuildConfig.GOOGLE_NAVIGATION_ENABLED) {
            startGoogleNavigation(context, location)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val compact = maxWidth < 980.dp

        if (compact) {
            Column(Modifier.fillMaxSize()) {
                MapPane(
                    location = location,
                    originLocation = originLocation,
                    query = query,
                    onQueryChange = { query = it },
                    isSearching = isSearching,
                    searchResults = searchResults,
                    searchError = searchError,
                    onSearch = onSearch,
                    onSelectResult = onSelectResult,
                    compact = true,
                    modifier = Modifier.weight(1f),
                )
                SearchPane(
                    location = location,
                    originLocation = originLocation,
                    searchResults = searchResults,
                    onSelectResult = onSelectResult,
                    onStartGuidance = startGuidance,
                    guidanceReady = !BuildConfig.GOOGLE_NAVIGATION_ENABLED || navigator != null,
                    embeddedGuidance = BuildConfig.GOOGLE_NAVIGATION_ENABLED,
                    guidanceStatus = guidanceStatus,
                    modifier = Modifier.weight(1f),
                    compact = true,
                )
            }
        } else {
            Row(Modifier.fillMaxSize()) {
                MapPane(
                    location = location,
                    originLocation = originLocation,
                    query = query,
                    onQueryChange = { query = it },
                    isSearching = isSearching,
                    searchResults = searchResults,
                    searchError = searchError,
                    onSearch = onSearch,
                    onSelectResult = onSelectResult,
                    compact = false,
                    modifier = Modifier.weight(0.68f),
                )
                SearchPane(
                    location = location,
                    originLocation = originLocation,
                    searchResults = searchResults,
                    onSelectResult = onSelectResult,
                    onStartGuidance = startGuidance,
                    guidanceReady = !BuildConfig.GOOGLE_NAVIGATION_ENABLED || navigator != null,
                    embeddedGuidance = BuildConfig.GOOGLE_NAVIGATION_ENABLED,
                    guidanceStatus = guidanceStatus,
                    modifier = Modifier.weight(0.32f),
                    compact = false,
                )
            }
        }
    }
}

@Composable
private fun MapPane(
    location: NavigationMapLocation,
    originLocation: NavigationMapLocation?,
    query: String,
    onQueryChange: (String) -> Unit,
    isSearching: Boolean,
    searchResults: List<NavigationSearchResult>,
    searchError: String?,
    onSearch: (String) -> Unit,
    onSelectResult: (NavigationSearchResult) -> Unit,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest),
    ) {
        InAppNavigationView(
            location = location,
            originLocation = originLocation,
            modifier = Modifier.fillMaxSize(),
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(if (compact) 12.dp else 18.dp)
                .fillMaxWidth(if (compact) 0.94f else 0.62f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkSurface.copy(alpha = 0.94f),
                border = BorderStroke(1.dp, DarkDivider),
            ) {
                Column(Modifier.padding(if (compact) 12.dp else 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search destination") },
                        leadingIcon = { Icon(Icons.Filled.Search, null) },
                        trailingIcon = {
                            IconButton(onClick = { query.trim().takeIf { it.isNotEmpty() }?.let(onSearch) }) {
                                if (isSearching) {
                                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        QuickChip("Home") { onSearch("Kuala Lumpur") }
                        QuickChip("Work") { onSearch("Petronas Twin Towers") }
                        QuickChip("Fuel") { onSearch("Petrol station Kuala Lumpur") }
                    }

                    searchError?.let {
                        Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(if (compact) 12.dp else 18.dp),
            shape = RoundedCornerShape(14.dp),
            color = DarkSurface.copy(alpha = 0.96f),
            border = BorderStroke(1.dp, DarkDivider),
        ) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(location.label, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    routeSummary(originLocation, location),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SearchPane(
    location: NavigationMapLocation,
    originLocation: NavigationMapLocation?,
    searchResults: List<NavigationSearchResult>,
    onSelectResult: (NavigationSearchResult) -> Unit,
    onStartGuidance: () -> Unit,
    guidanceReady: Boolean,
    embeddedGuidance: Boolean,
    guidanceStatus: String?,
    modifier: Modifier = Modifier,
    compact: Boolean,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(if (compact) 12.dp else 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkSurface.copy(alpha = 0.94f),
            border = BorderStroke(1.dp, DarkDivider),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(location.label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(routeSummary(originLocation, location), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(
                    onClick = onStartGuidance,
                    enabled = guidanceReady,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(Icons.Filled.Navigation, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (embeddedGuidance) "Start in DrivePad" else "Open Google Maps")
                }
                guidanceStatus?.let { status ->
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (guidanceReady) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        if (searchResults.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, DarkDivider),
            ) {
                Text(
                    text = "No search results yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(18.dp),
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                itemsIndexed(searchResults) { index, result ->
                    SearchResultCard(
                        index = index,
                        result = result,
                        selected = result.latitude == location.latitude && result.longitude == location.longitude,
                        onClick = { onSelectResult(result) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    index: Int,
    result: NavigationSearchResult,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) ElectricBlue.copy(alpha = 0.12f) else DarkSurfaceVariant.copy(alpha = 0.84f),
        border = BorderStroke(1.dp, if (selected) ElectricBlue.copy(alpha = 0.4f) else DarkDivider),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        if (selected) ElectricBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(12.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(result.label, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (result.subtitle.isNotBlank()) {
                    Text(result.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun QuickChip(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = DarkSurfaceVariant.copy(alpha = 0.86f),
        border = BorderStroke(1.dp, DarkDivider),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.MyLocation, null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

private fun routeSummary(origin: NavigationMapLocation?, destination: NavigationMapLocation): String {
    if (origin == null) return "Map centered on selected destination"
    val distanceKm = haversineKm(origin.latitude, origin.longitude, destination.latitude, destination.longitude)
    val minutes = (distanceKm * 2.0 + 3.0).roundToInt().coerceAtLeast(1)
    return String.format(
        java.util.Locale.US,
        "%.1f km away · about %d min drive",
        distanceKm,
        minutes,
    )
}

private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InAppNavigationView(
    location: NavigationMapLocation,
    originLocation: NavigationMapLocation? = null,
    modifier: Modifier = Modifier,
) {
    if (BuildConfig.GOOGLE_NAVIGATION_ENABLED) {
        GoogleNavigationView(location = location, modifier = modifier)
    } else {
        OpenStreetMapView(location = location, originLocation = originLocation, modifier = modifier)
    }
}

@Composable
private fun GoogleNavigationView(
    location: NavigationMapLocation,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val navigationView = remember(context) {
        NavigationView(context).apply {
            onCreate(null)
            setNavigationUiEnabled(true)
            setHeaderEnabled(true)
            setEtaCardEnabled(true)
            setRecenterButtonEnabled(true)
            setTripProgressBarEnabled(true)
        }
    }

    DisposableEffect(lifecycleOwner, navigationView) {
        var started = false
        var resumed = false

        fun moveToCurrentState() {
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) && !started) {
                navigationView.onStart()
                started = true
            }
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) && !resumed) {
                navigationView.onResume()
                resumed = true
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> if (!started) {
                    navigationView.onStart()
                    started = true
                }
                Lifecycle.Event.ON_RESUME -> if (!resumed) {
                    navigationView.onResume()
                    resumed = true
                }
                Lifecycle.Event.ON_PAUSE -> if (resumed) {
                    navigationView.onPause()
                    resumed = false
                }
                Lifecycle.Event.ON_STOP -> if (started) {
                    navigationView.onStop()
                    started = false
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        moveToCurrentState()

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            if (resumed) navigationView.onPause()
            if (started) navigationView.onStop()
            navigationView.onDestroy()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { navigationView },
        update = { view ->
            if (view.tag != location) {
                view.tag = location
                view.getMapAsync { map ->
                    val destination = LatLng(location.latitude, location.longitude)
                    map.clear()
                    map.addMarker(MarkerOptions().position(destination).title(location.label))
                    map.isTrafficEnabled = true
                    map.uiSettings.isMapToolbarEnabled = false
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 15f))
                }
            }
        },
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun OpenStreetMapView(
    location: NavigationMapLocation,
    originLocation: NavigationMapLocation?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mapScript = remember(location, originLocation) {
        if (originLocation == null) {
            "window.drivePadShowLocation(${location.latitude},${location.longitude},${jsString(location.label)});"
        } else {
            "window.drivePadShowRoute(${originLocation.latitude},${originLocation.longitude}," +
                "${location.latitude},${location.longitude},${jsString(location.label)});"
        }
    }
    AndroidView(
        modifier = modifier,
        factory = {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.setGeolocationEnabled(true)
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.allowFileAccess = true
                settings.allowUniversalAccessFromFileURLs = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                setBackgroundColor(AndroidColor.BLACK)
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String?) {
                        super.onPageFinished(view, url)
                        (view.tag as? String)?.let { script ->
                            view.evaluateJavascript(script, null)
                        }
                    }

                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        val uri = request.url
                        if (uri.scheme == "http" || uri.scheme == "https" || uri.scheme == "file") {
                            return false
                        }
                        return try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            true
                        } catch (_: Exception) {
                            true
                        }
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
                        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        callback?.invoke(origin, granted, false)
                    }
                }
                tag = mapScript
                loadUrl("file:///android_asset/drivepad_map.html")
            }
        },
        update = { view ->
            if (view.tag != mapScript) {
                view.tag = mapScript
                view.evaluateJavascript(mapScript, null)
            }
        },
    )
}

private fun navigationErrorMessage(errorCode: Int): String = when (errorCode) {
    NavigationApi.ErrorCode.NOT_AUTHORIZED -> "Google Navigation API key is not authorized"
    NavigationApi.ErrorCode.TERMS_NOT_ACCEPTED -> "Accept Google Navigation terms to continue"
    NavigationApi.ErrorCode.NETWORK_ERROR -> "Google Navigation could not connect"
    NavigationApi.ErrorCode.LOCATION_PERMISSION_MISSING -> "Location permission is required"
    else -> "Google Navigation is unavailable"
}

private fun routeErrorMessage(status: Navigator.RouteStatus): String = when (status) {
    Navigator.RouteStatus.NO_ROUTE_FOUND -> "No driving route was found"
    Navigator.RouteStatus.NETWORK_ERROR -> "Route calculation could not connect"
    Navigator.RouteStatus.QUOTA_CHECK_FAILED -> "Google Navigation quota is unavailable"
    Navigator.RouteStatus.LOCATION_DISABLED -> "Turn on tablet location services"
    Navigator.RouteStatus.LOCATION_UNKNOWN -> "Waiting for an accurate location"
    else -> "Could not start this route"
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun jsString(value: String): String =
    "\"" + value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", " ") + "\""

private fun startGoogleNavigation(context: android.content.Context, location: NavigationMapLocation) {
    val destination = String.format(
        java.util.Locale.US,
        "%f,%f",
        location.latitude,
        location.longitude,
    )
    val navigationIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("google.navigation:q=$destination&mode=d"),
    ).apply {
        setPackage("com.google.android.apps.maps")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val fallbackIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$destination&travelmode=driving"),
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    runCatching { context.startActivity(navigationIntent) }
        .recoverCatching { context.startActivity(fallbackIntent) }
}
