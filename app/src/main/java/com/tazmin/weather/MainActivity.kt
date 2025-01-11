package com.tazmin.weather


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tazmin.weather.retrofit.RetrofitClient
import com.tazmin.weather.retrofit.WeatherApi
import com.tazmin.weather.ui.theme.BlueI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
    var temperature by remember { mutableStateOf( "Unknown") }
    var city by remember { mutableStateOf("") }
    val yulongFamily = FontFamily(
        Font(R.font.yulong_regular)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxHeight(0.1f).fillMaxWidth().background(Color.White),
            contentAlignment = Alignment.Center){
            Text(text = "Weather in $city: $temperature", style = TextStyle(fontSize = 20.sp,
                fontFamily = yulongFamily,
                fontWeight = FontWeight.Normal,
                color = Color(0xff9ed6df)
            ))
        }

        Box(modifier = Modifier.fillMaxHeight(0.1f).fillMaxWidth().background(Color.White),
            contentAlignment = Alignment.Center){
            TextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Введите город") },
                placeholder = { Text("Например, Moscow") },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    color = Color.Blue,
                    fontFamily = yulongFamily,
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .fillMaxWidth(),
            )
        }

        Box(modifier = Modifier.fillMaxWidth().background(Color.White),
            contentAlignment = Alignment.Center){
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch{
                    val temp = getTemp(city)
                    temp?.let{
                        temperature=it
                    }
                }
            }, modifier = Modifier.padding(3.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color(0xff004D40),
                    containerColor = Color(0xff9ed6df))) {
                Text(text="Обновить",
                    style = TextStyle(fontSize = 20.sp,
                    fontFamily = yulongFamily,
                    fontWeight = FontWeight.Normal,
                    color = Color.White),
                )
            }
        }
    }
}


