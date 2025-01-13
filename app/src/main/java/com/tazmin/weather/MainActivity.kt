package com.tazmin.weather


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tazmin.weather.retrofit.RetrofitClient
import com.tazmin.weather.retrofit.WeatherApi
import com.tazmin.weather.ui.theme.Beige
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import com.tazmin.weather.retrofit.WeatherResponse
import com.tazmin.weather.ui.theme.BeigeD
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


const val API_KEY = "d0eb34618ca44deb9da85210250701"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PreviewMyContent()
        }
    }
}

private suspend fun getWeatherData(city: String): WeatherResponse? {
    return try {
        val api = RetrofitClient.instance.create(WeatherApi::class.java)
        api.getWeather(apiKey = API_KEY, city)
    } catch (e: Exception) {
        Log.e("WeatherError", "Ошибка при получении данных: ${e.message}")
        null
    }
}

@Preview
@Composable
fun PreviewMyContent() {
    var temperature by remember { mutableStateOf("N/A") }
    var windKph by remember { mutableStateOf("N/A") }
    var humidity by remember { mutableStateOf("N/A") }
    var ozone by remember { mutableStateOf("N/A") }
    var uvIndex by remember { mutableStateOf("N/A") }
    var inputCity by remember { mutableStateOf("") }
    var displayCity by remember { mutableStateOf("") }
    var precip by remember { mutableStateOf("N/A") }
    var feelLike by remember { mutableStateOf("N/A") }
    val interFamily = FontFamily(
        Font(R.font.inter_medium)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .background(
                Brush.linearGradient(
                    colors = listOf(Beige, BeigeD),
                    start = androidx.compose.ui.geometry.Offset(0f, 240f),
                    end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                )
            )
    ) {
        CityInputField(
            city = inputCity,
            onCityChange = { inputCity = it },
            fontFamily = interFamily,
            onFetchWeatherData = { newTemp, newWind, newHumidity, newOzone, newUvIndex, newPrecip, newFellLike ->
                temperature = newTemp
                windKph = newWind
                humidity = newHumidity
                ozone = newOzone
                uvIndex = newUvIndex
                precip = newPrecip
                feelLike = newFellLike
            },
            onCityConfirm = { displayCity = it}
        )

        WeatherDisplay(city = displayCity, temperature = temperature, fontFamily = interFamily)

        InfoDisplay(
            fontFamily = interFamily,
            wind = windKph,
            humidity = humidity,
            ozone = ozone,
            uvIndex = uvIndex,
            precip = precip,
            feelLike = feelLike
        )
    }
}

@Composable
fun CityInputField(
    city: String,
    onCityChange: (String) -> Unit,
    fontFamily: FontFamily,
    onFetchWeatherData: (String, String, String, String, String, String, String) -> Unit,
    onCityConfirm: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row {
            TextField(
                value = city,
                onValueChange = { newCity ->
                    onCityChange(newCity)
                },
                label = { Text("Введите город") },
                placeholder = { Text("Например, Moscow") },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Normal
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp)

            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    onCityConfirm(city)
                    coroutineScope.launch {
                        val weatherData = getWeatherData(city)
                        weatherData?.let {
                            onFetchWeatherData(
                                it.current.temp_c.toString(),
                                it.current.wind_kph.toString(),
                                it.current.humidity.toString(),
                                it.current.ozone.toString(),
                                it.current.uv.toString(),
                                it.current.precip_mm.toString(),
                                it.current.feelslike_c.toString()
                            )
                        } ?: onFetchWeatherData("Ошибка", "Ошибка", "Ошибка", "Ошибка", "Ошибка","Ошибка","Ошибка")
                    }
                }
            )  {
                val searchIcon = painterResource(id = R.drawable.ic_search)
                Image(
                    painter = searchIcon,
                    contentDescription = "SearchButton",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}

@Composable
fun WeatherDisplay(city: String, temperature: String, fontFamily: FontFamily) {
    Box(
        modifier = Modifier
            .fillMaxHeight(0.3f)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = city.ifEmpty { "Введите город" },
                style = TextStyle(
                    fontSize = 35.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM ", Locale("ru"))
            val formattedDate = currentDate.format(formatter)
            Text(
                text = formattedDate,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {

                val cloudSunIcon = painterResource(id = R.drawable.ic_cloudsun)
                Image(
                    painter = cloudSunIcon,
                    contentDescription = "Облачно",
                    modifier = Modifier
                        .size(160.dp)
                        .padding(start = 20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$temperature °C",
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                )

            }
        }
    }

}
@Composable
fun InfoDisplay(
    fontFamily: FontFamily,
    wind: String,
    humidity: String,
    ozone: String,
    uvIndex: String,
    feelLike: String,
    precip: String
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color.White.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center)
            ) {

                WeatherRow(
                    fontFamily = fontFamily,
                    items = listOf(
                        Triple(painterResource(R.drawable.ic_cloudrain), "Осадки", precip),
                        Triple(painterResource(R.drawable.ic_wind), "Ветер", wind),
                        Triple(painterResource(R.drawable.ic_humidity), "Влажность", humidity)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                WeatherRow(
                    fontFamily = fontFamily,
                    items = listOf(
                        Triple(painterResource(R.drawable.ic_sunhorizon), "УФ индекс", uvIndex),
                        Triple(painterResource(R.drawable.ic_thermometer), "Feels Like", feelLike),
                        Triple(painterResource(R.drawable.ic_sunhorizon), "Озон", ozone)
                    )
                )
            }
        }
    }
}

@Composable
fun WeatherRow(
    fontFamily: FontFamily,
    items: List<Triple<Painter, String, String>>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        items.forEach { (icon, label, value) ->
            WeatherInfoBox(label, value, fontFamily, icon,Modifier.weight(1f))
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun WeatherInfoBox(
    label: String,
    value: String,
    fontFamily: FontFamily,
    icon: Painter,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp)
    ) {
        Image(
            painter = icon,
            contentDescription = "icon",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(30.dp)
        )
        Text(
            text = label,
            style = TextStyle(
                fontSize = 15.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
        )
        Text(
            text = value,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        )
    }
}



