package koreea_colea_sasha.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import koreea_colea_sasha.presentation.navigations.Navigation
import koreea_colea_sasha.presentation.screens.addedittask.AddEditTaskViewModel
import koreea_colea_sasha.presentation.screens.calendar.CalendarViewModel
import koreea_colea_sasha.presentation.screens.home.HomeViewModel
import koreea_colea_sasha.ui.theme.MainBackground
import koreea_colea_sasha.ui.theme.TodoListAppTheme
import koreea_colea_sasha.ui.theme.TodoListAppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val lat = mutableDoubleStateOf(0.0)
    private val long = mutableDoubleStateOf(0.0)
    private val hasLocationAccess = mutableStateOf(false)

    // Объявляем переменную для регистрации ActivityResultLauncher
    private lateinit var requestLocationPermission: ActivityResultLauncher<String>

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })


        // Инициализация FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Регистрация ActivityResultLauncher для запроса разрешений
        requestLocationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(applicationContext, "Location Permission Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(applicationContext, "Location Permission Denied", Toast.LENGTH_SHORT).show()
                // Предложить открыть настройки
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = "package:$packageName".toUri()
                }
                startActivity(intent)

            }
        }

        // Получение текущего местоположения
        getCurrentLocation()

        //val background = if (MaterialTheme.colors.isLight) LightBackground else DarkBackground


        setContent {

            val addEditTaskViewModel = viewModel(AddEditTaskViewModel::class.java)
            val homeViewModel = viewModel(HomeViewModel::class.java)
            val calendarViewModel = viewModel(CalendarViewModel::class.java)

            if (isOnline(this)) homeViewModel.updateNetworkAccess(true)
            if (hasLocationAccess.value) homeViewModel.updateLocationAccess(true)

            if (isOnline(this) && hasLocationAccess.value) {
                homeViewModel.getWeatherData(lat.doubleValue, long.doubleValue)
                homeViewModel.getUserLocation(lat.doubleValue, long.doubleValue)
            }
            val LightBackground = Color(0xFFFFFFFF) // белый
            val DarkBackground = Color(0xFF121212)  // тёмно-серый (почти чёрный)

            TodoListAppTheme { // ⬅️ Тема применяется здесь
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background // ⬅️ Используем цвет из темы
                ) {
                    // Твой основной UI:
                    Navigation(
                        addEditTaskViewModel = viewModel(),
                        homeViewModel = viewModel(),
                        calendarViewModel = viewModel(),
                    )
                }
            }
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.activeNetwork?.let { network ->
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
                        capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
                        capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true
            }
        } else {
            // Для API < 23
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return networkInfo != null && networkInfo.isConnected
        }

        return false
    }


    private fun getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(applicationContext, "Unable to retrieve location", Toast.LENGTH_SHORT).show()
                    } else {
                        lat.doubleValue = location.latitude
                        long.doubleValue = location.longitude
                        hasLocationAccess.value = true
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Turn on Location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }



    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }




}
