package com.tazmin.weather


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.graphics.Brush
import com.tazmin.weather.ui.theme.Blue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
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

private suspend fun getTemp(city: String): String?{
    return try {
        val api = RetrofitClient.instance.create(WeatherApi::class.java)
        val response = api.getWeather(apiKey = API_KEY, city)
        response.current.temp_c.toString()
    } catch (e: Exception){
        null
    }
}

@Preview
@Composable
fun PreviewMyContent() {
    var temperature by remember { mutableStateOf("Null") }
    var city by remember { mutableStateOf("") }
    val roundedFamily = FontFamily(
        Font(R.font.yulong_regular)
    )

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .background(
                Brush.linearGradient(
                colors = listOf(Beige, Color.Cyan),
                start = androidx.compose.ui.geometry.Offset(0f, 240f),
                end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
            ))
    ) {
        CityInputField(
            city = city,
            onCityChange = { city = it },
            fontFamily = roundedFamily,
            onFetchTemperature = { newTemp ->
                temperature = newTemp }
        )
        WeatherDisplay(city = city, temperature = temperature, fontFamily = roundedFamily)
    }
}

@Composable
fun CityInputField(
    city: String,
    onCityChange: (String) -> Unit,
    fontFamily: FontFamily,
    onFetchTemperature: (String) -> Unit
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
                onValueChange = onCityChange,
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
                    coroutineScope.launch {
                        try {
                            val temp = withContext(Dispatchers.IO) { getTemp(city) }
                            temp?.let { onFetchTemperature(it) } ?: run {
                                onFetchTemperature("Ошибка")
                            }
                        } catch (e: Exception) {
                            onFetchTemperature("Ошибка")
                        }
                    }
                },
                modifier = Modifier.size(56.dp)
            ) {
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
                    fontSize = 50.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd", Locale("ru"))
            val formattedDate = currentDate.format(formatter)
            Text(
                text = formattedDate,
                style = TextStyle(
                    fontSize = 30.sp,
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
                        fontSize = 70.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                )

            }
        }
    }
}




